package de.killbuqs.mymall.search;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ml.job.results.Bucket;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;

import de.killbuqs.mymall.search.config.MyMallElasticSearchConfig;
import lombok.Data;

/**
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-index.html
 * 
 * @author jlong
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MyMallSearchApplicationTest {

	@Autowired
	private RestHighLevelClient client;

	@Test
	public void testElasticSearch() {

		System.out.println(client);
	}

	@Test
	public void testIndex() throws IOException {
		IndexRequest request = new IndexRequest("users");
		request.id("1");
//		request.source("userName", "zhangsan", "age", 18, "gender", "M");
		User user = new User();
		user.setUserName("zhangsan");
		user.setGender("M");
		user.setAge(18);
		String jsonString = JSON.toJSONString(user);
		request.source(jsonString, XContentType.JSON);
		
		IndexResponse indexResponse = client.index(request, MyMallElasticSearchConfig.COMMON_OPTIONS);	
		
		System.out.println(indexResponse);

	}
	
	/**
	 * 
	 * ##按照年龄聚合，并且请求这些年龄段的这些人的平均薪资
	 *
	 *GET bank/_search
	 *{
	 *  "query": {
	 *    "match": {
     * 		"address": "mill"
     *	   }
	 *  },
	 *  "aggs": {
	 *    "aggAgg": {
	 *      "terms": {
	 *        "field": "age",
	 *        "size": 10
	 *      }
	 *    },
 *        "balanceAvg": {
 *          "avg": {
 *            "field": "balance"
 *          }
 *        }
	 *  }
	 *}
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testSearch() throws IOException {
		
		SearchRequest searchRequest = new SearchRequest(); 
		
		searchRequest.indices("bank");
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
//		searchSourceBuilder.query(QueryBuilders.matchAllQuery()); 
		searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill")); 
		
		// 按照年龄的值分布进行聚合
		TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);;
		searchSourceBuilder.aggregation(ageAgg);
		
		// 计算平均薪资
		AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
		searchSourceBuilder.aggregation(balanceAvg);
		
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = client.search(searchRequest, MyMallElasticSearchConfig.COMMON_OPTIONS);

//		System.out.println(searchResponse.toString());
		
//		Map map = JSON.parseObject(searchResponse.toString(), Map.class);
		
		SearchHits hits = searchResponse.getHits();
		
		SearchHit[] searchHits = hits.getHits();
		
		for (SearchHit hit : searchHits) {
			String sourceAsString = hit.getSourceAsString();
			Account account = JSON.parseObject(sourceAsString, Account.class);
			System.out.println("accout: " + account);
		}
		
		Aggregations aggregations = searchResponse.getAggregations();
		for (Aggregation aggregation : aggregations.asList()) {
			System.out.println("当前聚合： " + aggregation.getName());
		}
		
		Terms ageAggregation = aggregations.get("ageAgg"); 
		
		for (Terms.Bucket bucket : ageAggregation.getBuckets()) {
			String keyAsString = bucket.getKeyAsString();
			System.out.println("年龄： " + keyAsString + "==>" + bucket.getDocCount());
		}
		
		Avg balanceAggregation = aggregations.get("balanceAvg");
		double avg = balanceAggregation.getValue();
		System.out.println("平均薪资： " + avg);
	} 

	@Data
	class User {
		private String userName;
		private String gender;
		private Integer age;
	}

}
