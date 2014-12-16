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
import scala.util.{Failure, Success, Try}

case class HeadingSlug(heading: String, route: String, method: String, cType: String)
case class TopicHeading(topic: String, headings: List[HeadingSlug])

class Client(implicit ec: ExecutionContext) {

  private val useLocal = false
  private val ES_HOST = sys.env.getOrElse("ES_HOST", "localhost")

  val client = if(useLocal) {
    ElasticClient.local
  } else {
    val elasticSearchSettings = ImmutableSettings.settingsBuilder().put("cluster.name", "zc0").build()
    ElasticClient.remote(elasticSearchSettings, (ES_HOST, 9300))
  }
  val indexType = "easydoc" -> "endpoint"
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val mapping = "endpoint".as(
    "topic".typed(StringType).analyzer(KeywordAnalyzer),
    "heading".typed(StringType).analyzer(KeywordAnalyzer),
    "method".typed(StringType).analyzer(KeywordAnalyzer),
    "route".typed(StringType).analyzer(KeywordAnalyzer),
    "description".typed(StringType).analyzer(SnowballAnalyzer),
    "contentType".typed(StringType).analyzer(KeywordAnalyzer),
    "authentication".typed(StringType).analyzer(KeywordAnalyzer)
  )


  //create the index if it doesn't exist
  createIndex()

  def getTopics: Future[List[TopicHeading]] = {
    client.execute(search.in(indexType).aggs(
      agg.terms("topics").field("topic").order(Order.term(true)).size(1000).aggs(
        agg.terms("headings").field("heading").order(Order.term(true)).size(1000).aggs(
          agg.terms("route").field("route").order(Order.term(true)).size(10000),
          agg.terms("method").field("method").order(Order.term(true)).size(10000),
          agg.terms("type").field("contentType").order(Order.term(true)).size(10000)
        )
      )
    ).limit(0)).map(sr => {
      sr.getAggregations.get[Terms]("topics").getBuckets.toList.map(bucket => {
        val topic = bucket.getKey
        val headings = bucket.getAggregations.get[Terms]("headings").getBuckets.toList.map(headingBucket => {
          val route = headingBucket.getAggregations.get[Terms]("route").getBuckets.toList.head.getKey
          val method = headingBucket.getAggregations.get[Terms]("method").getBuckets.toList.head.getKey
          val ctype = headingBucket.getAggregations.get[Terms]("type").getBuckets.toList.head.getKey
          val heading = headingBucket.getKey
          HeadingSlug(heading, route, method, ctype)
        })
        TopicHeading(topic, headings)
      })
    })
  }

  def getNavigationItems: Future[List[NavigationItem]] = {
    client.execute(search.in(indexType).aggs(
      agg.terms("routes").field("route").order(Order.term(true)).size(10000).aggs(
        agg.terms("methods").field("method").order(Order.term(true)).size(10).aggs(
          agg.terms("types").field("contentType").order(Order.term(true)).size(10)
        )
      )
    ).limit(0)).map(sr => {
      sr.getAggregations.get[Terms]("routes").getBuckets.toList.map(bucket => {
        val route = bucket.getKey
        bucket.getAggregations.get[Terms]("methods").getBuckets.map(methodBucket => {
          val method = methodBucket.getKey
          val cTypes = methodBucket.getAggregations.get[Terms]("types").getBuckets.map(_.getKey).toList
          cTypes.map(cType => NavigationItem(method, route, cType))
        }).toList
      }).flatten.flatten
    })
  }

  def doSearch(qString: String): Future[List[Endpoint]] = {
    client.execute(search.in(indexType).query(qString)).map(sr => {
      val sources = sr.getHits.getHits.toList.map(_.sourceAsString)
      sources.map(s => mapper.readValue(s, classOf[Endpoint]))
    })
  }

  def createIndex(): Unit = {
    client.execute(create.index("easydoc").mappings(mapping).replicas(0).shards(5))
      .map(cir => println("create index: " + cir.isAcknowledged))
      .recover({case e: Exception => println(e.getCause.getMessage)})
  }

  def dropDocs() = {
    client.execute(delete.from(indexType).where(matchall))
  }

  def deleteDoc(slug: String) = {
    client.execute(delete.from(indexType).where(ids(slug)))
  }

  def insertEndpoint(endpoint: Endpoint): Future[Boolean] = Try {
    val src = ObjectSource(endpoint)
    val slug = templates.slugify(endpoint)
    val cmd = index.into(indexType).doc(src).id(slug)
    client.execute(cmd)
      .map(ir => {println("true"); true})
      .recover({case e: Exception => println(e.getMessage); false})
  } match {
    case Success(f) => f
    case Failure(e) => println(e.getMessage); Future.successful(false)
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
