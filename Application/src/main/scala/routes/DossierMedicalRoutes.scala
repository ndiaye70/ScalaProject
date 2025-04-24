package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import services.DossierMedicalService
import utils.JsonProtocols._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import models.DossierMedical

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Route
import directives.AuthentificationDirective
import utils.LocalDateJsonProtocol.{LongJsonFormat, StringJsonFormat, mapFormat}

class DossierMedicalRoutes(dossierService: DossierMedicalService,authDirective: AuthentificationDirective)(implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("dossiers") {
    authDirective.authentifierAvecUtilisateur { case (_, user) =>
      pathEndOrSingleSlash {
        get {
          parameters("patientId".as[Long]) { patientId =>
            onSuccess(dossierService.findByPatientId(patientId)) { dossiers =>
              complete(StatusCodes.OK, dossiers)
            }
          }
        } ~
          post {
            entity(as[DossierMedical]) { dossier =>
              onSuccess(dossierService.patientExists(dossier.patientId)) { exists =>
                if (exists) {
                  onSuccess(dossierService.create(dossier)) { id =>
                    complete(StatusCodes.Created, Map("id" -> id))
                  }
                } else {
                  complete(StatusCodes.NotFound, "Patient non trouvé")
                }
              }
            }
          }
      } ~
        path(LongNumber) { id =>
          get {
            onSuccess(dossierService.findById(id)) {
              case Some(dossier) => complete(StatusCodes.OK, dossier)
              case None => complete(StatusCodes.NotFound, "Dossier non trouvé")
            }
          } ~
            put {
              entity(as[DossierMedical]) { dossier =>
                onSuccess(dossierService.update(id, dossier)) {
                  case 0 => complete(StatusCodes.NotFound, "Dossier non trouvé")
                  case _ => complete(StatusCodes.OK, s"Dossier $id mis à jour")
                }
              }
            } ~
            delete {
              onSuccess(dossierService.delete(id)) {
                case 0 => complete(StatusCodes.NotFound, "Dossier non trouvé")
                case _ => complete(StatusCodes.OK, s"Dossier $id supprimé")
              }
            }
        }
    }
  }
}