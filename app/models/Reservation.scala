package models

import java.time.LocalDateTime
import play.api.libs.json._

case class Reservation(id: Option[Long] = None,
                       roomId: Long,
                       guest: String,
                       checkin: LocalDateTime,
                       checkout: LocalDateTime)

object Reservation {
  implicit val localDateTimeReads: Reads[LocalDateTime] = Reads.localDateTimeReads("yyyy-MM-dd'T'HH:mm:ss")
  implicit val localDateTimeWrites: Writes[LocalDateTime] =
    Writes.temporalWrites[LocalDateTime, java.time.format.DateTimeFormatter](java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
  implicit val reservationFormat: OFormat[Reservation] = Json.format[Reservation]
}

