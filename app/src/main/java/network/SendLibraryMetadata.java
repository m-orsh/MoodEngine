package network;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import algorithm.Song;

//Used for communication with DB, sort of deprecated.

public class SendLibraryMetadata {

	public SendLibraryMetadata(Cursor song_list, int analysisFlag){
        System.out.println("sending songs to external DB");
		JSONArray list = new JSONArray();
        song_list.moveToFirst();
		String jsonText = null;
        try {
                while (!song_list.isAfterLast()) {
                    list.put(new JSONObject()
                                    .put("name", song_list.getString(song_list.getColumnIndex(MediaStore.Audio.Media.TITLE)))
                                    .put("artist", song_list.getString(song_list.getColumnIndex(MediaStore.Audio.Media.ARTIST)))
                                    .put("duration", song_list.getString(song_list.getColumnIndex(MediaStore.Audio.Media.DURATION)))
                    );
                    song_list.moveToNext();
                }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //jsonText = list.toString();
        //song_list.close();
        makeRequest(list,analysisFlag);
	}

    public SendLibraryMetadata(List<Song> song_list, int analysisFlag){
        System.out.println("sending songs to external DB");
        JSONArray list = new JSONArray();
        String jsonText = null;
        try {
            for (Song song:song_list) {
                list.put(new JSONObject()
                                .put("name", song.name())
                                .put("artist", song.artist())
                                .put("duration", song.duration())
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //jsonText = list.toString();
        //song_list.close();
        makeRequest(list,analysisFlag);
    }

    private void makeRequest(JSONArray list, int analysisFlag){

            String url = "http://www.moodengine.net/json_recv.php";

            try {
                System.out.println("executing");
                HttpPost request = new HttpPost(url);
                JSONObject metadata = new JSONObject();

                //Place JSON array into a JSONObject
                metadata.put("analysis", analysisFlag).put("songs",list);

                System.out.println("SENT: " + metadata.toString());

                List<NameValuePair> newlist = new ArrayList<NameValuePair>();

                newlist.add(new BasicNameValuePair("song", metadata.toString()));
                AsyncJSONParser jparser = new AsyncJSONParser(url, "POST", newlist);
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
