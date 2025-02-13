package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.Reservation
import repositories.{ReservationRepository, RoomRepository}

import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDate
import java.time.LocalTime

@Singleton
class ReservationController @Inject()(cc: ControllerComponents,
                                      reservationRepo: ReservationRepository,
                                      roomRepo: RoomRepository)
                                     (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def listReservations = Action.async {
    reservationRepo.all().map { reservations =>
      Ok(Json.toJson(reservations))
    }
  }

  def addReservation = Action.async(parse.json) { request =>
    request.body.validate[Reservation].fold(
      errors => Future.successful(BadRequest(Json.obj("message" -> "Invalid data for Reservation"))),
      reservation => {
        roomRepo.all().flatMap { rooms =>
          if (!rooms.exists(_.id.contains(reservation.roomId))) {
            Future.successful(NotFound(Json.obj("message" -> s"Room ${reservation.roomId} not found")))
          } else {
            reservationRepo.isRoomAvailable(reservation.roomId, reservation.checkin, reservation.checkout).flatMap { available =>
              if (available) {
                reservationRepo.insert(reservation).map { createdReservation =>
                  Created(Json.toJson(createdReservation))
                }
              } else {
                Future.successful(BadRequest(Json.obj("message" -> "Room not available for the requested period")))
              }
            }
          }
        }
      }
    )
  }

  def occupancy(date: String) = Action.async {
    try {
      val localDate = LocalDate.parse(date)
      val dayStart = localDate.atStartOfDay()
      val dayEnd = localDate.atTime(LocalTime.MAX)
      reservationRepo.all().map { reservations =>
        val overlapping = reservations.filter { r =>
          r.checkin.isBefore(dayEnd) && r.checkout.isAfter(dayStart)
        }
        Ok(Json.toJson(overlapping))
      }
    } catch {
      case ex: Exception =>
        Future.successful(BadRequest(Json.obj("message" -> "Formato de data inválido. Use YYYY-MM-DD")))
    }
  }
}

