package services

import models.{Materiel, MaterielTable}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class MaterielService(db: Database)(implicit ec: ExecutionContext) {

  // Initialisation de la base de données (création des tables si elles n'existent pas)
  def init(): Future[Unit] = {
    db.run(MaterielTable.table.schema.createIfNotExists)
  }

  // CRUD de base
  def create(materiel: Materiel): Future[Long] = {
    db.run((MaterielTable.table returning MaterielTable.table.map(_.id)) += materiel)
  }

  def readAll(): Future[Seq[Materiel]] = {
    db.run(MaterielTable.table.result)
  }

  def readById(id: Long): Future[Option[Materiel]] = {
    db.run(MaterielTable.table.filter(_.id === id).result.headOption)
  }

  def update(id: Long, materiel: Materiel): Future[Int] = {
    db.run(MaterielTable.table.filter(_.id === id)
      .update(materiel.copy(id = Some(id))))
  }

  def delete(id: Long): Future[Int] = {
    db.run(MaterielTable.table.filter(_.id === id).delete)
  }
}
