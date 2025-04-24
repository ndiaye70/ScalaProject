package models

import slick.jdbc.PostgresProfile.api._

import java.time.{LocalDate, LocalDateTime}
case class RendezVous(
                       id: Option[Long],
                       patientId: Long,
                       personnelId: Long,
                       dateHeure: LocalDateTime,
                       motif: String,
                       statut: String  // "Planifié", "Terminé"
                     )

class RendezVousTable(tag: Tag) extends Table[RendezVous](tag, "rendez_vous") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def patientId = column[Long]("patient_id")
  def personnelId = column[Long]("personnel_id")
  def dateHeure = column[LocalDateTime]("date_heure")
  def motif = column[String]("motif")
  def statut = column[String]("statut")

  def patient = foreignKey("patient_fk", patientId, Patients.table)(_.id)
  def personnel = foreignKey("personnel_fk", personnelId, Personnels.table)(_.id)

  def * = (id.?, patientId, personnelId, dateHeure, motif, statut) <>
    ((RendezVous.apply _).tupled, RendezVous.unapply)


}

object RendezVousTable {
  val table = TableQuery[RendezVousTable]
}