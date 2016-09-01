package cz.allcomp.shs.device;

public class SimulatorUnit {

	private final EwcUnitOutput output;
	private final long time;
	private final short targetState;
	
	public SimulatorUnit(EwcUnitOutput output, long time, short targetState) {
		this.output = output;
		this.time = time;
		this.targetState = targetState;
	}
	
	public long getTime() {
		return this.time;
	}
	
	public short getTargetState() {
		return this.targetState;
	}
	
	public EwcUnitOutput getOutput() {
		return this.output;
	}
	
	public void simulate(WorkingSimulator simulator) {
		simulator.simulate(this);
	}
}
