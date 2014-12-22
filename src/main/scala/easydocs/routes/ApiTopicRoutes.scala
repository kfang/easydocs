package easydocs.routes

import easydocs.Services
import easydocs.routes.responses.TopicListResponse
import spray.routing.Directives

import scala.concurrent.Future

trait ApiTopicRoutes {
  this: Services with Directives with JsonSupport =>

  import system.dispatcher

  private val listTopicRoute = (
    get &
    pathEnd
  ) {
    Future.successful(TopicListResponse(client = elasticClient))
  }

  val apiTopicRoutes = pathPrefix("topics"){
    listTopicRoute
  }

}
