package easydocs.templates

import easydocs.Client
import scala.concurrent.ExecutionContext
import scalatags.Text
import scalatags.Text.all._

class Content(client: Client)(implicit ec: ExecutionContext) {

  def render[T <: String](inner: Text.TypedTag[T]) = for {
    navigation <- new Navigation(client).render
  } yield {
    body(
      div(id:="container-fluid",
        div(`class`:="row",
          navigation,
          div(`class`:="col-md-8",
            inner
          )
        )
      )
    )
  }

}
