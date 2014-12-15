package easydocs.templates

import easydocs.{Client, Endpoint}
import scala.xml.XML
import scalatags.Text.all._
import scala.concurrent.{Future, ExecutionContext}
import com.github.rjeschke.txtmark

class EndpointPage(endpoint: Endpoint)(implicit ec: ExecutionContext, client: Client) {

  private val titleCSS = Seq(fontSize:="2em", fontWeight:="bold", padding:="0px 5px")

  private def coloredMethod = {
    val c = endpoint.method match {
      case "POST"   => "red"
      case "GET"    => "blue"
      case "PUT"    => "orange"
      case "DELETE" => "black"
      case _        => "#dddddd"
    }

    span(titleCSS, color:="white", backgroundColor:=c, endpoint.method)
  }

  private val theRouteTitle =
    span(titleCSS, color:="gray", endpoint.route)

  private val paramsBox = {
    val title = endpoint.method match {
      case "POST"   => "Request-Body:"
      case "GET"    => "Query-Params:"
      case "PUT"    => "Request-Body:"
      case "DELETE" => "Query-Params:"
      case _        => "#dddddd"
    }

    val preBox = if (endpoint.params.startsWith(">>>markdown")){
      txtmark.Processor.process(endpoint.params.replace(">>>markdown", ""))
    } else {
      pre(endpoint.params).toString()
    }

    div(
      h4(title),
      raw(preBox)
    )
  }

  def inner = div(`class`:="row", margin:="10px",
    div(marginBottom:="1em",
      coloredMethod,
      theRouteTitle,
      hr()
    ),
    div(marginTop:="1em",
      table(`class`:="table table-striped", maxWidth:="", minWidth:="30%",
        tr(td(width:="10px", "Description:"), td(endpoint.description)),
        tr(td(width:="10px", "Content-Type:"), td(endpoint.contentType)),
        tr(td(width:="10px", "Authentication:"), td(endpoint.authentication))
      ),
      paramsBox
    ),
    div(float:="right",
      form(display:="inline-block", marginRight:="10px", action:=s"/web/endpoints/${slugify(endpoint)}/update", method:="GET",
        button(`type`:="submit", `class`:="btn btn-warning", "Update")
      ),
      form(display:="inline-block", action:=s"/api/endpoints/${slugify(endpoint)}/delete?url=/web", method:="POST",
        button(`type`:="submit", `class`:="btn btn-danger", "Delete")
      )
    )
  )

  def render = for {
    header           <- Future.successful(Header.render)
    generatedContent <- new Content(inner).render
  } yield {
    html(
      header,
      generatedContent
    )
  }
}
