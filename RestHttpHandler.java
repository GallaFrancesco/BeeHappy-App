package ami.beehappy.beehappy;
// dependency compiled from maven (build.gradle app module)
import android.content.Context;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

// this class manages the connection to the server
// it performs http requests using a restful api
// it receives json strings which contain the information
public class RestHttpHandler {
    private String BASE_URL = "http://192.168.2.131:8080";
    private String defaultBaseUrl = "http://192.168.2.131:8080";
    private AsyncHttpClient client;

    public RestHttpHandler () {
        this.client = new AsyncHttpClient();
    }

    // perform an HTTP GET request to the server
    public void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    // perform an HTTP POST request to the server
    public void put(String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.post(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    // builds baseurl+relative (parameters) url to perform requests
    private String getAbsoluteUrl(String relativeUrl) {
        return this.BASE_URL + relativeUrl;
    }

    // setter, useful to give server location
    public void setBASE_URL(String url) {
        this.BASE_URL = url;
    }

    public void restoreBASE_URL(){
        this.BASE_URL = this.defaultBaseUrl;
    }

    public String getBASE_URL(){ return this.BASE_URL; }

}
