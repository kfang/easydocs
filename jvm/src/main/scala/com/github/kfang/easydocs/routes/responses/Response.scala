package com.github.kfang.easydocs.routes.responses

import akka.http.scaladsl.server.{Directives, Route}
import com.github.kfang.easydocs.utils.ERR
import spray.json.JsObject
import scala.concurrent.{Future, ExecutionContext}
import scala.language.implicitConversions
import scala.util.{Failure, Success}
import Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

trait Response {

  implicit def jsObjectToRoute(js: JsObject): Route = complete(js)

  implicit def futureJsObjectToRoute(fjs: Future[JsObject])(implicit ec: ExecutionContext): Route = onComplete(fjs){
    case Success(js) => complete(js)
    case Failure(e)  => ERR(e).complete
  }

  implicit def futureString(s: Future[String])(implicit ec: ExecutionContext): Route = onComplete(s){
    case Success(str) => complete(str)
    case Failure(e) => ERR(e).complete
  }

  def finish(implicit ec: ExecutionContext): Route

}
