package com.example.app;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import algorithm.MoodElement;
import algorithm.Song;

/**
 * Created by Steven on 2015-03-16.
 */
public class ExternalPlayerService extends MediaPlayerService {
    public class ExternalPlayerServiceBinder extends Binder {
        MediaPlayerService getService() {
            return ExternalPlayerService.this;
        }
    }

    private IBinder mBinder = new ExternalPlayerServiceBinder();

    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    protected void updatePreferences(MoodElement moodElement, Song song, String heaviness_pref, String tempo_pref, String complexity_pref){
         //TODO(Steven M): add code to update preferences for the external case.
    }

    protected void setNextSong(){
        //TODO(Steven M): add code to get next song for the external case.
    }

}
