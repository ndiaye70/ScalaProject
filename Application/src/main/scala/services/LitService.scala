// src/main/scala/services/LitService.scala
package services


import models.{Lit, LitTable}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class LitService(db: Database,chambreService: ChambreService)(implicit ec: ExecutionContext) {

  def init(): Future[Unit] = {
    db.run(LitTable.table.schema.createIfNotExists)
  }



  def readAll(): Future[Seq[Lit]] = {
    db.run(LitTable.table.result)
  }

  def readById(id: Long): Future[Option[Lit]] = {
    db.run(LitTable.table.filter(_.id === id).result.headOption)
  }

  def update(id: Long, lit: Lit): Future[Int] = {
    val action = for {
      updateCount <- db.run(LitTable.table.filter(_.id === id).update(lit.copy(id = Some(id))))
      _ <- chambreService.updateChambreStatutSelonLits(lit.chambreId)
    } yield updateCount

    action
  }

  def delete(id: Long): Future[Int] = {
    val chambreIdAction = db.run(LitTable.table.filter(_.id === id).map(_.chambreId).result.headOption)

    val action = for {
      chambreIdOpt <- chambreIdAction
      deleteCount <- db.run(LitTable.table.filter(_.id === id).delete)
      _ <- chambreIdOpt match {
        case Some(chambreId) => chambreService.updateChambreStatutSelonLits(chambreId)
        case None => Future.successful(())
      }
    } yield deleteCount

    action
  }


  def getByChambre(chambreId: Long): Future[Seq[Lit]] = {
    db.run(LitTable.table.filter(_.chambreId === chambreId).result)
  }

  def getByStatut(statut: String): Future[Seq[Lit]] = {
    db.run(LitTable.table.filter(_.statut.toLowerCase === statut.toLowerCase).result)
  }

  def searchByNumero(numero: String): Future[Seq[Lit]] = {
    db.run(LitTable.table.filter(_.numero.toLowerCase like s"%${numero.toLowerCase}%").result)
  }

  def create(lit: Lit): Future[Long] = {
    val insertAction = (LitTable.table returning LitTable.table.map(_.id)) += lit
    val result = for {
      id <- db.run(insertAction)
      _ <- chambreService.updateChambreStatutSelonLits(lit.chambreId) // Appel ici
    } yield id

    result
  }

}
