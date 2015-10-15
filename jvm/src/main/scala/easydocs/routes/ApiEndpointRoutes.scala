package easydocs.routes

import com.sksamuel.elastic4s.ElasticClient
import easydocs.models.ESEndpoint
import easydocs.routes.requests.{EndpointDeleteRequest, EndpointUpdateRequest, EndpointCreateRequest}
import easydocs.routes.requests.EndpointUpdateRequest.EndpointUpdate
import easydocs.AppPackage
import easydocs.routes.responses.EndpointListResponse
import spray.routing.Route


class ApiEndpointRoutes(implicit App: AppPackage) extends ExtendedDirectives(App) {

  import App.system.dispatcher
  implicit val elasticClient: ElasticClient = App.services.elasticClient

  private val readEndpointRoute: Route = (
    get &
    path(JavaUUID)
  ){(id) => {
    ESEndpoint
      .fromId(id)
      .map(endpoint => EndpointListResponse(List(endpoint)))
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

  val routes: Route = pathPrefix("endpoints") {
    readEndpointRoute ~
    createEndpointRoute ~
    updateEndpointRoute ~
    deleteEndpointRoute
  }

}


