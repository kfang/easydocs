package com.github.kfang.easydocs.routes.responses

import com.github.kfang.easydocs.models.ESSite
import spray.json._
import spray.routing.Route

import scala.concurrent.ExecutionContext

case class SiteReadResponse(
                           site: ESSite
                             ) extends Response {

  override def finish(implicit ec: ExecutionContext): Route = {
    JsObject(
      "sites" -> JsArray(site.toJson)
    )
  }

}
