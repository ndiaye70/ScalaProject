package services

import models.{Garde, Gardes}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}
import java.time.{LocalDate}

class GardeServices(db: Database)(implicit ec: ExecutionContext) {

  def planifierGarde(garde: Garde): Future[Long] =
    db.run((Gardes.table returning Gardes.table.map(_.id)) += garde)

  def getGardesParPersonnel(personnelId: Long): Future[Seq[Garde]] =
    db.run(Gardes.table.filter(_.personnelId === personnelId).result)

  def getGardesParPeriode(debut: LocalDate, fin: LocalDate): Future[Seq[Garde]] =
    db.run(Gardes.table.filter(g =>
      g.dateDebutPeriode <= fin && g.dateFinPeriode >= debut
    ).result)

  def supprimerGarde(id: Long): Future[Int] =
    db.run(Gardes.table.filter(_.id === id).delete)

  def modifierGarde(id: Long, garde: Garde): Future[Int] =
    db.run(Gardes.table.filter(_.id === id).update(garde.copy(id = Some(id))))

  def getGardesAujourdHui(): Future[Seq[Garde]] = {
    val aujourdHui = LocalDate.now
    db.run(Gardes.table.filter(g =>
      g.dateDebutPeriode <= aujourdHui && g.dateFinPeriode >= aujourdHui
    ).result)
  }

  def getGardeParId(id: Long): Future[Option[Garde]] = {
    db.run(Gardes.table.filter(_.id === id).result.headOption)
  }

  def init(): Future[Unit] = db.run(Gardes.table.schema.createIfNotExists)
}
