package network;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.example.app.ExternalPlayerFragment;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import algorithm.MoodElement;
import algorithm.Song;

//Used for communication with DB, sort of deprecated.

public class GetExternalRecommendation {

    public GetExternalRecommendation(MoodElement mood, Context context, ProgressDialog progress){
        System.out.println("sending songs to external DB");
        double range = 2 * Math.pow(0.98, mood.range_counter ) + 1;
        JSONObject data = new JSONObject();
        try {
             data.put("heaviness",Double.toString(mood.heaviness())).put("tempo",Double.toString(mood.tempo())).put("complexity",Double.toString(mood.complexity())).put("range",Double.toString(range));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        makeRequest(data, mood, context, progress);
    }

    public GetExternalRecommendation(MoodElement mood, ProgressDialog progress, ExternalPlayerFragment fragment){
        System.out.println("sending songs to external DB");
        double range = 2 * Math.pow(0.98, mood.range_counter ) + 1;
        JSONObject data = new JSONObject();
        try {
            data.put("heaviness",Double.toString(mood.heaviness())).put("tempo",Double.toString(mood.tempo())).put("complexity",Double.toString(mood.complexity())).put("range",Double.toString(range));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        makeRequest(data, mood, progress, fragment);
    }

    private void makeRequest(JSONObject data, MoodElement mood, Context context, ProgressDialog progress){

        String url = "http://www.moodengine.net/json_recommendation.php";

        try {
            System.out.println("getting recommendation from extern DB");
            HttpPost request = new HttpPost(url);

            System.out.println("SENT: " + data.toString());

            List<NameValuePair> newlist = new ArrayList<NameValuePair>();

            newlist.add(new BasicNameValuePair("recommend", data.toString()));
            GetExternalRecommendationRequest jparser = new GetExternalRecommendationRequest(url, "POST", newlist, mood, context, progress);

            if (Build.VERSION.SDK_INT >= 11) {
                //--post GB use serial executor by default --
                jparser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                jparser.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeRequest(JSONObject data, MoodElement mood, ProgressDialog progress, ExternalPlayerFragment fragment){

        String url = "http://www.moodengine.net/json_recommendation.php";

        try {
            System.out.println("getting recommendation from extern DB");
            HttpPost request = new HttpPost(url);

            System.out.println("SENT: " + data.toString());

            List<NameValuePair> newlist = new ArrayList<NameValuePair>();

            newlist.add(new BasicNameValuePair("recommend", data.toString()));
            GetExternalRecommendationRequest jparser = new GetExternalRecommendationRequest(url, "POST", newlist, mood, progress, fragment);

            if (Build.VERSION.SDK_INT >= 11) {
                //--post GB use serial executor by default --
                jparser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                jparser.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
