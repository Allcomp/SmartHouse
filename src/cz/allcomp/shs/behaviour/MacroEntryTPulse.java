package cz.allcomp.shs.behaviour;

import cz.allcomp.shs.device.EwcUnit;
import cz.allcomp.shs.logging.Messages;

public class MacroEntryTPulse extends MacroEntry {

	private long duration;
	private long pause;
	private boolean reverse;
	private boolean overcontrol;
	private EwcUnit io;
	private int repeat;
	private boolean noChange;
	
	public MacroEntryTPulse(MacroEntryType type, long delay, EwcUnit io, boolean reverse, long duration,
			boolean overcontrol, int repeat, boolean noChange, long pause) {
		super(type, delay);
		
		this.duration = duration;
		this.reverse = reverse;
		this.overcontrol = overcontrol;
		this.io = io;
		this.repeat = repeat;
		this.noChange = noChange;
		this.pause = pause;
	}

	@Override
	public void execute() {
		if(this.overcontrol)
			this.io.setOvercontroled(true);
		for(int i = 0; i <= this.repeat; i++) {
			if(this.noChange)
				this.io.setStateValueWithoutChange(this.reverse ? (short)0 : (short)1);
			else
				this.io.setStateValue(this.reverse ? (short)0 : (short)1);
			
			try {
				Thread.sleep(this.duration);
			} catch (InterruptedException e) {
				Messages.warning(Messages.getStackTrace(e));
			}
			
			if(this.noChange)
				this.io.setStateValueWithoutChange(this.reverse ? (short)1 : (short)0);
			else
				this.io.setStateValue(this.reverse ? (short)1 : (short)0);
			
			try {
				Thread.sleep(this.pause);
			} catch (InterruptedException e) {
				Messages.warning(Messages.getStackTrace(e));
			}
		}
		if(this.overcontrol)
			this.io.setOvercontroled(false);
	}
	
	public EwcUnit getIO() {
		return this.io;
	}
	
	public boolean isWithoutChange() {
		return this.noChange;
	}
	
	public long getDuration() {
		return this.duration;
	}
	
	public boolean isOvercontroled() {
		return this.overcontrol;
	}
	
	public int getRepetitions() {
		return this.repeat;
	}
	
	public boolean isReversed() {
		return this.reverse;
	}
}
