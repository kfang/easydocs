package easydocs.routes

import com.sksamuel.elastic4s.ElasticClient
import easydocs.AppPackage
import easydocs.models.ESSite
import easydocs.routes.requests.SiteCreateRequest
import easydocs.routes.responses.{TopicListResponse, SiteReadResponse, SiteListResponse}
import spray.routing.Route

import scala.concurrent.Future

class ApiSiteRoutes(implicit App: AppPackage) extends ExtendedDirectives(App){

  import App.system.dispatcher
  implicit val elasticClient: ElasticClient = App.services.elasticClient

  private val createSiteRoute: Route = (
    post &
    pathEnd &
    entity(as[SiteCreateRequest])
  ){request => {

    request.getResponse

  }}

  private val readSiteRoute: Route = (
    get &
    path(JavaUUID)
  ){id => {

    ESSite
      .fromId(id)
      .map(site => SiteReadResponse(site))

  }}

  private val listSitesRoute: Route = (
    get &
    pathEnd
  ){
    Future.successful(SiteListResponse(elasticClient))
  }

  private val listSiteTopicsRoute: Route = (
    get &
    path(JavaUUID / "topics")
  ){siteId => {

    ESSite.fromId(siteId).map(site => {
      TopicListResponse(site.id, elasticClient)
    })

  }}

  val routes: Route = pathPrefix("sites"){
    createSiteRoute ~
    readSiteRoute ~
    listSitesRoute ~
    listSiteTopicsRoute
  }

}
