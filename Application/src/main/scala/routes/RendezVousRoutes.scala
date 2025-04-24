// src/main/scala/routes/RendezVousRoutes.scala
package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import services.RendezVousService
import models.RendezVous
import utils.JsonProtocols._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import directives.AuthentificationDirective

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext

class RendezVousRoutes(rendezVousService: RendezVousService,authDirective: AuthentificationDirective)(implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("rendezvous") {
    pathEndOrSingleSlash {
      get {
        onSuccess(rendezVousService.readAll()) { rendezvous =>
          complete((StatusCodes.OK, rendezvous))
        }
      } ~
        post {
          entity(as[RendezVous]) { rendezVous =>
            onSuccess(rendezVousService.checkDisponibilite(rendezVous.personnelId, rendezVous.dateHeure)) {
              case true =>
                onSuccess(rendezVousService.create(rendezVous)) { id =>
                  complete((StatusCodes.Created, s"Rendez-vous créé avec ID: $id"))
                }
              case false =>
                complete((StatusCodes.Conflict, "Le personnel n'est pas disponible à cette heure"))
            }
          }
        }
    } ~
      path(LongNumber) { id =>
        get {
          onSuccess(rendezVousService.readById(id)) {
            case Some(rendezVous) => complete(rendezVous)
            case None => complete(StatusCodes.NotFound)
          }
        } ~
          put {
            entity(as[RendezVous]) { rendezVous =>
              onSuccess(rendezVousService.update(id, rendezVous)) {
                case 0 => complete((StatusCodes.NotFound, "Rendez-vous non trouvé"))
                case _ => complete(s"Rendez-vous avec ID $id mis à jour")
              }
            }
          } ~
          delete {
            onSuccess(rendezVousService.delete(id)) {
              case 0 => complete((StatusCodes.NotFound, "Rendez-vous non trouvé"))
              case _ => complete(s"Rendez-vous avec ID $id supprimé")
            }
          }
      } ~
      path("patient" / LongNumber) { patientId =>
        get {
          onSuccess(rendezVousService.getByPatient(patientId)) { rendezvous =>
            complete((StatusCodes.OK, rendezvous))
          }
        }
      } ~
      path("personnel" / LongNumber) { personnelId =>
        get {
          onSuccess(rendezVousService.getByPersonnel(personnelId)) { rendezvous =>
            complete((StatusCodes.OK, rendezvous))
          }
        }
      } ~
      path("periode") {
        get {
          parameters("debut".as[String], "fin".as[String]) { (debut, fin) =>
            val debutDt = LocalDateTime.parse(debut)
            val finDt = LocalDateTime.parse(fin)
            onSuccess(rendezVousService.getByDateRange(debutDt, finDt)) { rendezvous =>
              complete((StatusCodes.OK, rendezvous))
            }
          }
        }
      } ~
      path(LongNumber / "statut") { id =>
        patch {
          parameter("statut") { statut =>
            onSuccess(rendezVousService.updateStatus(id, statut)) {
              case 0 => complete((StatusCodes.NotFound, "Rendez-vous non trouvé"))
              case _ => complete(s"Statut du rendez-vous avec ID $id mis à jour")
            }
          }
        }
      }
  }
}