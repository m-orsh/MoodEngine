package com.example.app;

import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import algorithm.AsyncTabuMod;
import algorithm.ModificationType;
import algorithm.MoodElement;
import algorithm.Song;

/**
 * Created by Adnan on 9/27/14.
 */
public abstract class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
                                                                    MediaPlayer.OnErrorListener, ControllerView.MediaPlayerControl {
    //private Cursor mCursor;
    protected MoodElement mMood;
    protected MenuItem mAssessmentItem;
    protected ArrayList<Song> songList;
    protected int songIter = 0;

    protected boolean manualPrefChoice = false;
    protected boolean assessmentExists = false;
    protected boolean dialogOpened = false;
    protected boolean dialogActive = false;
    protected boolean clickedNext = false;
    protected boolean songComplete = false;

    protected ModificationType current_heaviness_mod = null;
    protected ModificationType current_tempo_mod = null;
    protected ModificationType current_complexity_mod = null;

    protected static final String ACTION_PLAY = "com.example.action.PLAY";
    protected PlayMusicFragment mPlayMusicFragment;
    protected ContentResolver mContentResolver;
    protected Dialog dialog;

    public MediaPlayer mMediaPlayer = null;

    public boolean mediaPlayerPrepared;

    protected abstract void updatePreferences(MoodElement moodElement, Song song, String heaviness_pref, String tempo_pref, String complexity_pref);
    protected abstract void setNextSong();

    public void startPlayer(PlayMusicFragment fragment ){
        mPlayMusicFragment = fragment;

        mAssessmentItem = mPlayMusicFragment.mMenu.findItem(R.id.action_feedback);
        mMood = mPlayMusicFragment.MoodElement();
        songList = mPlayMusicFragment.SongList();
        mContentResolver = mPlayMusicFragment.getActivity().getContentResolver();

        mPlayMusicFragment.mController.setMediaPlayer(this);
        mPlayMusicFragment.mController.setAnchorView((FrameLayout)mPlayMusicFragment.getActivity().findViewById(R.id.controller_container));

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);

        mPlayMusicFragment.mAlbumArt.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                manualPrefChoice = true;
                showPreferenceSelectionDialog();
                return true;
            }
        });

        setNextSong();

        //TODO: Maybe need this code for adding a mini mp3 player to notifications
        /*PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification();
        notification.tickerText = mPlayMusicFragment.mSong.getText().toString();
        //notification.icon = R.drawable.play0;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(getApplicationContext(), "Mood Engine Player",
                "Playing: " + notification.tickerText.toString(), pi);
        startForeground(NOTIFICATION_ID, notification);*/
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayerPrepared = false;
        mMediaPlayer.reset();
        return true;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
    }

    public void onPrepared(final MediaPlayer mediaPlayer) {

        mMediaPlayer.start();
        mediaPlayerPrepared = true;
        mPlayMusicFragment.mSong.setSelected(true);

        mPlayMusicFragment.mController.setPrevNextListeners(
                //Next Listener
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //if(!PrefChosen) {
                        MoodElement moodElement = MainActivity.table.getMood(mMood.mood_name());
                        Song song = songList.get(songIter);
                        clickedNext = true;
                        if (((double) mediaPlayer.getCurrentPosition() / (double) mediaPlayer.getDuration()) < 0.8) {
                            if ( !assessmentExists ) {
                                showPreferenceSelectionDialog();
                                dialogOpened = true;
                                return;
                            }else{
                                updatePreferences(moodElement, song, current_heaviness_mod.mod_name(), current_tempo_mod.mod_name(), current_complexity_mod.mod_name());
                            }
                        } else {
                            if( !assessmentExists ) {
                                updatePreferences(moodElement, song, PERFECT_MOD, PERFECT_MOD, PERFECT_MOD);
                                MainActivity.dbhandler.updateAssessment(moodElement.id(), song.id(), current_heaviness_mod, current_tempo_mod, current_complexity_mod);
                            }else{
                                updatePreferences(moodElement, song, current_heaviness_mod.mod_name(), current_tempo_mod.mod_name(), current_complexity_mod.mod_name());
                            }
                        }
                        //}else{
                        songIter++;
                        if(songIter == songList.size()){
                            List<Song> newList = MainActivity.dbhandler.getRecommendation(mMood.mood_name());
                            long seed = System.nanoTime();
                            Collections.shuffle(newList, new Random(seed));
                            if(newList.size() > 10){
                                newList = newList.subList(0,10);
                            }
                            songList.addAll(newList);
                        }
                        playNextPrevious();
                        //}
                    }
                },
                //Prev Listener
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(songIter == 0){
                            Toast toast = Toast.makeText(getApplicationContext(), "This is the start of the playlist.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            mMediaPlayer.seekTo(0);
                            return;
                        }

                        songIter--;

                        playNextPrevious();
                    }
                }
        );
        mPlayMusicFragment.nextPrevListenerSet = true;

