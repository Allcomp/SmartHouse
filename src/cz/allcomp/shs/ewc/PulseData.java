package cz.allcomp.shs.ewc;

import cz.allcomp.shs.states.SwitchState;

public class PulseData {

	private float dutyCycle;
	private int frequency;

	private SwitchState phase;
	private SwitchState lastState;
	private long counter;
	
	public PulseData(float dutyCycle, int frequency) {
		this.dutyCycle = dutyCycle;
		this.frequency = frequency;
		this.phase = SwitchState.ON;
		this.lastState = SwitchState.ON;
		this.counter = 0;
	}

	public float getDutyCycle() {
		return dutyCycle;
	}

	public int getFrequency() {
		return frequency;
	}
	
	public SwitchState getState() {
		return this.lastState;
	}
	
	public boolean hasChanged() {
		if(this.lastState != this.phase) {
			this.lastState = this.phase;
			return true;
		}
		return false;
	}
	
	public void update() {
		if(counter < (dutyCycle/100f)*(1000f/(float)this.frequency))
			this.phase = SwitchState.ON;
		else
			this.phase = SwitchState.OFF;
		counter++;
		if(counter >= (1000f/(float)this.frequency))
			counter = 0;
	}
}
