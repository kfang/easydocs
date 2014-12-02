package easydocs

import spray.http.{StatusCodes, Uri}
import spray.routing.{Route, Directives}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object AddEndpoint extends Directives {

  def routes(client: Client)(implicit ec: ExecutionContext): Route = {
    (get & path("add") & complete){
      for {
        navigation <- new Navigation().build(client)
      } yield {
        <html>
          {header}

          <body>
            <div class="container-fluid">
              <div class="row">
                {navigation}
                <div class="col-md-8">
                  <h1>Add Endpoint</h1>
                  {Endpoint.formBuilder(None)}
                </div>
              </div>
            </div>
          </body>
        </html>
      }
    } ~ (post & path("add") & formFields(
      'method,
      'route,
      'description,
      'contentType,
      'authentication,
      'params
    ).as(Endpoint.apply _)){endpoint =>
      onComplete(client.insertEndpoint(endpoint)){
        case Success(b) => redirect(Uri("/"), StatusCodes.Found)
        case Failure(e) => complete(e.getMessage)
      }
    }
  }
}