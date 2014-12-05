package easydocs.routes

import easydocs.Services
import spray.routing.{Route, Directives}

/**
 * Routes generating the "pretty stuff"; basically kinda like the views
 */
trait WebRoutes
  extends WebHomeRoutes
  with WebEndpointRoutes
{
  this: Services with Directives =>

  val webRoutes: Route = pathPrefix("web"){
    homeRoutes ~          //=> ----  /web
    webEndpointRoutes     //=> ----  /web/endpoints
  }

}
