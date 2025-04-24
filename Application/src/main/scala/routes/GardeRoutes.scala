package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import services.GardeServices
import models.Garde
import utils.JsonProtocols._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import directives.AuthentificationDirective

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class GardeRoutes(gardeService: GardeServices, authDirective: AuthentificationDirective)(implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("gardes") {
    authDirective.authentifierAvecUtilisateur { case (_, user) =>
      pathEndOrSingleSlash {
        get {
          parameters("personnelId".as[Long].?, "debut".?, "fin".?) { (personnelId, debutOpt, finOpt) =>
            val result = (personnelId, debutOpt, finOpt) match {
              case (Some(id), _, _) =>
                gardeService.getGardesParPersonnel(id)
              case (_, Some(debut), Some(fin)) =>
                gardeService.getGardesParPeriode(LocalDate.parse(debut), LocalDate.parse(fin))
              case (None, None, None) =>
                gardeService.getGardesAujourdHui()
              case _ =>
                Future.successful(Seq.empty[Garde])
            }
            onSuccess(result)(gardes => complete(StatusCodes.OK, gardes))
          }
        } ~
          post {
            entity(as[Garde]) { garde =>
              onSuccess(gardeService.planifierGarde(garde)) { id =>
                complete(StatusCodes.Created, Map("id" -> id))
              }
            }
          }
      } ~
        path(LongNumber) { id =>
          get {
            onSuccess(gardeService.getGardeParId(id)) {
              case Some(garde) => complete(StatusCodes.OK, garde)
              case None => complete(StatusCodes.NotFound, s"Garde avec l'ID $id non trouvée")
            }
          } ~
            put {
              entity(as[Garde]) { garde =>
                onSuccess(gardeService.modifierGarde(id, garde)) { updatedRows =>
                  if (updatedRows > 0)
                    complete(StatusCodes.OK, s"Garde avec l'ID $id mise à jour")
                  else
                    complete(StatusCodes.NotFound, "Garde non trouvée")
                }
              }
            } ~
            delete {
              onSuccess(gardeService.supprimerGarde(id)) { deletedRows =>
                if (deletedRows > 0)
                  complete(StatusCodes.OK, s"Garde avec l'ID $id supprimée")
                else
                  complete(StatusCodes.NotFound, "Garde non trouvée")
              }
            }
        }
    }
  }
}