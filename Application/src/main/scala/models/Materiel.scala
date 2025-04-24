package models

import slick.jdbc.PostgresProfile.api._
import java.time.LocalDateTime

// Le modèle de Materiel
case class Materiel(
                     id: Option[Long],               // Identifiant unique du matériel
                     nom: String,                    // Nom du matériel (ex: "Scanner", "Lit médical")
                     description: Option[String],     // Description détaillée du matériel
                     typeMateriel: String,            // Type de matériel (ex: "Médical", "Technologique")
                     statut: String,                  // Statut du matériel (ex: "Disponible", "En réparation", "En maintenance")
                     dateAchat: LocalDateTime,        // Date d'achat
                     dateDerniereMaintenance: Option[LocalDateTime], // Dernière maintenance effectuée
                     fournisseur: String,             // Fournisseur du matériel
                     quantite: Int,                  // Quantité disponible
                     localisation: Option[String]     // Localisation dans l'hôpital (ex: "Bloc opératoire", "Service Cardiologie")
                   )

class MaterielTable(tag: Tag) extends Table[Materiel](tag, "materiels") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def nom = column[String]("nom")
  def description = column[Option[String]]("description")
  def typeMateriel = column[String]("type_materiel")
  def statut = column[String]("statut")
  def dateAchat = column[LocalDateTime]("date_achat")
  def dateDerniereMaintenance = column[Option[LocalDateTime]]("date_derniere_maintenance")
  def fournisseur = column[String]("fournisseur")
  def quantite = column[Int]("quantite")
  def localisation = column[Option[String]]("localisation")

  def * = (id.?, nom, description, typeMateriel, statut, dateAchat, dateDerniereMaintenance, fournisseur, quantite, localisation) <> ((Materiel.apply _).tupled, Materiel.unapply)
}

object MaterielTable {
  val table = TableQuery[MaterielTable]
}
