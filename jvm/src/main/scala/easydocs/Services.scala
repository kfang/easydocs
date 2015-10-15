package easydocs

import akka.actor.ActorSystem
import akka.event.Logging
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.ConfigFactory
import easydocs.services.IndexService
import org.elasticsearch.common.settings.ImmutableSettings

trait Services {

  implicit val Config = AppConfig()
  implicit val system = ActorSystem("easydoc", Config.CONFIG)

  implicit val elasticClient = {
    if (Config.IS_PRODUCTION) {
      val elasticSearchSettings = ImmutableSettings.settingsBuilder().put("cluster.name", "zc0").build()
      ElasticClient.remote(elasticSearchSettings, (Config.ELASTIC_REMOTE_HOST, Config.ELASTIC_REMOTE_PORT))
    } else {
      ElasticClient.local
    }
  }

  val endpointService = system.actorOf(IndexService.props(elasticClient), "endpoint-service")

}
