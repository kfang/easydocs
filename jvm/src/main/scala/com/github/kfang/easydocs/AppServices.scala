package com.github.kfang.easydocs

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.ElasticClient
import com.github.kfang.easydocs.services.{SitesHandler, IndexService}
import org.elasticsearch.common.settings.Settings

case class AppServices(system: ActorSystem, config: AppConfig) {

  //TODO: configure remote elasticsearch client
  private val esClientSettings = Settings.settingsBuilder()
    .put("http.enabled", false)
    .put("path.home", "~/elastic/data")
  val elasticClient = ElasticClient.local(esClientSettings.build)

  val endpointService = system.actorOf(IndexService.props(elasticClient), "index-service")
  val sitesHandler = system.actorOf(SitesHandler.props(elasticClient), SitesHandler.NAME)
}
