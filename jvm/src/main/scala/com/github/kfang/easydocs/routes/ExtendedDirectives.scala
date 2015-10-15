package com.github.kfang.easydocs.routes

import com.github.kfang.easydocs.AppPackage
import com.github.kfang.easydocs.routes.responses.Response
import spray.httpx.SprayJsonSupport
import spray.json.JsObject
import spray.routing.{Route, Directives}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.xml.Elem
import scala.language.implicitConversions
import spray.json.DefaultJsonProtocol._

abstract class ExtendedDirectives(App: AppPackage) extends Directives with SprayJsonSupport {

  import App.system.dispatcher
  //TODO: we should compact print json responses (look in SprayJsonSupport)

  //TODO: finish filling out possible returns
  implicit def completeF(f: Future[Any]): Route = onComplete(f)({
    case Success(elem: Elem) => complete(elem)
    case Success(json: JsObject) => complete(json)
    case Success(any) => complete(s"unknown response type: ${any.toString}")
    case Failure(t) => complete(t.getMessage)
  })

  implicit def completeResponse(r: Response): Route = r.finish

}
