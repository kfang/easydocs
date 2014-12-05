package easydocs

import spray.http.{HttpEntity, MediaTypes}
import spray.httpx.marshalling.Marshaller

import scalatags.Text.TypedTag

package object routes {

  implicit val typedTagMarshaller = Marshaller.of[TypedTag[String]](MediaTypes.`text/html`){
    (value, contentType, ctx) =>
      ctx.marshalTo(HttpEntity(contentType, value.toString()))
  }

}
