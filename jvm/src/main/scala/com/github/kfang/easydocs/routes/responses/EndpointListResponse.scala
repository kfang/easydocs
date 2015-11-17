package com.github.kfang.easydocs.routes.responses

import akka.http.scaladsl.server.Route
import com.github.kfang.easydocs.models.ESEndpoint
import spray.json._
import scala.concurrent.ExecutionContext
import DefaultJsonProtocol._

case class EndpointListResponse(
  endpoints: List[ESEndpoint]
) extends Response {

  override def finish(implicit ec: ExecutionContext): Route = {
    JsObject("endpoints" -> endpoints.toJson)
  }

}

