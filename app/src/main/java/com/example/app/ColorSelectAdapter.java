package com.example.app;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import algorithm.MoodElement;
import algorithm.Song;

/**
 * Created by Steven on 2015-02-10.
 */
public class ColorSelectAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mColors;

    public int selectedIndex;

    public ColorSelectAdapter(Context c, String[] colors) {
        mContext = c;
        mColors = colors;
        selectedIndex = -1;
    }

    public int getCount() {
        return mColors.length;
    }

    public String getItem(int pos) {
        return mColors[pos];
    }

    public long getItemId(int pos) {
        return 0;
    }

    public View getView(final int pos, View convertView, ViewGroup parent) {
        View color;
        if (convertView == null) {
            color = new View(mContext);
            color.setLayoutParams(new GridView.LayoutParams((((GridView)parent).getColumnWidth()), ((GridView)parent).getColumnWidth()));
        } else {
            color = convertView;
        }

        color.setBackgroundColor(Color.parseColor(mColors[pos]));
        if(pos == selectedIndex) {
            color.setAlpha(1);
        } else {
            color.setAlpha(0.25f);
        }

        return color;
    }
}
