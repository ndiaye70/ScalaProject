// src/main/scala/models/Hospitalisation.scala
package models

import slick.jdbc.PostgresProfile.api._
import java.time.LocalDateTime

case class Hospitalisation(
                            id: Option[Long],
                            patientId: Long,
                            litId: Long,
                            dateDebut: LocalDateTime,
                            dateFin: Option[LocalDateTime],
                            motif: Option[String],
                            personnelResponsableId: Option[Long], // médecin ou infirmier responsable
                            notes: Option[String]
                          )

class HospitalisationTable(tag: Tag) extends Table[Hospitalisation](tag, "hospitalisations") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def patientId = column[Long]("patient_id")
  def litId = column[Long]("lit_id")
  def dateDebut = column[LocalDateTime]("date_debut")
  def dateFin = column[Option[LocalDateTime]]("date_fin")
  def motif = column[Option[String]]("motif")
  def personnelResponsableId = column[Option[Long]]("personnel_responsable_id")
  def notes = column[Option[String]]("notes")

  def * = (id.?, patientId, litId, dateDebut, dateFin, motif, personnelResponsableId, notes) <>
    ((Hospitalisation.apply _).tupled, Hospitalisation.unapply)

  // Clés étrangères (facultatif selon niveau d'intégration)
  def fkPatient = foreignKey("fk_hospitalisation_patient", patientId, Patients.table)(_.id)
  def fkLit = foreignKey("fk_hospitalisation_lit", litId, LitTable.table)(_.id)
  def fkPersonnel = foreignKey("fk_hospitalisation_personnel", personnelResponsableId, Personnels.table)(_.id.?)
}

object HospitalisationTable {
  val table = TableQuery[HospitalisationTable]
}
