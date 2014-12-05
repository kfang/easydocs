package easydocs.routes

import easydocs.Services
import spray.http.MediaTypes
import spray.routing.{Route, Directives}
import easydocs.templates

trait WebHomeRoutes {
  this: Services with Directives =>

  import system.dispatcher

  val homeRoutes: Route =
    (get & pathEndOrSingleSlash){
      respondWithMediaType(MediaTypes.`text/html`){
        complete {
          new templates.Home().render
        }
      }
    }

}
