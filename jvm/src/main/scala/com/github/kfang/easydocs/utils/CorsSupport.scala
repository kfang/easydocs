package com.github.kfang.easydocs.utils

import akka.http.scaladsl.model.{StatusCodes, HttpHeader, HttpMethods}
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`}
import akka.http.scaladsl.server.{Directives, Route}
import Directives._

object CorsSupport {

  private val allowedHeaders = Seq(
    "Accept",
    "Authorization",
    "Content-Type"
  )

  private val allowedMethods = Seq(
    HttpMethods.GET,
    HttpMethods.POST,
    HttpMethods.PUT,
    HttpMethods.DELETE,
    HttpMethods.OPTIONS
  )

  private def corsHeaders(clientOrigin: String) = List[HttpHeader](
    `Access-Control-Allow-Origin`(clientOrigin),
    `Access-Control-Allow-Headers`(allowedHeaders: _*),
    `Access-Control-Allow-Methods`(allowedMethods: _*),
    `Access-Control-Allow-Credentials`(allow = true)
  )

  private def respondWithCors(origin: String, route: Route): Route = {
    respondWithHeaders(corsHeaders(origin)) {
      //preflight request
      (options & complete(StatusCodes.NoContent)) ~
      route
    }
  }

  def cors(route: Route): Route = {
    optionalHeaderValueByName("Origin"){
      case Some(clientOrigin) => respondWithCors(clientOrigin, route)
      case None               => route
    }
  }

}
