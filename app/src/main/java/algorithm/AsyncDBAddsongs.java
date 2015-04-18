package algorithm;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;

import com.example.app.MainActivity;

import network.SendLibraryMetadata;

//Adds songs to the database initially when first starting the app.

public class AsyncDBAddsongs extends AsyncTask<String, String, Boolean> {

    static Cursor mCursor = null;
    static ContentResolver contentResolver = null;
    boolean isMobileConnected = false;
    boolean isLocalConnected = false;

    // constructor
    public AsyncDBAddsongs(Cursor cursor, ContentResolver contentResolver, boolean isLocalConnected, boolean isMobileConnected) {
        this.mCursor = cursor;
        this.contentResolver = contentResolver;
        this.isLocalConnected = isLocalConnected;
        this.isMobileConnected = isMobileConnected;
    }
    @Override
    protected  Boolean doInBackground(String... params) {

        //start analyzing these songs (asynchronously)

        //send songs out to external DB (receive anything that's already analyzed back)


        System.out.println("Starting DB add");
        MainActivity.dbhandler.addInitialSongs(mCursor);

        if(isLocalConnected||isMobileConnected) {
            SendLibraryMetadata sender = new SendLibraryMetadata(mCursor, 0);

            System.out.println("Starting Echonest querying");
            EchoNestData getter = new EchoNestData(mCursor, contentResolver, isMobileConnected, isLocalConnected);
            if (Build.VERSION.SDK_INT >= 11) {
                //--post GB use serial executor by default --
                getter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                getter.execute();
            }
        }

        //add the songs to the DB in the meantime
        //start the analysis process
        return true;
    }
}
