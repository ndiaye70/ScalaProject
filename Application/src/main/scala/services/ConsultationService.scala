// src/main/scala/services/ConsultationService.scala
package services

import models.{Consultation, ConsultationTable, DossiersMedicaux, Patients, Personnels}
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class ConsultationService(db: Database)(implicit ec: ExecutionContext) {

  def init(): Future[Unit] = {
    db.run(ConsultationTable.table.schema.createIfNotExists)
  }

  // CRUD de base
  def create(consultation: Consultation): Future[Long] = {
    db.run((ConsultationTable.table returning ConsultationTable.table.map(_.id)) += consultation)
  }

  def readAll(): Future[Seq[Consultation]] = {
    db.run(ConsultationTable.table.result)
  }

  def readById(id: Long): Future[Option[Consultation]] = {
    db.run(ConsultationTable.table.filter(_.id === id).result.headOption)
  }

  def update(id: Long, consultation: Consultation): Future[Int] = {
    db.run(ConsultationTable.table.filter(_.id === id)
      .update(consultation.copy(id = Some(id))))
  }

  def delete(id: Long): Future[Int] = {
    db.run(ConsultationTable.table.filter(_.id === id).delete)
  }

  // Fonctions spécifiques
  def getByPatient(patientId: Long): Future[Seq[Consultation]] = {
    db.run(ConsultationTable.table.filter(_.patientId === patientId)
      .sortBy(_.dateRealisation.desc).result)
  }

  def getByPersonnel(personnelId: Long): Future[Seq[Consultation]] = {
    db.run(ConsultationTable.table.filter(_.personnelId === personnelId)
      .sortBy(_.dateRealisation.desc).result)
  }

  def getRecentByPatient(patientId: Long, limit: Int): Future[Seq[Consultation]] = {
    db.run(ConsultationTable.table.filter(_.patientId === patientId)
      .sortBy(_.dateRealisation.desc)
      .take(limit).result)
  }

  def searchByDiagnostic(keyword: String): Future[Seq[Consultation]] = {
    db.run(ConsultationTable.table.filter(_.diagnostic like s"%$keyword%").result)
  }

  // Ajoutez ces méthodes à ConsultationService

  def createConsultationWithDossierUpdate(consultation: Consultation): Future[Long] = {
    val action = for {
      consultationId <- (ConsultationTable.table returning ConsultationTable.table.map(_.id)) += consultation
      dossier <- DossiersMedicaux.table.filter(_.patientId === consultation.patientId).result.headOption
      _ <- dossier match {
        case Some(d) =>
          val newTreatments = if (d.traitementsEnCours.nonEmpty)
            d.traitementsEnCours + "\n" + consultation.prescriptions
          else
            consultation.prescriptions

          val newNotes = if (d.notes.nonEmpty)
            d.notes + "\n" + consultation.notesInternes
          else
            consultation.notesInternes

          DossiersMedicaux.table.filter(_.id === d.id.get)
            .map(d => (d.traitementsEnCours, d.notes))
            .update((newTreatments, newNotes))
        case None => DBIO.successful(0)
      }
    } yield consultationId

    db.run(action.transactionally)
  }

  def updateConsultationWithDossier(consultationId: Long, updatedConsultation: Consultation): Future[Int] = {
    val action = for {
      originalConsultation <- ConsultationTable.table.filter(_.id === consultationId).result.headOption
      updateCount <- ConsultationTable.table.filter(_.id === consultationId)
        .update(updatedConsultation.copy(id = Some(consultationId)))
      _ <- originalConsultation match {
        case(Some(original)) if original.prescriptions != updatedConsultation.prescriptions =>
          DossiersMedicaux.table.filter(_.patientId === updatedConsultation.patientId)
            .map(_.traitementsEnCours)
            .update(updatedConsultation.prescriptions)
        case _ => DBIO.successful(0)
      }
    } yield updateCount

    db.run(action.transactionally)
  }
}