package easydocs

import spray.routing.{Directive1, Directives}

case class Endpoint(
  topic: Option[String],
  heading: Option[String],
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
    'topic.?,
    'heading.?,
    'method,
    'route,
    'description,
    'contentType,
    'authentication,
    'params
  ).as(Endpoint.apply _)

}