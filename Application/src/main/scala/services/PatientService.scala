// src/main/scala/services/PatientService.scala

package services

import models.{Patient, Patients, DossierMedical, DossiersMedicaux}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{Future, ExecutionContext}

class PatientService(db: Database)(implicit ec: ExecutionContext) {
  private val dossierService = new DossierMedicalService(db)

  def init(): Future[Unit] = {
    val createTables = DBIO.seq(
      Patients.table.schema.createIfNotExists,
      DossiersMedicaux.table.schema.createIfNotExists
    )
    db.run(createTables)
  }

  /*// CRUD de base pour Patient
  def create(patient: Patient): Future[Long] =
    db.run((Patients.table returning Patients.table.map(_.id)) += patient)*/

  def create(patient: Patient): Future[Long] = {
    val emptyDossier = DossierMedical(
      id = None,
      patientId = 0, // Temporaire, sera remplacé après
      dateCreation = java.time.LocalDateTime.now(),
      antecedents = "",
      allergies = "",
      traitementsEnCours = "",
      notes = ""
    )

    val action = for {
      patientId <- (Patients.table returning Patients.table.map(_.id)) += patient
      _ <- DossiersMedicaux.table += emptyDossier.copy(patientId = patientId)
    } yield patientId

    db.run(action.transactionally)
  }


  def update(patientId: Long, patient: Patient): Future[Int] = {
    val updatePatient = Patients.table.filter(_.id === patientId).update(patient.copy(id = Some(patientId)))

    val updateDossierNote = DossiersMedicaux.table
      .filter(_.patientId === patientId)
      .map(_.notes)
      .update(s"Patient mis à jour le ${java.time.LocalDateTime.now()}")

    db.run(for {
      p <- updatePatient
      _ <- updateDossierNote
    } yield p)
  }



  def readAll(): Future[Seq[Patient]] =
    db.run(Patients.table.result)

  def readById(patientId: Long): Future[Option[Patient]] =
    db.run(Patients.table.filter(_.id === patientId).result.headOption)

  /*def update(patientId: Long, patient: Patient): Future[Int] =
    db.run(Patients.table.filter(_.id === patientId).update(patient.copy(id = Some(patientId))))*/

  def delete(patientId: Long): Future[Int] =
    db.run(Patients.table.filter(_.id === patientId).delete)


  def createPatientWithDossier(patient: Patient, dossierInitial: Option[DossierMedical]): Future[(Long, Option[Long])] = {
    val action = for {
      patientId <- (Patients.table returning Patients.table.map(_.id)) += patient
      dossierId <- dossierInitial match {
        case Some(dossier) =>
          (DossiersMedicaux.table returning DossiersMedicaux.table.map(_.id))
            .+= (dossier.copy(patientId = patientId))
            .map(Some(_)) // Corrigé ici
        case None => DBIO.successful(None)
      }
    } yield (patientId, dossierId)

    db.run(action.transactionally)
  }

  def getPatientWithDossiers(patientId: Long): Future[Option[(Patient, Seq[DossierMedical])]] = {
    val query = for {
      patient <- Patients.table.filter(_.id === patientId).result.headOption
      dossiers <- DossiersMedicaux.table.filter(_.patientId === patientId).result
    } yield patient.map(_ -> dossiers)

    db.run(query)
  }

  def deletePatientWithDossiers(patientId: Long): Future[Int] = {
    // La suppression en cascade est gérée par la foreign key
    db.run(Patients.table.filter(_.id === patientId).delete)
  }
}