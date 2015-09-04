package easydocs.routes

import easydocs.Services
import easydocs.routes.responses.{ExportEndpointsResponse, ExportSitesResponse}
import spray.routing.Directives

trait ExportRoutes {
  this: Services with Directives with JsonSupport =>
  import system.dispatcher
  
  
  private val exportSites = (get & path("sites")){ ExportSitesResponse(elasticClient) }
  private val exportEndpoints = (get & path("endpoints")){ ExportEndpointsResponse(elasticClient) }

  val exportRoutes = pathPrefix("export"){
    exportSites ~
    exportEndpoints
  }

}
