package models

import slick.jdbc.PostgresProfile.api._
import java.time.LocalDate

// Version simplifiée de Patient sans la séquence de dossiers (pour le mapping Slick)
case class Patient(
                    id: Option[Long],
                    nom: String,
                    prenom: String,
                    dateNaissance: LocalDate,
                    sexe: String,
                    telephone: String,
                    adresse: String,
                    numeroAssurance: Option[String],
                    codePatient: String
                  )

// Version enrichie pour les opérations de lecture (avec les dossiers)
case class PatientWithDossiers(
                                patient: Patient,
                                dossiers: Seq[DossierMedical]
                              )

class Patients(tag: Tag) extends Table[Patient](tag, "patients") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private def nom = column[String]("nom")
  private def prenom = column[String]("prenom")
  private def dateNaissance = column[LocalDate]("date_naissance")
  private def sexe = column[String]("sexe")
  private def telephone = column[String]("telephone")
  private def adresse = column[String]("adresse")
  private def numeroAssurance = column[Option[String]]("numero_assurance")
  private def codePatient = column[String]("code_patient")

  // Mapping sans le champ 'dossiers'
  def * = (id.?, nom, prenom, dateNaissance, sexe, telephone, adresse, numeroAssurance, codePatient) <>
    ((Patient.apply _).tupled, Patient.unapply)
}

object Patients {
  val table = TableQuery[Patients]

  // Méthode pour convertir un Patient + ses dossiers en PatientWithDossiers
  def toPatientWithDossiers(patient: Patient, dossiers: Seq[DossierMedical]): PatientWithDossiers = {
    PatientWithDossiers(patient, dossiers)
  }
}