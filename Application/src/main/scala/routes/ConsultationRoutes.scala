// src/main/scala/routes/ConsultationRoutes.scala
package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import services.{ConsultationService, MedicalCoordinatorService}
import models.Consultation
import utils.JsonProtocols._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import directives.AuthentificationDirective

import scala.concurrent.ExecutionContext

class ConsultationRoutes(consultationService: ConsultationService,medicalCoordinatorService: MedicalCoordinatorService,authDirective: AuthentificationDirective)(implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("consultations") {
    authDirective.authentifierAvecUtilisateur { case (_, user) =>
      pathEndOrSingleSlash {
        get {
          onSuccess(consultationService.readAll()) { consultations =>
            complete((StatusCodes.OK, consultations))
          }
        } ~
          post {
            entity(as[Consultation]) { consultation =>
              onSuccess(medicalCoordinatorService.completeConsultation(consultation)) { id =>
                complete((StatusCodes.Created, s"Consultation créée avec ID: $id"))
              }
            }
          }
      } ~
        path(LongNumber) { id =>
          get {
            onSuccess(consultationService.readById(id)) {
              case Some(consultation) => complete(consultation)
              case None => complete(StatusCodes.NotFound)
            }
          } ~
            put {
              entity(as[Consultation]) { consultation =>
                onSuccess(consultationService.update(id, consultation)) {
                  case 0 => complete((StatusCodes.NotFound, "Consultation non trouvée"))
                  case _ => complete(s"Consultation avec ID $id mise à jour")
                }
              }
            } ~
            delete {
              onSuccess(consultationService.delete(id)) {
                case 0 => complete((StatusCodes.NotFound, "Consultation non trouvée"))
                case _ => complete(s"Consultation avec ID $id supprimée")
              }
            }
        } ~
        path("patient" / LongNumber) { patientId =>
          get {
            onSuccess(consultationService.getByPatient(patientId)) { consultations =>
              complete((StatusCodes.OK, consultations))
            }
          }
        } ~
        path("patient" / LongNumber / "historique") { patientId =>
          get {
            onSuccess(medicalCoordinatorService.getFullMedicalHistory(patientId)) {
              case (consultations, Some(dossier)) =>
                complete(StatusCodes.OK, (consultations, dossier))
              case _ =>
                complete(StatusCodes.NotFound, "Aucun dossier médical trouvé pour ce patient")
            }
          }
        } ~
        path("personnel" / LongNumber) { personnelId =>
          get {
            onSuccess(consultationService.getByPersonnel(personnelId)) { consultations =>
              complete((StatusCodes.OK, consultations))
            }
          }
        } ~
        path("recent" / LongNumber) { patientId =>
          get {
            parameters("limit".as[Int].?(5)) { limit =>
              onSuccess(consultationService.getRecentByPatient(patientId, limit)) { consultations =>
                complete((StatusCodes.OK, consultations))
              }
            }
          }
        } ~
        path("search") {
          get {
            parameter("diagnostic") { keyword =>
              onSuccess(consultationService.searchByDiagnostic(keyword)) { consultations =>
                complete((StatusCodes.OK, consultations))
              }
            }
          }
        }
    }
  }
}
