// src/main/scala/models/Personnel.scala
package models


import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime
import scala.concurrent.Future

case class Personnel(
                      id: Option[Long],
                      nom: String,
                      prenom: String,
                      specialite: String,
                      typePersonnel: String,
                      telephone: String,
                      horaires: String
                    )

class Personnels(tag: Tag) extends Table[Personnel](tag, "personnels") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def nom = column[String]("nom")
  def prenom = column[String]("prenom")
  def specialite = column[String]("specialite")
  def typePersonnel = column[String]("type_personnel")
  def telephone = column[String]("telephone")
  def horaires = column[String]("horaires")

  def * = (id.?, nom, prenom, specialite, typePersonnel, telephone,horaires) <>
    ((Personnel.apply _).tupled, Personnel.unapply)


}

// Objet compagnon avec la TableQuery
object Personnels {
  val table = TableQuery[Personnels]
}
