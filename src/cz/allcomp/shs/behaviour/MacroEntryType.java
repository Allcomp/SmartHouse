package cz.allcomp.shs.behaviour;

public enum MacroEntryType {

	UNKNOWN(-1), VALUE(0), PULSE(1);
	
	private int id;
	
	private MacroEntryType(int id) {
		this.id = id;
	}
	
	public static MacroEntryType getByName(String name) {
		for(MacroEntryType mt : MacroEntryType.values())
			if(mt.toString().equalsIgnoreCase(name))
				return mt;
		return UNKNOWN;
	}
	
	public static MacroEntryType getById(int id) {
		for(MacroEntryType mt : MacroEntryType.values())
			if(mt.id == id)
				return mt;
		return UNKNOWN;
	}
	
	public static MacroEntryType get(String nameOrId) {
		try {
			int id = Integer.parseInt(nameOrId);
			MacroEntryType resType = MacroEntryType.getById(id);
			if(resType == MacroEntryType.UNKNOWN)
				return MacroEntryType.getByName(nameOrId);
			return resType;
		} catch (NumberFormatException e) {
			return MacroEntryType.getByName(nameOrId);
		}
	}
}
