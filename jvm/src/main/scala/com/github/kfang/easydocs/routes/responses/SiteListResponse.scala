package com.github.kfang.easydocs.routes.responses

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.github.kfang.easydocs.models.ESSite
import spray.json._
import spray.routing.Route

import scala.concurrent.ExecutionContext

case class SiteListResponse(
  client: ElasticClient
) extends Response {

  override def finish(implicit ec: ExecutionContext): Route = {
    client.execute(search.in(ESSite.ALIAS_TYPE).query(matchall).limit(10000)).map(res => {
      val sites = JsArray(res.getHits.hits().toVector.map(_.sourceAsString().parseJson))
      JsObject(
        "sites" -> sites
      )
    })
  }

}
