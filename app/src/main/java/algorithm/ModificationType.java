package algorithm;

public enum ModificationType{

	PERFECT("Perfect", 0),
    TOO_LOW("Too Low", 1),
	TOO_MUCH("Too Much", 2);

    public static final String TOO_LOW_MOD = "Too Low";
    public static final String PERFECT_MOD = "Perfect";
    public static final String TOO_MUCH_MOD = "Too Much";

	private final String mod_name;
	private final int mod_id;

	ModificationType( String mod_name, int mod_id )
    {
		this.mod_name = mod_name;
        this.mod_id = mod_id;
	}

    public String mod_name(){ return mod_name; };
    public int mod_id(){ return mod_id; };

    public static ModificationType getModificationType(String modification_type)
    {
        ModificationType type = null;

        if (modification_type.equals(TOO_LOW_MOD)) {
            type = ModificationType.TOO_LOW;
        } else if (modification_type.equals(PERFECT_MOD)) {
            type = ModificationType.PERFECT;
        } else if (modification_type.equals(TOO_MUCH_MOD)) {
            type = ModificationType.TOO_MUCH;
        }

        return type;
    }



}
