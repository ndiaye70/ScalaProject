package services

import models.{Utilisateur, Utilisateurs}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class UtilisateurService(db: Database)(implicit ec: ExecutionContext) {

  // Initialisation du service
  def init(): Unit = {
    val setup = DBIO.seq(
      Utilisateurs.table.schema.createIfNotExists
    )
    db.run(setup)
  }

  // Trouver un utilisateur par son email
  def trouverParEmail(email: String): Future[Option[Utilisateur]] = {
    val query = Utilisateurs.table.filter(_.email === email).result.headOption
    db.run(query)
  }

  // Ajouter un utilisateur
  def ajouterUtilisateur(utilisateur: Utilisateur): Future[Utilisateur] = {
    val utilisateurAction = (Utilisateurs.table returning Utilisateurs.table.map(_.id) into ((utilisateur, id) => utilisateur.copy(id = Some(id)))) += utilisateur
    db.run(utilisateurAction)
  }

  // Lister tous les utilisateurs
  def listerUtilisateurs(): Future[Seq[Utilisateur]] = {
    db.run(Utilisateurs.table.result)
  }

  // Modifier un utilisateur
  def modifierUtilisateur(email: String, utilisateur: Utilisateur): Future[Int] = {
    val query = Utilisateurs.table.filter(_.email === email).update(utilisateur)
    db.run(query)
  }

  // Supprimer un utilisateur par email
  def supprimerUtilisateur(email: String): Future[Int] = {
    val query = Utilisateurs.table.filter(_.email === email).delete
    db.run(query)
  }
}
