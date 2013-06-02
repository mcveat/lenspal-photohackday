package controllers

import _root_.models.{Post, Blog, User}
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.gridfs.GridFS
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import scala.concurrent._
import reactivemongo.api.gridfs.Implicits.DefaultReadFileReader

object Application extends Controller with MongoController with Databases with Secure {
  def index = Action { implicit request =>
    withUserOpt { implicit userOpt =>
      AsyncResult {
        blogs.find(BSONDocument()).cursor[Blog].toList.map { list =>
          val posts = list.map(_.posts).flatten.filter(_.images.size > 1).sortWith(_.date after _.date).take(5)
          Ok(views.html.index(posts))
        }
      }
    }
  }

  def register = Action {
    Ok(views.html.register(Registration.form))
  }

  def doRegister = Action { implicit request =>
    Registration.form.bindFromRequest().fold(
      error => Ok(views.html.register(error)),
      form => AsyncResult {
        val id = BSONObjectID.generate
        tryToCreateBlog.flatMap { blog =>
          val user = form.toUser.copy(id = Some(id), blog = blog.map(_.url))
          users.insert(user).map(_ => blog)
        }.flatMap {
          case Some(blog) => {
            blogs.update(
              BSONDocument("_id" -> blog.id.get),
              BSONDocument(
                "$push" -> BSONDocument("users" -> id)
              )).map(_ => Some(blog))
          }
          case _ => future { None }
        }.map { blog =>
          Redirect(
            blog.map(_.url).map(routes.Application.blog(_)).getOrElse(routes.Application.index())
          ).withSession("user" -> id.stringify).flashing(
            blog match {
              case Some(_) =>
                ("info", "We found your lenspal. This is a page where you exchange photos. Have fun!")
              case None =>
                ("info", "We're looking for a lenspal for you. You'll receive an email when it happens.")
            }
          )
        }
      }
    )
  }

  def tryToCreateBlog = {
    val query = BSONDocument("blog" -> BSONDocument("$exists" -> false))
    users.find(query).cursor[User].headOption.flatMap {
      case Some(user) => {
        val id = BSONObjectID.generate
        val blog = Blog(Some(id), NameGenerator.get, List(user.id.get))
        blogs.insert(blog)
        users.update(
            BSONDocument("_id" -> user.id.get),
            BSONDocument("$set" -> BSONDocument("blog" -> blog.url))
        )
        future { Some(blog) }
      }
      case _ => future { None }
    }
  }

  def logout = Action {
    Redirect(routes.Application.index()).withNewSession
  }

  def login = Action {
    Ok(views.html.login(LogIn.form))
  }

  def doLogin = Action { implicit request =>
    LogIn.form.bindFromRequest().fold(
      error => BadRequest,
      form => Async {
        val query = BSONDocument(
          "username" -> form.username,
          "password" -> form.password
        )
        users.find(query).cursor[User].headOption.map { userOpt =>
          userOpt.map { user =>
            Ok(
              user.blog.map(routes.Application.blog(_)).getOrElse(routes.Application.index()).absoluteURL())
                .withSession("user" -> user.id.get.stringify)
          }.getOrElse {
            BadRequest
          }
        }
      }
    )
  }

  def blog(name: String) = Action { implicit request =>
    withUserOpt { implicit userOpt =>
      AsyncResult {
        blogs.find(BSONDocument("name" -> name.replace("-", " "))).cursor[Blog].headOption.flatMap {
          case Some(b) => users.find(BSONDocument("_id" -> BSONDocument("$in" -> b.users))).cursor[User].toList.map {
            list => Some((b, list))
          }
          case None => future { None }
        }.map {
          case Some((blog, users)) => Ok(views.html.blog(blog, users))
          case None => Redirect(routes.Application.index())
        }
      }
    }
  }

  def upload(name: String) = Action(gridFSBodyParser(gridFS)) { implicit request =>
    withUserOpt {
      case None => BadRequest
      case Some(user) => {
        val updated = for {
          blog <- blogs.find(BSONDocument("name" -> name.replace("-", " "))).one[Blog].filter(_.isDefined).map(_.get)
          file <- request.body.files.head.ref
        } yield blog.newPost(file.id.asInstanceOf[BSONObjectID], user.id.get)
        AsyncResult {
          updated.flatMap {
            case Some(b) => blogs.update(BSONDocument("_id" -> b.id.get), b)
            case None => future { () }
          }.map { _ => Redirect(routes.Application.blog(name)) }
        }
      }
    }
  }

  def image(id: String) = Action { request =>
    Async {
      val file = gridFS.find(BSONDocument("_id" -> new BSONObjectID(id)))
      serve(gridFS, file)
    }
  }
}

trait Secure { self: Controller with MongoController with Databases =>
  def withUserOpt(f: Option[User] => Result)(implicit request: Request[_]) =
    request.session.get("user").map { id =>
      Async {
        val query = BSONDocument("_id" -> new BSONObjectID(id))
        users.find(query).cursor[User].headOption().map(f)
        }
    }.getOrElse {
      f(None)
    }
}

trait Databases { self: MongoController =>
  val users = db[BSONCollection]("users")
  val blogs = db[BSONCollection]("blogs")
  val gridFS = new GridFS(db)
}

case class Registration(username: String, password: String, email: String) {
  def toUser = User(None, username, password, email, None)
}
object Registration {
  def form = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "email" -> nonEmptyText
    )(Registration.apply)(Registration.unapply)
  )
}

case class LogIn(username: String, password: String)
object LogIn {
  def form = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LogIn.apply)(LogIn.unapply)
  )
}

case class Context(userOpt: Option[User] = None, blogOpt: Option[Blog] = None)
