package net.rezxis.mchosting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import net.rezxis.mchosting.database.Tables;

public class StatisticsLogging implements Runnable {

	@Override
	public void run() {
		while(true) {
			log("OnlineServers", Tables.getSTable().getOnlineServers().size());
			log("OnlinePlayers", Tables.getPTable().getOnlinePlayers());
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void log(String type, int value) {
		try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.timeField("@timestamp", sdf.format(System.currentTimeMillis()));
                builder.field("type", type);
                builder.field("value", value);
            }
            builder.endObject();
            IndexRequest request = new IndexRequest("statistics").source(builder);
            Start.rcl.indexAsync(request, Start.COMMON_OPTIONS, Start.listener);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
	}
	
	public static LinkedHashMap<Date,String> search(String type, Date from) {
		LinkedHashMap<Date, String> values = new LinkedHashMap<>();
		try {
			SearchSourceBuilder builder = new SearchSourceBuilder().fetchSource(new String[] {"*"}, new String[0]).query(QueryBuilders.rangeQuery("@timestamp").from(from).to(new Date()));
			SearchRequest request = new SearchRequest("statistics").source(builder);
			request.indicesOptions(IndicesOptions.lenientExpandOpen());
	        SearchResponse response = Start.rcl.search(request, RequestOptions.DEFAULT);
	        for (SearchHit hit : response.getHits()) {
	        	Date date = null;
	        	String val = null;
	        	for (Entry<String,Object> e : hit.getSourceAsMap().entrySet()) {
	        		if (e.getKey().equalsIgnoreCase("@timestamp")) {
	        			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	        			date = sdf.parse((String) e.getValue());
	        		} else if (e.getKey().equalsIgnoreCase("value")) {
	        			val = String.valueOf(e.getValue());
	        		}
	        	}
	        	values.put(date, val);
 	        }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		LinkedList<Date> keys = new LinkedList<Date>(values.keySet());
		Collections.sort(keys, (o1, o2) -> o1.after(o2) ? 1 : 0);
		LinkedHashMap<Date,String> result = new LinkedHashMap<>();
		for (Date date : keys) {
			result.put(date, values.get(date));
		}
		return result;
	}
	
	public static LinkedHashMap<Date,Integer> searchI(String type, Date from) {
		LinkedHashMap<Date, Integer> values = new LinkedHashMap<>();
		try {
			SearchSourceBuilder builder = new SearchSourceBuilder().fetchSource(new String[] {"*"}, new String[0]).query(QueryBuilders.rangeQuery("@timestamp").from(from).to(new Date()));
			SearchRequest request = new SearchRequest("statistics").source(builder);
			request.indicesOptions(IndicesOptions.lenientExpandOpen());
	        SearchResponse response = Start.rcl.search(request, RequestOptions.DEFAULT);
	        for (SearchHit hit : response.getHits()) {
	        	Date date = null;
	        	int val = -1;
	        	for (Entry<String,Object> e : hit.getSourceAsMap().entrySet()) {
	        		if (e.getKey().equalsIgnoreCase("@timestamp")) {
	        			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	        			date = sdf.parse((String) e.getValue());
	        		} else if (e.getKey().equalsIgnoreCase("value")) {
	        			val = Integer.valueOf(String.valueOf(e.getValue()));
	        		}
	        	}
	        	values.put(date, val);
 	        }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		LinkedList<Date> keys = new LinkedList<Date>(values.keySet());
		Collections.sort(keys, (o1, o2) -> o1.after(o2) ? 1 : 0);
		LinkedHashMap<Date,Integer> result = new LinkedHashMap<>();
		for (Date date : keys) {
			result.put(date, values.get(date));
		}
		return result;
	}
}
