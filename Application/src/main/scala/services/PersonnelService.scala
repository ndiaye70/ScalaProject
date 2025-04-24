// src/main/scala/services/PersonnelService.scala

package services
import models.{Utilisateur, Utilisateurs}

import models.{Gardes, Personnel, Personnels}
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class PersonnelService(db: Database)(implicit ec: ExecutionContext) {

  def init(): Future[Unit] = {
    db.run(Personnels.table.schema.createIfNotExists)
  }

  // CRUD de base
  def create(personnel: Personnel): Future[Long] =
    db.run((Personnels.table returning Personnels.table.map(_.id)) += personnel)

  def readAll(): Future[Seq[Personnel]] =
    db.run(Personnels.table.result)

  def readById(personnelId: Long): Future[Option[Personnel]] =
    db.run(Personnels.table.filter(_.id === personnelId).result.headOption)

  def update(personnelId: Long, personnel: Personnel): Future[Int] =
    db.run(Personnels.table.filter(_.id === personnelId).update(personnel.copy(id = Some(personnelId))))

  def delete(personnelId: Long): Future[Int] =
    db.run(Personnels.table.filter(_.id === personnelId).delete)

  // Recherche spécialisée
  def findByType(typePersonnel: String): Future[Seq[Personnel]] =
    db.run(Personnels.table.filter(_.typePersonnel === typePersonnel).result)

  def findBySpecialite(specialite: String): Future[Seq[Personnel]] =
    db.run(Personnels.table.filter(_.specialite === specialite).result)







  def createWithUser(personnel: Personnel, utilisateur: Utilisateur): Future[(Long, Long)] = {
    val insertAction = for {
      // Insère le personnel et récupère son ID
      personnelId <- (Personnels.table returning Personnels.table.map(_.id)) += personnel
      // Insère l'utilisateur en liant le personnelId
      utilisateurWithPersonnel = utilisateur.copy(idPersonnel = personnelId)
      utilisateurId <- (Utilisateurs.table returning Utilisateurs.table.map(_.id)) += utilisateurWithPersonnel
    } yield (personnelId, utilisateurId)

    db.run(insertAction.transactionally)
  }

}