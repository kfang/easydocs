package easydocs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import com.sksamuel.elastic4s.source.ObjectSource
import com.sksamuel.elastic4s.{ElasticClient, KeywordAnalyzer, SnowballAnalyzer}
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order

import scala.collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Future}

class Client(implicit ec: ExecutionContext) {

  private val useLocal = false

  val client = if(useLocal) {
    ElasticClient.local
  } else {
    val elasticSearchSettings = ImmutableSettings.settingsBuilder().put("cluster.name", "zc0").build()
    ElasticClient.remote(elasticSearchSettings, ("localhost",9300))
  }
  val indexType = "easydoc" -> "endpoint"
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val mapping = "endpoint".as(
    "method".typed(StringType).analyzer(KeywordAnalyzer),
    "route".typed(StringType).analyzer(KeywordAnalyzer),
    "description".typed(StringType).analyzer(SnowballAnalyzer),
    "contentType".typed(StringType).analyzer(KeywordAnalyzer),
    "authentication".typed(StringType).analyzer(KeywordAnalyzer)
  )

  //create the index if it doesn't exist
  createIndex()

  def getRoutes: Future[List[(String, List[String])]] = {
    client.execute(search.in(indexType).aggs(
      agg.terms("routes").field("route").order(Order.term(true)).size(10000).aggs(
        agg.terms("methods").field("method").order(Order.term(true)).size(10)
      )
    ).limit(0)).map(sr => {
      sr.getAggregations.get[Terms]("routes").getBuckets.toList.map(bucket => {
        val route = bucket.getKey
        val methods = bucket.getAggregations.get[Terms]("methods").getBuckets.map(_.getKey).toList
        route -> methods
      })
    })
  }

  def doSearch(qString: String): Future[List[Endpoint]] = {
    client.execute(search.in(indexType).query(qString)).map(sr => {
      val sources = sr.getHits.getHits.toList.map(_.sourceAsString)
      sources.map(s => mapper.readValue(s, classOf[Endpoint]))
    })
  }

  def createIndex(): Unit = {
    client.execute(create.index("easydoc").mappings(mapping).replicas(0).shards(10))
      .map(cir => println("create index: " + cir.isAcknowledged))
      .recover({case e: Exception => println(e.getMessage)})
  }

  def dropDocs() = {
    client.execute(delete.from(indexType).where(matchall))
  }

  def deleteDoc(slug: String) = {
    client.execute(delete.from(indexType).where(ids(slug)))
  }

  def insertEndpoint(e: Endpoint): Future[Boolean] = {
    val cmd = index.into(indexType).doc(ObjectSource(e)).id(slugify(e))
    client.execute(cmd)
      .map(ir => true)
      .recover({case e: Exception => println(e.getMessage); false})
  }

  def getEndpoint(slug: String): Future[Endpoint] = {
    client.execute(get.id(slug).from(indexType)).map(gr => {
      mapper.readValue(gr.getSourceAsString, classOf[Endpoint])
    }).recover({
      case e: Exception =>
        println(e.getMessage)
        throw e
    })
  }

}
