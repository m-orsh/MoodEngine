package algorithm;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import com.example.app.MainActivity;

import com.echonest.api.v4.CatalogUpdater;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.SongCatalog;
import com.echonest.api.v4.SongCatalogItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import network.AsyncUploadAnalyzer;
import network.SendAnalyzedLibraryMetadata;

//Sends a list of songs for echonest analysis.

public class EchoNestData extends AsyncTask<String, String, Boolean> {

    static Cursor songCursor = null;
    static ContentResolver contentResolver;
    private EchoNestAPI en;
    static String result = "";
    String method = null;
    boolean isConnectedLocal;
    boolean isConnectedMobile;
    List<Song> songList = null;
    List<String> IDList = null;
    // constructor
    public EchoNestData(Cursor params, ContentResolver contentResolver, boolean isConnectedMobile, boolean isConnectedLocal) {
        this.songCursor = params;
        this.contentResolver = contentResolver;
        this.en = new EchoNestAPI("BYOD8UP4TVVYZ1GSA");
        this.isConnectedLocal = isConnectedLocal;
        this.isConnectedMobile = isConnectedMobile;
    }

    public EchoNestData(List<Song> params, ContentResolver contentResolver, boolean isConnectedMobile, boolean isConnectedLocal) {
        this.songList = params;
        this.contentResolver = contentResolver;
        this.en = new EchoNestAPI("BYOD8UP4TVVYZ1GSA");
        this.isConnectedLocal = isConnectedLocal;
        this.isConnectedMobile = isConnectedMobile;
        this.IDList = new ArrayList<String>();
    }

    @Override
    // function get json from url
    // by making HTTP POST or GET mehtod
    protected  Boolean doInBackground(String... params) {
        try {
            EchonestMain();
        }
        catch(EchoNestException e){
            System.out.println(e);
        }
        if(isConnectedMobile||isConnectedLocal&&songCursor!=null) {
            SendAnalyzedLibraryMetadata sendEchoDatatoExternalDB = new SendAnalyzedLibraryMetadata(MainActivity.dbhandler.getAllSongsOfAnalysisType(1), 1, false);
        }
        else if(isConnectedMobile||isConnectedLocal&&songList!=null&&IDList.size()!=0){
            SendAnalyzedLibraryMetadata sendEchoDatatoExternalDB = new SendAnalyzedLibraryMetadata(MainActivity.dbhandler.getSongsFromIDList(IDList), 1, false);
        }
        if(isConnectedLocal) {
            AsyncUploadAnalyzer uploadSongs = new AsyncUploadAnalyzer(contentResolver);

            if (Build.VERSION.SDK_INT >= 11) {
                //--post GB use serial executor by default --
                uploadSongs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                uploadSongs.execute();
            }
        }
        return true;
    }

    public void EchonestMain() throws EchoNestException {
        SongCatalog tp = null;
        if(songCursor!=null) {
            songCursor.moveToFirst();
            String tpName = "MoodEngineUser-" + songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media._ID));
            SongCatalog deletethis = findTasteProfile(tpName);
            if (deletethis != null) {
                deletethis.delete();
            }
            tp = createTasteProfile(tpName, songCursor);
        }
        else if(songList!=null){
                String tpName = "MoodEngineUser-" + songList.get(0).fileid();
                SongCatalog deletethis = findTasteProfile(tpName);
                if (deletethis != null) {
                    deletethis.delete();
                }
                tp = createTasteProfile(tpName, songList);
        }
        String[] bucket = {"audio_summary"};
        java.util.List<SongCatalogItem> get = tp.read(bucket);
        for (SongCatalogItem song : get) {
            Map songdata = song.getMap("audio_summary");
            if(songdata!=null){
                double valence = Double.parseDouble(songdata.get("valence").toString());
                double acousticness = Double.parseDouble(songdata.get("acousticness").toString());
                double energy = Double.parseDouble(songdata.get("energy").toString());
                double tempo1 = Double.parseDouble(songdata.get("tempo").toString());
                double danceability = Double.parseDouble(songdata.get("danceability").toString());
                double heaviness = (((1-(valence))*10)+((1-(acousticness))*10)+energy*10)/3;      //heavy songs are less "happy" (low valence), less acoustic, and very energetic
                double tempo = (((tempo1-60)/14)+(energy*10))/2;                                  //fast songs have a high tempo but even songs without a high tempo can seem fast if they have a lot of energy
                if(tempo<0.0){
                    tempo = 0.0;
                }
                else if(tempo>10){
                    tempo = 10;
                }
                double complexity = (1-danceability)*10;                                          //complex songs change a lot over the course of the song, and are difficult to dance to.
                if(IDList!=null){
                    IDList.add(song.getID());
                }
                MainActivity.dbhandler.updateSongPrefsFromID(song.getID(),new Preference(heaviness,tempo,complexity),1);
                //System.out.println(song.getString("song_name")+" - "+song.getString("artist_name")+" Heaviness: "+heaviness+" Tempo: "+tempo+" Complexity: "+complexity);
            }
        }
        tp.delete();
    }

    public boolean addSongs(SongCatalog tp, Cursor songCursor) throws EchoNestException {
        CatalogUpdater updater = new CatalogUpdater();
        songCursor.moveToFirst();

        while (!songCursor.isAfterLast()) {
            SongCatalogItem item = new SongCatalogItem(songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            item.setArtistName(songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            item.setSongName(songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            updater.update(item);
            songCursor.moveToNext();
        }
        System.out.println("Done adding songs to catalog. Updating echonest profile.");
        String ticket = tp.update(updater);
        return tp.waitForUpdates(ticket, 30000);
    }

    public boolean addSongs(SongCatalog tp, List<Song> songList) throws EchoNestException {
        CatalogUpdater updater = new CatalogUpdater();

        for (Song song : songList) {
            SongCatalogItem item = new SongCatalogItem(song.fileid());
            item.setArtistName(song.artist());
            item.setSongName(song.name());
            updater.update(item);
        }
        System.out.println("Done adding songs to catalog. Updating echonest profile.");
        String ticket = tp.update(updater);
        return tp.waitForUpdates(ticket, 30000);
    }

    public SongCatalog createTasteProfile(String name, Cursor songCursor) throws EchoNestException {
        System.out.println("Creating Taste Profile " + name);
        SongCatalog tp = en.createSongCatalog(name);
        addSongs(tp, songCursor);
        System.out.println("Done addsongs");
        return tp;
    }

    public SongCatalog createTasteProfile(String name, List<Song> songList) throws EchoNestException {
        System.out.println("Creating Taste Profile " + name);
        SongCatalog tp = en.createSongCatalog(name);
        addSongs(tp, songList);
        System.out.println("Done addsongs");
        return tp;
    }

    public SongCatalog findTasteProfile(String name) throws EchoNestException {
        for (SongCatalog ac : en.listSongCatalogs()) {
            if (ac.getName().equals(name)) {
                return ac;
            }
        }
        return null;
    }



        //MainActivity.dbhandler.addSong()
    }
