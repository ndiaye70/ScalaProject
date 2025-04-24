package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import utils.JwtUtils
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

// Modèles pour les requêtes JSON
case class GenerateTokenRequest(username: String)
case class VerifyTokenRequest(token: String)

// Formats JSON
object TokenJsonProtocol {
  implicit val generateTokenFormat = jsonFormat1(GenerateTokenRequest)
  implicit val verifyTokenFormat = jsonFormat1(VerifyTokenRequest)
}

class AuthRoutes {

  import TokenJsonProtocol._

  // Route pour générer un token JWT
  def generateTokenRoute: Route = path("generateToken") {
    post {
      entity(as[GenerateTokenRequest]) { req =>
        val token = JwtUtils.generateToken(req.username)
        complete(token)
      }
    }
  }

  // Route pour vérifier un token JWT
  def verifyTokenRoute: Route = path("verifyToken") {
    post {
      entity(as[VerifyTokenRequest]) { req =>
        try {
          val username = JwtUtils.decodeToken(req.token)
          complete(s"Token valid. User: $username")
        } catch {
          case e: Exception => complete(s"Invalid token: ${e.getMessage}")
        }
      }
    }
  }

  // Routes combinées
  def routes: Route = generateTokenRoute ~ verifyTokenRoute
}
