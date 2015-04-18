package network;

import android.os.AsyncTask;

import com.example.app.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

//Used for communication with external DB, sort of deprecated.

public class AsyncGroovesharkRequest extends AsyncTask<String, String, JSONObject> {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    String params = null;
    StringEntity postparams;
    String URL=null;
    String method = null;
    String requesttype = null;
    // constructor
    public AsyncGroovesharkRequest(String url, String method, String params, String requesttype) {
        URL = url;
        this.method = method;
        this.params = params;
        this.requesttype = requesttype;
        // function get json from url
        // by making HTTP POST or GET mehtod
    }

    @Override
    protected  JSONObject doInBackground(String... para) {
        // Making HTTP request
        try {
            this.postparams = new StringEntity(this.params);
            // check for request method
            if(method == "POST"){
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(URL);
                httpPost.setEntity(postparams);

                HttpResponse httpResponse = client.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }/*else if(method == "GET"){
                // request method is GET
                DefaultHttpClient client = new DefaultHttpClient();
                String paramString = params;
                System.out.println(paramString);
                URL += "?" + paramString;
                HttpGet httpGet = new HttpGet(URL);

                HttpResponse httpResponse = client.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }*/

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // try parse the string to a JSON object
        try {
            System.out.println("JSON: " + json);
            jObj = new JSONObject(json);

            System.out.println("got grooveshark response");
            if(requesttype.equals("country")){
                MainActivity.groovesharkCountryID = jObj.get("result").toString();
                System.out.println(MainActivity.groovesharkCountryID);
            }
            else if(requesttype.equals("session")){
                MainActivity.groovesharkSessionID = jObj.getJSONObject("result").get("sessionID").toString();
                System.out.println(MainActivity.groovesharkSessionID);
            }
            else if(requesttype.equals("stream")){
                //System.out.println(jObj)
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*} catch (JSONException e) {
            e.printStackTrace();
        }*/

        return jObj;
    }
}
