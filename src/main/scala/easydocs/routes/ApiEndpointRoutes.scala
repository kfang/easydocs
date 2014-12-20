package easydocs.routes

import easydocs.models.ESEndpoint
import easydocs.routes.requests.{EndpointDeleteRequest, EndpointUpdateRequest, EndpointCreateRequest}
import easydocs.routes.requests.EndpointUpdateRequest.EndpointUpdate
import easydocs.Services
import spray.routing.{Route, Directives}


trait ApiEndpointRoutes {
  this: Services with Directives with JsonSupport =>

  import system.dispatcher

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

    ESEndpoint
      .fromId(id)
      .flatMap(endpoint => request.getResponse(endpoint))

  }}

  private val deleteEndpointRoute: Route = (
    delete &
    path(JavaUUID)
  ){(id) => {

    ESEndpoint
      .fromId(id)
      .map(endpoint => EndpointDeleteRequest(endpoint))
      .flatMap(_.getResponse)

  }}

  val apiEndpointRoutes: Route = pathPrefix("endpoints") {
    createEndpointRoute ~
    updateEndpointRoute ~
    deleteEndpointRoute
  }

}


