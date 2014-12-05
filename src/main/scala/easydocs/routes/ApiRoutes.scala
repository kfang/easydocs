package easydocs.routes

import easydocs.Services
import spray.routing.Directives

trait ApiRoutes extends ApiEndpointRoutes {
  this: Services with Directives =>


  val apiRoutes = pathPrefix("api"){
    apiEndpointRoutes
  }
}
