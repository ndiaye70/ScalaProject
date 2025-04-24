// models/Paiement.scala
package models

import slick.jdbc.PostgresProfile.api._
import java.time.LocalDateTime

case class Paiement(
                     id: Option[Long],
                     patientId: Long,
                     montant: BigDecimal,
                     mode: String, // "Espèce", "Chèque", "Assurance"
                     datePaiement: LocalDateTime,
                     reference: Option[String] // n° chèque ou n° assurance si dispo
                   )

class PaiementTable(tag: Tag) extends Table[Paiement](tag, "paiements") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def patientId = column[Long]("patient_id")
  def montant = column[BigDecimal]("montant")
  def mode = column[String]("mode")
  def datePaiement = column[LocalDateTime]("date_paiement")
  def reference = column[Option[String]]("reference")

  def * = (id.?, patientId, montant, mode, datePaiement, reference) <> (Paiement.tupled, Paiement.unapply)
}

object PaiementTable {
  val table = TableQuery[PaiementTable]
}
