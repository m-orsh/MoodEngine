package network;

import android.os.AsyncTask;
import android.os.Build;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import algorithm.Song;

//Used for communication with DB, sort of deprecated.

public class SendAnalyzedLibraryMetadata {

    public SendAnalyzedLibraryMetadata(List<Song> song_list, int analysisFlag, boolean async){
        System.out.println("sending songs to external DB");
        JSONArray list = new JSONArray();
        String jsonText = null;
        try {
                for (Song song:song_list) {
                    list.put(new JSONObject()
                                    .put("name", song.name())
                                    .put("artist", song.artist())
                                    .put("duration", song.duration())
                                    .put("heaviness", song.heaviness())
                                    .put("complexity", song.complexity())
                                    .put("tempo", song.tempo())
                    );
                }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //jsonText = list.toString();

        makeRequest(list,analysisFlag, async);
    }

    private void makeRequest(JSONArray list, int analysisFlag, boolean async){

        String url = "http://www.moodengine.net/json_recv.php";

        try {
            System.out.println("sending echonest stuff to extern DB");
            HttpPost request = new HttpPost(url);
            JSONObject metadata = new JSONObject();

            //Place JSON array into a JSONObject
            metadata.put("analysis", analysisFlag).put("songs",list);

            System.out.println("SENT: " + metadata.toString());

            List<NameValuePair> newlist = new ArrayList<NameValuePair>();

            newlist.add(new BasicNameValuePair("song", metadata.toString()));
            if(async==false) {
                JSONParser jparser = new JSONParser(url, "POST", newlist);
            }else{
                AsyncJSONParser jparser = new AsyncJSONParser(url, "POST", newlist);
                if (Build.VERSION.SDK_INT >= 11) {
                    //--post GB use serial executor by default --
                    jparser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    jparser.execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
