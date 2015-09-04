package easydocs.routes.requests

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.source.ObjectSource
import easydocs.ERR
import easydocs.models.ESEndpoint
import easydocs.routes.responses.BooleanResponse
import spray.json.DefaultJsonProtocol._
import com.sksamuel.elastic4s.ElasticDsl._
import scala.concurrent.{Future, ExecutionContext}

case class EndpointUpdateRequest (
  topic: Option[String],
  subTopic: Option[String],
  notes: Option[String],

  route: Option[String],
  method: Option[String],
  contentType: Option[String],

  authentication: Option[String],
  parameters: Option[String]
)

object EndpointUpdateRequest {

  implicit val endpointUpdateJS = jsonFormat8(EndpointUpdateRequest.apply)

  implicit class EndpointUpdate(request: EndpointUpdateRequest)(implicit ec: ExecutionContext, client: ElasticClient){

    private def checkTopicSubTopic(endpoint: ESEndpoint): Future[Option[(String, String)]] = {
      val topic = request.topic.getOrElse(endpoint.topic)
      val subTopic = request.subTopic.getOrElse(endpoint.subTopic)

      client.execute(count(ESEndpoint.ALIAS_TYPE).where(must(
        not(ids(endpoint.id)),
        term("topic", topic),
        term("subTopic", subTopic)
      ))).map(_.getCount > 0).map({
        case true  => Some(ERR.TOPIC_SUBTOPIC_EXISTS)
        case false => None
      })
    }

    private def checkRouteMethodType(endpoint: ESEndpoint): Future[Option[(String, String)]] = {
      val route = request.route.getOrElse(endpoint.route)
      val method = request.route.getOrElse(endpoint.method)
      val cType = request.contentType.getOrElse(endpoint.contentType)

      client.execute(count(ESEndpoint.ALIAS_TYPE).where(must(
        not(ids(endpoint.id)),
        term("route", route),
        term("method", method),
        term("contentType", cType)
      ))).map(_.getCount > 0).map({
        case true => Some(ERR.ROUTE_METHOD_TYPE_EXISTS)
        case false => None
      })
    }

    private def checkForBlanks(endpoint: ESEndpoint): Future[Option[(String, String)]] = Future.successful {
      val noBlanks =
        request.topic.getOrElse(endpoint.topic) != "" &&
        request.subTopic.getOrElse(endpoint.subTopic) != "" &&
        request.route.getOrElse(endpoint.route) != "" &&
        request.method.getOrElse(endpoint.method) != "" &&
        request.contentType.getOrElse(endpoint.contentType) != ""
      if(noBlanks) None else Some(ERR.FIELDS_HAVE_BLANK)
    }

    private def generateUpdateIfValid(endpoint: ESEndpoint): Future[UpdateDefinition] = (for {
      topicSubTopic <- checkTopicSubTopic(endpoint)
      routeMethodType <- checkRouteMethodType(endpoint)
      forBlanks <- checkForBlanks(endpoint)
    } yield {
      List(topicSubTopic, routeMethodType, forBlanks).flatten
    }).map({
      case Nil =>
        update
          .id(endpoint.id)
          .in(ESEndpoint.ALIAS_TYPE)
          .doc(ObjectSource(Seq(
            request.topic.map(s => "topic" -> s),
            request.subTopic.map(s => "subTopic" -> s),
            request.notes.map(s => "notes" -> s),
            request.route.map(s => "route" -> s),
            request.method.map(s => "method" -> s),
            request.contentType.map(s => "contentType" -> s),
            request.authentication.map(s => "authentication" -> s),
            request.parameters.map(s => "parameters" -> s)
          ).flatten.toMap))
      case ers => throw ERR.badRequest(ers)
    })

    def getResponse(endpoint: ESEndpoint): Future[BooleanResponse] = for {
      updateDef <- generateUpdateIfValid(endpoint)
      updateRes <- client.execute(updateDef)
    } yield {
      BooleanResponse(b = true)
    }

  }
}


