// src/main/scala/services/HospitalisationService.scala
package services

import models.{Hospitalisation, HospitalisationTable}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class HospitalisationService(db: Database)(implicit ec: ExecutionContext) {

  def init(): Future[Unit] = {
    db.run(HospitalisationTable.table.schema.createIfNotExists)
  }

  def create(hosp: Hospitalisation): Future[Long] = {
    db.run((HospitalisationTable.table returning HospitalisationTable.table.map(_.id)) += hosp)
  }

  def readAll(): Future[Seq[Hospitalisation]] = {
    db.run(HospitalisationTable.table.result)
  }

  def readById(id: Long): Future[Option[Hospitalisation]] = {
    db.run(HospitalisationTable.table.filter(_.id === id).result.headOption)
  }

  def update(id: Long, hosp: Hospitalisation): Future[Int] = {
    db.run(HospitalisationTable.table.filter(_.id === id).update(hosp.copy(id = Some(id))))
  }

  def delete(id: Long): Future[Int] = {
    db.run(HospitalisationTable.table.filter(_.id === id).delete)
  }

  def getByPatient(patientId: Long): Future[Seq[Hospitalisation]] = {
    db.run(HospitalisationTable.table.filter(_.patientId === patientId).result)
  }

  def getByLit(litId: Long): Future[Seq[Hospitalisation]] = {
    db.run(HospitalisationTable.table.filter(_.litId === litId).result)
  }

  def getByStatut(actif: Boolean): Future[Seq[Hospitalisation]] = {
    val query = if (actif)
      HospitalisationTable.table.filter(_.dateFin.isEmpty)
    else
      HospitalisationTable.table.filter(_.dateFin.isDefined)

    db.run(query.result)
  }
}
