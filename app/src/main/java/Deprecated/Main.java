package Deprecated;
import java.util.ArrayList;

import algorithm.MoodElement;
import algorithm.MoodTable;
import algorithm.Preference;
import algorithm.Song;

//This was a testing point of entry before we had the app UI. You can use it to see how to get a recommendation from an arraylist using recommendation.


public class Main {
	public static Preference userpref;
	public static MoodTable table;
	//public static JFrame f;
	public static ArrayList<Song> listofsongs = new ArrayList<Song>();
	public static MoodElement moodpref;
	public static Recommendation rec;

	public static void main(String[] args) {

		listofsongs.add(new Song("Twisted Storm", "Kemilon" , new Preference(3,7,4)));
		listofsongs.add(new Song("Shy", "Sonata Arctica", new Preference(1,1,2)));
		listofsongs.add(new Song("Alice", "Sunn O)))", new Preference(9,4,4)));
		listofsongs.add(new Song("Boogie Man","Sid Phillips",new Preference(4,5,4)));
		listofsongs.add(new Song("A Thousand Miles", "Vanessa Carlton", new Preference(2,4,4)));
		listofsongs.add(new Song("Spirit Inspiration","Nothing's Carved in Stone", new Preference(6,7,4)));
		listofsongs.add(new Song("Iconoclast","Symphony X", new Preference(7,8,10)));
		listofsongs.add(new Song("Take One Breath", "Sonata Arctica", new Preference(6,5,6)));
		listofsongs.add(new Song("Masser","Jeremy Soule",new Preference(1,2,4)));
		listofsongs.add(new Song("Five Guys Named Moe", "Louis Jordan", new Preference(3,7,3)));
		listofsongs.add(new Song("Valestein Castle","Falcom Sound Team jdk",new Preference(7,7,6)));
		listofsongs.add(new Song("By Perseverance and Bloodshed","Primalfrost",new Preference(10,10,8)));
		listofsongs.add(new Song("I Choose You to Die","Starbomb",new Preference(5,5,5)));
		listofsongs.add(new Song("Highlander (The One)","Lost Horizon",new Preference(7,7,7)));
		listofsongs.add(new Song("Angel of Death","Slayer",new Preference(10,9,7)));
		listofsongs.add(new Song("Solitude","zircon & C-GPO",new Preference(5,6,7)));
		listofsongs.add(new Song("Shadows","Mark Moore",new Preference(8,6,7)));
		listofsongs.add(new Song("Into the Green World","Sam Dillard",new Preference(3,4,8)));
        listofsongs.add(new Song("Ashes of Dreams (New - English version)","岡部啓一",new Preference(1,1,1)));
		listofsongs.add(new Song("Melodies Of Life ~Final Fantasy","植松伸夫",new Preference(1,2,4)));
		listofsongs.add(new Song("Absolute Configuration","梶浦由記",new Preference(7,4,8)));
		listofsongs.add(new Song("Wareta Ringo (TV edit)","Shigeo Komori",new Preference(3,5,4)));
		listofsongs.add(new Song("Hyrule Field Main Theme","近藤浩治",new Preference(4,6,9)));
        listofsongs.add(new Song("Machi, Toki no Nagare, Hito", "折戸伸治", new Preference(1,2,3)));
        listofsongs.add(new Song("Tori no Uta (Off Vocal)", "折戸伸治", new Preference(1,2,5)));
        listofsongs.add(new Song("Before my Body is Dry","澤野弘之",new Preference(6,5,4)));
        listofsongs.add(new Song("Tenacity","桜庭統",new Preference(6,8,7)));
		listofsongs.add(new Song("San Sebastian","Sonata Arctica",new Preference(8,10,8)));
		listofsongs.add(new Song("Majora's Wrath","Theophany",new Preference(6,2,10)));
		listofsongs.add(new Song("Red Glowing Dust","CCP Games",new Preference(2,1,3)));
		listofsongs.add(new Song("Symphony No. 9, \"From the New World\": Largo","Antonín Dvořák",new Preference(2,1,10)));
		
		//SendLibraryMetadata data = new SendLibraryMetadata(listofsongs);
		
		Recommendation rec = new Recommendation(listofsongs,moodpref);
	}
}
