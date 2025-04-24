package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import services.PaiementService
import models.Paiement
import utils.JsonProtocols._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Route
import directives.AuthentificationDirective

class PaiementRoutes(paiementService: PaiementService, authDirective: AuthentificationDirective)(implicit ec: ExecutionContext) {
  val routes: Route = pathPrefix("paiements") {
    pathEndOrSingleSlash {
      get {
        onSuccess(paiementService.readAll()) { paiements =>
          complete(StatusCodes.OK, paiements)
        }
      } ~
        post {
          entity(as[Paiement]) { paiement =>
            onSuccess(paiementService.create(paiement)) { id =>
              complete(StatusCodes.Created, s"Paiement enregistré avec ID: $id")
            }
          }
        }
    } ~
      path(LongNumber) { id =>
        get {
          onSuccess(paiementService.readById(id)) {
            case Some(p) => complete(StatusCodes.OK, p)
            case None    => complete(StatusCodes.NotFound, "Paiement non trouvé")
          }
        } ~
          delete {
            onSuccess(paiementService.delete(id)) {
              case 0 => complete(StatusCodes.NotFound, "Aucun paiement supprimé")
              case _ => complete(StatusCodes.OK, s"Paiement avec ID $id supprimé")
            }
          } ~
          put {
            entity(as[Paiement]) { paiement =>
              onSuccess(paiementService.update(id, paiement)) {
                case 0 => complete(StatusCodes.NotFound, "Aucun paiement mis à jour")
                case _ => complete(StatusCodes.OK, s"Paiement avec ID $id mis à jour")
              }
            }
          }
      } ~
      path("patient" / LongNumber) { patientId =>
        get {
          onSuccess(paiementService.getByPatient(patientId)) { paiements =>
            complete(StatusCodes.OK, paiements)
          }
        }
      }
  }
}
