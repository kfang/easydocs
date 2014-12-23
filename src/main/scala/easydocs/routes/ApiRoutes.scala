package easydocs.routes

import easydocs.Services
import spray.routing.Directives

trait ApiRoutes
  extends JsonSupport
  with ApiEndpointRoutes
  with ApiTopicRoutes
  with ApiSiteRoutes
{
  this: Services with Directives =>


  val apiRoutes = pathPrefix("api"){
    apiEndpointRoutes ~
    apiSiteRoutes ~
    apiTopicRoutes
  }
}
