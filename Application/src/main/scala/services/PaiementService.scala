// services/PaiementService.scala
package services

import models.{Paiement, PaiementTable}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class PaiementService(db: Database)(implicit ec: ExecutionContext) {
  def init(): Future[Unit] = db.run(PaiementTable.table.schema.createIfNotExists)

  def create(paiement: Paiement): Future[Long] = {
    db.run((PaiementTable.table returning PaiementTable.table.map(_.id)) += paiement)
  }

  def readAll(): Future[Seq[Paiement]] = db.run(PaiementTable.table.result)

  def readById(id: Long): Future[Option[Paiement]] = {
    db.run(PaiementTable.table.filter(_.id === id).result.headOption)
  }

  def getByPatient(patientId: Long): Future[Seq[Paiement]] = {
    db.run(PaiementTable.table.filter(_.patientId === patientId).result)
  }

  def delete(id: Long): Future[Int] = {
    db.run(PaiementTable.table.filter(_.id === id).delete)
  }

  def update(id: Long, paiement: Paiement): Future[Int] = {
    val updatedPaiement = paiement.copy(id = Some(id))
    db.run(PaiementTable.table.filter(_.id === id).update(updatedPaiement))
  }

}
