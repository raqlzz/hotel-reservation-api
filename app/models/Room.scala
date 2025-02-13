package models

import play.api.libs.json._

case class Room(id: Option[Long] = None, number: Int, roomType: String)

object Room {
  implicit val roomFormat: OFormat[Room] = Json.format[Room]
}

