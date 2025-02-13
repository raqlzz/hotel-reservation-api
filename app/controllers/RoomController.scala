package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.Room
import repositories.RoomRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoomController @Inject()(cc: ControllerComponents, roomRepo: RoomRepository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def listRooms = Action.async {
    roomRepo.all().map { rooms =>
      Ok(Json.toJson(rooms))
    }
  }

  def addRoom = Action.async(parse.json) { request =>
    request.body.validate[Room].fold(
      errors => Future.successful(BadRequest(Json.obj("message" -> "Invalid data for Room"))),
      room => {
        roomRepo.insert(room).map { createdRoom =>
          Created(Json.toJson(createdRoom))
        }
      }
    )
  }

  def deleteRoom(id: Long) = Action.async {
    roomRepo.delete(id).map { count =>
      if (count > 0) Ok(Json.obj("message" -> s"Room $id removed"))
      else NotFound(Json.obj("message" -> s"Room $id not found"))
    }
  }
}

