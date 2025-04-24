package services

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import routes.AuthRoutes

import scala.concurrent.ExecutionContext

class HttpService(implicit system: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext) {

  private val authRoutes = new AuthRoutes()

  def startServer(): Unit = {
    val routes: Route = authRoutes.routes  // Combinaison des routes d'authentification

    Http().newServerAt("localhost", 8080).bind(routes)  // Démarre le serveur sur le port 8080
    println("Server started at http://localhost:8080")
  }
}
