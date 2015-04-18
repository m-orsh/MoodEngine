package algorithm;

public class Song {

    //In case we use something like this later
    /*public enum AnalysisState{
        NOT_ANALYZED(0),
        ANALYZED_BY_ECHO_NEST(1),
        ANALYZED_BY_EXTERNAL_DATABASE(2);

        private int num = 0;

        private AnalysisState(int num)
        {
            this.num = num;
        }

        public int num(){ return num; }
    };*/
	
	private Preference song_preference;
	
	private int song_counter = 0;
    private int id = 0;
	private final double TEMP = 0.98;
	private final double USER_INFLUENCE = 0.95;
	private String name = "";
    private String artist = "";
    private int duration = 0;
    private String fileid = "";
    private int analysis_state = 0;

    public Song( int id, String name , String artist, double heaviness, double tempo, double complexity, int song_counter, int duration , String fileid, int analysis_state){

        this.id = id;
        this.name = name;
        this.artist = artist;
        this.song_preference = new Preference( heaviness, tempo, complexity );
        this.song_counter = song_counter;
        this.duration = duration;
        this.fileid = fileid;
        this.analysis_state = analysis_state;
    }

	public Song( String name , String artist, Preference song_preference ){

        this.song_preference = song_preference;
        this.name = name;
        this.artist = artist;
    }

    public Song( String name , String artist, double heaviness, double tempo, double complexity, String duration, String fileid, int analysis_state){

        this.name = name;
        this.artist = artist;
        this.duration = Integer.parseInt(duration);
        this.fileid = fileid;
        this.analysis_state = analysis_state;
        this.song_preference = new Preference( heaviness, tempo, complexity);
    }
	
	public Preference preference(){
		return song_preference;
	}
	
	public int counter()
	{
		return song_counter;
	}

    public void setCounter(int new_counter){
        this.song_counter = new_counter;
    }

    public int id(){ return id; }

	public String name(){
		return name;
	}

    public String artist(){
        return artist;
    }

    public String fileid(){
        return fileid;
    }

    public int analysis_state() { return analysis_state; }

    public void setAnalysisState(int analysis_state )
    {
        this.analysis_state = analysis_state;
    }

    public int duration(){
        return duration;
    }

	public double heaviness()
	{
		return song_preference.heaviness();
	}

    public void setHeaviness(double heaviness){ this.song_preference.SetHeaviness(heaviness); }

    public void setTempo(double tempo){ this.song_preference.SetTempo(tempo); }

    public void setComplexity(double complexity){ this.song_preference.SetComplexity(complexity); }
	
	public void UpdateHeaviness( double heaviness_new, ModificationType mod_type, int user_counter )
	{
		double heaviness;
		
		heaviness = heaviness() + UserToSongInfluenceFactor( user_counter, heaviness(), heaviness_new, mod_type );
		song_preference.SetHeaviness( heaviness );

		song_counter++;
	}
	
	public double tempo()
	{
		return song_preference.tempo();
	}
	
	public void UpdateTempo( double tempo_new, ModificationType mod_type, int user_counter )
	{
		double tempo;
		
		tempo = tempo() + UserToSongInfluenceFactor( user_counter, tempo(), tempo_new, mod_type );
		song_preference.SetTempo( tempo );

		song_counter++;
	}
	
	public double complexity()
	{
		return song_preference.complexity();
	}
	
	public void UpdateComplexity( double complexity_new, ModificationType mod_type, int user_counter )
	{
		double complexity;
		
		complexity = complexity() + UserToSongInfluenceFactor( user_counter, complexity(), complexity_new, mod_type );
		song_preference.SetComplexity( complexity );

		song_counter++;
	}
	
	private double UserToSongInfluenceFactor( int user_counter, double old_value, double new_value, ModificationType mod_type )
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
				influence_factor = -Math.abs( new_value - old_value );
                if(influence_factor>-1.0){
                    //Preference is incorrect, compensate
                    influence_factor = -2.0;
                }
                break;
			}
			case TOO_MUCH:
			{
				influence_factor = Math.abs( new_value - old_value );
                if(influence_factor<1.0){
                    //Preference is incorrect, compensate
                    influence_factor = 2.0;
                }
				break;
			}
		}
		
		double result = //( 1 - Math.pow( USER_INFLUENCE, user_counter ) ) *
		 Math.pow(TEMP, song_counter) * influence_factor;
		
		return result;
	}
}
