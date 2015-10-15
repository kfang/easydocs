package easydocs.routes

import easydocs.AppPackage
import easydocs.utils.CorsSupport
import CorsSupport._

class ApiRoutes(implicit App: AppPackage) extends ExtendedDirectives(App) {

  val routes = cors(pathPrefix("api"){
    new ApiSiteRoutes().routes ~
    new ApiEndpointRoutes().routes ~
    new ExportRoutes().routes
  })

}
