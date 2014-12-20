package easydocs.routes.requests

import com.sksamuel.elastic4s.ElasticClient
import easydocs.models.ESEndpoint
import easydocs.routes.responses.BooleanResponse
import com.sksamuel.elastic4s.ElasticDsl._
import scala.concurrent.{Future, ExecutionContext}

case class EndpointDeleteRequest(
  endpoint: ESEndpoint
)

object EndpointDeleteRequest {
  implicit class EndpointDelete(request: EndpointDeleteRequest)(implicit ec: ExecutionContext, client: ElasticClient){
    def getResponse: Future[BooleanResponse] = {
      client.execute(
        delete.id(request.endpoint.id).from(ESEndpoint.ALIAS).types(ESEndpoint.TYPE)
      ).map(delRes => {
        BooleanResponse(b = delRes.isFound)
      })
    }
  }
}
