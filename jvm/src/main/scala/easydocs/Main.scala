package easydocs

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import scala.util.{Failure, Success}

object Main
  extends App
  with SimpleRoutingApp
{

  implicit val Config = AppConfig()
  implicit val system = ActorSystem("easydocs", Config.CONFIG)
  implicit val services = AppServices()

  import system.dispatcher

  startServer("0.0.0.0", port = 8080)({
//    apiRoutes ~  //=> ----  /api
    path(RestPath){file => {getFromResource(file.toString())}}
  }).onComplete({
    case Success(b) => println(s"Successfully bound to ${b.localAddress}")
    case Failure(e) => println(e.getMessage); system.shutdown()
  })

}


