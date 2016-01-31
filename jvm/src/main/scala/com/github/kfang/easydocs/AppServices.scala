package com.github.kfang.easydocs

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.ElasticClient
import com.github.kfang.easydocs.services.{SitesHandler, IndexService}

case class AppServices(system: ActorSystem, config: AppConfig) {

  //TODO: configure remote elasticsearch client
  val elasticClient = ElasticClient.local
  val endpointService = system.actorOf(IndexService.props(elasticClient), "index-service")

  SitesHandler.ensureSitesIndex(elasticClient)(system)

}
