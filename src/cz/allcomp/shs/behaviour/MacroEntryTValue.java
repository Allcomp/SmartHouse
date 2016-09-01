package cz.allcomp.shs.behaviour;

import cz.allcomp.shs.device.EwcUnit;

public class MacroEntryTValue extends MacroEntry {

	private short targetValue;
	private EwcUnit io;
	boolean noChange;
	
	public MacroEntryTValue(MacroEntryType type, long delay, short targetValue, 
			EwcUnit io, boolean noChange) {
		super(type, delay);
		this.targetValue = targetValue;
		this.io = io;
		this.noChange = noChange;
	}

	@Override
	public void execute() {
		if(this.noChange)
			this.io.setStateValueWithoutChange(this.targetValue);
		else
			this.io.setStateValue(this.targetValue);
	}
	
	public EwcUnit getIO() {
		return this.io;
	}

	public short getTargetValue() {
		return this.targetValue;
	}
	
	public boolean isWithoutChange() {
		return this.noChange;
	}
}
