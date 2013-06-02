package models

import reactivemongo.bson._

/**
 * User: mcveat
 */
case class Image(id: BSONObjectID, owner: BSONObjectID)
object Image {
  implicit object ImageBSONReader extends BSONDocumentReader[Image] {
    def read(bson: BSONDocument) =
      Image(
        bson.getAs[BSONObjectID]("id").get,
        bson.getAs[BSONObjectID]("owner").get
      )
  }
  implicit object ImageBSONWriter extends BSONDocumentWriter[Image] {
    def write(i: Image) =
      BSONDocument(
        "id" -> i.id,
        "owner" -> i.owner
      )
  }
}
