package com.github.kfang.easydocs.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.{Route, Directives}
import com.github.kfang.easydocs.AppPackage
import com.github.kfang.easydocs.routes.responses.Response
import spray.json.JsValue
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scala.language.implicitConversions

abstract class ExtendedDirectives(App: AppPackage) extends Directives {

  implicit def completeF(f: Future[Any]): Route = onComplete(f)({
    case Success(js: JsValue) => complete(js)
    case Success(any) => complete(s"unknown response type: ${any.toString}")
    case Failure(t) => complete(t.getMessage)
  })

  implicit def completeR(r: Response): Route = {
    r.finish(App.system.dispatcher)
  }

}
