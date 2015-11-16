package com.github.kfang.easydocs

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.util.{Failure, Success}

object Main extends App {

  /** Initialize Configuration **/
  private val Config = AppConfig()

  /** Initialize ActorSystem **/
  private implicit val system = ActorSystem("easydocs", Config.CONFIG)
  private implicit val materializer = ActorMaterializer()

  /** Initialize background services and clients **/
  private val services = AppServices(system, Config)

  /** Package everything together to pass to the routes **/
  private implicit val appPackage = AppPackage(system, Config, services)

  //ExecutionContext, needed to start SimpleRoutingApp
  import system.dispatcher

  //TODO: migrate to using akka-http instead of spray.io (note, it'll need a materializer)
  //start spray-can HTTP server
//  startServer("0.0.0.0", port = 8080)({
//    new ApiRoutes().routes ~  //=> ----  /api
//    path(RestPath){file => {getFromResource(file.toString())}}
//  }).onComplete({
//    case Success(b) => println(s"Successfully bound to ${b.localAddress}")
//    case Failure(e) => println(e.getMessage); system.shutdown()
//  })

  private val routes = {
    (get & pathEndOrSingleSlash)(complete("OK"))
  }

  Http().bindAndHandle(routes, "0.0.0.0", 8080).onComplete({
    case Success(b) => println(s"Successfully bound to ${b.localAddress}")
    case Failure(e) => println(e.getMessage); system.shutdown()
  })
}


