package algorithm;

import android.os.AsyncTask;
import android.os.Build;

import java.util.Arrays;

public class MoodElement {
	
	public int modification_counter_h = 0;
	public int modification_counter_t = 0;
	public int modification_counter_c = 0;

    public int range_counter = 0;

	private final double TEMP = 0.98;

    private int id;
    private int mood_position;
	private Preference MoodElementPreference;
	private String mood_name;
    private String mood_colour;

    /*TODO Create a colour object for mood instead of just string*/
	public MoodElement( MoodType mood_type, Preference preference, int position )
	{
		mood_name = mood_type.mood_name();
		mood_colour = mood_type.mood_colour();
		double heaviness = preference.heaviness() + mood_type.heaviness_factor();
		double tempo = preference.tempo() + mood_type.tempo_factor();
		double complexity = preference.complexity() + mood_type.complexity_factor();
		MoodElementPreference = new Preference( heaviness, tempo, complexity );

        mood_position = position;
	}

    public MoodElement( String name, Preference preference, String colour, int pos)
    {
        mood_name = name;
        MoodElementPreference = preference;
        mood_colour = colour;
        mood_position = pos;
    }

    /*TODO Create a colour object for mood instead of just string*/
    public MoodElement( int id, String name, Preference preference, int mod_counter_h, int mod_counter_t, int mod_counter_c, String colour, int pos )
    {
        this.id = id;
        mood_name = name;
        MoodElementPreference = preference;
        modification_counter_h = mod_counter_h;
        modification_counter_t = mod_counter_t;
        modification_counter_c = mod_counter_c;
        mood_colour = colour;
        mood_position = pos;
    }

    public int id() { return id; }

    public void setID( int id )
    {
        this.id = id;
    }

	public String mood_name()
	{
		return mood_name;
	}

	public double heaviness()
	{
		return MoodElementPreference.heaviness();
	}

	public double UpdateHeaviness( Song song, ModificationType mod_type, boolean MoodOnly )
	{
		double heaviness;
		double heaviness_new = song.heaviness();
		double heaviness_factor = SongToUserInfluenceFactor( modification_counter_h, heaviness(), heaviness_new, mod_type );

        heaviness = heaviness() + heaviness_factor;

        if(!MoodOnly)
            song.UpdateHeaviness( heaviness(), mod_type, modification_counter_h );

        MoodElementPreference.SetHeaviness( heaviness );
		
		modification_counter_h++;

        return heaviness_factor;
	}

	public double tempo()
	{
		return MoodElementPreference.tempo();
	}
	
	public double UpdateTempo( Song song, ModificationType mod_type, boolean MoodOnly )
	{
		double tempo;
		double tempo_new = song.tempo();
		double tempo_factor = SongToUserInfluenceFactor( modification_counter_t, tempo(), tempo_new, mod_type );

		tempo = tempo() + tempo_factor;

        if(!MoodOnly)
    		song.UpdateTempo( tempo(), mod_type, modification_counter_t );

        MoodElementPreference.SetTempo( tempo );

		modification_counter_t++;

        return tempo_factor;
	}
	
	public double complexity()
	{
		return MoodElementPreference.complexity();
	}
	
	public double UpdateComplexity( Song song, ModificationType mod_type, boolean MoodOnly )
	{
		double complexity;
		double complexity_new = song.complexity();
		double complexity_factor = SongToUserInfluenceFactor( modification_counter_c, complexity(), complexity_new, mod_type );

		complexity = complexity() + complexity_factor;

        if(!MoodOnly)
            song.UpdateComplexity( complexity(), mod_type, modification_counter_c );

        MoodElementPreference.SetComplexity( complexity );

		modification_counter_c++;

        return complexity_factor;
	}

	public Preference preference()
	{
		return MoodElementPreference;
	}

    public String mood_colour(){ return mood_colour; }

    public int mood_position(){ return mood_position; }

    public void UpdateAllPreferences(Song song, ModificationType mod_heavy, ModificationType mod_tempo, ModificationType mod_complexity, boolean MoodOnly)
    {
        double heaviness_factor = UpdateHeaviness(song, mod_heavy, MoodOnly);
        double tempo_factor = UpdateTempo(song, mod_tempo, MoodOnly);
        double complexity_factor = UpdateComplexity(song, mod_complexity, MoodOnly);

        boolean isPerfect = (mod_heavy == ModificationType.PERFECT && mod_tempo == ModificationType.PERFECT && mod_tempo == ModificationType.PERFECT);

        if(isPerfect)
            range_counter++;

        AsyncTabuMod async_update = new AsyncTabuMod(this, heaviness_factor, tempo_factor, complexity_factor);
        if (Build.VERSION.SDK_INT >= 11) {
            //--post GB use serial executor by default --
            async_update.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            async_update.execute();
        }
    }

	private double SongToUserInfluenceFactor(double counter, double old_value, double new_value, ModificationType mod_type )
	{
		double influence_factor = 0;
		
		switch( mod_type ){
		
			case PERFECT:
			{
				influence_factor = ( old_value + new_value ) / 2 - old_value;
				break;
			}
			case TOO_LOW:
			{
				influence_factor = Math.abs( new_value - old_value );
                if(influence_factor<1.0){
                    //Preference is incorrect, compensate
                    influence_factor = 2.0;
                }
				break;
			}
			case TOO_MUCH:
			{
				influence_factor = -Math.abs( new_value - old_value );
                if(influence_factor>-1.0){
                    //Preference is incorrect, compensate
                    influence_factor = -2.0;
                }
				break;
			}
		}
		
		double result = Math.pow(TEMP, counter) * influence_factor;
		
		return result;
	}
}
