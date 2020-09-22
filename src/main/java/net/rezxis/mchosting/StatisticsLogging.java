package net.rezxis.mchosting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.lucene.util.QueryBuilder;
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
	
	public static HashMap<Date,String> search(String type, Date from) {
		HashMap<Date, String> values = new HashMap<>();
		try {
			SearchSourceBuilder builder = new SearchSourceBuilder().fetchSource(new String[] {"*"}, new String[0]).query(QueryBuilders.rangeQuery("@timestamp").from(from).to(new Date()));
			SearchRequest request = new SearchRequest("statistics").source(builder);
			request.indicesOptions(IndicesOptions.lenientExpandOpen());
	        SearchResponse response = Start.rcl.search(request, RequestOptions.DEFAULT);
	        for (SearchHit hit : response.getHits()) {
	        	Date date = null;
	        	String val = null;
	        	boolean put = false;
	        	for (Entry<String,Object> e : hit.getSourceAsMap().entrySet()) {
	        		if (e.getKey().equalsIgnoreCase("type") && ((String)e.getValue()).equalsIgnoreCase(type)) {
	        			put = true;
	        		}
	        		if (e.getKey().equalsIgnoreCase("@timestamp")) {
	        			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	        			date = sdf.parse((String) e.getValue());
	        		} else if (e.getKey().equalsIgnoreCase("value")) {
	        			val = String.valueOf(e.getValue());
	        		}
	        	}
	        	if (put)
	        		values.put(date, val);
 	        }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return values;
	}
	
	public static HashMap<Date,Integer> searchI(String type, Date from) {
		HashMap<Date, Integer> values = new HashMap<>();
		try {
			SearchSourceBuilder builder = new SearchSourceBuilder()//.query(QueryBuilders.termQuery("type", type))
					.fetchSource(new String[] {"*"}, new String[0]).query(QueryBuilders.rangeQuery("@timestamp").from(from).to(new Date()));
			SearchRequest request = new SearchRequest("statistics").source(builder);
			request.indicesOptions(IndicesOptions.lenientExpandOpen());
	        SearchResponse response = Start.rcl.search(request, RequestOptions.DEFAULT);
	        for (SearchHit hit : response.getHits()) {
	        	Date date = null;
	        	int val = -1;
	        	boolean put = false;
	        	for (Entry<String,Object> e : hit.getSourceAsMap().entrySet()) {
	        		if (e.getKey().equalsIgnoreCase("type") && ((String)e.getValue()).equalsIgnoreCase(type)) {
	        			put = true;
	        		}
	        		if (e.getKey().equalsIgnoreCase("@timestamp")) {
	        			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	        			date = sdf.parse((String) e.getValue());
	        		} else if (e.getKey().equalsIgnoreCase("value")) {
	        			val = Integer.valueOf(String.valueOf(e.getValue()));
	        		}
	        	}
	        	if (put)
	        		values.put(date, val);
 	        }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return values;
	}
	
	private static Comparator<Date> sorter = new Comparator<Date>() {
		@Override
		public int compare(Date arg0, Date arg1) {
			if (arg0.before(arg1))
				return 0;
			else
				return 1;
		}};
	
	public static ProcessedData processData(HashMap<Date,Integer> data) {
		//minutes
		LinkedHashMap<Date,Integer> minutes = new LinkedHashMap<>();
		//hours
		LinkedHashMap<Date,Integer> hours = new LinkedHashMap<>();
		ArrayList<Date> list = new ArrayList<Date>(data.keySet());
		list.sort(sorter);
		LinkedHashMap<Date,Integer> sorted = new LinkedHashMap<>();
		for (Date d : list) {
			sorted.put(d, data.get(d));
			System.out.println(d.toString()+" ---- "+data.get(d));
		}
		//minutes
		{
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.add(Calendar.MINUTE, 60);
			cal.add(Calendar.SECOND, 1);
			Date end = cal.getTime();
			int times = 0;
			int current = 0;
			Date lastTime = null;
			for (Entry<Date,Integer> entry : sorted.entrySet()) {
				Date now = entry.getKey();
				if (lastTime == null) {
					lastTime = now;
				}
				if (lastTime.getMinutes() != now.getMinutes()) {
					Calendar out = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
					out.setTime(lastTime);
					out.set(Calendar.SECOND, 0);
					minutes.put(out.getTime(), current/times);
					System.out.println("M"+times+":"+current+":"+out.getTime().getMinutes());
					lastTime = now;
					current = 0;
					times = 0;
					if (now.after(end)) {
						break;
					}
				}
				current += entry.getValue();
				++times;
			}
		}
		//hours
		{
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.add(Calendar.HOUR, 24);
			cal.add(Calendar.SECOND, 1);
			Date end = cal.getTime();
			int times = 0;
			int current = 0;
			Date lastTime = null;
			for (Entry<Date,Integer> entry : sorted.entrySet()) {
				Date now = entry.getKey();
				if (lastTime == null) {
					lastTime = now;
				}
				if (lastTime.getHours() != now.getHours()) {
					Calendar out = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
					out.setTime(lastTime);
					out.set(Calendar.SECOND, 0);
					out.set(Calendar.MINUTE, 0);
					hours.put(out.getTime(), current/times);
					System.out.println("H"+times+":"+current);
					lastTime = now;
					current = 0;
					times = 0;
					if (now.after(end))
						break;
				}
				current += entry.getValue();
				++times;
			}
		}
		return new ProcessedData(minutes,hours);
	}
	
	public static class ProcessedData {
		public LinkedHashMap<Date,Integer> minutes;
		public LinkedHashMap<Date,Integer> hours;
		
		public ProcessedData(LinkedHashMap<Date,Integer> m, LinkedHashMap<Date,Integer> h) {
			this.minutes = m;
			this.hours = h;
		}
	}
}
