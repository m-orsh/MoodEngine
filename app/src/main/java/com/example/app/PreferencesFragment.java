package com.example.app;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import algorithm.AsyncDBAddsongs;
import algorithm.MoodElement;
import algorithm.MoodTable;
import algorithm.Preference;
import network.AsyncUploadAnalyzer;

/**
 * Created by Steven on 24/07/14.
 */
public class PreferencesFragment extends Fragment {
    SeekBar tempoSeekbar, complexitySeekbar, heavinessSeekbar;
    Button submitButton;

    public PreferencesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("Preferences");
        View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);
        heavinessSeekbar = (SeekBar)rootView.findViewById(R.id.heaviness_seekbar);
        tempoSeekbar = (SeekBar)rootView.findViewById(R.id.tempo_seekbar);
        complexitySeekbar = (SeekBar)rootView.findViewById(R.id.complexity_seekbar);
        submitButton = (Button)rootView.findViewById(R.id.submitprefbutton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.userpref = new Preference(heavinessSeekbar.getProgress(),tempoSeekbar.getProgress(),complexitySeekbar.getProgress());
                MainActivity.table = new MoodTable( MainActivity.userpref );
                for(MoodElement mood : MainActivity.table.getAllMoods()) {
                    int moodID = MainActivity.dbhandler.addMood(mood);
                    MainActivity.table.getMood(mood.mood_name()).setID(moodID);
                }
                Cursor mCursor = getActivity().getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[] {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION},
                        MediaStore.Audio.Media.IS_MUSIC + " == 1",
                        null,
                        MediaStore.Audio.Media._ID + " ASC");
                //Deal with connection
                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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


                //Adds songs to DB on a second thread, this will create another thread to analyze songs as well
                AsyncDBAddsongs addSongs = new AsyncDBAddsongs(mCursor, getActivity().getContentResolver(), localConnection, mobileConnection);
                if (Build.VERSION.SDK_INT >= 11) {
                    //--post GB use serial executor by default --
                    addSongs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    addSongs.execute();
                }

                ((MainActivity) v.getContext()).switchToFragment(new MoodSelectFragment(), false);
            }
        });
        return rootView;
    }
}
