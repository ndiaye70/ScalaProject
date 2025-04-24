package models

import slick.jdbc.PostgresProfile.api._

case class Utilisateur(
                        id: Option[Long],
                        idPersonnel: Long,      // Référence à Personnel
                        email: String,
                        motDePasse: String,
                        role: String           // "admin", "medecin", etc.
                      )

class Utilisateurs(tag: Tag) extends Table[Utilisateur](tag, "utilisateurs") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def idPersonnel = column[Long]("id_personnel")  // Lien vers Personnel
  def email = column[String]("email", O.Unique)
  def motDePasse = column[String]("mot_de_passe")
  def role = column[String]("role")

  // Définition de la relation entre Utilisateur et Personnel
  def personnelFK = foreignKey("personnel_fk", idPersonnel, TableQuery[Personnels])(_.id)

  def * = (id.?, idPersonnel, email, motDePasse, role) <> ((Utilisateur.apply _).tupled, Utilisateur.unapply)
}

object Utilisateurs {
  val table = TableQuery[Utilisateurs]
}
