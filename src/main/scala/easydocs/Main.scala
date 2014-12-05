package easydocs

import easydocs.routes.{ApiRoutes, WebRoutes}
import spray.routing.SimpleRoutingApp
import scala.util.{Failure, Success}

object Main
  extends App
  with Services
  with SimpleRoutingApp
  with WebRoutes
  with ApiRoutes
{

  import system.dispatcher

  startServer("0.0.0.0", port = 8080)({
    webRoutes ~  //=> ----  /web
    apiRoutes ~  //=> ----  /api
    path(RestPath){file => {getFromResource(file.toString())}}
  }).onComplete({
    case Success(b) => println(s"Successfully bound to ${b.localAddress}")
    case Failure(e) => println(e.getMessage); system.shutdown()
  })

}
