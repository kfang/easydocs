package easydocs.routes

import easydocs.Services
import easydocs.CorsSupport
import spray.routing.Directives

trait ApiRoutes
  extends JsonSupport
  with ApiEndpointRoutes
  with ApiSiteRoutes
  with ExportRoutes
  with CorsSupport
{
  this: Services with Directives =>


  val apiRoutes = cors(pathPrefix("api"){
    apiEndpointRoutes ~
    apiSiteRoutes ~
    exportRoutes
  })

}
