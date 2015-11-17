package com.github.kfang.easydocs.models

import java.util.UUID

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, StandardAnalyzer, KeywordAnalyzer}
import com.sksamuel.elastic4s.mappings.FieldType.{MultiFieldType, StringType}
import com.github.kfang.easydocs.utils.ERR
import scala.concurrent.{ExecutionContext, Future}
import spray.json.DefaultJsonProtocol._
import spray.json._

case class ESEndpoint(
  id: String,
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

object ESEndpoint {

  val ALIAS = "easydocs-endpoints"
  val INDEX = "easydocs-endpoints-v1"
  val TYPE  = "endpoint"

  val ALIAS_TYPE = ALIAS -> TYPE

  val MAPPING = TYPE.as(
    "id".typed(StringType).analyzer(KeywordAnalyzer),
    "site".typed(StringType).analyzer(KeywordAnalyzer),

    "topic".typed(MultiFieldType).as(
      "topic".typed(StringType).analyzer(KeywordAnalyzer),
      "analyzed".typed(StringType).analyzer(StandardAnalyzer)
    ),
    "subTopic".typed(MultiFieldType).as(
      "subTopic".typed(StringType).analyzer(KeywordAnalyzer),
      "analyzed".typed(StringType).analyzer(StandardAnalyzer)
    ),
    "notes".typed(StringType).analyzer(StandardAnalyzer),

    "route".typed(StringType).analyzer(KeywordAnalyzer),
    "method".typed(StringType).analyzer(KeywordAnalyzer),
    "contentType".typed(StringType).analyzer(KeywordAnalyzer),

    "authentication".typed(StringType).analyzer(KeywordAnalyzer),
    "parameters".typed(StringType).analyzer(StandardAnalyzer)
  )

  implicit val jsf = jsonFormat10(ESEndpoint.apply)

  def fromId(id: UUID)(implicit ec: ExecutionContext, client: ElasticClient): Future[ESEndpoint] = {
    client.execute(get.id(id.toString).from(ALIAS_TYPE)).map(getRes => {
      getRes.getSourceAsString.parseJson.convertTo[ESEndpoint]
    }).recover({
      case e: Exception => throw ERR.notFound(ERR.ENDPOINT_MISSING)
    })
  }

}

