package net.rezxis.mchosting;

import net.rezxis.mchosting.database.Database;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class Start {
    public static Props props;
    public static RestHighLevelClient rcl = null;
	public static final RequestOptions COMMON_OPTIONS;

    public static void main(String[] args) throws InterruptedException, IOException {
    	rcl = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.0.1",9200,"http")));
        props = new Props("api.propertis");
        Database.init(props.DB_HOST,props.DB_USER,props.DB_PASS,props.DB_PORT,props.DB_NAME);

        RezxisAPI api = new RezxisAPI(8080);
        api.start();
        while(api.isAlive()){
            Thread.sleep(60000);
        }
    }
    
    public static ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
        @Override
        public void onResponse(IndexResponse indexResponse) {
            return;
        }

        @Override
        public void onFailure(Exception e) {
            e.printStackTrace();
        }
    };
    
    static {
        RequestOptions.Builder build = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = build.build();
    }
}
