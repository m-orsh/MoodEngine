package com.example.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

import algorithm.MoodElement;

/**
 * Created by Steven on 24/07/14.
 */
public class MoodSelectFragment extends Fragment {
    private List<MoodElement> moods;
    private boolean mLaunchExternalPlayer;

    public MoodSelectFragment() {
        moods = MainActivity.dbhandler.getAllMoods();
    }
    public MoodSelectFragment(boolean launchExternalPlayer) {
        moods = MainActivity.dbhandler.getAllMoods();
        mLaunchExternalPlayer = launchExternalPlayer;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mood_select, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.mood_select_gridview);
        gridView.setAdapter(new MoodAdapter(getActivity(), mLaunchExternalPlayer) );

        ((MainActivity)getActivity()).setActionBarTitle(mLaunchExternalPlayer ? "Play Any Music" : "Play Local Music");
        return rootView;
    }
}
