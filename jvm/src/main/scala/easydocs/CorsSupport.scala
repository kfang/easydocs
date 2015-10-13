package easydocs

import spray.http.HttpHeaders.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Origin`}
import spray.http._
import spray.routing.{Directive0, Directives, Route}

trait CorsSupport {
  this: Directives =>

  private val allowedHeaders = Seq[String](
    "Accept",
    "Authorization",
    "Content-Type"
  )

  private val allowedMethods = Seq[HttpMethod](
    HttpMethods.GET,
    HttpMethods.POST,
    HttpMethods.PUT,
    HttpMethods.DELETE,
    HttpMethods.OPTIONS
  )

  private def corsHeaders(clientOrigin: String) = List[HttpHeader](
    `Access-Control-Allow-Origin`(AllOrigins),
    `Access-Control-Allow-Headers`(allowedHeaders),
    `Access-Control-Allow-Methods`(allowedMethods),
    `Access-Control-Allow-Credentials`(allow = true)
  )

  private def respondWithCors(origin: String, route: Route): Route = {
    respondWithHeaders(corsHeaders(origin)) {
      //preflight request
      (options & respondWithStatus(StatusCodes.NoContent) & complete)("") ~
      route
    }
  }

  def cors(route: Route): Route = {
    optionalHeaderValueByName("Origin"){
      case Some(clientOrigin) => respondWithCors(clientOrigin, route)
      case None               => route
    }
  }

}