package com.github.kfang.easydocs.routes.responses

import akka.http.scaladsl.server.Route
import com.github.kfang.easydocs.models.ESSite
import spray.json._

import scala.concurrent.ExecutionContext

case class SiteCreateResponse(
  site: ESSite
) extends Response {

  override def finish(implicit ec: ExecutionContext): Route = {
    JsObject(
      "sites" -> JsArray(site.toJson)
    )
  }

}
