package easydocs

import spray.routing.{Directive1, Directives}

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

  def urlEncodedForm: Directive1[Endpoint] = formFields(
    'method,
    'route,
    'description,
    'contentType,
    'authentication,
    'params
  ).as(Endpoint.apply _)

}