package com.genentech.util.elasticsearch;


import java.io.IOException;
import com.mashape.unirest.http.Unirest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ElasticSender {

    private static final ObjectMapper OM = new ObjectMapper();
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json";
    private final static Logger log = LoggerFactory.getLogger(ElasticSender.class);

    public static void send(String location, ElasticModel testStatus){
        try {
            Unirest.post(location)
                    .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                    .body(OM.writeValueAsString(testStatus)).asJson();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendJson(String location, JSONObject jsonObject) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(location);
        httppost.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);

        // Request parameters and other properties.
        HttpEntity httpEntity = new StringEntity(jsonObject.toString());
        httppost.setEntity(httpEntity);

        
        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        
        log.info("ElasticSearch POST Response: {}", response.getStatusLine());
    }

    public static void sendStringAsJson(String location, String jsonObject) throws IOException {

        if (location != null) {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(location);
            httppost.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);

            // Request parameters and other properties.
            HttpEntity httpEntity = new StringEntity(jsonObject);
            httppost.setEntity(httpEntity);

            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            
            log.info("ElasticSearch POST Response: {}", response.getStatusLine());
            
            
        }
    }
}