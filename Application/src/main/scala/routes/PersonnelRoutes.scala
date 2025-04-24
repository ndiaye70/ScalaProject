package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import services.{PersonnelService, UtilisateurService}
import utils.JsonProtocols._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import directives.AuthentificationDirective
import models.{Personnel, PersonnelAvecUtilisateur,Utilisateurs}
import spray.json._
import utils.JsonProtocols._



import scala.concurrent.ExecutionContext

class PersonnelRoutes(utilisateurService: UtilisateurService,personnelService: PersonnelService,authDirective: AuthentificationDirective)(implicit ec: ExecutionContext) {

  val routes: Route = pathPrefix("personnel") {
    pathEndOrSingleSlash {
      get {
        parameters("type".?, "specialite".?) { (typeParam, specialiteParam) =>
          val result = (typeParam, specialiteParam) match {
            case (Some(t), _) => personnelService.findByType(t)
            case (_, Some(s)) => personnelService.findBySpecialite(s)
            case _ => personnelService.readAll()
          }

          onSuccess(result) { personnel =>
            complete(StatusCodes.OK, personnel)
          }
        }
      } ~
        post {
          entity(as[Personnel]) { personnel =>
            onSuccess(personnelService.create(personnel)) { id =>
              complete(StatusCodes.Created, JsObject("id" -> JsNumber(id)))
            }
          }
        }

    } ~
      path(LongNumber) { id =>
        get {
          onSuccess(personnelService.readById(id)) {
            case Some(personnel) => complete(StatusCodes.OK, personnel)
            case None => complete(StatusCodes.NotFound, "Personnel non trouvé")
          }
        } ~
          put {
            entity(as[Personnel]) { personnel =>
              onSuccess(personnelService.update(id, personnel)) {
                case 0 => complete(StatusCodes.NotFound, "Personnel non trouvé")
                case _ => complete(StatusCodes.OK, s"Personnel $id mis à jour")
              }
            }
          } ~
          delete {
            onSuccess(personnelService.delete(id)) {
              case 0 => complete(StatusCodes.NotFound, "Personnel non trouvé")
              case _ => complete(StatusCodes.OK, s"Personnel $id supprimé")
            }
          }
      } ~
      pathPrefix("medecins") {
        get {
          onSuccess(personnelService.findByType("Medecin")) { medecins =>
            complete(StatusCodes.OK, medecins)
          }
        }
      } ~
      pathPrefix("infirmiers") {
        get {
          onSuccess(personnelService.findByType("Infirmier")) { infirmiers =>
            complete(StatusCodes.OK, infirmiers)
          }
        }
      } ~
      path("avec-utilisateur") {
        post {
          entity(as[PersonnelAvecUtilisateur]) { pwu =>
            println(s"[DEBUG] Reçu PersonnelAvecUtilisateur: $pwu")

            val futurCreation = for {
              personnelId <- {
                println(s"[DEBUG] Création du personnel: ${pwu.personnel}")
                personnelService.create(pwu.personnel)
              }
              utilisateurCree <- {
                println(s"[DEBUG] Ajout de l'utilisateur: ${pwu.utilisateur}")
                utilisateurService.ajouterUtilisateur(pwu.utilisateur)
              }
            } yield {
              println(s"[DEBUG] Résultat - personnelId: $personnelId, utilisateurId: ${utilisateurCree.id}")
              (personnelId, utilisateurCree.id.getOrElse(0L))
            }

            onComplete(futurCreation) {
              case scala.util.Success((personnelId, utilisateurId)) =>
                println(s"[DEBUG] Succès - Créés: personnelId = $personnelId, utilisateurId = $utilisateurId")
                complete(StatusCodes.Created, JsObject(
                  "personnelId" -> JsNumber(personnelId),
                  "utilisateurId" -> JsNumber(utilisateurId)
                ))

              case scala.util.Failure(ex) =>
                println(s"[ERROR] Une erreur est survenue: ${ex.getMessage}")
                ex.printStackTrace()
                complete(StatusCodes.InternalServerError, JsObject(
                  "error" -> JsString("Échec de la création du personnel et de l'utilisateur"),
                  "details" -> JsString(ex.getMessage)
                ))
            }
          }
        }
      }
  }
  }
