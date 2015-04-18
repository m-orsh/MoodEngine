package network;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.echonest.api.v4.Track;
import com.example.app.MainActivity;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import algorithm.Preference;
import algorithm.Song;
import network.SendAnalyzedLibraryMetadata;

//Upload songs for analysis that haven't been analyzed.

public class AsyncUploadAnalyzer extends AsyncTask<String, String, Boolean> {

    static ContentResolver contentResolver = null;


    // constructor
    public AsyncUploadAnalyzer(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }
    @Override
    protected  Boolean doInBackground(String... params) {


        //start uploading these songs (asynchronously)
        System.out.println("Starting upload analysis");
        List<Song> songlist = MainActivity.dbhandler.getAllSongsOfAnalysisType(0);
        int iter = 0;
        while (songlist.size()!=0&&MainActivity.Uploadflag) {
            System.out.println("#of songs to upload: " + songlist.size());
            //File file = new File(path);
            Cursor cursor = contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.AudioColumns.DATA},
                    MediaStore.Audio.Media._ID + "=?",
                    new String[]{songlist.get(0).fileid()},
                    null);

            if(cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                String filePath = cursor.getString(column_index);
                File file = new File(filePath);
                try {
                    List<Song> songlist2 = new ArrayList<Song>();
                    Song song = EchonestUpload(file, filePath, songlist.get(0));
                    songlist2.add(song);
                    SendAnalyzedLibraryMetadata sendUploadAnalyzedSongtoExternalDB = new SendAnalyzedLibraryMetadata(songlist2,1,false);
                }
                catch(EchoNestException e){
                    System.out.println(e);
                }
            }
            iter++;
            System.out.println("Song upload #: " + iter);
            cursor.close();
            songlist = MainActivity.dbhandler.getAllSongsOfAnalysisType(0);
        }
        System.out.println("Done upload analysis");
        //start the analysis process
        return true;
    }

    public Song EchonestUpload(File file, String filePath, Song song) throws EchoNestException {
        try {
            EchoNestAPI en = new EchoNestAPI("BYOD8UP4TVVYZ1GSA");
            Track track = en.uploadTrack(file);
            track.waitForAnalysis(30000);
            if (track.getStatus() == Track.AnalysisStatus.COMPLETE) {
                System.out.println(song.name());
                song.setAnalysisState(1);
                track.fetchBucket("audio_summary");
                double valence = track.getDouble("audio_summary.valence");
                double acousticness = track.getDouble("audio_summary.acousticness");
                double energy = track.getEnergy();
                double tempo1 = track.getTempo();
                double danceability = track.getDanceability();
                double heaviness=0;
                if(valence!=valence&&acousticness!=acousticness){
                    heaviness = energy*10;      //heavy songs are less "happy" (low valence), less acoustic, and very energetic
                }
                else{
                    heaviness = (((1-valence)*10)+((1-acousticness)*10)+energy*10)/3;      //heavy songs are less "happy" (low valence), less acoustic, and very energetic
                }
                System.out.println(heaviness);
                double tempo = (((tempo1-60)/14)+(energy*10))/2;                                  //fast songs have a high tempo but even songs without a high tempo can seem fast if they have a lot of energy
                if(tempo<0.0){
                    tempo = 0.0;
                }
                else if(tempo>10) {
                    tempo = 10;
                }
                double complexity = (1-danceability)*10;                                          //complex songs change a lot over the course of the song, and are difficult to dance to.
                //Checks for NaN, which happens if the song is too short or some weird cases, just set the values to something in the mid-range then.
                if(complexity!=complexity){
                    complexity=5;
                }
                if(tempo!=tempo){
                    tempo = 5;
                }
                if(heaviness!=heaviness){
                    heaviness=5;
                }
                MainActivity.dbhandler.updateSongPrefsFromID(song.fileid(),new Preference(heaviness,tempo,complexity),1);
                System.out.println("Heaviness: " + heaviness);
                System.out.println("Tempo: " + tempo);
                System.out.println("Complexity: " + complexity);
                System.out.println();
                song.setHeaviness(heaviness);
                song.setComplexity(complexity);
                song.setTempo(tempo);
                song.setAnalysisState(1);
                return song;
            }
        }
        catch(IOException e) {
            System.err.println("Trouble uploading file");
        }
        return null;
    }
}
