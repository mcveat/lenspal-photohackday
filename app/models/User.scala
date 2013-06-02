package models

import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}

/**
 * User: mcveat
 */
case class User(id: Option[BSONObjectID], username: String, password: String, email: String,
                blog: Option[String])
object User {
  implicit object UserBSONReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument) =
      User(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("username").get,
        doc.getAs[String]("password").get,
        doc.getAs[String]("email").get,
        doc.getAs[String]("blog")
      )
  }
  implicit object UserBSONWriter extends BSONDocumentWriter[User] {
    def write(u: User): BSONDocument =
      BSONDocument(
        "_id" -> u.id.getOrElse(BSONObjectID.generate),
        "username" -> u.username,
        "password" -> u.password,
        "email" -> u.email,
        "blog" -> u.blog
      )
  }
}
