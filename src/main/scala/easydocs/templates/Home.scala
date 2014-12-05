package easydocs.templates

import easydocs.Client
import spray.http.DateTime
import scala.concurrent.ExecutionContext
import scalatags.Text.all._

class Home(implicit ec: ExecutionContext, client: Client) {

  def b = h1("Home " + DateTime.now.toString)

  def render = for {
    renderedContent <- new Content(b).render
  } yield {
    html(
      Header.render,
      renderedContent
    )
  }

}
