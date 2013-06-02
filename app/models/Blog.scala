package models

import reactivemongo.bson._

/**
 * User: mcveat
 */
case class Blog(id: Option[BSONObjectID], name: String, users: List[BSONObjectID], posts: List[Post] = List()) {
  def url = name.replace(" ", "-")
  def latestPost = posts.sortWith(_.date after _.date).headOption
  def visiblePosts = posts.sortWith(_.date after _.date).filter(_.images.size > 1)
  def newPost(id: BSONObjectID, user: BSONObjectID): Option[Blog] = {
    if (!users.contains(user)) return None
    latestPost match {
      case None => Some(copy(posts = posts :+ Post(id, user)))
      case Some(post) => {
        if (post.images.size > 1) Some(copy(posts = posts :+ Post(id, user)))
        else if (post.images.head.owner == user) None
        else Some(
          copy(posts = posts.filterNot(_ == post) :+ post.copy(images = post.images :+ Image(id, user)))
        )
      }
    }
  }
  def ownsBlog(user: User) =users.contains(user.id.get)
  def canPost(user: User) = {
    def hisTurn = latestPost.map(p => p.images.size > 1 || p.images.head.owner != user.id.get).getOrElse(true)
    ownsBlog(user) && hisTurn
  }
}
object Blog {
  implicit object BlogBSONReader extends BSONDocumentReader[Blog] {
    def read(bson: BSONDocument) =
      Blog(
        bson.getAs[BSONObjectID]("_id"),
        bson.getAs[String]("name").get,
        bson.getAs[List[BSONObjectID]]("users").getOrElse(List()),
        bson.getAs[List[Post]]("posts").getOrElse(List())
      )
  }
  implicit object BlogBSONWriter extends BSONDocumentWriter[Blog] {
    def write(b: Blog) =
      BSONDocument(
        "_id" -> b.id.getOrElse(BSONObjectID.generate),
        "name" -> b.name,
        "users" -> b.users,
        "posts" -> b.posts
      )
  }
}
