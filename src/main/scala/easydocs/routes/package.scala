package easydocs

import easydocs.routes.responses.Response
import spray.http.{HttpEntity, MediaTypes}
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.Marshaller
import spray.json.DefaultJsonProtocol
import spray.routing.Route
import spray.routing.directives.FutureDirectives

import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}
import scalatags.Text.TypedTag

package object routes {

  implicit val typedTagMarshaller = Marshaller.of[TypedTag[String]](MediaTypes.`text/html`){
    (value, contentType, ctx) =>
      ctx.marshalTo(HttpEntity(contentType, value.toString()))
  }

  implicit def responseToRoute(res: Response)(implicit ec: ExecutionContext): Route = res.finish

  implicit def fResToRoute(res: Future[Response])(implicit ec: ExecutionContext): Route = {
    FutureDirectives.onComplete(res){
      case Success(r) => r.finish
      case Failure(e) => ERR(e).complete
    }
  }

  trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport

}
