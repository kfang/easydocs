package com.github.kfang.easydocs.routes

import akka.http.scaladsl.server.Route
import com.sksamuel.elastic4s.ElasticClient
import com.github.kfang.easydocs.AppPackage
import com.github.kfang.easydocs.models.ESSite
import com.github.kfang.easydocs.routes.requests.SiteCreateRequest
import com.github.kfang.easydocs.routes.responses.{TopicListResponse, SiteReadResponse, SiteListResponse}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.Future

class ApiSiteRoutes(implicit App: AppPackage) extends ExtendedDirectives(App){

  import App.system.dispatcher
  implicit val elasticClient: ElasticClient = App.services.elasticClient

  private val createSiteRoute: Route = (
    post &
    pathEnd &
    entity(as[SiteCreateRequest])
  ){request => {

    request.getResponse

  }}

  private val readSiteRoute: Route = (
    get &
    path(JavaUUID)
  ){id => {

    ESSite
      .fromId(id)
      .map(site => SiteReadResponse(site))

  }}

  private val listSitesRoute: Route = (
    get &
    pathEnd
  ){
    Future.successful(SiteListResponse(elasticClient))
  }

  private val listSiteTopicsRoute: Route = (
    get &
    path(JavaUUID / "topics")
  ){siteId => {

    ESSite.fromId(siteId).map(site => {
      TopicListResponse(site.id, elasticClient)
    })

  }}

  val routes: Route = pathPrefix("sites"){
    createSiteRoute ~
    readSiteRoute ~
    listSitesRoute ~
    listSiteTopicsRoute
  }

}
