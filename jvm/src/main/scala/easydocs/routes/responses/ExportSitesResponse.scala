package easydocs.routes.responses

import com.sksamuel.elastic4s.{SearchType, ElasticClient}
import easydocs.models.ESSite
import spray.routing.Route
import com.sksamuel.elastic4s.ElasticDsl._
import scala.concurrent.{Future, ExecutionContext}
import spray.json._

case class ExportSitesResponse(client: ElasticClient)(implicit ec: ExecutionContext) extends Response {

  private def getSites: Future[List[ESSite]] = {

    def _getSites(scrollID: String): Future[List[ESSite]] = {
      client.searchScroll(scrollID, "1m").flatMap(sr => {
        val sites = sr.getHits.hits().toList.map(_.sourceAsString().parseJson.convertTo[ESSite])
        if(sites.isEmpty) Future.successful(sites) else _getSites(sr.getScrollId).map(_ ++ sites)
      })
    }

    client.execute(search.in(ESSite.ALIAS_TYPE).searchType(SearchType.Scan).scroll("1m"))
      .flatMap(sr => _getSites(sr.getScrollId) )
  }

  override def finish(implicit ec: ExecutionContext): Route = {
    getSites.map(sites => sites.map(_.toJson.compactPrint).mkString("\n"))
  }
}
