package algorithm;

public enum MoodType{
    //"#ffc0392b", "#ffcf000f", "#ffe08283", "#ff674172", "#ff3a539b", "#ff26a65b", "#fff7ca18", "#fff9690e", "#ff674172"
	THOUGHTFUL( -3, 0, 3, "#ff26a65b", "Thoughtful" ),
	INSPIRED( 0, 3, 3, "#fff64747", "Inspired" ),
	ANGRY( 3, 4, 3, "#ffcf000f", "Angry" ),
	NOSTALGIC( -3, -3, 0, "#ff1abc9c", "Nostalgic" ),
	HAPPY( 0, 0, 0, "#fff7ca18", "Happy" ),
	EXCITED( 3, 3, 0, "#fff9690e", "Excited" ),
	SAD( -3, -4, -3, "#ff3a539b", "Sad" ),
	RELAXED( 0, -3, -3, "#ff3498db", "Relaxed" ),
	TIRED( 3, 0, -3, "#ff8e44ad", "Tired" );
	
	private final String mood_name;
    private final String mood_colour;
	private final double heaviness_factor;
	private final double tempo_factor;
	private final double complexity_factor;

    /*TODO Create a colour object for mood instead of just string*/
	MoodType( double heaviness_factor, double tempo_factor, double complexity_factor, String mood_colour, String mood_name )
	{
		this.mood_name = mood_name;
        this.mood_colour = mood_colour;
		this.heaviness_factor = heaviness_factor;
		this.tempo_factor = tempo_factor;
		this.complexity_factor = complexity_factor;
	}
	
	public final String mood_name(){ return mood_name; }
    public final String mood_colour(){ return mood_colour; }
	public final double heaviness_factor(){ return heaviness_factor; }
	public final double tempo_factor(){ return tempo_factor; }
	public final double complexity_factor(){ return complexity_factor; }
}