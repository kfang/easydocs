package easydocs.routes

import easydocs.{Endpoint, Services}
import spray.http.{StatusCodes, Uri}
import spray.routing.{Route, Directives}

import scala.util.{Failure, Success}

trait ApiEndpointRoutes {
  this: Services with Directives =>

  import system.dispatcher

  private def redirectOrBool(url: Option[String], bool: Boolean): Route = if(url.isDefined){
    redirect(Uri(url.get).withQuery("result" -> bool.toString), StatusCodes.Found)
  } else {
    complete(bool.toString)
  }

  private lazy val createEndpointRoute: Route = (
    post &
    pathEnd &
    parameters('url.?) &
    Endpoint.urlEncodedForm
  ){(url: Option[String], endpoint: Endpoint) => {
    onComplete(esClient.insertEndpoint(endpoint)){
      case Success(bool)  => redirectOrBool(url, bool)
      case Failure(e)     => complete(e.toString)
    }
  }}

  private lazy val deleteEndpointRoute: Route = (
    post &
    path("""(.*)""".r / "delete") &
    parameters('url.?)
  ){(idOrSlug: String, url: Option[String]) => {
    onComplete(esClient.deleteDoc(idOrSlug).map(_ => true)){
      case Success(bool) => redirectOrBool(url, bool)
      case Failure(e)    => complete(e.toString)
    }
  }}


  def apiEndpointRoutes: Route = pathPrefix("endpoints") {
    createEndpointRoute ~
    deleteEndpointRoute
  }

}
