package easydocs

import akka.actor.ActorSystem
import akka.event.Logging
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.ConfigFactory
import easydocs.services.IndexService
import org.elasticsearch.common.settings.ImmutableSettings

trait Services {

  val systemConfig = ConfigFactory.load()
  implicit val system = ActorSystem("easydoc", systemConfig)
  val systemLog = Logging(system.eventStream, "main-system-log")
  systemLog.info("booted ActorSystem")

  private val ES_USE_REMOTE = false
  private val ES_HOST = "localhost"

  implicit val elasticClient = if (ES_USE_REMOTE) {
    val elasticSearchSettings = ImmutableSettings.settingsBuilder().put("cluster.name", "zc0").build()
    ElasticClient.remote(elasticSearchSettings, (ES_HOST, 9300))
  } else {
    ElasticClient.local
  }

  systemLog.info("booted ElasticClient")

  val endpointService = system.actorOf(IndexService.props(elasticClient), "endpoint-service")

}
