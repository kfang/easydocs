package easydocs

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.ElasticClient
import easydocs.services.IndexService
import org.elasticsearch.common.settings.ImmutableSettings

case class AppServices(system: ActorSystem, config: AppConfig) {

  val elasticClient = {
    if (config.IS_PRODUCTION) {
      val elasticSearchSettings = ImmutableSettings.settingsBuilder().put("cluster.name", "zc0").build()
      ElasticClient.remote(elasticSearchSettings, (config.ELASTIC_REMOTE_HOST, config.ELASTIC_REMOTE_PORT))
    } else {
      ElasticClient.local
    }
  }

  val endpointService = system.actorOf(IndexService.props(elasticClient), "index-service")

}
