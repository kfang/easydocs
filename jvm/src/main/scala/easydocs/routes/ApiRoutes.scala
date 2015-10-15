package easydocs.routes

import easydocs.{AppPackage, Services, CorsSupport}
import spray.routing.Directives
import CorsSupport._

class ApiRoutes(implicit App: AppPackage)
  extends JsonSupport
  with ApiEndpointRoutes
  with ExportRoutes
{
  this: Services with Directives =>

  private val apiSiteRoutes = new ApiSiteRoutes().routes

  val apiRoutes = cors(pathPrefix("api"){
    apiEndpointRoutes ~
    apiSiteRoutes ~
    exportRoutes
  })

}
