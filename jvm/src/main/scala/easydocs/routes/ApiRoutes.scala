package easydocs.routes

import easydocs.{AppPackage, Services, CorsSupport}
import spray.routing.Directives
import CorsSupport._

class ApiRoutes(implicit App: AppPackage)
  extends JsonSupport
  with ExportRoutes
{
  this: Services with Directives =>


  val apiRoutes = cors(pathPrefix("api"){
    new ApiSiteRoutes().routes ~
    new ApiEndpointRoutes().routes ~
    exportRoutes
  })

}
