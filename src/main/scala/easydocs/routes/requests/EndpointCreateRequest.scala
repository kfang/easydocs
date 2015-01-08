package easydocs.routes.requests

import java.util.UUID

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.ObjectSource
import easydocs.ERR
import easydocs.models.{ESSite, ESEndpoint}
import easydocs.routes.responses.{EndpointListResponse, BooleanResponse}
import spray.json.DefaultJsonProtocol._

import scala.concurrent.{Future, ExecutionContext}

case class EndpointCreateRequest(
  site: String,

  topic: String,
  subTopic: String,
  notes: Option[String],

  route: String,
  method: String,
  contentType: String,

  authentication: Option[String],
  parameters: Option[String]
)

object EndpointCreateRequest {

  implicit val endpointCreateJS = jsonFormat9(EndpointCreateRequest.apply)

  implicit class EndpointCreate(request: EndpointCreateRequest)(implicit ec: ExecutionContext, client: ElasticClient){

    private def checkSite: Future[Option[(String, String)]] = {
      ESSite.fromId(UUID.fromString(request.site)).map(_ => None)
        .recover({case e: Exception => Some(ERR.SITE_MISSING)})
    }

    private def checkTopicAndSubTopic: Future[Option[(String, String)]] = {
      client.execute(count(ESEndpoint.ALIAS_TYPE).where(must(
        term("topic", request.topic),
        term("subTopic", request.subTopic)
      ))).map(_.getCount > 0).map({
        case true  => Some(ERR.TOPIC_SUBTOPIC_EXISTS)
        case false => None
      })
    }

    private def checkRouteMethodType: Future[Option[(String, String)]] = {
      client.execute(count(ESEndpoint.ALIAS_TYPE).where(must(
        term("route", request.route),
        term("method", request.method),
        term("contentType", request.contentType)
      ))).map(_.getCount > 0).map({
        case true => Some(ERR.ROUTE_METHOD_TYPE_EXISTS)
        case false => None
      })
    }

    private def checkForBlanks: Future[Option[(String, String)]] = Future.successful {
      val noBlanks =
        request.topic != "" &&
        request.subTopic != "" &&
        request.route != "" &&
        request.method != "" &&
        request.contentType != ""
      if(noBlanks) None else Some(ERR.FIELDS_HAVE_BLANK)
    }

    private def generateEndpoint: Future[ESEndpoint] = (for {
      topicAndSubTopic <- checkTopicAndSubTopic
      routeMethodType  <- checkRouteMethodType
      forBlanks        <- checkForBlanks
      site             <- checkSite
    } yield {
      List(topicAndSubTopic, routeMethodType, forBlanks).flatten
    }).map({
      case Nil =>
        ESEndpoint(
          id = UUID.randomUUID().toString,
          site = request.site,
          topic = request.topic,
          subTopic = request.subTopic,
          notes = request.notes,
          route = request.route,
          method = request.method,
          contentType = request.contentType,
          authentication = request.authentication,
          parameters = request.parameters
        )
      case ers => throw ERR.badRequest(ers)
    })

    private def saveEndpoint(endpoint: ESEndpoint) = {
      val cmd = index.into(ESEndpoint.ALIAS_TYPE).doc(ObjectSource(endpoint)).id(endpoint.id)
      client.execute(cmd).map(_.isCreated)
    }

    def getResponse: Future[EndpointListResponse] = for {
      esEndpoint <- generateEndpoint
      saveResult <- saveEndpoint(esEndpoint)
    } yield {
      EndpointListResponse(List(esEndpoint))
    }
  }
}