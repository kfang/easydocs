package easydocs.routes.responses

import spray.json.{JsBoolean, JsObject}
import spray.routing.Route

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
