package easydocs.routes

import easydocs.Services
import easydocs.templates
import spray.http.MediaTypes
import spray.routing.{Route, Directives}

trait WebEndpointRoutes {
  this: Services with Directives =>

  import system.dispatcher

  val webEndpointRoutes: Route = (respondWithMediaType(MediaTypes.`text/html`) & pathPrefix("endpoints")){
    (get & path("add")){
      //get the create endpoint page
      complete {
        new templates.EndpointForm(None, "Create Endpoint").render
      }
    } ~
    (get & path("""(.*)""".r)){ endpointID: String =>
      //TODO: get the info page
      complete {
        esClient.getEndpoint(endpointID).flatMap(endpoint => {
          new templates.EndpointPage(endpoint).render
        })
      }
    } ~
    (get & path("""(.*)""".r / "update")){ endpointID: String =>
      //get the update endpoint page
      complete {
        esClient.getEndpoint(endpointID).flatMap(endpoint => {
          new templates.EndpointForm(Some(endpoint), "Update Endpoint").render
        })
      }
    }
  }

}
