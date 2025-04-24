package directives

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import models.Utilisateur
import services.AuthService

import scala.concurrent.ExecutionContext

class AuthentificationDirective(authService: AuthService)(implicit ec: ExecutionContext) {

  def authentifier: Directive1[String] = {
    headerValueByName("Authorization").flatMap { authHeader =>
      val token = authHeader.replace("Bearer ", "")
      onSuccess(authService.validerToken(token)).flatMap {
        case Some(_) => provide(token)
        case None => reject(AuthorizationFailedRejection)
      }
    }
  }

  def authentifierAvecUtilisateur: Directive1[(String, Utilisateur)] = {
    headerValueByName("Authorization").flatMap { authHeader =>
      val token = authHeader.replace("Bearer ", "")
      onSuccess(authService.validerToken(token)).flatMap {
        case Some(user) => provide((token, user))
        case None => reject(AuthorizationFailedRejection)
      }
    }
  }
}