package com.github.kfang.easydocs.routes.responses

import akka.http.scaladsl.server.Route
import spray.json.{JsBoolean, JsObject}

import scala.concurrent.ExecutionContext

/**
 * Response Class for the super lazy, just returns a boolean
 * @param b Boolean
 */
case class BooleanResponse(
  b: Boolean
) extends Response {
  override def finish(implicit ec: ExecutionContext): Route = JsObject(
    "meta" -> JsObject("result" -> JsBoolean(x = b))
  )
}
