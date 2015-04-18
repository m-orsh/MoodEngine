package network;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import algorithm.Song;


public class SendInternalAssessment extends AsyncTask<String, String, Boolean> {
    Song song;

    public SendInternalAssessment(Song song){
        this.song = song;
    }
    protected  Boolean doInBackground(String... params) {
        System.out.println("sending internal assessment to external DB");
        JSONObject data = new JSONObject();
        try {
            data.put("type","internal").put("song_values",new JSONObject().put("heaviness",Double.toString(song.heaviness())).put("tempo",Double.toString(song.tempo())).put("complexity",Double.toString(song.complexity())))
                    .put("name",song.name()).put("artist", song.artist()).put("duration", Integer.toString(song.duration()));
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
