// src/main/scala/services/MedicalCoordinatorService.scala
package services

import models.{Consultation, DossierMedical}
import scala.concurrent.{Future, ExecutionContext}

class MedicalCoordinatorService(
                                 consultationService: ConsultationService,
                                 dossierService: DossierMedicalService
                               )(implicit ec: ExecutionContext) {

  def completeConsultation(consultation: Consultation): Future[Long] = {
    for {
      consultationId <- consultationService.createConsultationWithDossierUpdate(consultation)
      _ <- dossierService.appendToMedicalNotes(
        consultation.patientId,
        s"Consultation du ${consultation.dateRealisation}: ${consultation.notesInternes}"
      )
    } yield consultationId
  }

  def updateConsultationAndRecords(
                                    consultationId: Long,
                                    updatedConsultation: Consultation
                                  ): Future[Int] = {
    consultationService.updateConsultationWithDossier(consultationId, updatedConsultation)
  }

  def getFullMedicalHistory(patientId: Long): Future[(Seq[Consultation], Option[DossierMedical])] = {
    for {
      consultations <- consultationService.getByPatient(patientId)
      dossier <- dossierService.findByPatientId(patientId).map(_.headOption)
    } yield (consultations, dossier)
  }
}