package easydocs.routes.responses

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import easydocs.models.ESEndpoint
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order
import spray.json.{JsObject, JsArray, JsString}
import spray.routing.Route
import scala.concurrent.ExecutionContext
import scala.collection.JavaConversions._

case class TopicListResponse(
                            site: String,
  client: ElasticClient
) extends Response {

  override def finish(implicit ec: ExecutionContext): Route = {
    client.execute(search.in(ESEndpoint.ALIAS_TYPE).query(term("site", site)).limit(0).aggs(
      agg.terms("topics").field("topic").order(Order.term(true)).aggs(
        agg.terms("subTopics").field("subTopic").order(Order.term(true)).aggs(
          agg.terms("ids").field("id")
        )
      )
    )).map(searchResponse => {

      val topicSubtopicIdTuples = searchResponse.getAggregations.get[Terms]("topics").getBuckets.toList.map(topicBucket => {
        val topic = topicBucket.getKey
        val subTopicIdTuples = topicBucket.getAggregations.get[Terms]("subTopics").getBuckets.toList.map(subTopicBucket => {
          val subTopic = subTopicBucket.getKey
          val id = subTopicBucket.getAggregations.get[Terms]("ids").getBuckets.toList.head.getKey
          subTopic -> id
        })

        topic -> subTopicIdTuples
      })

      val parsed = topicSubtopicIdTuples.map({case (topic, subTopics) =>
        JsObject(
          "topic" -> JsString(topic),
          "subTopics" -> JsArray(subTopics.map({case (subTopic, id) =>
              JsObject("subTopic" -> JsString(subTopic), "id" -> JsString(id))
          }).toVector)
        )
      }).toVector

      JsObject("topics" -> JsArray(parsed))

    })
  }

}
