package org.tests.accounts;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import sun.misc.IOUtils;

import java.io.IOException;

/**
 * Created by amd on 27.05.2016.
 */
public class Poster {

    String url;

    public Poster(String url){
        this.url = url;
    }

    public String postJson(String Json2post) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead

        try{
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(Json2post);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);

            //new String;
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            return responseString;

            // handle response here...
        } finally {
            httpClient.getConnectionManager().shutdown(); //Deprecated
        }
    }
}
