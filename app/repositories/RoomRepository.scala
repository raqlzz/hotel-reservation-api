package repositories

import javax.inject._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import models.Room
import scala.concurrent.{Future, ExecutionContext}

@Singleton
class RoomRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[H2Profile] {

  private class RoomTable(tag: Tag) extends Table[Room](tag, "rooms") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def number = column[Int]("number")
    def roomType = column[String]("room_type")
    def * = (id.?, number, roomType) <> ((Room.apply _).tupled, Room.unapply)
  }

  private val rooms = TableQuery[RoomTable]

  def all(): Future[Seq[Room]] = db.run(rooms.result)

  def insert(room: Room): Future[Room] = {
    val insertQuery = (rooms returning rooms.map(_.id) into ((room, id) => room.copy(id = Some(id)))) += room
    db.run(insertQuery)
  }

  def delete(id: Long): Future[Int] = db.run(rooms.filter(_.id === id).delete)

  def createSchema(): Future[Unit] = db.run(rooms.schema.createIfNotExists)
}

