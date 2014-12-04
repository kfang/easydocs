package easydocs.templates

import easydocs.Client
import spray.http.DateTime
import scala.concurrent.ExecutionContext
import scalatags.Text.all._

class Home(client: Client)(implicit ec: ExecutionContext) {

  def b = h1("Home " + DateTime.now.toString)

  def render = for {
    renderedContent <- new Content(client).render(b)
  } yield {
    html(
      Header.render,
      renderedContent
    )
  }

}
