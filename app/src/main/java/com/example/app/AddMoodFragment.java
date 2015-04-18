package com.example.app;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import algorithm.MoodElement;
import algorithm.Preference;

/**
 * Created by Steven on 24/07/14.
 */
public class AddMoodFragment extends Fragment {
    private List<MoodElement> mMoods;

    private TextView mAddMoodTitle;
    private EditText mMoodName;
    private TextView mColorTitle;
    private GridView mColourGridView;
    private SeekBar mHeavinessSeekBar;
    private SeekBar mTempoSeekBar;
    private SeekBar mComplexitySeekBar;
    private Button mCreateMood;

    private ColorSelectAdapter mColorSelectAdapter;

    public AddMoodFragment() {
        mMoods = MainActivity.dbhandler.getAllMoods();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_mood_fragment, container, false);

        mAddMoodTitle = (TextView) rootView.findViewById(R.id.add_mood_title);
        mMoodName = (EditText) rootView.findViewById(R.id.add_mood_name);
        mColorTitle = (TextView) rootView.findViewById(R.id.add_mood_colour_title);
        mColourGridView = (GridView) rootView.findViewById(R.id.add_mood_colour_select);
        mCreateMood = (Button) rootView.findViewById(R.id.add_mood_submit_button);
        mHeavinessSeekBar = (SeekBar) rootView.findViewById(R.id.heaviness_seekbar);
        mTempoSeekBar = (SeekBar) rootView.findViewById(R.id.tempo_seekbar);
        mComplexitySeekBar = (SeekBar) rootView.findViewById(R.id.complexity_seekbar);

        mMoodName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mMoodName.getWindowToken(), 0);
                }
            }
        });
        final String[] colors = {"#ff26a65b", "#fff64747", "#ffcf000f", "#ff1abc9c", "#fff7ca18", "#fff9690e", "#ff3a539b", "#ff3498db", "#ff8e44ad"};
        mColorSelectAdapter = new ColorSelectAdapter(getActivity(), colors);
        mColourGridView.setAdapter(mColorSelectAdapter);
        mColourGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mColorSelectAdapter.selectedIndex = i;
                mColorSelectAdapter.notifyDataSetChanged();
                mMoodName.clearFocus();
            }
        });
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mMoodName.clearFocus();
                return false;
            }
        };
        mHeavinessSeekBar.setOnTouchListener(touchListener);
        mTempoSeekBar.setOnTouchListener(touchListener);
        mComplexitySeekBar.setOnTouchListener(touchListener);

        mCreateMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mColorSelectAdapter.selectedIndex != -1 && !mMoodName.getText().toString().equals("")) {
                    MoodElement moodToAdd = new MoodElement(mMoodName.getText().toString(), new Preference(mHeavinessSeekBar.getProgress(), mTempoSeekBar.getProgress(), mComplexitySeekBar.getProgress()), colors[mColorSelectAdapter.selectedIndex], colors.length + 1);
                    moodToAdd.setID(MainActivity.dbhandler.addMood(moodToAdd));
                    MainActivity.table.addMood(moodToAdd);

                    MoodSelectFragment fragmentToLaunch = new MoodSelectFragment();
                    ((MainActivity) view.getContext()).switchToFragment(fragmentToLaunch);
                } else {
                    Toast.makeText(getActivity(), "Please fill out all of the information", Toast.LENGTH_LONG).show();
                }
            }
        });

        ((MainActivity) getActivity()).setActionBarTitle("Add Mood");
        return rootView;
    }

    @Override
    public void onStop(){
        super.onStop();
        ((MainActivity) getActivity()).hideActionBarTitle();
    }
}
