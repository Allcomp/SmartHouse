package cz.allcomp.shs.behaviour;

public abstract class MacroEntry implements Runnable {

	protected final long delay;
	protected final MacroEntryType type;
	
	public MacroEntry(MacroEntryType type, long delay) {
		this.delay = delay;
		this.type = type;
	}
	
	public long getDelay() {
		return this.delay;
	}
	
	public MacroEntryType getType() {
		return this.type;
	}
	
	@Override
	public final void run() {
		this.execute();
	}
	
	public abstract void execute();
}
