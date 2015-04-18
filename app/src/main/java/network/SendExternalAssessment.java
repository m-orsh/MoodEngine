package network;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import algorithm.MoodElement;
import algorithm.Song;


public class SendExternalAssessment extends AsyncTask<String, String, Boolean> {
    Song song;
    MoodElement mood;
    int H_mod;
    int T_mod;
    int C_mod;

    public SendExternalAssessment(Song song, MoodElement mood, int H_mod, int T_mod, int C_mod){
        this.song = song;
        this.mood = mood;
        this.H_mod = H_mod;
        this.T_mod = T_mod;
        this.C_mod = C_mod;
    }
    protected  Boolean doInBackground(String... params) {
        System.out.println("sending internal assessment to external DB");
        JSONObject data = new JSONObject();
        try {
            data.put("type", "external").put("mood_values", new JSONObject().put("heaviness", Double.toString(mood.heaviness())).put("tempo", Double.toString(mood.tempo())).put("complexity", Double.toString(mood.complexity())))
                    .put("id", song.id()).put("assessment", new JSONObject().put("heaviness", Integer.toString(H_mod)).put("tempo", Integer.toString(T_mod)).put("complexity", Integer.toString(C_mod)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        makeRequest(data);
        return true;
    }

    private void makeRequest(JSONObject data){

        String url = "http://www.moodengine.net/json_assessment.php";

        try {
            System.out.println("getting recommendation from extern DB");
            HttpPost request = new HttpPost(url);

            System.out.println("SENT: " + data.toString());

            List<NameValuePair> newlist = new ArrayList<NameValuePair>();

            newlist.add(new BasicNameValuePair("assess", data.toString()));
            SendAssessmentRequest jparser = new SendAssessmentRequest(url, "POST", newlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
