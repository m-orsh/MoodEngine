package com.example.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import algorithm.MoodElement;
import algorithm.Song;
import network.GetExternalRecommendation;

/**
 * Created by Steven on 2015-02-10.
 */
public class MoodAdapter extends BaseAdapter {
    private Context mContext;
    private List<MoodElement> mMoods;
    private boolean mIsExternalPlayer;
    private int mSmallestTextSize;

    public MoodAdapter(Context c, boolean isExternalPlayer) {
        mContext = c;
        mIsExternalPlayer = isExternalPlayer;
        mMoods = MainActivity.dbhandler.getAllMoods();
    }

    public int getCount() {
        return mMoods.size();
    }

    public MoodElement getItem(int pos) {
        return mMoods.get(pos);
    }

    public long getItemId(int pos) {
        return mMoods.get(pos).id();
    }

    public View getView(final int pos, View convertView, ViewGroup parent) {
        final Button button;
        if (convertView == null) {
            button = new Button(mContext);
            button.setLayoutParams(new GridView.LayoutParams((((GridView)parent).getColumnWidth()), ((GridView)parent).getColumnWidth()));
        } else {
            button = (Button) convertView;
        }

        MoodElement mood = mMoods.get(pos);

        Display dd = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dd.getMetrics(dm);
        float m_ScaledDensity = dm.scaledDensity;
        button.setText(mood.mood_name());
        button.setSingleLine();
        button.setTextSize((((GridView) parent).getColumnWidth() / 7) / m_ScaledDensity);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.parseColor(mood.mood_colour()));
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        button.setAlpha(0.7f);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        button.setAlpha(1.0f);
                        break;
                }
                return false;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mIsExternalPlayer) {
                    ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo ni = cm.getActiveNetworkInfo();

                    if (ni == null || !ni.isConnected()) {
                        // There are no active networks.
                        Toast toast = Toast.makeText(mContext, "You need to be connected to a network to stream music.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 50);
                        toast.show();
                        return;
                    } else {
                        ProgressDialog progress = new ProgressDialog(view.getContext());
                        progress.setTitle("Loading");
                        progress.setMessage("Getting a recommendation from internet...");
                        progress.show();
                        GetExternalRecommendation newrec = new GetExternalRecommendation(mMoods.get(pos), mContext, progress);
                    }
                } else {

                    ArrayList<Song> list = MainActivity.dbhandler.getRecommendation(mMoods.get(pos).mood_name());

                    if (!list.isEmpty()) {
                        try {
                            Fragment fragment = new PlayMusicFragment(mMoods.get(pos), mIsExternalPlayer, list);
                            ((MainActivity) view.getContext()).switchToFragment(fragment);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        return button;
    }
}
