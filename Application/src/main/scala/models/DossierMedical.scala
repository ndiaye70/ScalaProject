package models

import slick.jdbc.PostgresProfile.api._
import java.time.LocalDateTime

case class DossierMedical(
                           id: Option[Long],
                           patientId: Long,
                           dateCreation: LocalDateTime,
                           antecedents: String,
                           allergies: String,
                           traitementsEnCours: String,
                           notes: String
                         )

class DossiersMedicaux(tag: Tag) extends Table[DossierMedical](tag, "dossiers_medicaux") {
  def id                 = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def patientId          = column[Long]("patient_id")
  def dateCreation       = column[LocalDateTime]("date_creation") // <-- plus "private"
  def antecedents        = column[String]("antecedents")
  def allergies          = column[String]("allergies")
  def traitementsEnCours = column[String]("traitements_en_cours")
  def notes              = column[String]("notes")

  def patient = foreignKey("patient_fk", patientId, Patients.table)(_.id, onDelete = ForeignKeyAction.Cascade)

  def * = (id.?, patientId, dateCreation, antecedents, allergies, traitementsEnCours, notes) <>
    ((DossierMedical.apply _).tupled, DossierMedical.unapply)
}

object DossiersMedicaux {
  val table = TableQuery[DossiersMedicaux]
}
