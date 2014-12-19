package easydocs.routes

import easydocs.routes.requests.{EndpointUpdateRequest, EndpointCreateRequest}
import easydocs.routes.requests.EndpointUpdateRequest.EndpointUpdate
import easydocs.{Endpoint, Services}
import spray.http.{StatusCodes, Uri}
import spray.routing.{Route, Directives}
import scala.util.{Failure, Success}


trait ApiEndpointRoutes {
  this: Services with Directives with JsonSupport =>

  import system.dispatcher

  private def redirectOrBool(url: Option[String], bool: Boolean): Route = if(url.isDefined){
    redirect(Uri(url.get).withQuery("result" -> bool.toString), StatusCodes.Found)
  } else {
    complete(bool.toString)
  }

  private lazy val createEndpointRouteForm: Route = (
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

  private val createEndpointRoute: Route = (
    post &
    pathEnd &
    entity(as[EndpointCreateRequest])
  ){(request) => {

    request.getResponse

  }}

  private val updateEndpointRoute: Route = (
    put &
    path(JavaUUID) &
    entity(as[EndpointUpdateRequest])
  ){(id, request) => {

    request.getResponse()

  }}


  val apiEndpointRoutes: Route = pathPrefix("endpoints") {
    createEndpointRouteForm ~
    deleteEndpointRoute
  }

}
