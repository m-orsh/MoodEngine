package com.example.app;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import algorithm.ModificationType;
import algorithm.MoodElement;
import algorithm.Song;
import network.SendInternalAssessment;

/**
 * Created by Steven on 2015-03-16.
 */
public class LocalPlayerService extends MediaPlayerService {

    public class LocalPlayerServiceBinder extends Binder {
        LocalPlayerService getService() {
            return LocalPlayerService.this;
        }
    }

    private final IBinder mBinder = new LocalPlayerServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    protected void updatePreferences(MoodElement moodElement, Song song, String heaviness_pref, String tempo_pref, String complexity_pref){

        current_heaviness_mod = ModificationType.getModificationType(heaviness_pref); //getModPreferences(HEAVINESS_TYPE, selection_heaviness);
        current_tempo_mod = ModificationType.getModificationType(tempo_pref);//getModPreferences(TEMPO_TYPE, selection_tempo);
        current_complexity_mod = ModificationType.getModificationType(complexity_pref);//getModPreferences(COMPLEXITY_TYPE, selection_complexity);

        moodElement.UpdateAllPreferences(song, current_heaviness_mod, current_tempo_mod, current_complexity_mod, false);

        MainActivity.dbhandler.updateSong(song);
        MainActivity.dbhandler.updateMood(moodElement);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni!=null) {
            if (ni.isConnected()) {
                SendInternalAssessment sendAssessmenttoExternDB = new SendInternalAssessment(song);
                if (Build.VERSION.SDK_INT >= 11) {
                    //--post GB use serial executor by default --
                    sendAssessmenttoExternDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    sendAssessmenttoExternDB.execute();
                }
            }
        }

        System.out.println(USER_PREF);
        System.out.println(moodElement.heaviness());
        System.out.println(moodElement.tempo());
        System.out.println(moodElement.complexity());
        System.out.println(SONG_PREF);
        System.out.println(song.name());
        System.out.println(song.heaviness());
        System.out.println(song.tempo());
        System.out.println(song.complexity());
    }

    protected void setNextSong()
    {
        try {
            Cursor mCursor;
            mCursor = mContentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Audio.Media._ID + " = " + songList.get(songIter).fileid(),
                    null,
                    MediaStore.Audio.Media.TITLE + " ASC");
            mCursor.moveToNext();
            System.out.println(songList.get(songIter).name());
            System.out.println(songList.get(songIter).artist());

            mMediaPlayer.setDataSource(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA)));

            mPlayMusicFragment.mSong.setText(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            mPlayMusicFragment.mSong.setEnabled(true);
            mPlayMusicFragment.mSong.setSelected(true);
            mPlayMusicFragment.mArtist.setText(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            mPlayMusicFragment.mArtist.setEnabled(true);
            mPlayMusicFragment.mArtist.setSelected(true);
            Song song = songList.get(songIter);
            MoodElement moodElement = MainActivity.table.getMood(mMood.mood_name());

            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            try{
                ContentResolver res = getApplicationContext().getContentResolver();
                InputStream in = res.openInputStream(uri);
                Bitmap artwork = BitmapFactory.decodeStream(in);
                //String path = null;
                //if (cursor.moveToFirst()) {
                //    path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                //}

                mPlayMusicFragment.mAlbumArt.setImageBitmap(artwork);
                //Log.d(PATH, path);
            } catch(java.io.FileNotFoundException e) {
                mPlayMusicFragment.mAlbumArt.setImageResource(R.drawable.albumartdefault);
                Log.d(PATH, "");
            }

            ArrayList<ModificationType> current_assessments = MainActivity.dbhandler.getAssessment(moodElement.id(), song.id());

            if(current_assessments != null){
                assessmentExists = true;
                mAssessmentItem.setIcon(R.drawable.selectdata_icon_checked);
                current_heaviness_mod = current_assessments.get(0);
                current_tempo_mod = current_assessments.get(1);
                current_complexity_mod = current_assessments.get(2);
            }
            else
                mAssessmentItem.setIcon(R.drawable.selectdata);

            mCursor.close();

            mMediaPlayer.prepare();

        } catch (IOException e) {
           // e.printStackTrace();
        }
    }
}
