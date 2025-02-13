package modules

import javax.inject._
import play.api.{Configuration, Logger}
import play.api.inject.ApplicationLifecycle
import repositories.{ReservationRepository, RoomRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DatabaseInitializer @Inject()(
                                     roomRepo: RoomRepository,
                                     reservationRepo: ReservationRepository,
                                     config: Configuration,
                                     lifecycle: ApplicationLifecycle
                                   )(implicit ec: ExecutionContext) {
  private val logger = Logger(this.getClass)

  if (config.get[Boolean]("app.autoCreateSchema")) {
    logger.info("Criando o schema do banco de dados...")
    for {
      _ <- roomRepo.createSchema()
      _ <- reservationRepo.createSchema()
    } yield logger.info("Schema criado com sucesso.")
  } else {
    logger.info("Criação automática do schema desativada.")
  }

  lifecycle.addStopHook(() => Future.successful(()))
}
