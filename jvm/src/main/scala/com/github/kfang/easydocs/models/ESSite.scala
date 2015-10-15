package com.github.kfang.easydocs.models

import java.util.UUID

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, KeywordAnalyzer}
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import com.github.kfang.easydocs.utils.ERR
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.concurrent.{Future, ExecutionContext}

case class ESSite(
  id: String,
  name: String
)

object ESSite {

  val ALIAS = "easydocs-sites"
  val INDEX = "easydocs-sites-v1"
  val TYPE  = "site"

  val ALIAS_TYPE = ALIAS -> TYPE

  val MAPPING = TYPE.as(
    "id".typed(StringType).analyzer(KeywordAnalyzer),
    "name".typed(StringType).analyzer(KeywordAnalyzer)
  )

  implicit val esSiteJS = jsonFormat2(ESSite.apply)

  def fromId(id: UUID)(implicit ec: ExecutionContext, client: ElasticClient): Future[ESSite] = {
    client.execute(get.id(id.toString).from(ALIAS_TYPE)).map(getRes => {
      getRes.getSourceAsString.parseJson.convertTo[ESSite]
    }).recover({
      case e: Exception => throw ERR.notFound(ERR.SITE_MISSING)
    })
  }

  def fromName(name: String)(implicit ec: ExecutionContext, client: ElasticClient): Future[ESSite] = {
    client.execute(search.in(ALIAS_TYPE).query(termQuery("name", name)).limit(1)).map(sr => {
      sr.getHits.hits().toList.head.sourceAsString().parseJson.convertTo[ESSite]
    }).recover({
      case e: Exception => throw ERR.notFound(ERR.SITE_MISSING)
    })
  }

}


