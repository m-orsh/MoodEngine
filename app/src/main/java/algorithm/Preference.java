package algorithm;

public class Preference {
	
	private final double LOWER_BOUND = 0.0;
	private final double UPPER_BOUND = 10.0;
	
	private double heaviness;
	private double tempo;
	private double complexity;
	
	public Preference( double heaviness, double tempo, double complexity )
	{	
		this.heaviness = heaviness;
		this.tempo = tempo;
		this.complexity = complexity;
	}
	
	public double heaviness()
	{
		return heaviness;
	}
	
	public void SetHeaviness( double heaviness)
	{
		this.heaviness = heaviness;
		
		if( this.heaviness < LOWER_BOUND ) this.heaviness = 1;
		if( this.heaviness > UPPER_BOUND ) this.heaviness = 10;
	}
	
	public double tempo()
	{
		return tempo;
	}
	
	public void SetTempo( double tempo)
	{
		this.tempo = tempo;
		
		if( this.tempo < LOWER_BOUND ) this.tempo = 1;
		if( this.tempo > UPPER_BOUND ) this.tempo = 10;
	}
	
	public double complexity()
	{
		return complexity;
	}
	
	public void SetComplexity( double complexity)
	{
		this.complexity = complexity;
		
		if( this.complexity < LOWER_BOUND ) this.complexity = 1;
		if( this.complexity > UPPER_BOUND ) this.complexity = 10;
	}

}