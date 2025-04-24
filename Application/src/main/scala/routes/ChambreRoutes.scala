// src/main/scala/routes/ChambreRoutes.scala
package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import services.ChambreService
import models.Chambre
import utils.JsonProtocols._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import directives.AuthentificationDirective

import scala.concurrent.ExecutionContext

class ChambreRoutes(chambreService: ChambreService,authDirective: AuthentificationDirective)(implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("chambres") {
    pathEndOrSingleSlash {
      get {
        onSuccess(chambreService.readAll()) { chambres =>
          complete((StatusCodes.OK, chambres))
        }
      } ~
        post {
          entity(as[Chambre]) { chambre =>
            onSuccess(chambreService.create(chambre)) { id =>
              complete((StatusCodes.Created, s"Chambre créée avec ID: $id"))
            }
          }
        }
    } ~
      path(LongNumber) { id =>
        get {
          onSuccess(chambreService.readById(id)) {
            case Some(chambre) => complete(chambre)
            case None => complete(StatusCodes.NotFound)
          }
        } ~
          put {
            entity(as[Chambre]) { chambre =>
              onSuccess(chambreService.update(id, chambre)) {
                case 0 => complete((StatusCodes.NotFound, "Chambre non trouvée"))
                case _ => complete(s"Chambre avec ID $id mise à jour")
              }
            }
          } ~
          delete {
            onSuccess(chambreService.delete(id)) {
              case 0 => complete((StatusCodes.NotFound, "Chambre non trouvée"))
              case _ => complete(s"Chambre avec ID $id supprimée")
            }
          }
      } ~
      path("search") {
        parameter("numero") { numero =>
          onSuccess(chambreService.searchByNumero(numero)) {
            case Some(chambre) => complete((StatusCodes.OK, chambre))
            case None => complete(StatusCodes.NotFound)
          }
        }
      } ~
      path("statut" / Segment) { statut =>
        get {
          onSuccess(chambreService.getByStatut(statut)) { chambres =>
            complete((StatusCodes.OK, chambres))
          }
        }
      } ~
      path("localisation") {
        parameters("bloc") { bloc =>
          onSuccess(chambreService.getByBlocEtage(bloc)) { chambres =>
            complete((StatusCodes.OK, chambres))
          }
        }
      }
  }
}
