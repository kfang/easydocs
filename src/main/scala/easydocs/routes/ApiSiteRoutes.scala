package easydocs.routes

import easydocs.Services
import easydocs.models.ESSite
import easydocs.routes.requests.SiteCreateRequest
import easydocs.routes.responses.{SiteReadResponse, SiteListResponse}
import spray.routing.{Route, Directives}

import scala.concurrent.Future

trait ApiSiteRoutes {
  this: Services with Directives with JsonSupport =>

  import system.dispatcher

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

  val apiSiteRoutes: Route = pathPrefix("sites"){
    createSiteRoute ~
    readSiteRoute ~
    listSitesRoute
  }

}
