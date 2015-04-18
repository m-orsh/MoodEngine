package algorithm;

import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.example.app.MainActivity;

import com.echonest.api.v4.CatalogUpdater;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.SongCatalog;
import com.echonest.api.v4.SongCatalogItem;

import java.util.ArrayList;
import java.util.Map;

//This class is used to modify songs marked as perfect for a mood in "tabulist" style when a mood gets modified.
//Songs will follow that mood around so they stay within selection range.

public class AsyncTabuMod extends AsyncTask<String, String, Boolean> {

    private EchoNestAPI en;
    MoodElement mood = null;
    double heaviness_mod = 0;
    double tempo_mod = 0;
    double complexity_mod = 0;
    static String result = "";
    String method = null;
    // constructor
    public AsyncTabuMod(MoodElement mood, double heaviness_mod, double tempo_mod, double complexity_mod) {
        this.mood = mood;
        this.heaviness_mod = heaviness_mod;
        this.tempo_mod = tempo_mod;
        this.complexity_mod = complexity_mod;
    }
    @Override
    protected Boolean doInBackground(String... params) {
        MainActivity.dbhandler.updatePerfectAssessments(mood.id(), this.heaviness_mod, this.tempo_mod, this.complexity_mod);
        return true;
    }


    //MainActivity.dbhandler.addSong()
}
