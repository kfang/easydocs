package easydocs.templates

import easydocs.Client
import scala.concurrent.ExecutionContext
import scalatags.Text.TypedTag
import scalatags.Text.all._

class Content(inner: TypedTag[String]*)(implicit ec: ExecutionContext, client: Client) {

  def render = for {
    navigation <- new Navigation(client).render
  } yield {
    body(
      div(id:="container-fluid",
        div(`class`:="row",
          navigation,
          div(`class`:="col-md-9",
            inner
          )
        )
      )
    )
  }

}