/*   TODO: **** HAVE TO CHECK IF PLAYMUSICFRAGMENT WILL RUN HANDLER WHEN FRAGMENT RESUMES ****      */
        mPlayMusicFragment.mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPlayMusicFragment.mController.setEnabled(true);
                mPlayMusicFragment.mController.show(0);
            }
        });


    }

    public void onCompletion(final MediaPlayer mediaPlayer){

        songComplete = true;

        if(!dialogActive) {

            MoodElement moodElement = MainActivity.table.getMood(mMood.mood_name());
            Song song = songList.get(songIter);

            if(!assessmentExists) {
                updatePreferences(moodElement, song, PERFECT_MOD, PERFECT_MOD, PERFECT_MOD);
                MainActivity.dbhandler.updateAssessment(moodElement.id(), song.id(), current_heaviness_mod, current_tempo_mod, current_complexity_mod);
            }else{
                updatePreferences(moodElement, song, current_heaviness_mod.mod_name(), current_tempo_mod.mod_name(), current_complexity_mod.mod_name());
            }
            songIter++;
            if(songIter == songList.size()){
                List<Song> newList = MainActivity.dbhandler.getRecommendation(mMood.mood_name());
                long seed = System.nanoTime();
                Collections.shuffle(newList, new Random(seed));
                if(newList.size() > 10){
                    newList = newList.subList(0,10);
                }
                songList.addAll(newList);
            }
            playNextPrevious();
        }
    }

    private void playNextPrevious(){
        mediaPlayerPrepared = false;
        mMediaPlayer.reset();

        dialogOpened = false;
        dialogActive = false;
        assessmentExists = false;
        songComplete = false;
        clickedNext = false;

        current_heaviness_mod = null;
        current_complexity_mod = null;
        current_tempo_mod = null;

        setNextSong();
    }

    public Dialog showPreferenceSelectionDialog(){
        dialog = new Dialog(mPlayMusicFragment.getActivity());
        dialog.setContentView(R.layout.dialog_song_feedback);
        dialog.setTitle(FEEDBACK);

        final RadioGroup heavinessRadioGroup = (RadioGroup) dialog.findViewById(R.id.heaviness_radio_group);
        final RadioGroup tempoRadioGroup = (RadioGroup) dialog.findViewById(R.id.tempo_radio_group);
        final RadioGroup complexityRadioGroup = (RadioGroup) dialog.findViewById(R.id.compexity_radio_group);
        if(assessmentExists) {
            //Show assessed values if the song is already assessed, otherwise radio button to perfect
            if (current_heaviness_mod == ModificationType.TOO_MUCH) {
                ((RadioButton) (heavinessRadioGroup.getChildAt(2))).setChecked(true);
            } else if (current_heaviness_mod == ModificationType.TOO_LOW) {
                ((RadioButton) (heavinessRadioGroup.getChildAt(0))).setChecked(true);
            }
            if (current_tempo_mod == ModificationType.TOO_MUCH) {
                ((RadioButton) (tempoRadioGroup.getChildAt(2))).setChecked(true);
            } else if (current_tempo_mod == ModificationType.TOO_LOW) {
                ((RadioButton) (tempoRadioGroup.getChildAt(0))).setChecked(true);
            }
            if (current_complexity_mod == ModificationType.TOO_MUCH) {
                ((RadioButton) (complexityRadioGroup.getChildAt(2))).setChecked(true);
            } else if (current_complexity_mod == ModificationType.TOO_LOW) {
                ((RadioButton) (complexityRadioGroup.getChildAt(0))).setChecked(true);
            }
        }
        Button submitButton = (Button) dialog.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selection_heaviness = ((RadioButton) dialog.findViewById(heavinessRadioGroup.getCheckedRadioButtonId())).getText().toString();
                String selection_tempo = ((RadioButton) dialog.findViewById(tempoRadioGroup.getCheckedRadioButtonId())).getText().toString();
                String selection_complexity = ((RadioButton) dialog.findViewById(complexityRadioGroup.getCheckedRadioButtonId())).getText().toString();

                MoodElement moodElement = MainActivity.table.getMood(mMood.mood_name());
                Song song = songList.get(songIter);

                if(!manualPrefChoice)
                    updatePreferences(moodElement, song, selection_heaviness, selection_tempo, selection_complexity);
                else{
                    current_heaviness_mod = ModificationType.getModificationType(selection_heaviness); //getModPreferences(HEAVINESS_TYPE, selection_heaviness);
                    current_tempo_mod = ModificationType.getModificationType(selection_tempo);//getModPreferences(TEMPO_TYPE, selection_tempo);
                    current_complexity_mod = ModificationType.getModificationType(selection_complexity);//getModPreferences(COMPLEXITY_TYPE, selection_complexity);

                    manualPrefChoice = false;
                }

                MainActivity.dbhandler.updateAssessment(moodElement.id(), song.id(), current_heaviness_mod, current_tempo_mod, current_complexity_mod);
                assessmentExists = true;

                dialog.dismiss();
                dialogActive = false;


                if(songComplete || clickedNext){
                    songIter++;
                    if(songIter == songList.size()){
                        List<Song> newList = MainActivity.dbhandler.getRecommendation(mMood.mood_name());
                        long seed = System.nanoTime();
                        Collections.shuffle(newList, new Random(seed));
                        if(newList.size() > 10){
                            newList = newList.subList(0,10);
                        }
                        songList.addAll(newList);
                    }
                    playNextPrevious();
                }
                else
                    mAssessmentItem.setIcon(R.drawable.selectdata_icon_checked);
            }
        });

        dialog.show();
        dialogActive = true;

        if(dialogOpened)
        {
            dialogOpened = false;
            clickedNext = false;
        }
        return dialog;
    }

    public double getCurrentSongHeviness() {
        return songList.get(songIter).heaviness();
    }

    public double getCurrentSongeTempo() {
        return songList.get(songIter).tempo();
    }

    public double getCurrentSongComplexity() {
        return songList.get(songIter).complexity();
    }

    //MediaPlayerControl methods
    public void start() {
        mMediaPlayer.start();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public int getDuration() {
        //This check is to make sure the mediaplayer is not in the preparing state before calling getDuration
        if (mediaPlayerPrepared && mPlayMusicFragment.isActive) {
            return mMediaPlayer.getDuration();
        } else {
            return -1;
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayerPrepared && mPlayMusicFragment.isActive) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return -1;
        }
    }

    public void seekTo(int i){
        mMediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        if (mPlayMusicFragment.isActive && mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        } else {
            return false;
        }
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


    private final String PERFECT_MOD = "Perfect";

    protected final String USER_PREF = "Userpref";
    protected final String SONG_PREF = "Songpref";

    private final String FEEDBACK = "Feedback";

    protected final String PATH = "PATH";
}


