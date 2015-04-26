package com.example.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.util.List;

import algorithm.AsyncSyncProcess;
import algorithm.DatabaseHandler;
import algorithm.MoodElement;
import algorithm.MoodTable;
import algorithm.Preference;
import network.AsyncUploadAnalyzer;

public class MainActivity extends ActionBarActivity {
    private String[] mSideTrayOptions;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CheckBox mUploadCheckBox;
    public static DatabaseHandler dbhandler;
    public static Preference userpref;
    public static MoodTable table;
    private boolean maintainDataBase = true;
    public static boolean Uploadflag;
    public static String groovesharkSessionID = null;
    public static String groovesharkCountryID = null;
    public static final String PREFS_NAME = "MoodEnginePreferences";
    public static SharedPreferences settings;
    private boolean mIgnoreCheckedChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        settings = getSharedPreferences(PREFS_NAME, 0);
        Uploadflag = settings.getBoolean("uploadSongs", true);

        if (savedInstanceState == null) {
            if(maintainDataBase==true) {
                if (doesDatabaseExist(context, "moodEngineManager")) {
                    //query preferences
                    //check add/removed songs
                    //context.deleteDatabase("moodEngineManager");
                    dbhandler = new DatabaseHandler(getApplicationContext());
                    List<MoodElement> list = dbhandler.getAllMoods();
                    if(!list.isEmpty()) {
                        table = new MoodTable(list);
                        for(MoodElement get:list){
                            System.out.println(get.id());
                            System.out.println(get.mood_name());
                            System.out.println(get.heaviness());
                            System.out.println(get.tempo());
                            System.out.println(get.complexity());
                        }
                        AsyncSyncProcess syncProcess = new AsyncSyncProcess(getContentResolver(), getApplicationContext());
                        if (Build.VERSION.SDK_INT >= 11) {
                            //--post GB use serial executor by default --
                            syncProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            syncProcess.execute();
                        }
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.container, new MoodSelectFragment())
                                .commit();
                    }
                    else{
                        dbhandler = new DatabaseHandler(getApplicationContext());
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.container, new PreferencesFragment())
                                .commit();
                    }
                } else {
                    //create DB
                    dbhandler = new DatabaseHandler(getApplicationContext());
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new PreferencesFragment())
                            .commit();

                }
            }
            else{
                if (doesDatabaseExist(context, "moodEngineManager")) {
                    //query preferences
                    //check add/removed songs
                    context.deleteDatabase("moodEngineManager");
                } else {
                    //create DB
                    dbhandler = new DatabaseHandler(getApplicationContext());
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new PreferencesFragment())
                            .commit();
                }
            }
        }

        mSideTrayOptions = getResources().getStringArray(R.array.sidebar_option);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        Display dd = this.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dd.getMetrics(dm);

        mUploadCheckBox = new CheckBox(this);
        ListView.LayoutParams lp = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
        mUploadCheckBox.setLayoutParams(lp);
        mUploadCheckBox.setText("Upload songs?");
        mUploadCheckBox.setTextSize(8 * dm.scaledDensity);
        mUploadCheckBox.setTextColor(Color.WHITE);
        int padding = (int)(9*dm.density);
        mUploadCheckBox.setPadding(padding, 0, 0, 0);
        mUploadCheckBox.setGravity(Gravity.CENTER_VERTICAL);
        mUploadCheckBox.setChecked(Uploadflag);
        //This is really hacky lol.
        mUploadCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mDrawerLayout.isDrawerVisible(mDrawerList) && !mIgnoreCheckedChanged) {
                    Uploadflag = isChecked;
                    settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("uploadSongs", isChecked);
                    editor.commit();
                    if (isChecked) {
                        AsyncUploadAnalyzer uploadSongs = new AsyncUploadAnalyzer(getContentResolver());
                        if (Build.VERSION.SDK_INT >= 11) {
                            //--post GB use serial executor by default --
                            uploadSongs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            uploadSongs.execute();
                        }
                    }
                } else {
                    if (mIgnoreCheckedChanged) {
                        mIgnoreCheckedChanged = false;
                    }
                    mUploadCheckBox.setChecked(!isChecked);
                }
            }
        });

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Do nothing.
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mUploadCheckBox.setChecked(Uploadflag);
                mIgnoreCheckedChanged = Uploadflag;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //Do nothing.
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                //Do nothing.
            }
        });

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mSideTrayOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.addFooterView(mUploadCheckBox);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(getResources().getDrawable(R.drawable.menu_icon));
    }

    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case (android.R.id.home):
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK && mDrawerLayout.isDrawerOpen(mDrawerList)){
            mDrawerLayout.closeDrawer(mDrawerList);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    public void hideActionBarTitle() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
            mDrawerLayout.closeDrawers();
        }
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                switchToFragment(new MoodSelectFragment(false));
                break;
            case 1:
                //switchToFragment(new MoodSelectFragment(true));
                break;
            case 2:
                switchToFragment(new AddMoodFragment());
                break;
        }
    }

    public void switchToFragment(Fragment frag){
        switchToFragment(frag, true);
    }

    public void switchToFragment(Fragment frag, boolean addToBackstack) {
        Fragment activeFrag = getSupportFragmentManager().findFragmentByTag("active");
        if (activeFrag != null && activeFrag.getClass() == PlayMusicFragment.class) {
            ((PlayMusicFragment)activeFrag).stopService();
        }

        if (frag.getClass() == PlayMusicFragment.class || frag.getClass() == ExternalPlayerFragment.class) {
            frag.setHasOptionsMenu(true);
        } else {
            frag.setHasOptionsMenu(false);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, frag, "active");
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
