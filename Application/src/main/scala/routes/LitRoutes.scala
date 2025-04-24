// src/main/scala/routes/LitRoutes.scala
package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import services.LitService
import models.Lit
import utils.JsonProtocols._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import directives.AuthentificationDirective

import scala.concurrent.ExecutionContext

class LitRoutes(litService: LitService,authDirective: AuthentificationDirective)(implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("lits") {
    pathEndOrSingleSlash {
      get {
        onSuccess(litService.readAll()) { lits =>
          complete((StatusCodes.OK, lits))
        }
      } ~
        post {
          entity(as[Lit]) { lit =>
            onSuccess(litService.create(lit)) { id =>
              complete((StatusCodes.Created, s"Lit créé avec ID: $id"))
            }
          }
        }
    } ~
      path(LongNumber) { id =>
        get {
          onSuccess(litService.readById(id)) {
            case Some(lit) => complete(lit)
            case None => complete(StatusCodes.NotFound)
          }
        } ~
          put {
            entity(as[Lit]) { lit =>
              onSuccess(litService.update(id, lit)) {
                case 0 => complete((StatusCodes.NotFound, "Lit non trouvé"))
                case _ => complete(s"Lit avec ID $id mis à jour")
              }
            }
          } ~
          delete {
            onSuccess(litService.delete(id)) {
              case 0 => complete((StatusCodes.NotFound, "Lit non trouvé"))
              case _ => complete(s"Lit avec ID $id supprimé")
            }
          }
      } ~
      path("chambre" / LongNumber) { chambreId =>
        get {
          onSuccess(litService.getByChambre(chambreId)) { lits =>
            complete((StatusCodes.OK, lits))
          }
        }
      } ~
      path("statut" / Segment) { statut =>
        get {
          onSuccess(litService.getByStatut(statut)) { lits =>
            complete((StatusCodes.OK, lits))
          }
        }
      } ~
      path("search") {
        get {
          parameter("numero") { numero =>
            onSuccess(litService.searchByNumero(numero)) { lits =>
              complete((StatusCodes.OK, lits))
            }
          }
        }
      }
  }
}
