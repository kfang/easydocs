package easydocs

import spray.http.MediaTypes
import spray.routing.SimpleRoutingApp
import scala.util.{Failure, Success}

object Main
  extends App
  with Services
  with SimpleRoutingApp
  with SearchRoutes
  with EndpointRoutes
{

  import system.dispatcher

  startServer("0.0.0.0", port = 8080)({
    (get & pathEndOrSingleSlash){
      respondWithMediaType(MediaTypes.`text/html`){
        complete {
          new templates.Home(esClient).render
        }
      }
    } ~
    Endpoint.routes(esClient) ~
    AddEndpoint.routes(esClient) ~
    deleteEndpointRoute ~
    updateEndpointRoute ~
    searchRoutes ~
    path(RestPath){file => {getFromResource(file.toString())}}
  }).onComplete({
    case Success(b) => println(s"Successfully bound to ${b.localAddress}")
    case Failure(e) => println(e.getMessage); system.shutdown()
  })

}
