package algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class MoodTable {
	
	private LinkedHashMap< String, MoodElement > mood_table;
	
	public MoodTable( Preference preference )
	{
		mood_table = new LinkedHashMap< String, MoodElement >();
		
		mood_table.put( MoodType.THOUGHTFUL.mood_name(), new MoodElement( MoodType.THOUGHTFUL, preference, 0 ) );
		mood_table.put( MoodType.INSPIRED.mood_name(), new MoodElement( MoodType.INSPIRED, preference, 1 ) );
		mood_table.put( MoodType.ANGRY.mood_name(), new MoodElement( MoodType.ANGRY, preference, 2 ) );
		mood_table.put( MoodType.NOSTALGIC.mood_name(), new MoodElement( MoodType.NOSTALGIC, preference, 3 ) );
		mood_table.put( MoodType.HAPPY.mood_name(), new MoodElement( MoodType.HAPPY, preference, 4 ) );
		mood_table.put( MoodType.EXCITED.mood_name(), new MoodElement( MoodType.EXCITED, preference, 5 ) );
		mood_table.put( MoodType.SAD.mood_name(), new MoodElement( MoodType.SAD, preference, 6 ) );
		mood_table.put( MoodType.RELAXED.mood_name(), new MoodElement( MoodType.RELAXED, preference, 7 ) );
		mood_table.put( MoodType.TIRED.mood_name(), new MoodElement( MoodType.TIRED, preference, 8 ) );
	}

    public MoodTable( List<MoodElement> moodList)
    {
        mood_table = new LinkedHashMap< String, MoodElement >();

        for( MoodElement mood: moodList ) {
            mood_table.put( mood.mood_name(), mood );
        }
    }

    public void addMood(MoodElement moodElement)
    {
        mood_table.put( moodElement.mood_name(), moodElement );
    }

	public MoodElement getMood ( String name )
	{
		for( String mood_name : mood_table.keySet() )
		{
			if( mood_name.equals(name) )
				return mood_table.get( name );
		}
		
		return null;
	}

    public Collection<MoodElement> getAllMoods()
    {
        return mood_table.values();
    }

    public MoodElement removeMood(MoodElement moodElement)
    {
        return mood_table.remove(moodElement.mood_name());
    }
}
