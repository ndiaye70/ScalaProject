package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import services.MaterielService
import models.Materiel
import utils.JsonProtocols._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import directives.AuthentificationDirective

import scala.concurrent.ExecutionContext

class MaterielRoutes(materielService: MaterielService,authDirective: AuthentificationDirective)(implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("materiels") {
    pathEndOrSingleSlash {
      // Récupérer tous les matériels
      get {
        onSuccess(materielService.readAll()) { materiels =>
          complete((StatusCodes.OK, materiels))
        }
      } ~
        // Créer un nouveau matériel
        post {
          entity(as[Materiel]) { materiel =>
            onSuccess(materielService.create(materiel)) { id =>
              complete((StatusCodes.Created, s"Matériel créé avec ID: $id"))
            }
          }
        }
    } ~
      path(LongNumber) { id =>
        // Récupérer un matériel par ID
        get {
          onSuccess(materielService.readById(id)) {
            case Some(materiel) => complete(materiel)
            case None => complete(StatusCodes.NotFound)
          }
        } ~
          // Mettre à jour un matériel par ID
          put {
            entity(as[Materiel]) { materiel =>
              onSuccess(materielService.update(id, materiel)) {
                case 0 => complete((StatusCodes.NotFound, "Matériel non trouvé"))
                case _ => complete(s"Matériel avec ID $id mis à jour")
              }
            }
          } ~
          // Supprimer un matériel par ID
          delete {
            onSuccess(materielService.delete(id)) {
              case 0 => complete((StatusCodes.NotFound, "Matériel non trouvé"))
              case _ => complete(s"Matériel avec ID $id supprimé")
            }
          }
      }
  }
}
