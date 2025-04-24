package models

import slick.jdbc.PostgresProfile.api._
import java.time.LocalDateTime

case class Chambre(
                    id: Option[Long],
                    numero: String,                // Numéro unique de la chambre (ex: "A101")
                    bloc: String,                  // Bloc/Bâtiment (ex: "A", "B", "Cardio")
                    statut: String,                // "Libre", "Occupée",
                    dateDerniereMaintenance: Option[LocalDateTime],  // Dernière maintenance
                    capacite: Int,                 // Nombre de lits maximum
                    equipements: Option[String]    // Équipements spéciaux (JSON ou texte libre)
                  )

class ChambreTable(tag: Tag) extends Table[Chambre](tag, "chambres") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def numero = column[String]("numero", O.Unique)
  def bloc = column[String]("bloc")
  def statut = column[String]("statut")
  def dateDerniereMaintenance = column[Option[LocalDateTime]]("date_derniere_maintenance")
  def capacite = column[Int]("capacite")
  def equipements = column[Option[String]]("equipements")

  // Index pour les recherches courantes
  def idxNumero = index("idx_chambre_numero", numero, unique = true)
  def idxStatut = index("idx_chambre_statut", statut)
  def idxBlocEtage = index("idx_bloc_etage", (bloc))

  def * = (id.?, numero,  bloc, statut,
    dateDerniereMaintenance, capacite, equipements) <>
    ((Chambre.apply _).tupled, Chambre.unapply)
}

object ChambreTable {
  val table = TableQuery[ChambreTable]
}