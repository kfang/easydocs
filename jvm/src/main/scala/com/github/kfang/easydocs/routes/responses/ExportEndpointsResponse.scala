package com.github.kfang.easydocs.routes.responses

import akka.http.scaladsl.server.Route
import com.sksamuel.elastic4s.{SearchType, ElasticClient}
import com.github.kfang.easydocs.models.ESEndpoint
import com.sksamuel.elastic4s.ElasticDsl._
import scala.concurrent.{Future, ExecutionContext}
import spray.json._

case class ExportEndpointsResponse(client: ElasticClient)(implicit ec: ExecutionContext) extends Response {

  private def getEndpoints: Future[List[ESEndpoint]] = {

    def _getEndpoints(scrollID: String): Future[List[ESEndpoint]] = {
      client.execute(searchScroll(scrollID).keepAlive("1m")).flatMap(sr => {
        val sites = sr.getHits.hits().toList.map(_.sourceAsString().parseJson.convertTo[ESEndpoint])
        if(sites.isEmpty) Future.successful(sites) else _getEndpoints(sr.getScrollId).map(_ ++ sites)
      })
    }

    client.execute(search.in(ESEndpoint.ALIAS_TYPE).searchType(SearchType.Scan).scroll("1m"))
      .flatMap(sr => _getEndpoints(sr.getScrollId) )
  }

  override def finish(implicit ec: ExecutionContext): Route = {
    getEndpoints.map(sites => sites.map(_.toJson.compactPrint).mkString("\n"))
  }
}
