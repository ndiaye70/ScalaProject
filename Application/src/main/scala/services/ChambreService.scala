// src/main/scala/services/ChambreService.scala
package services

import models.{Chambre, ChambreTable, LitTable}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class ChambreService(db: Database)(implicit ec: ExecutionContext) {

  def init(): Future[Unit] = {
    db.run(ChambreTable.table.schema.createIfNotExists)
  }

  // CRUD de base
  def create(chambre: Chambre): Future[Long] =
    db.run((ChambreTable.table returning ChambreTable.table.map(_.id)) += chambre)

  def readAll(): Future[Seq[Chambre]] =
    db.run(ChambreTable.table.result)

  def readById(id: Long): Future[Option[Chambre]] =
    db.run(ChambreTable.table.filter(_.id === id).result.headOption)

  def update(id: Long, chambre: Chambre): Future[Int] =
    db.run(ChambreTable.table.filter(_.id === id).update(chambre.copy(id = Some(id))))

  def delete(id: Long): Future[Int] =
    db.run(ChambreTable.table.filter(_.id === id).delete)

  // Fonctions spécifiques
  def searchByNumero(numero: String): Future[Option[Chambre]] =
    db.run(ChambreTable.table.filter(_.numero === numero).result.headOption)

  def getByStatut(statut: String): Future[Seq[Chambre]] =
    db.run(ChambreTable.table.filter(_.statut === statut).result)

  def getByBlocEtage(bloc: String): Future[Seq[Chambre]] =
    db.run(ChambreTable.table.filter(c => c.bloc === bloc ).result)


  def updateChambreStatutSelonLits(chambreId: Long): Future[Int] = {
    val litsDansChambre = LitTable.table.filter(_.chambreId === chambreId)

    val isLibreQuery = litsDansChambre.filter(_.statut.toLowerCase === "libre").exists.result

    val updateStatutAction = for {
      libreExiste <- isLibreQuery
      newStatut = if (libreExiste) "Libre" else "Occupée"
      updateCount <- ChambreTable.table.filter(_.id === chambreId).map(_.statut).update(newStatut)
    } yield updateCount

    db.run(updateStatutAction)
  }

}
