package services

import models.{DossierMedical, DossiersMedicaux, Patient, Patients}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime

class DossierMedicalService(db: Database)(implicit ec: ExecutionContext) {

  def init(): Future[Unit] = db.run(DossiersMedicaux.table.schema.createIfNotExists)

  def create(dossier: DossierMedical): Future[Long] = {
    val dossierToInsert = dossier.copy(dateCreation = LocalDateTime.now())
    db.run((DossiersMedicaux.table returning DossiersMedicaux.table.map(_.id)) += dossierToInsert)
  }

  def findById(id: Long): Future[Option[DossierMedical]] = {
    db.run(DossiersMedicaux.table.filter(_.id === id).result.headOption)
  }

  def findByPatientId(patientId: Long): Future[Seq[DossierMedical]] = {
    db.run(DossiersMedicaux.table.filter(_.patientId === patientId).sortBy(_.dateCreation.desc).result)
  }

  def update(id: Long, dossier: DossierMedical): Future[Int] = {
    db.run(
      DossiersMedicaux.table.filter(_.id === id)
        .map(d => (d.antecedents, d.allergies, d.traitementsEnCours, d.notes))
        .update((dossier.antecedents, dossier.allergies, dossier.traitementsEnCours, dossier.notes))
    )
  }


  def delete(id: Long): Future[Int] = {
    db.run(DossiersMedicaux.table.filter(_.id === id).delete)
  }

  def patientExists(patientId: Long): Future[Boolean] = {
    db.run(Patients.table.filter(_.id === patientId).exists.result)
  }

  // Ajoutez ces méthodes à DossierMedicalService

  def updateTreatments(patientId: Long, newPrescriptions: String): Future[Int] = {
    db.run(
      DossiersMedicaux.table.filter(_.patientId === patientId)
        .map(_.traitementsEnCours)
        .update(newPrescriptions)
    )
  }

  def appendToMedicalNotes(patientId: Long, additionalNotes: String): Future[Int] = {
    val action = for {
      dossier <- DossiersMedicaux.table.filter(_.patientId === patientId).result.headOption
      updateCount <- dossier match {
        case Some(d) =>
          val updatedNotes = if (d.notes.nonEmpty)
            d.notes + "\n" + additionalNotes
          else
            additionalNotes
          DossiersMedicaux.table.filter(_.id === d.id.get)
            .map(_.notes)
            .update(updatedNotes)
        case None => DBIO.successful(0)
      }
    } yield updateCount

    db.run(action)
  }
}