package Deprecated;

import java.util.ArrayList;

import algorithm.MoodElement;
import algorithm.Preference;
import algorithm.Song;
// This class gets a recommendation based on a list of songs in an array.
// Since the database is where we get stuff in the full app, we don't need this, but it's useful for testing purposes.
public class Recommendation {
	
	private final double TEMP = 0.98;
	
	public ArrayList<Song> recommendations = new ArrayList<Song>();
	
	public Recommendation( ArrayList<Song> song_list, MoodElement mood_element){
		
		ArrayList<Song> step2 = new ArrayList<Song>();
		ArrayList<Song> step3 = new ArrayList<Song>();
		
		Preference userPref = mood_element.preference();

		double range_value = PrefRangeValue( mood_element.modification_counter_h );
		
		for( Song song : song_list ){
			
			if(song.preference().heaviness() <= userPref.heaviness() + range_value &&
					song.preference().heaviness() >= userPref.tempo() - range_value ){
				step2.add(song);
			}
		}
		
		for( Song song : step2){
			
			if(song.preference().tempo() <= userPref.tempo() + range_value && 
					song.preference().tempo() >= userPref.tempo() - range_value ){
				step3.add(song);
			}
		}
		
		for( Song song : step3){
			
			if(song.preference().complexity() <= userPref.complexity() + range_value && 
				song.preference().complexity() >= userPref.complexity() - range_value ){
				recommendations.add(song);
			}
		}
	}
	
	private double PrefRangeValue( int counter )
	{
		return ( 2 *Math.pow(TEMP, counter ) + 1 );
	}
}
