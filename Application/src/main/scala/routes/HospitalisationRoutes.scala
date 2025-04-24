// src/main/scala/routes/HospitalisationRoutes.scala
package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import directives.AuthentificationDirective
import services.HospitalisationService
import models.Hospitalisation
import utils.JsonProtocols._

import scala.concurrent.ExecutionContext

class HospitalisationRoutes(hospitalisationService: HospitalisationService,authDirective: AuthentificationDirective)(implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("hospitalisations") {
    authDirective.authentifierAvecUtilisateur { case (_, user) =>
      pathEndOrSingleSlash {
        get {
          onSuccess(hospitalisationService.readAll()) { hs =>
            complete(StatusCodes.OK, hs)
          }
        } ~
          post {
            entity(as[Hospitalisation]) { hosp =>
              onSuccess(hospitalisationService.create(hosp)) { id =>
                complete((StatusCodes.Created, s"Hospitalisation créée avec ID: $id"))
              }
            }
          }
      } ~
        path(LongNumber) { id =>
          get {
            onSuccess(hospitalisationService.readById(id)) {
              case Some(hosp) => complete(StatusCodes.OK, hosp)
              case None => complete(StatusCodes.NotFound, "Hospitalisation non trouvée")
            }
          } ~
            put {
              entity(as[Hospitalisation]) { hosp =>
                onSuccess(hospitalisationService.update(id, hosp)) {
                  case 0 => complete(StatusCodes.NotFound, "Hospitalisation non trouvée")
                  case _ => complete(StatusCodes.OK, s"Hospitalisation $id mise à jour")
                }
              }
            } ~
            delete {
              onSuccess(hospitalisationService.delete(id)) {
                case 0 => complete(StatusCodes.NotFound, "Hospitalisation non trouvée")
                case _ => complete(StatusCodes.OK, s"Hospitalisation $id supprimée")
              }
            }
        } ~
        path("patient" / LongNumber) { patientId =>
          get {
            onSuccess(hospitalisationService.getByPatient(patientId)) { hs =>
              complete(StatusCodes.OK, hs)
            }
          }
        } ~
        path("lit" / LongNumber) { litId =>
          get {
            onSuccess(hospitalisationService.getByLit(litId)) { hs =>
              complete(StatusCodes.OK, hs)
            }
          }
        } ~
        path("statut" / Segment) { status =>
          get {
            val actif = status.toLowerCase == "actif"
            onSuccess(hospitalisationService.getByStatut(actif)) { hs =>
              complete(StatusCodes.OK, hs)
            }
          }
        }
    }
  }
}
