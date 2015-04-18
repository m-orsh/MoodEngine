package network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.app.ExternalPlayerFragment;
import com.example.app.MainActivity;
import com.example.app.PlayMusicFragment;

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
import java.util.Collections;
import java.util.List;

import algorithm.ModificationParams;
import algorithm.MoodElement;

import android.view.View;
import android.view.ViewGroup;
import algorithm.Preference;
import algorithm.Song;

//Used for communication with external DB, sort of deprecated.

public class GetExternalRecommendationRequest extends AsyncTask<String, String, JSONObject> {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    List<NameValuePair> postparams= new ArrayList<NameValuePair>();
    String URL=null;
    String method = null;
    MoodElement mood;
    Context context = null;
    ArrayList<Song> sList;
    ProgressDialog progress;
    boolean duringPlayer = false;
    ExternalPlayerFragment fragment;
    // constructor
    public GetExternalRecommendationRequest(String url, String method, List<NameValuePair> params, MoodElement mood, Context context, ProgressDialog progress) {
        URL=url;
        postparams=params;
        this.method = method;
        this.mood = mood;
        this.context = context;
        this.sList = new ArrayList<Song>();
        this.progress = progress;
    }

    public GetExternalRecommendationRequest(String url, String method, List<NameValuePair> params, MoodElement mood, ProgressDialog progress, ExternalPlayerFragment fragment) {
        URL=url;
        postparams=params;
        this.method = method;
        this.mood = mood;
        this.sList = new ArrayList<Song>();
        this.fragment = fragment;
        this.progress = progress;
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
                sList.add(new Song(song.getInt("id"),song.getString("name"),song.getString("artist"),song.getDouble("heaviness"),song.getDouble("tempo"),song.getDouble("complexity"),0,song.getInt("duration"),song.getString("grooveshark_url"), song.getInt("analysis_state")));
            }
            /*JSONArray songs = jObj.getJSONArray("songs");
            for(int iter = 0; iter<songs.length();iter++) {
                JSONObject song = songs.getJSONObject(iter);
                MainActivity.dbhandler.updateSongPrefsFromNameArtist(new Song(song.getString("name"), song.getString("artist"),
                        new Preference(song.getDouble("heaviness"), song.getDouble("tempo"), song.getDouble("complexity"))), song.getInt("analysis_state"));
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return JSON String
        return jObj;

    }

    protected void onPostExecute(JSONObject result) {
        if(context!=null) {
            ExternalPlayerFragment playFragment = new ExternalPlayerFragment(sList, mood);
            ((MainActivity) context).switchToFragment(playFragment);
            progress.dismiss();
        }else{
            fragment.mExternalSongList.addAll(sList);
            List<ModificationParams> newlist = new ArrayList<ModificationParams>();
            for(int x=0;x<sList.size();x++) {
                ModificationParams newone = new ModificationParams();
                newlist.add(newone);
            }
            fragment.assessmentList.addAll(newlist);
            fragment.updateUIFor(sList.get(0));
            progress.dismiss();
        }
    }
}
