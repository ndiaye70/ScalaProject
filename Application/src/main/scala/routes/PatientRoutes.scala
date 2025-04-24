package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import services.PatientService
import utils.JsonProtocols._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import models.Patient
import directives.AuthentificationDirective
import models.Utilisateur

import scala.concurrent.ExecutionContext

class PatientRoutes(patientService: PatientService, authDirective: AuthentificationDirective)
                   (implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("patients") {
    authDirective.authentifierAvecUtilisateur { case (_, user) =>
      pathEndOrSingleSlash {
        get {
          onSuccess(patientService.readAll()) { patients: Seq[Patient] =>
            complete((StatusCodes.OK, patients))
          }
        } ~
          post {
            entity(as[Patient]) { patient =>
              onSuccess(patientService.create(patient)) { id =>
                complete((StatusCodes.Created, s"Patient added with ID: $id"))
              }
            }
          }
      } ~
        pathPrefix(LongNumber) { id =>
          pathEndOrSingleSlash {
            get {
              onSuccess(patientService.readById(id)) {
                case Some(patient) => complete(patient)
                case None => complete(StatusCodes.NotFound)
              }
            } ~
              put {
                entity(as[Patient]) { patient =>
                  onSuccess(patientService.update(id, patient)) {
                    case 0 => complete((StatusCodes.NotFound, "Patient not found"))
                    case _ => complete(s"Patient with ID $id updated")
                  }
                }
              } ~
              delete {
                onSuccess(patientService.delete(id)) {
                  case 0 => complete((StatusCodes.NotFound, "Patient not found"))
                  case _ => complete(s"Patient with ID $id deleted")
                }
              }
          } ~
            // 🚀 GET /patients/{id}/with-dossiers
            path("with-dossiers") {
              get {
                onSuccess(patientService.getPatientWithDossiers(id)) {
                  case Some((patient, dossiers)) => complete(StatusCodes.OK, (patient, dossiers))
                  case None => complete(StatusCodes.NotFound, "Patient not found or has no dossier")
                }
              }
            }
        }
    }
  }

}