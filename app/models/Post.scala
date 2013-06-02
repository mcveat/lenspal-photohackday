package models

import reactivemongo.bson._
import java.util.Date
import java.text.SimpleDateFormat

/**
 * User: mcveat
 */
case class Post(images: List[Image], date: Date) {
  private val format = new SimpleDateFormat("dd/MM/yyyy")
  def prettyDate = format.format(date)
}
object Post {
  implicit object PostBSONReader extends BSONDocumentReader[Post] {
    def read(bson: BSONDocument) =
      Post(
        bson.getAs[List[Image]]("images").getOrElse(List()),
        new Date(bson.getAs[Long]("date").get)
      )
  }
  implicit object PostBSONWriter extends BSONDocumentWriter[Post] {
    def write(p: Post) =
      BSONDocument(
        "images" -> p.images,
        "date" -> p.date.getTime
      )
  }
  def apply(id: BSONObjectID, user: BSONObjectID): Post = Post(List(Image(id, user)), new Date())
}
