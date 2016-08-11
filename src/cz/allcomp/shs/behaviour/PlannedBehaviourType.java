package cz.allcomp.shs.behaviour;

public enum PlannedBehaviourType {
	UNKNOWN(-1),
	ORDINARY(0),
	REPEATING(1);
	
	private int intVal;
	
	private PlannedBehaviourType(int intVal) {
		this.intVal = intVal;
	}
	
	public int toInt() {
		return this.intVal;
	}
	
	public static PlannedBehaviourType getByInt(int intVal) {
		for(PlannedBehaviourType bt : PlannedBehaviourType.values())
			if(bt.toInt() == intVal)
				return bt;
		return UNKNOWN;
	}
}
