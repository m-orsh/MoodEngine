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

import algorithm.Preference;
import algorithm.Song;

public class AsyncJSONParser extends AsyncTask<String, String, JSONObject> {
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    List<NameValuePair> postparams= new ArrayList<NameValuePair>();
    String URL=null;
    String method = null;
    // constructor
    public AsyncJSONParser(String url, String method, List<NameValuePair> params) {
        URL=url;
        postparams=params;
        this.method = method;
    }
    @Override
    // function get json from url
    // by making HTTP POST or GET mehtod
    protected  JSONObject doInBackground(String... params) {
 
        // Making HTTP request
        try {
 
            // check for request method
            if(method == "POST"){
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(URL);
                httpPost.setEntity(new UrlEncodedFormEntity(postparams,"UTF-8"));

                HttpResponse httpResponse = client.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
 
            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient client = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(postparams, "UTF-8");
                System.out.println(paramString);
                URL += "?" + paramString;
                HttpGet httpGet = new HttpGet(URL);

                HttpResponse httpResponse = client.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }          
 
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
            JSONArray songs = jObj.getJSONArray("songs");
            for(int iter = 0; iter<songs.length();iter++) {
                JSONObject song = songs.getJSONObject(iter);
                MainActivity.dbhandler.updateSongPrefsFromNameArtist(new Song(song.getString("name"), song.getString("artist"),
                        new Preference(song.getDouble("heaviness"), song.getDouble("tempo"), song.getDouble("complexity"))), song.getInt("analysis_state"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return JSON String
        return jObj;
 
    }

    /*protected void onPostExecute(JSONObject result) {
        System.out.println("Got initial response");
        try {


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}
