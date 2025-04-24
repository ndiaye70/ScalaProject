// src/main/scala/models/Lit.scala
package models

import slick.jdbc.PostgresProfile.api._

case class Lit(
                id: Option[Long],
                numero: String,         // Numéro du lit (ex: "L1", "L2")
                chambreId: Long,        // Référence à la chambre
                statut: String          // "Libre", "Occupé", "Réservé", "En nettoyage"
              )

class LitTable(tag: Tag) extends Table[Lit](tag, "lits") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def numero = column[String]("numero")
  def chambreId = column[Long]("chambre_id")
  def statut = column[String]("statut")

  def chambreFk = foreignKey("fk_lit_chambre", chambreId, ChambreTable.table)(_.id, onDelete = ForeignKeyAction.Cascade)

  def * = (id.?, numero, chambreId, statut) <> (Lit.tupled, Lit.unapply)
}

object LitTable {
  val table = TableQuery[LitTable]
}
