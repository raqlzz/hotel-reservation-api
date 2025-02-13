package repositories

import javax.inject._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import models.Reservation
import java.time.LocalDateTime
import scala.concurrent.{Future, ExecutionContext}

@Singleton
class ReservationRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[H2Profile] {

  private class ReservationTable(tag: Tag) extends Table[Reservation](tag, "reservations") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def roomId = column[Long]("room_id")
    def guest = column[String]("guest")
    def checkin = column[LocalDateTime]("checkin")
    def checkout = column[LocalDateTime]("checkout")
    def * = (id.?, roomId, guest, checkin, checkout) <> ((Reservation.apply _).tupled, Reservation.unapply)
  }

  private val reservations = TableQuery[ReservationTable]

  def all(): Future[Seq[Reservation]] = db.run(reservations.result)

  def insert(reservation: Reservation): Future[Reservation] = {
    val insertQuery = (reservations returning reservations.map(_.id) into ((reservation, id) => reservation.copy(id = Some(id)))) += reservation
    db.run(insertQuery)
  }

  def delete(id: Long): Future[Int] = db.run(reservations.filter(_.id === id).delete)

  def createSchema(): Future[Unit] = db.run(reservations.schema.createIfNotExists)

  // Valida a disponibilidade: não permite sobreposição considerando 4h de limpeza após checkout
  def isRoomAvailable(roomId: Long, newCheckin: LocalDateTime, newCheckout: LocalDateTime): Future[Boolean] = {
    db.run(reservations.filter(_.roomId === roomId).result).map { existingReservations =>
      val cleanupHours = 4
      // Conflito se: novo checkin < (checkout existente + 4h) e novo checkout > checkin existente
      !existingReservations.exists { r =>
        val adjustedCheckout = r.checkout.plusHours(cleanupHours)
        newCheckin.isBefore(adjustedCheckout) && newCheckout.isAfter(r.checkin)
      }
    }
  }
}
