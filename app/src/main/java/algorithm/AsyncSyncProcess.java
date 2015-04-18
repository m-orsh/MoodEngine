package algorithm;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import com.example.app.MainActivity;

import java.util.ArrayList;
import java.util.List;

import network.AsyncUploadAnalyzer;
import network.GetGrooveSharkSessionCountry;
import network.SendLibraryMetadata;

//This class is used to modify songs marked as perfect for a mood in "tabulist" style when a mood gets modified.
//Songs will follow that mood around so they stay within selection range.

public class AsyncSyncProcess extends AsyncTask<String, String, Boolean> {

    private ContentResolver contentResolver;
    private Context context;
    // constructor
    public AsyncSyncProcess(ContentResolver contentResolver, Context context) {
        this.contentResolver = contentResolver;
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(String... params) {

        MainActivity.dbhandler.setAllSongsInactive();

        Cursor mCursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION},
                MediaStore.Audio.Media.IS_MUSIC + " == 1",
                null,
                MediaStore.Audio.Media._ID + " ASC");
        //Sync files
        MainActivity.dbhandler.syncActiveSongs(mCursor);


        //Deal with connection

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        boolean localConnection = false;
        boolean mobileConnection = false;
        if(ni!=null) {
            boolean isWiFi = ni.getType() == ConnectivityManager.TYPE_WIFI;
            boolean isMobile = ni.getType() == ConnectivityManager.TYPE_MOBILE;
            boolean isETH = ni.getType() == ConnectivityManager.TYPE_ETHERNET;
            localConnection = isWiFi||isETH;
            mobileConnection = isMobile;
        }

        System.out.println(localConnection);
        System.out.println(mobileConnection);

        if(mobileConnection||localConnection){
            System.out.println("run");
            List<Song> songList = MainActivity.dbhandler.getAllSongsOfAnalysisType(0);
            if(songList.size()!=0) {
                SendLibraryMetadata sender = new SendLibraryMetadata(songList, 0);
                EchoNestData sendEchonest = new EchoNestData(songList, contentResolver, mobileConnection, localConnection);
                if (Build.VERSION.SDK_INT >= 11) {
                    //--post GB use serial executor by default --
                    sendEchonest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    sendEchonest.execute();
                }
            }
        }
        //GetGrooveSharkSessionCountry grooveshark = new GetGrooveSharkSessionCountry();
        return true;
    }


    //MainActivity.dbhandler.addSong()
}
