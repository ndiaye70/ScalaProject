package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import services.UtilisateurService
import utils.JsonProtocols._ // Assurez-vous que vos formats JSON sont définis ici
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import models.Utilisateur
import scala.concurrent.ExecutionContext

class UtilisateurRoutes(utilisateurService: UtilisateurService)(implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("utilisateurs") {
    pathEndOrSingleSlash {
      // Route pour lister les utilisateurs
      get {
        onSuccess(utilisateurService.listerUtilisateurs()) { utilisateurs: Seq[Utilisateur] =>
          complete((StatusCodes.OK, utilisateurs))
        }
      } ~
        // Route pour ajouter un utilisateur
        post {
          entity(as[Utilisateur]) { utilisateur =>
            onSuccess(utilisateurService.ajouterUtilisateur(utilisateur)) { createdUtilisateur =>
              complete((StatusCodes.Created, createdUtilisateur))
            }
          }
        }
    } ~
      // Route pour gérer les utilisateurs par email
      path(Segment) { email =>  // Remplacer LongNumber par Segment pour l'email
        // Route pour obtenir un utilisateur par email
        get {
          onSuccess(utilisateurService.trouverParEmail(email)) {
            case Some(utilisateur) => complete(utilisateur)
            case None => complete(StatusCodes.NotFound)
          }
        } ~
          // Route pour modifier un utilisateur par email
          put {
            entity(as[Utilisateur]) { utilisateur =>
              onSuccess(utilisateurService.modifierUtilisateur(email, utilisateur)) {
                case 0 => complete((StatusCodes.NotFound, "Utilisateur non trouvé"))
                case _ => complete(s"Utilisateur avec email $email mis à jour")
              }
            }
          } ~
          // Route pour supprimer un utilisateur par email
          delete {
            onSuccess(utilisateurService.supprimerUtilisateur(email)) {
              case 0 => complete((StatusCodes.NotFound, "Utilisateur non trouvé"))
              case _ => complete(s"Utilisateur avec email $email supprimé")
            }
          }
      }
  }
}
