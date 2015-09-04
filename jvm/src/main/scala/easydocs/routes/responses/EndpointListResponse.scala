package easydocs.routes.responses

import easydocs.models.ESEndpoint
import spray.routing.Route
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

