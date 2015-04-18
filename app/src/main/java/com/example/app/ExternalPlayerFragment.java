package com.example.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import algorithm.ModificationParams;
import algorithm.ModificationType;
import algorithm.MoodElement;
import algorithm.Song;
import network.GetExternalRecommendation;
import network.SendExternalAssessment;

/**
 * Created by Steven on 2015-03-18.
 */
public class ExternalPlayerFragment extends Fragment {

    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private ImageButton mUrlButton;
    private ControllerView mControllerView;
    private MoodElement mMood;
    private int songIter = 0;
    public static ArrayList<Song> mExternalSongList;
    public ExternalPlayerFragment thisFragment;
    protected Dialog dialog;
    private final String FEEDBACK = "Feedback";
    public List<ModificationParams> assessmentList = new ArrayList<ModificationParams>();
    boolean manualPrefChoice = false;
    protected MenuItem mAssessmentItem;

    public Menu mMenu;

    private MediaPlayerService mService;

    public ExternalPlayerFragment(){}

    public ExternalPlayerFragment(ArrayList<Song> songList, MoodElement mood) {
        this.mMood = mood;
        this.mExternalSongList = songList;
        this.thisFragment = this;
        for(int x=0;x<songList.size();x++) {
            ModificationParams newone = new ModificationParams();
            this.assessmentList.add(newone);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        this.mAssessmentItem = this.mMenu.findItem(R.id.action_feedback);
        updateUIFor(mExternalSongList.get(songIter));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_feedback:
                showPreferenceSelectionDialog(false);
                return true;
            case R.id.action_graphs:
                showSongStatGraphs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_external_player, container, false);

        mControllerView = new ControllerView(rootView.getContext(), false);
        mTitleTextView = (TextView) rootView.findViewById(R.id.title_text_view);
        mArtistTextView = (TextView) rootView.findViewById(R.id.artist_text_view);
        mUrlButton = (ImageButton) rootView.findViewById(R.id.url_link_button);
        ((MainActivity) getActivity()).setActionBarTitle(mMood.mood_name());

        mControllerView.setAnchorView((FrameLayout) rootView.findViewById(R.id.controller_view_container));
        mControllerView.setUpForExternalPlayer();
        mControllerView.setPrevNextListeners(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!assessmentList.get(songIter).isNull()) {
                            updatePreferences(mMood,mExternalSongList.get(songIter),
                                    assessmentList.get(songIter).getH().mod_name(),
                                    assessmentList.get(songIter).getT().mod_name(),
                                    assessmentList.get(songIter).getC().mod_name());
                            if (songIter == mExternalSongList.size() - 1) {
                                ConnectivityManager cm = (ConnectivityManager) getView().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo ni = cm.getActiveNetworkInfo();
                                if(ni!=null) {
                                    if (ni.isConnected()) {
                                        songIter++;
                                        ProgressDialog progress = new ProgressDialog(thisFragment.getView().getContext());
                                        progress.setTitle("Loading");
                                        progress.setMessage("Getting a recommendation from internet...");
                                        progress.show();
                                        GetExternalRecommendation newrec = new GetExternalRecommendation(mMood, progress, thisFragment);
                                    }
                                    else{
                                        Toast toast = Toast.makeText(getView().getContext(), "Can't get more recommendations without a network connection.", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 50);
                                        toast.show();
                                    }
                                }
                                else{
                                    Toast toast = Toast.makeText(getView().getContext(), "Can't get more recommendations without a network connection.", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 50);
                                    toast.show();
                                }
                            } else {
                                songIter++;
                                //setURL
                                updateUIFor(mExternalSongList.get(songIter));
                            }
                        }
                        else{
                            showPreferenceSelectionDialog(true);
                        }
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(songIter == 0){
                            Toast toast = Toast.makeText(thisFragment.getView().getContext(), "This is the start of the playlist.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        songIter--;
                        //setURL
                        updateUIFor(mExternalSongList.get(songIter));
                        System.out.println(mExternalSongList.get(songIter).name());
                        //setText
                    }
                }
        );
        mControllerView.show(0);
        return rootView;
    }

    public void showSongStatGraphs() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_song_stat_graphs);
        dialog.setTitle("Song Stats");
        BarGraphView heavinessBarGraph = (BarGraphView) dialog.findViewById(R.id.heaviness_bar_graph);
        BarGraphView tempoBarGraph = (BarGraphView) dialog.findViewById(R.id.tempo_bar_graph);
        BarGraphView complexityBarGraph = (BarGraphView) dialog.findViewById(R.id.complexity_bar_graph);

        heavinessBarGraph.setBarValue(mExternalSongList.get(songIter).heaviness());
        tempoBarGraph.setBarValue(mExternalSongList.get(songIter).tempo());
        complexityBarGraph.setBarValue(mExternalSongList.get(songIter).complexity());

        dialog.show();}

    public void updateUIFor(Song song) {
        mTitleTextView.setText(song.name());
        mTitleTextView.setEnabled(true);
        mTitleTextView.setSelected(true);
        mArtistTextView.setText(song.artist());
        mArtistTextView.setEnabled(true);
        mArtistTextView.setSelected(true);
        if(assessmentList.get(songIter).isNull()){
            mAssessmentItem.setIcon(R.drawable.selectdata_icon);
        }
        else{
            mAssessmentItem.setIcon(R.drawable.selectdata_icon_checked);
        }

        mUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mExternalSongList.get(songIter).fileid()));
                startActivity(intent);
            }
        });
    }

    public Dialog showPreferenceSelectionDialog(final boolean nextSong){
        dialog = new Dialog(this.getActivity());
        dialog.setContentView(R.layout.dialog_song_feedback);
        dialog.setTitle(FEEDBACK);

        final RadioGroup heavinessRadioGroup = (RadioGroup) dialog.findViewById(R.id.heaviness_radio_group);
        final RadioGroup tempoRadioGroup = (RadioGroup) dialog.findViewById(R.id.tempo_radio_group);
        final RadioGroup complexityRadioGroup = (RadioGroup) dialog.findViewById(R.id.compexity_radio_group);
        if(assessmentList.get(songIter)!=null) {
            //Show assessed values if the song is already assessed, otherwise radio button to perfect
            if (assessmentList.get(songIter).getH() == ModificationType.TOO_MUCH) {
                ((RadioButton) (heavinessRadioGroup.getChildAt(2))).setChecked(true);
            } else if (assessmentList.get(songIter).getH() == ModificationType.TOO_LOW) {
                ((RadioButton) (heavinessRadioGroup.getChildAt(0))).setChecked(true);
            }
            if (assessmentList.get(songIter).getT() == ModificationType.TOO_MUCH) {
                ((RadioButton) (tempoRadioGroup.getChildAt(2))).setChecked(true);
            } else if (assessmentList.get(songIter).getT() == ModificationType.TOO_LOW) {
                ((RadioButton) (tempoRadioGroup.getChildAt(0))).setChecked(true);
            }
            if (assessmentList.get(songIter).getC() == ModificationType.TOO_MUCH) {
                ((RadioButton) (complexityRadioGroup.getChildAt(2))).setChecked(true);
            } else if (assessmentList.get(songIter).getC() == ModificationType.TOO_LOW) {
                ((RadioButton) (complexityRadioGroup.getChildAt(0))).setChecked(true);
            }
        }
        Button submitButton = (Button) dialog.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selection_heaviness = ((RadioButton) dialog.findViewById(heavinessRadioGroup.getCheckedRadioButtonId())).getText().toString();
                String selection_tempo = ((RadioButton) dialog.findViewById(tempoRadioGroup.getCheckedRadioButtonId())).getText().toString();
                String selection_complexity = ((RadioButton) dialog.findViewById(complexityRadioGroup.getCheckedRadioButtonId())).getText().toString();


                assessmentList.get(songIter).setH(ModificationType.getModificationType(selection_heaviness)); //getModPreferences(HEAVINESS_TYPE, selection_heaviness);
                assessmentList.get(songIter).setT(ModificationType.getModificationType(selection_tempo));//getModPreferences(TEMPO_TYPE, selection_tempo);
                assessmentList.get(songIter).setC(ModificationType.getModificationType(selection_complexity));//getModPreferences(COMPLEXITY_TYPE, selection_complexity);

                dialog.dismiss();

                mAssessmentItem.setIcon(R.drawable.selectdata_icon_checked);

                if (nextSong) {
                    updatePreferences(mMood, mExternalSongList.get(songIter),
                            assessmentList.get(songIter).getH().mod_name(),
                            assessmentList.get(songIter).getT().mod_name(),
                            assessmentList.get(songIter).getC().mod_name());
                    if (songIter == mExternalSongList.size() - 1) {
                        ConnectivityManager cm = (ConnectivityManager) getView().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo ni = cm.getActiveNetworkInfo();
                        if(ni!=null) {
                            if (ni.isConnected()) {
                                songIter++;
                                ProgressDialog progress = new ProgressDialog(thisFragment.getView().getContext());
                                progress.setTitle("Loading");
                                progress.setMessage("Getting a recommendation from internet...");
                                progress.show();
                                GetExternalRecommendation newrec = new GetExternalRecommendation(mMood, progress, thisFragment);
                            }
                            else{
                                Toast toast = Toast.makeText(getView().getContext(), "Can't get more recommendations without a network connection.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 50);
                                toast.show();
                            }
                        }
                        else{
                            Toast toast = Toast.makeText(getView().getContext(), "Can't get more recommendations without a network connection.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 50);
                            toast.show();
                        }
                    } else {
                        songIter++;
                        //setURL
                        updateUIFor(mExternalSongList.get(songIter));
                    }
                }
            }
        });

        dialog.show();
        return dialog;
    }

    protected void updatePreferences(MoodElement moodElement, Song song, String heaviness_pref, String tempo_pref, String complexity_pref){

        moodElement.UpdateAllPreferences(song,
                ModificationType.getModificationType(heaviness_pref),
                ModificationType.getModificationType(tempo_pref),
                ModificationType.getModificationType(complexity_pref), true);

        MainActivity.dbhandler.updateMood(moodElement);

        ConnectivityManager cm = (ConnectivityManager) this.getView().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni!=null) {
            if (ni.isConnected()) {
                SendExternalAssessment sendAssessmenttoExternDB =
                        new SendExternalAssessment(song,moodElement,
                                ModificationType.getModificationType(heaviness_pref).mod_id(),
                                ModificationType.getModificationType(tempo_pref).mod_id(),
                                ModificationType.getModificationType(complexity_pref).mod_id());
                if (Build.VERSION.SDK_INT >= 11) {
                    //--post GB use serial executor by default --
                    sendAssessmenttoExternDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                   sendAssessmenttoExternDB.execute();
                }
            }
        }

        mMood = moodElement;
    }


}

