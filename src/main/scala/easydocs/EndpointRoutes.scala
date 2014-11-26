package easydocs

import spray.http.{StatusCodes, Uri}
import spray.routing.{Route, Directives}

import scala.util.{Failure, Success}

trait EndpointRoutes {
  this: Directives with Services =>

  import system.dispatcher

  val deleteEndpointRoute: Route = (
    post &
    path("endpoints" / """(.*)""".r / "delete")
  ){ slug => {
    onComplete(esClient.deleteDoc(slug)){
      case Success(_) => redirect(Uri("/"), StatusCodes.Found)
      case Failure(e) => complete(<h1>{e.getMessage}</h1>)
    }
  }}
  
  val updateEndpointRoute: Route = {
    path("endpoints" / """(.*)""".r / "update"){slug => {
      (get & complete){
        for {
          navigation <- new Navigation().build(esClient)
          endpoint   <- esClient.getEndpoint(slug)
        } yield {
          <html>
            {header}
            <div class="container-fluid">
              <div class="row">
                {navigation}

                <div class="col-md-8">
                  <h1>Update Endpoint: </h1>
                  {Endpoint.formBuilder(Some(endpoint))}
                </div>

                {endpointActions(endpoint)}
              </div>
            </div>
          </html>

        }
      } ~
      (post & complete){
        "POST"
      }
    }}
  }

}
