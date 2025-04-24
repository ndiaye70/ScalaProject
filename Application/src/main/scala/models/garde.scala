package models

import java.time.{LocalDate, LocalTime}
import slick.jdbc.PostgresProfile.api._

case class Garde(
                  id: Option[Long],
                  personnelId: Long,
                  dateDebutPeriode: LocalDate,
                  dateFinPeriode: LocalDate,
                  heureDebut: LocalTime,
                  heureFin: LocalTime,
                )

class Gardes(tag: Tag) extends Table[Garde](tag, "gardes") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def personnelId = column[Long]("personnel_id")
  def dateDebutPeriode = column[LocalDate]("date_debut_periode")
  def dateFinPeriode = column[LocalDate]("date_fin_periode")
  def heureDebut = column[LocalTime]("heure_debut")
  def heureFin = column[LocalTime]("heure_fin")

  def personnel = foreignKey("personnel_fk", personnelId, Personnels.table)(_.id)

  def * = (id.?, personnelId, dateDebutPeriode, dateFinPeriode, heureDebut, heureFin) <>
    ((Garde.apply _).tupled, Garde.unapply)
}

object Gardes {
  val table = TableQuery[Gardes]
}
