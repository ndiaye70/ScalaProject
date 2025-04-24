package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import services.AuthService
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.model.StatusCodes

case class LoginRequest(email: String, password: String)

class AuthRoute(authService: AuthService)(implicit ec: ExecutionContext) {

  import utils.JsonProtocols._

  implicit val loginRequestFormat = jsonFormat2(LoginRequest)

  val routes: Route = pathPrefix("auth") {
      path("login") {
        post {
          entity(as[LoginRequest]) { request =>
            onSuccess(authService.authentifier(request.email, request.password)) {
              case Some(token) => complete(token)
              case None        => complete(StatusCodes.Unauthorized, "Email ou mot de passe incorrect")
            }
          }
        }
      } ~
        path("validate") {
          post {
            headerValueByName("Authorization") { authHeader =>
              val token = authHeader.replace("Bearer ", "")
              onSuccess(authService.validerToken(token)) {
                case Some(user) => complete(user)
                case None => complete(StatusCodes.Unauthorized, "Token invalide ou expiré")
              }
            }
          }
        }

  }
}
