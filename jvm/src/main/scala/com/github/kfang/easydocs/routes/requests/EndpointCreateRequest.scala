package com.github.kfang.easydocs.routes.requests

import java.util.UUID

import com.github.kfang.easydocs.models.{ESEndpoint, ESSite}
import com.github.kfang.easydocs.routes.responses.EndpointListResponse
import com.github.kfang.easydocs.utils.ERR
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

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
      client.execute(search.in(ESEndpoint.ALIAS_TYPE).query(must(
        termQuery("site", request.site),
        termQuery("topic", request.topic),
        termQuery("subTopic", request.subTopic)
      )).size(0)).map(_.totalHits > 0).map({
        case true  => Some(ERR.TOPIC_SUBTOPIC_EXISTS)
        case false => None
      })
    }

    private def checkRouteMethodType: Future[Option[(String, String)]] = {
      client.execute(search.in(ESEndpoint.ALIAS_TYPE).query(must(
        termQuery("site", request.site),
        termQuery("route", request.route),
        termQuery("method", request.method),
        termQuery("contentType", request.contentType)
      ))).map(_.totalHits > 0).map({
        case true => Some(ERR.ROUTE_METHOD_TYPE_EXISTS)
        case false => None
      })
    }

    private def checkForBlanks: Future[Option[(String, String)]] = Future.successful {
      val noBlanks =
        request.site != "" &&
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
      val cmd = index.into(ESEndpoint.ALIAS_TYPE).source(endpoint.toJson).id(endpoint.id)
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