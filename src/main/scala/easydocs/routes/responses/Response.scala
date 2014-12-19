package easydocs.routes.responses

import easydocs.ERR
import spray.json.JsObject
import spray.routing.Route
import spray.routing.directives.FutureDirectives._
import spray.routing.directives.RouteDirectives._
import scala.concurrent.{Future, ExecutionContext}
import scala.language.implicitConversions
import scala.util.{Failure, Success}
import spray.json.DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._

trait Response {

  implicit def jsObjectToRoute(js: JsObject): Route = complete(js)

  implicit def futureJsObjectToRoute(fjs: Future[JsObject])(implicit ec: ExecutionContext): Route = onComplete(fjs){
    case Success(js) => complete(js)
    case Failure(e)  => ERR(e).complete
  }

  def finish(implicit ec: ExecutionContext): Route

}
