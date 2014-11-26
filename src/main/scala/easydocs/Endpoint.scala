package easydocs

import spray.routing.{Directives, Route}
import scala.concurrent.ExecutionContext
import scala.xml.Elem

case class Endpoint(
  method: String,
  route: String,
  description: String,
  contentType: String,
  authentication: String,
  params: String
){
  require(method != "", "method cannot be empty")
  require(route != "", "route cannot be empty")
  require(description != "", "description cannot be empty")
  require(contentType != "", "contentType cannot be empty")
}

object Endpoint extends Directives {

  def formBuilder(e: Option[Endpoint]): Elem = {
    <form role="form" action="/add" method="POST">
      <div class="form-group">
        <label for="method-input">Method:</label>
        <select class="form-control" id="method-input" name="method">
          <option value="POST" selected={e.map(_.method).find(_ == "POST").orNull}>POST</option>
          <option value="GET" selected={e.map(_.method).find(_ == "GET").orNull}>GET</option>
          <option value="PUT" selected={e.map(_.method).find(_ == "PUT").orNull}>PUT</option>
          <option value="DELETE" selected={e.map(_.method).find(_ == "DELETE").orNull}>DELETE</option>
        </select>
      </div>

      <div class="form-group">
        <label for="route-input">Route:</label>
          <input type="text" class="form-control" id="route-input" value={e.map(_.route).getOrElse("")} name="route" disabled={e.map(_.route).orNull}></input>
      </div>

      {if(e.isDefined) <input type="hidden" name="route" value={e.get.route}></input> }

      <div class="form-group">
        <label for="description-input">Description:</label>
        <input type="text" class="form-control" id="description-input" value={e.map(_.description).getOrElse("")} name="description"></input>
      </div>

      <div class="form-group">
        <label for="contenttype-input">Content-Type:</label>
        <select class="form-control" id="contenttype-input" name="contentType">
          <option value="application/json" selected={e.map(_.contentType).find(_ == "application/json").orNull}>application/json</option>
          <option value="application/x-www-form-urlencoded" selected={e.map(_.contentType).find(_ == "application/x-www-form-urlencoded").orNull}>application/x-www-form-urlencoded</option>
          <option value="multipart/form-data" selected={e.map(_.contentType).find(_ == "multipart/form-data").orNull}>multipart/form-data</option>
          <option value="text/plain" selected={e.map(_.contentType).find(_ == "text/plain").orNull}>text/plain</option>
        </select>
      </div>

      <div class="form-group">
        <label for="authentication-input">Authentication:</label>
        <input type="text" class="form-control" id="authentication-input" value={e.map(_.authentication).getOrElse("")} name="authentication"></input>
      </div>

      <div class="form-group">
        <label for="params-input">Params: </label>
        <textarea class="form-control" id="params-input" name="params" rows="10">{e.map(_.params).getOrElse("")}</textarea>
      </div>

      <button type="submit" class="btn btn-default">Submit</button>
    </form>
  }

  def routes(client: Client)(implicit ec: ExecutionContext): Route = {
    (get & path("endpoints" / """(.*)""".r)){slug => complete(Endpoint.getResponse(slug, client)) }
  }

  def getResponse(slug: String, client: Client)(implicit ec: ExecutionContext) = (for {
    endpoint <- client.getEndpoint(slug)
    navigation <- new Navigation().build(client)
  } yield {
    <html lang="en">
      {header}
      <body>
        <div class="container-fluid">
          <div class="row">

            {navigation}

            <div class="col-md-8">
              <h1>
                {endpoint.method.toUpperCase}
                =>
                {endpoint.route}
              </h1>
              <h3>Description:
                {endpoint.description}
              </h3>
              <h3>Content-Type:
                {endpoint.contentType}
              </h3>
              <h3>Authentication:
                {endpoint.authentication}
              </h3>{endpoint.method.toUpperCase match {
              case "POST" => <h3>Request-Body:</h3>
              case "GET" => <h3>Query-Params:</h3>
              case "PUT" => <h3>Request-Body:</h3>
              case "DELETE" => <h3>Query-Params:</h3>
            }}
              <pre>{endpoint.params}</pre>
            </div>

            {endpointActions(endpoint)}

          </div>
        </div>
      </body>
    </html>
  }).recover({
    case e: Exception =>
      <h1>{e}</h1>
  })
}
