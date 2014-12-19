package easydocs

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.ElasticClient
import easydocs.services.EndpointService
import org.elasticsearch.common.settings.ImmutableSettings

trait Services {

  implicit val system = ActorSystem("easydoc")
  import system.dispatcher

  private val ES_HOST = "localhost"

  implicit val elasticClient = {
    val elasticSearchSettings = ImmutableSettings.settingsBuilder().put("cluster.name", "zc0").build()
    ElasticClient.remote(elasticSearchSettings, (ES_HOST, 9300))
  }

  val endpointService = system.actorOf(EndpointService.props(elasticClient), "endpoint-service")

  implicit val esClient = new Client()

}
