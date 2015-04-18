package algorithm;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.MediaStore;

import com.example.app.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**

 * Created by Adnan on 7/23/14.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "moodEngineManager";

    // table names
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_MOODS = "moods";
    private static final String TABLE_ASSESSMENTS = "assessments";
    private final double TEMP = 0.98;

    // Song Table Column Names
    private static final String KEY_SONG_ID = "id";
    private static final String KEY_SONG_NAME = "name";
    private static final String KEY_ARTIST_NAME = "artist";
    private static final String KEY_SONG_HEAVINESS = "heaviness";
    private static final String KEY_SONG_TEMPO = "tempo";
    private static final String KEY_SONG_COMPLEXITY = "complexity";
    private static final String KEY_SONG_COUNTER = "counter";
    private static final String KEY_SONG_DURATION = "duration";
    private static final String KEY_SONG_FILEID = "fileid";
    private static final String KEY_SONG_ANALYSIS_STATE = "analysis";
    private static final String KEY_SONG_ISACTIVE = "active";

    //Mood Table Column Names
    private static final String KEY_MOOD_ID = "id";
    private static final String KEY_MOOD_NAME = "name";
    private static final String KEY_MOOD_HEAVINESS = "heaviness";
    private static final String KEY_MOOD_TEMPO = "tempo";
    private static final String KEY_MOOD_COMPLEXITY = "complexity";
    private static final String KEY_MOOD_COUNTER_H = "heaviness_counter";
    private static final String KEY_MOOD_COUNTER_T = "tempo_counter";
    private static final String KEY_MOOD_COUNTER_C = "complexity_counter";
    private static final String KEY_MOOD_COLOUR = "colour";
    private static final String KEY_MOOD_POSITION =  "position";


    //Assessment Table Column Names
    private static final String KEY_ASSESSMENT_ID = "id";
    private static final String KEY_ASSESSMENT_MOOD_ID = "mood_id";
    private static final String KEY_ASSESSMENT_SONG_ID = "song_id";
    private static final String KEY_ASSESSMENT_HEAVINESS = "heaviness";
    private static final String KEY_ASSESSMENT_COMPLEXITY = "complexity";
    private static final String KEY_ASSESSMENT_TEMPO = "tempo";

    //Table Create Statements
    //Song table create statement
    private static final String CREATE_TABLE_SONGS = "CREATE TABLE " + TABLE_SONGS + "("
                                                     + KEY_SONG_ID + " INTEGER PRIMARY KEY," + KEY_SONG_NAME + " NVARCHAR,"
                                                     + KEY_ARTIST_NAME + " NVARCHAR," + KEY_SONG_HEAVINESS + " REAL,"
                                                     + KEY_SONG_TEMPO + " REAL,"+ KEY_SONG_COMPLEXITY + " REAL,"
                                                     + KEY_SONG_COUNTER + " INTEGER," +  KEY_SONG_DURATION + " INTEGER,"
                                                     + KEY_SONG_FILEID + " NVARCHAR," + KEY_SONG_ANALYSIS_STATE + " INTEGER, "
                                                     + KEY_SONG_ISACTIVE + " INTEGER)";

    //Mood table create statement
    private static final String CREATE_TABLE_MOODS = "CREATE TABLE " + TABLE_MOODS + "("
                                                     + KEY_MOOD_ID + " INTEGER PRIMARY KEY," + KEY_MOOD_NAME + " TEXT,"
                                                     + KEY_MOOD_HEAVINESS + " REAL,"+ KEY_MOOD_TEMPO + " REAL,"
                                                     + KEY_MOOD_COMPLEXITY + " REAL,"  + KEY_MOOD_COUNTER_H + " INTEGER,"
                                                     + KEY_MOOD_COUNTER_T + " INTEGER," + KEY_MOOD_COUNTER_C + " INTEGER,"
                                                     + KEY_MOOD_COLOUR + " STRING," + KEY_MOOD_POSITION + " INTEGER" + ")";

    //Assessment table create statement
    private static final String CREATE_TABLE_ASSESSMENTS = "CREATE TABLE " + TABLE_ASSESSMENTS + "("
                                                           + KEY_ASSESSMENT_ID + " INTEGER PRIMARY KEY," + KEY_ASSESSMENT_MOOD_ID + " INTEGER,"
                                                           + KEY_ASSESSMENT_SONG_ID + " INTEGER," + KEY_ASSESSMENT_HEAVINESS + " INTEGER,"
                                                           + KEY_ASSESSMENT_TEMPO + " INTEGER," + KEY_ASSESSMENT_COMPLEXITY + " INTEGER" + ")";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SONGS);
        db.execSQL(CREATE_TABLE_MOODS);
        db.execSQL(CREATE_TABLE_ASSESSMENTS);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOODS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);

        // Create tables again
        onCreate(db);
    }


    // Adding new song
    public void addSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put( KEY_SONG_NAME, song.name() ); // Song Name
        values.put( KEY_ARTIST_NAME, song.artist() ); // Song's Artist Name
        values.put( KEY_SONG_HEAVINESS, song.heaviness() ); // Song's Heaviness
        values.put( KEY_SONG_TEMPO, song.tempo() ); // Song's Tempo
        values.put( KEY_SONG_COMPLEXITY, song.complexity() ); // Song's Complexity
        values.put( KEY_SONG_COUNTER, song.counter() ); // Song's Counter
        values.put( KEY_SONG_DURATION, song.duration() ); // Song's duration
        values.put( KEY_SONG_FILEID, song.fileid() ); // Song's fileid
        values.put( KEY_SONG_ANALYSIS_STATE, song.analysis_state());//New songs haven't been analyzed
        values.put( KEY_SONG_ISACTIVE, 1);//New songs haven't been analyzed


        // Inserting Row
        db.insert(TABLE_SONGS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new song
    public void addInitialSongs(Cursor songCursor) {
        SQLiteDatabase db = this.getWritableDatabase();
        songCursor.moveToFirst();

        ContentValues values = new ContentValues();
        while(!songCursor.isAfterLast()) {

            values.put(KEY_SONG_NAME, songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))); // Song Name
            values.put(KEY_ARTIST_NAME, songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))); // Song's Artist Name
            values.put(KEY_SONG_HEAVINESS, MainActivity.userpref.heaviness()); // Song's Heaviness
            values.put(KEY_SONG_TEMPO, MainActivity.userpref.tempo()); // Song's Tempo
            values.put(KEY_SONG_COMPLEXITY, MainActivity.userpref.complexity()); // Song's Complexity
            values.put(KEY_SONG_COUNTER, 0); // Song's Counter
            values.put(KEY_SONG_DURATION, songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))); // Song's duration
            values.put(KEY_SONG_FILEID, songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media._ID))); // Song's fileid
            values.put(KEY_SONG_ANALYSIS_STATE, 0);//New songs haven't been analyzed
            values.put(KEY_SONG_ISACTIVE, 1);//New songs should be set active

            // Inserting Row
            db.insert(TABLE_SONGS, null, values);
            songCursor.moveToNext();
        }
        System.out.println("Done DB add");
        db.close(); // Closing database connection
    }

    // Getting single song
    public Song getSong(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SONGS, new String[] { KEY_SONG_ID, KEY_SONG_NAME, KEY_ARTIST_NAME,
                KEY_SONG_HEAVINESS, KEY_SONG_TEMPO, KEY_SONG_COMPLEXITY, KEY_SONG_COUNTER, KEY_SONG_DURATION, KEY_SONG_FILEID, KEY_SONG_ANALYSIS_STATE, KEY_SONG_ISACTIVE }, KEY_SONG_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Song song = new Song(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                             Double.parseDouble( cursor.getString(3) ), Double.parseDouble( cursor.getString(4) ),
                             Double.parseDouble( cursor.getString(5) ), Integer.parseInt( cursor.getString(6)),
                             Integer.parseInt( cursor.getString(7)), cursor.getString(8), Integer.parseInt(cursor.getString(9)));

        cursor.close();

        // return song
        return song;
    }

    // Getting All Songs
    public List<Song> getAllSongs() {
        List<Song> songList = new ArrayList<Song>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SONGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song( Integer.parseInt( cursor.getString(0) ), cursor.getString(1), cursor.getString(2),
                                      Double.parseDouble( cursor.getString(3) ), Double.parseDouble( cursor.getString(4) ),
                                      Double.parseDouble( cursor.getString(5) ), Integer.parseInt( cursor.getString(6)),
                                      Integer.parseInt( cursor.getString(7)), cursor.getString(8), Integer.parseInt(cursor.getString(9)));
                // Adding song to list
                songList.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return song list
        return songList;
    }

    // Getting All Songs
    public List<Song> getSongsFromIDList(List<String> IDs) {
        List<Song> songList = new ArrayList<Song>();
        // Select All Query
        Cursor cursor;

        SQLiteDatabase db = this.getWritableDatabase();
        for(String ID : IDs){
            String selectQuery = "SELECT  * FROM " + TABLE_SONGS + " WHERE " + KEY_SONG_FILEID + " = " + ID;
            cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            Song song = new Song( Integer.parseInt( cursor.getString(0) ), cursor.getString(1), cursor.getString(2),
                    Double.parseDouble( cursor.getString(3) ), Double.parseDouble( cursor.getString(4) ),
                    Double.parseDouble( cursor.getString(5) ), Integer.parseInt( cursor.getString(6)),
                    Integer.parseInt( cursor.getString(7)), cursor.getString(8), Integer.parseInt(cursor.getString(9)));
            // Adding song to list
            songList.add(song);
            cursor.close();
        }

        // return song list
        return songList;
    }


    public List<Song> getAllNewSongs(){
        List<Song> songList = new ArrayList<Song>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SONGS + " WHERE " + KEY_SONG_ANALYSIS_STATE + " = 0" + " AND " + KEY_SONG_ISACTIVE + " = 1 AND " + KEY_SONG_HEAVINESS + " = -10";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song( Integer.parseInt( cursor.getString(0) ), cursor.getString(1), cursor.getString(2),
                        Double.parseDouble( cursor.getString(3) ), Double.parseDouble( cursor.getString(4) ),
                        Double.parseDouble( cursor.getString(5) ), Integer.parseInt( cursor.getString(6)),
                        Integer.parseInt( cursor.getString(7)), cursor.getString(8), Integer.parseInt(cursor.getString(9)));
                // Adding song to list
                songList.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();

        //Initialize these new songs so they don't have weird values anymore.
        selectQuery = "UPDATE " + TABLE_SONGS + " SET " + KEY_SONG_HEAVINESS + "=5, "+KEY_SONG_TEMPO + "=5, "+KEY_SONG_COMPLEXITY+"=5 WHERE "
                + KEY_SONG_ANALYSIS_STATE + " = 0" + " AND " + KEY_SONG_ISACTIVE + " = 1 AND " + KEY_SONG_HEAVINESS + " = -10";
        cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        cursor.close();

        // return song list
        return songList;
    }

    // Getting All Songs
    public List<Song> getAllSongsOfAnalysisType(int isAnalyzed) {
        List<Song> songList = new ArrayList<Song>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SONGS + " WHERE " + KEY_SONG_ANALYSIS_STATE + " = " + Integer.toString(isAnalyzed) + " AND " + KEY_SONG_ISACTIVE + " = 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song( Integer.parseInt( cursor.getString(0) ), cursor.getString(1), cursor.getString(2),
                        Double.parseDouble( cursor.getString(3) ), Double.parseDouble( cursor.getString(4) ),
                        Double.parseDouble( cursor.getString(5) ), Integer.parseInt( cursor.getString(6)),
                        Integer.parseInt( cursor.getString(7)), cursor.getString(8), Integer.parseInt(cursor.getString(9)));
                // Adding song to list
                songList.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return song list
        return songList;
    }

    public ArrayList<Song> getRecommendation(String mood){
        Preference moodpref = MainActivity.table.getMood(mood).preference();
        Cursor cursor = null;
        ArrayList<Song> songList = null;
        double rval = PrefRangeValue(MainActivity.table.getMood(mood).range_counter);
        int song_count = 0;

        while(song_count == 0) {
            String selectQuery = "SELECT  * FROM " + TABLE_SONGS
                    + " WHERE " + KEY_SONG_ISACTIVE + " = 1" +
                    " AND "+ KEY_SONG_HEAVINESS + " BETWEEN " + String.valueOf(moodpref.heaviness() - rval) + " AND "
                    + String.valueOf(moodpref.heaviness() + rval) + " AND "
                    + KEY_SONG_TEMPO + " BETWEEN " + String.valueOf(moodpref.tempo() - rval) + " AND "
                    + String.valueOf(moodpref.tempo() + rval) + " AND "
                    + KEY_SONG_COMPLEXITY + " BETWEEN " + String.valueOf(moodpref.complexity() - rval) + " AND "
                    + String.valueOf(moodpref.complexity() + rval);
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            song_count = cursor.getCount();

            if(song_count != 0) {
                songList = new ArrayList<Song>();
                if (cursor.moveToFirst()) {
                    do {
                        Song song = new Song(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                                Double.parseDouble(cursor.getString(3)), Double.parseDouble(cursor.getString(4)),
                                Double.parseDouble(cursor.getString(5)), Integer.parseInt(cursor.getString(6)),
                                Integer.parseInt(cursor.getString(7)), cursor.getString(8), Integer.parseInt(cursor.getString(9)));
                        // Adding song to list
                        songList.add(song);
                    } while (cursor.moveToNext());
                }
            }
            else
                rval += 1;
        }

        cursor.close();
        return songList;
    }

    private double PrefRangeValue( int counter )
    {
        return ( 2 * Math.pow(TEMP, counter ) + 1 );
    }

    // Getting song Count
    public int getSongCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SONGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Getting active song Count
    public int getActiveSongCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SONGS + " WHERE " + KEY_SONG_ISACTIVE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void setAllSongsInactive(){

        String setInactiveQuery = "UPDATE " + TABLE_SONGS + " SET " + KEY_SONG_ISACTIVE + " = 0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(setInactiveQuery, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public void syncActiveSongs(Cursor songlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor2;
        songlist.moveToFirst();
        String Fromclause;
        while(!songlist.isAfterLast()) {
            String songname = songlist.getString(songlist.getColumnIndex(MediaStore.Audio.Media.TITLE)).replace("'","''");
            String songartist = songlist.getString(songlist.getColumnIndex(MediaStore.Audio.Media.ARTIST)).replace("'","''");
            Fromclause = " FROM " + TABLE_SONGS + " WHERE "
                    + KEY_SONG_NAME + " = '" + songname + "' AND "
                    + KEY_ARTIST_NAME + " = '" + songartist + "' AND "
                    + KEY_SONG_DURATION + " = " + songlist.getString(songlist.getColumnIndex(MediaStore.Audio.Media.DURATION)) + ")";
            String syncQuery = "INSERT OR REPLACE INTO " +  TABLE_SONGS +  "("
                    + KEY_SONG_ID  + ", "
                    + KEY_SONG_NAME + ", "
                    + KEY_ARTIST_NAME + ", "
                    + KEY_SONG_HEAVINESS + ", "
                    + KEY_SONG_TEMPO + ", "
                    + KEY_SONG_COMPLEXITY + ", "
                    + KEY_SONG_COUNTER + ", "
                    + KEY_SONG_DURATION + ", "
                    + KEY_SONG_FILEID + ", "
                    + KEY_SONG_ANALYSIS_STATE + ", "
                    + KEY_SONG_ISACTIVE + ") VALUES ( "
                    + "(SELECT " + KEY_SONG_ID + Fromclause + ", "
                    + "COALESCE" + "((SELECT " + KEY_SONG_NAME + Fromclause + ", '"
                        + songname + "'), "
                    + "COALESCE" + "((SELECT " + KEY_ARTIST_NAME + Fromclause + ", '"
                        + songartist + "'), "
                    + "COALESCE" + "((SELECT " + KEY_SONG_HEAVINESS + Fromclause + ", "
                        + "-10), "
                    + "COALESCE" + "((SELECT " + KEY_SONG_TEMPO + Fromclause + ", "
                        + "-10), "
                    + "COALESCE" + "((SELECT " + KEY_SONG_COMPLEXITY + Fromclause + ", "
                        + "-10), "
                    + "COALESCE" + "((SELECT " + KEY_SONG_COUNTER + Fromclause + ", "
                        + "0), "
                    + "COALESCE" + "((SELECT " + KEY_SONG_DURATION + Fromclause + ", "
                        + songlist.getString(songlist.getColumnIndex(MediaStore.Audio.Media.DURATION)) + "), '"
                    + songlist.getString(songlist.getColumnIndex(MediaStore.Audio.Media._ID)) + "', "
                    + "COALESCE" + "((SELECT " + KEY_SONG_ANALYSIS_STATE + Fromclause + ", "
                        + "0), "
                    + "1)";
            // Inserting Row
            //System.out.println(songname);
            cursor2 = db.rawQuery(syncQuery, null);
            cursor2.moveToFirst();
            cursor2.close();
            songlist.moveToNext();
        }

        System.out.println("Done DB sync");
    }

    // Updating single song
    public int updateSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SONG_NAME, song.name());
        values.put(KEY_ARTIST_NAME, song.artist());
        values.put(KEY_SONG_HEAVINESS, song.heaviness());
        values.put(KEY_SONG_TEMPO, song.tempo());
        values.put(KEY_SONG_COMPLEXITY, song.complexity());
        values.put(KEY_SONG_COUNTER, song.counter());
        values.put(KEY_SONG_DURATION, song.duration());
        values.put(KEY_SONG_FILEID, song.fileid());
        values.put(KEY_SONG_ANALYSIS_STATE, song.analysis_state());
        values.put(KEY_SONG_ISACTIVE, 1);

        // updating row
        return db.update(TABLE_SONGS, values, KEY_SONG_ID + " = ?",
        new String[] { String.valueOf(song.id()) });
    }

    public int updateSongPrefsFromNameArtist(Song song, int isAnalyzed) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SONG_HEAVINESS, song.heaviness());
        values.put(KEY_SONG_TEMPO, song.tempo());
        values.put(KEY_SONG_COMPLEXITY, song.complexity());
        values.put(KEY_SONG_ANALYSIS_STATE, isAnalyzed);
        values.put(KEY_SONG_ISACTIVE, 1);

        // updating row
        return db.update(TABLE_SONGS, values, KEY_SONG_NAME + " = ? AND " + KEY_ARTIST_NAME + " = ?" ,
                new String[] { song.name(), song.artist() });
    }

    public int updateSongPrefsFromID(String fileid, Preference prefvals, int isAnalyzed) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SONG_HEAVINESS, prefvals.heaviness());
        values.put(KEY_SONG_TEMPO, prefvals.tempo());
        values.put(KEY_SONG_COMPLEXITY, prefvals.complexity());
        values.put(KEY_SONG_ANALYSIS_STATE, isAnalyzed);
        values.put(KEY_SONG_ISACTIVE, 1);

        // updating row
        return db.update(TABLE_SONGS, values, KEY_SONG_FILEID + " = ? AND " + KEY_SONG_ANALYSIS_STATE + " = ?",
                new String[] { fileid,"0" });
    }

    // Deleting single song
    public void deleteSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SONGS, KEY_SONG_ID + " = ?",
                new String[] { String.valueOf(song.id()) });
        db.close();
    }




    // Adding new mood
    public int addMood(MoodElement mood) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put( KEY_MOOD_NAME, mood.mood_name() ); // Mood Name
        values.put( KEY_MOOD_HEAVINESS, mood.heaviness() ); // Mood's Heaviness
        values.put( KEY_MOOD_TEMPO, mood.tempo() ); // Mood's Tempo
        values.put( KEY_MOOD_COMPLEXITY, mood.complexity() ); // Mood's Complexity
        values.put( KEY_MOOD_COUNTER_H, mood.modification_counter_h ); // Mood's Heaviness Counter
        values.put( KEY_MOOD_COUNTER_T, mood.modification_counter_t ); // Mood's Tempo Counter
        values.put( KEY_MOOD_COUNTER_C, mood.modification_counter_c ); // Mood's Complexity Counter
        values.put( KEY_MOOD_COLOUR, mood.mood_colour() ); // Mood's Colour
        values.put( KEY_MOOD_POSITION, mood.mood_position() ); //Mood's Position

        // Inserting Row
        db.insert(TABLE_MOODS, null, values);



        Cursor cursor = db.query(TABLE_MOODS, new String[] { KEY_MOOD_ID }, KEY_MOOD_NAME + "=?",
                new String[] { mood.mood_name() }, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        int moodID = Integer.parseInt( cursor.getString(0) );

        db.close(); // Closing database connection
        // return mood ID
        return moodID;
    }

    // Getting single mood
    public MoodElement getMood(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MOODS, new String[] { KEY_MOOD_ID, KEY_MOOD_NAME, KEY_MOOD_HEAVINESS,
                         KEY_MOOD_TEMPO, KEY_MOOD_COMPLEXITY, KEY_MOOD_COUNTER_H, KEY_MOOD_COUNTER_T, KEY_MOOD_COUNTER_C, KEY_MOOD_COLOUR, KEY_MOOD_POSITION }, KEY_MOOD_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MoodElement mood = new MoodElement(Integer.parseInt( cursor.getString(0) ), cursor.getString(1),
                                           new Preference( Double.parseDouble( cursor.getString(2) ), Double.parseDouble( cursor.getString(3) ),
                                           Double.parseDouble( cursor.getString(4) ) ), Integer.parseInt(cursor.getString(5)),
                                           Integer.parseInt( cursor.getString(6)), Integer.parseInt( cursor.getString(7)),
                                           cursor.getString(8), Integer.parseInt(cursor.getString(9)) );

        cursor.close();
        // return mood
        return mood;
    }

    // Getting All Moods
    public List<MoodElement> getAllMoods() {
        List<MoodElement> moodList = new ArrayList<MoodElement>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MOODS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MoodElement mood = new MoodElement(Integer.parseInt( cursor.getString(0) ), cursor.getString(1),
                                                   new Preference( Double.parseDouble( cursor.getString(2) ), Double.parseDouble( cursor.getString(3) ),
                                                   Double.parseDouble( cursor.getString(4) ) ), Integer.parseInt(cursor.getString(5)),
                                                   Integer.parseInt( cursor.getString(6)), Integer.parseInt( cursor.getString(7) ),
                                                   cursor.getString(8), Integer.parseInt(cursor.getString(9)) );

                // Adding mood to list
                moodList.add(mood);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return mood list
        return moodList;
    }

    // Getting mood Count
    public int getMoodCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MOODS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single mood
    public int updateMood(MoodElement mood) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put( KEY_MOOD_NAME, mood.mood_name() );
        values.put(KEY_MOOD_HEAVINESS, mood.heaviness());
        values.put( KEY_MOOD_TEMPO, mood.tempo() );
        values.put( KEY_MOOD_COMPLEXITY, mood.complexity() );
        values.put( KEY_MOOD_COUNTER_H, mood.modification_counter_h );
        values.put( KEY_MOOD_COUNTER_T, mood.modification_counter_t );
        values.put( KEY_MOOD_COUNTER_C, mood.modification_counter_c );
        values.put( KEY_MOOD_COLOUR, mood.mood_colour() );
        values.put( KEY_MOOD_POSITION, mood.mood_position() );


        // updating row
        return db.update(TABLE_MOODS, values, KEY_MOOD_ID + " = ?",
                new String[]{String.valueOf(mood.id())});
    }

    // Deleting single mood
    public void deleteMood(MoodElement mood) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOODS, KEY_MOOD_ID + " = ?",
                new String[] { String.valueOf(mood.id()) });
        db.close();
    }

    //Adding new assessment
    public void addAssessment( int mood_id, int song_id, ModificationType heaviness_preference, ModificationType tempo_preference, ModificationType complexity_preference )
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put( KEY_ASSESSMENT_MOOD_ID, mood_id );
        values.put( KEY_ASSESSMENT_SONG_ID, song_id );
        values.put( KEY_ASSESSMENT_HEAVINESS, heaviness_preference.mod_id() );
        values.put( KEY_ASSESSMENT_TEMPO, tempo_preference.mod_id() );
        values.put( KEY_ASSESSMENT_COMPLEXITY, complexity_preference.mod_id() );

        // Inserting Row
        db.insert(TABLE_ASSESSMENTS, null, values);
        db.close(); // Closing database connection
    }

    //Get assessment preferences
    /*
        ArrayList Indices
        -----------------
        0 - Heaviness Preference
        1 - Tempo Preference
        2 - Complexity Preference
     */
    public ArrayList<ModificationType> getAssessment( int mood_id, int song_id )
    {
        ArrayList<ModificationType> preference_list = new ArrayList<ModificationType>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ASSESSMENTS, new String[] { KEY_ASSESSMENT_HEAVINESS, KEY_ASSESSMENT_TEMPO, KEY_ASSESSMENT_COMPLEXITY }, KEY_ASSESSMENT_MOOD_ID + " = ? and " + KEY_ASSESSMENT_SONG_ID + " = ?",
                new String[] { String.valueOf(mood_id), String.valueOf(song_id) }, null, null, null);

        if(cursor.getCount() == 0)
            return null;

        if (cursor != null)
            cursor.moveToFirst();

        for( int i = 0; i < 3; i++ ) {
            int type_id = Integer.parseInt(cursor.getString(i));
            ModificationType type = null;

            if ( type_id == ModificationType.PERFECT.mod_id() )
                type = ModificationType.PERFECT;
            else if( type_id == ModificationType.TOO_LOW.mod_id() )
                type = ModificationType.TOO_LOW;
            else if( type_id == ModificationType.TOO_MUCH.mod_id() )
                type = ModificationType.TOO_MUCH;

            preference_list.add(type);
        }

        cursor.close();

        // return preferences
        return preference_list;
    }

    //Check if assessment exists
    public boolean checkAssessment( int mood_id, int song_id )
    {
        ArrayList<ModificationType> preference_list = new ArrayList<ModificationType>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ASSESSMENTS, new String[] { KEY_ASSESSMENT_ID }, KEY_ASSESSMENT_MOOD_ID + " = ? and " + KEY_ASSESSMENT_SONG_ID + " = ?",
                new String[] { String.valueOf(mood_id), String.valueOf(song_id) }, null, null, null);

        boolean exists = (cursor.getCount() != 0);
        cursor.close();
        return exists;
    }

    //Check for assessments for this mood of the given format
    public void updatePerfectAssessments( int mood_id, double heavinessmod, double tempomod, double complexitymod ) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + TABLE_SONGS +
                " SET " + KEY_SONG_HEAVINESS + " = "
                + "CASE WHEN " + KEY_SONG_HEAVINESS + "+('" + heavinessmod + "')>10 THEN 10"
                + " WHEN " + KEY_SONG_HEAVINESS + "+('" + heavinessmod + "')<0 THEN 0"
                + " ELSE " + KEY_SONG_HEAVINESS + "+('" + heavinessmod + "') END, " +

                KEY_SONG_TEMPO + " = "
                + "CASE WHEN " + KEY_SONG_TEMPO + "+('" + tempomod + "')>10 THEN 10"
                + " WHEN " + KEY_SONG_TEMPO + "+('" + tempomod + "')<0 THEN 0"
                + " ELSE " + KEY_SONG_TEMPO + "+('" + tempomod + "') END, " +

                KEY_SONG_COMPLEXITY + " = "
                + "CASE WHEN " + KEY_SONG_COMPLEXITY + "+('" + complexitymod + "')>10 THEN 10"
                + " WHEN " + KEY_SONG_COMPLEXITY + "+('" + complexitymod + "')<0 THEN 0"
                + " ELSE " + KEY_SONG_COMPLEXITY + "+('" + complexitymod + "') END " +

                " WHERE " + KEY_SONG_ID + " IN (SELECT " + KEY_ASSESSMENT_SONG_ID + " FROM " + TABLE_ASSESSMENTS +
                " WHERE " + KEY_MOOD_ID + " = " + mood_id +
                " AND " + KEY_ASSESSMENT_HEAVINESS + " = " + ModificationType.PERFECT.mod_id() +
                " AND " + KEY_ASSESSMENT_TEMPO + " = " + ModificationType.PERFECT.mod_id() +
                " AND " + KEY_ASSESSMENT_COMPLEXITY + " = " + ModificationType.PERFECT.mod_id() + ") AND "
                + KEY_SONG_ISACTIVE + " = 1";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        cursor.close();

    }

    //Updating single assessment
    public void updateAssessment( int mood_id, int song_id, ModificationType heaviness_preference, ModificationType tempo_preference, ModificationType complexity_preference )
    {

        SQLiteDatabase db = this.getWritableDatabase();
        String sqlQuery = "INSERT OR REPLACE INTO " + TABLE_ASSESSMENTS + " ( " + KEY_ASSESSMENT_ID + ", "
                                                                               + KEY_ASSESSMENT_MOOD_ID + ", "
                                                                               + KEY_ASSESSMENT_SONG_ID + ", "
                                                                               + KEY_ASSESSMENT_HEAVINESS + ", "
                                                                               + KEY_ASSESSMENT_TEMPO + ", "
                                                                               + KEY_ASSESSMENT_COMPLEXITY + ") values ("
                          + "(select " + KEY_ASSESSMENT_ID + " from " + TABLE_ASSESSMENTS + " where " + KEY_ASSESSMENT_MOOD_ID + " = " + mood_id + " AND " + KEY_ASSESSMENT_SONG_ID + " = " + song_id + "),"
                          + mood_id + ", " + song_id + ", " + heaviness_preference.mod_id() + ", " + tempo_preference.mod_id() + ", " + complexity_preference.mod_id() + ")";

        try {
            db.execSQL(sqlQuery);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        /*SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put( KEY_ASSESSMENT_HEAVINESS, heaviness_preference.mod_id() );
        values.put( KEY_ASSESSMENT_TEMPO, tempo_preference.mod_id() );
        values.put( KEY_ASSESSMENT_COMPLEXITY, complexity_preference.mod_id() );

        // updating row
        return db.update(TABLE_ASSESSMENTS, values, KEY_ASSESSMENT_MOOD_ID + " = ? and " + KEY_ASSESSMENT_SONG_ID + " = ?",
                new String[] { String.valueOf(mood_id), String.valueOf(song_id) });*/
    }

    //Deleting single assessment
    public void deleteAssessment( int mood_id, int song_id )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ASSESSMENTS, KEY_ASSESSMENT_MOOD_ID + " = ? and " + KEY_ASSESSMENT_SONG_ID + " = ?",
                new String[] { String.valueOf(mood_id), String.valueOf(song_id) });
        db.close();
    }

}
