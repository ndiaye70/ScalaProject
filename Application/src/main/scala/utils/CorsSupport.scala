package utils

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}

object CorsSupport {

  private val corsResponseHeaders = List(
    `Access-Control-Allow-Origin`(HttpOrigin("http://localhost:4200")),
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Headers`("Authorization, Content-Type, X-Requested-With"),
    `Access-Control-Allow-Methods`(OPTIONS, GET, POST, PUT, DELETE, PATCH)
  )

  private def addAccessControlHeaders: Directive0 = {
    mapResponse { resp =>
      resp.withHeaders(corsResponseHeaders)
    }
  }


  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(StatusCodes.OK).withHeaders(corsResponseHeaders))
  }

  def corsHandler(r: Route): Route = addAccessControlHeaders {
    preflightRequestHandler ~ r
  }
}
