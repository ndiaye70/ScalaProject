// src/main/scala/services/RendezVousService.scala
package services

import models.{RendezVous, RendezVousTable, Patients, Personnels}
import slick.jdbc.PostgresProfile.api._
import java.time.LocalDateTime
import scala.concurrent.{Future, ExecutionContext}

class RendezVousService(db: Database)(implicit ec: ExecutionContext) {

  def init(): Future[Unit] = {
    db.run(RendezVousTable.table.schema.createIfNotExists)
  }

  // CRUD de base
  def create(rendezVous: RendezVous): Future[Long] = {
    db.run((RendezVousTable.table returning RendezVousTable.table.map(_.id)) += rendezVous)
  }

  def readAll(): Future[Seq[RendezVous]] = {
    db.run(RendezVousTable.table.result)
  }

  def readById(id: Long): Future[Option[RendezVous]] = {
    db.run(RendezVousTable.table.filter(_.id === id).result.headOption)
  }

  def update(id: Long, rendezVous: RendezVous): Future[Int] = {
    db.run(RendezVousTable.table.filter(_.id === id)
      .update(rendezVous.copy(id = Some(id))))
  }

  def delete(id: Long): Future[Int] = {
    db.run(RendezVousTable.table.filter(_.id === id).delete)
  }

  // Fonctions spécifiques
  def getByPatient(patientId: Long): Future[Seq[RendezVous]] = {
    db.run(RendezVousTable.table.filter(_.patientId === patientId)
      .sortBy(_.dateHeure.asc).result)
  }

  def getByPersonnel(personnelId: Long): Future[Seq[RendezVous]] = {
    db.run(RendezVousTable.table.filter(_.personnelId === personnelId)
      .sortBy(_.dateHeure.asc).result)
  }

  def getByDateRange(debut: LocalDateTime, fin: LocalDateTime): Future[Seq[RendezVous]] = {
    db.run(RendezVousTable.table.filter(rv =>
      rv.dateHeure >= debut && rv.dateHeure <= fin).result)
  }

  def updateStatus(id: Long, statut: String): Future[Int] = {
    db.run(RendezVousTable.table.filter(_.id === id)
      .map(_.statut).update(statut))
  }

  def checkDisponibilite(personnelId: Long, dateHeure: LocalDateTime): Future[Boolean] = {
    db.run(RendezVousTable.table
      .filter(rv => rv.personnelId === personnelId && rv.dateHeure === dateHeure)
      .exists.result).map(!_)
  }
}