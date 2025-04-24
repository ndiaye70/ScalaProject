package models

import slick.jdbc.PostgresProfile.api._
import java.time.LocalDateTime

case class Consultation(
                         id: Option[Long],
                         patientId: Long,          // Obligatoire
                         personnelId: Long,        // Obligatoire
                         dateRealisation: LocalDateTime,
                         symptomes: String,        // Max 1000 chars
                         diagnostic: String,       // Max 2000 chars
                         prescriptions: String,    // Traitements prescrits
                         notesInternes: String     // Réservé au personnel
                       )

class ConsultationTable(tag: Tag) extends Table[Consultation](tag, "consultations") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def patientId = column[Long]("patient_id")
  def personnelId = column[Long]("personnel_id")
  def dateRealisation = column[LocalDateTime]("date_realisation")
  def symptomes = column[String]("symptomes", O.Length(1000))
  def diagnostic = column[String]("diagnostic", O.Length(2000))
  def prescriptions = column[String]("prescriptions")
  def notesInternes = column[String]("notes_internes")

  // Clés étrangères seulement vers Patient/Personnel
  def patient = foreignKey("consult_patient_fk", patientId, Patients.table)(_.id)
  def personnel = foreignKey("consult_personnel_fk", personnelId, Personnels.table)(_.id)

  def * = (id.?, patientId, personnelId, dateRealisation, symptomes,
    diagnostic, prescriptions, notesInternes) <>
    ((Consultation.apply _).tupled, Consultation.unapply)
}

object ConsultationTable {
  val table = TableQuery[ConsultationTable]
}