package com.example.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import algorithm.ModificationType;
import algorithm.MoodElement;
import algorithm.Song;

/**
 * Created by Steven on 24/07/14.
 */
public class PlayMusicFragment extends Fragment implements ControllerView.MediaPlayerControl{

    public MediaPlayer mMediaPlayer;
    public ControllerView mController;
    public ImageView mAlbumArt;
    public TextView mSong;
    public TextView mArtist;
    public Menu mMenu;
    public Handler mHandler = new Handler();

    private Activity mActivity;
    private Cursor mCursor;
    private MoodElement mMood;
    private ArrayList<Song> songList;
    private boolean isPlaying = true;
    private MediaPlayerServiceConnection mServiceConnection = new MediaPlayerServiceConnection();
    private MediaPlayerService mService = null;
    private boolean mIsExternalPlayer;

    public boolean isActive = false;
    public boolean nextPrevListenerSet = false;

    public PlayMusicFragment(){}

    public PlayMusicFragment(MoodElement m, boolean isExternalPlayer) {
        mMood = m;
        mIsExternalPlayer = isExternalPlayer;
        songList = MainActivity.dbhandler.getRecommendation(mMood.mood_name());
        long seed = System.nanoTime();
        Collections.shuffle(songList, new Random(seed));
    }
    public PlayMusicFragment(MoodElement m, boolean isExternalPlayer, ArrayList<Song> sList) {
        mMood = m;
        mIsExternalPlayer = isExternalPlayer;
        songList = sList;
        long seed = System.nanoTime();
        Collections.shuffle(songList, new Random(seed));
        //Select Songs to play with this mood
    }

    public void startService(){
        Intent mIntent = new Intent(getActivity(), mIsExternalPlayer ? ExternalPlayerService.class : LocalPlayerService.class);
        getActivity().bindService(mIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopService(){
        getActivity().unbindService(mServiceConnection);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_feedback:
                mService.showPreferenceSelectionDialog();
                return true;
            case R.id.action_graphs:
                showSongStatGraphs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play_music, container, false);

        mController = new ControllerView(rootView.getContext(), false);
        mSong = (TextView) rootView.findViewById(R.id.song);
        mArtist = (TextView) rootView.findViewById(R.id.artist);
        mAlbumArt = (ImageView) rootView.findViewById(R.id.album_cover);
        mCursor = getActivity().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media._ID + " = "+songList.get(0).fileid(),
                null,
                MediaStore.Audio.Media.TITLE + " ASC");
        mCursor.moveToNext();

        startService();

        return rootView;
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        mActivity = activity;
    }

    @Override
    public void onStart(){
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle(mMood.mood_name());
        if (mController != null) {
            mController.setAnchorView((ViewGroup)getActivity().findViewById(R.id.controller_container));
            mController.setEnabled(true);
            if (!nextPrevListenerSet) {
                mController.setPrevNextListeners(null, null);
            }
            mController.show(0);
        }
        isActive = true;
        mController.updatePausePlay();
    }
    @Override
    public void onStop(){
        super.onStop();
        isActive = false;
    }
    @Override
    public void onDestroy()
    {
        stopService();
        super.onDestroy();
    }

    public void showSongStatGraphs() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_song_stat_graphs);
        dialog.setTitle("Song Stats");
        BarGraphView heavinessBarGraph = (BarGraphView) dialog.findViewById(R.id.heaviness_bar_graph);
        BarGraphView tempoBarGraph = (BarGraphView) dialog.findViewById(R.id.tempo_bar_graph);
        BarGraphView complexityBarGraph = (BarGraphView) dialog.findViewById(R.id.complexity_bar_graph);

        heavinessBarGraph.setBarValue(mService.getCurrentSongHeviness());
        tempoBarGraph.setBarValue(mService.getCurrentSongeTempo());
        complexityBarGraph.setBarValue(mService.getCurrentSongComplexity());

        dialog.show();
    }

    public MoodElement MoodElement(){
        return mMood;
    }

    public ArrayList<Song> SongList(){
        return songList;
    }

    class MediaPlayerServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = mIsExternalPlayer ? ((ExternalPlayerService.ExternalPlayerServiceBinder)service).getService() : ((LocalPlayerService.LocalPlayerServiceBinder)service).getService();
            mService.startPlayer(PlayMusicFragment.this);
        }

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    }

    //MediaPlayerControl methods
    public void start() {
        mService.mMediaPlayer.start();
    }

    public void pause() {
        mService.mMediaPlayer.pause();
    }

    public int getDuration() {
        return mService.mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mService.mMediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i){
        mService.mMediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mService.mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    public int getAudioSessionId() {
        return 0;
    }

}