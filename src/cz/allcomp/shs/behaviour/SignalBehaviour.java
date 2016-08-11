/**
 * Copyright (c) 2015, Václav Vilímek
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	- Redistributions of source code must retain the above copyright notice, this list 
 * 	  of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright , this list 
 *    of conditions and the following disclaimer in the documentation and/or other materials 
 *    provided with the distribution.
 *  - Neither the name of the ALLCOMP a.s. nor the of its contributors may be used to endorse 
 *    or promote products from this software without specific prior written permission.
 *    
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL VÁCLAV VILÍMEK BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package cz.allcomp.shs.behaviour;

import cz.allcomp.shs.ewc.EwcManager;
import cz.allcomp.shs.ewc.EwcUnit;
import cz.allcomp.shs.ewc.EwcUnitInput;
import cz.allcomp.shs.ewc.EwcUnitOutput;
import cz.allcomp.shs.ewc.EwcValueType;
import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.states.SwitchState;

public class SignalBehaviour extends Behaviour {

	protected short inputEWC;
	protected int delay;
	protected SignalBehaviourType type;
	
	protected int pirBehaviourDelayCounter;

	public SignalBehaviour(EwcManager ewcManager, short inputEWC, int delay, SignalBehaviourType type, short outputEWC, 
			BehaviourMetadata metadata, BehaviourConditions turnOnConditions, 
			BehaviourConditions turnOffConditions) {
		super(ewcManager, outputEWC, metadata, turnOnConditions, turnOffConditions);
		
		this.inputEWC = inputEWC;
		this.delay = delay;
		this.type = type;
		this.pirBehaviourDelayCounter = 0;
	}
	
	public short getInputEWC() {
		return inputEWC;
	}
	
	public SignalBehaviourType getType() {
		return this.type;
	}
	
	public int getDelay() {
		return this.delay;
	}

	@Override
	public void execute() {
		
		switch(this.getType()) {
			case BUTTON:
				this.buttonBehaviour();
				break;
			case SWITCH:
				this.switchBehaviour();
				break;
			case TIMED:
				this.timedBehaviour();
				break;
			case PIR_SENSOR:
				this.pirSensorBehaviour();
				break;
			case NEGATION:
				this.negationBehaviour();
				break;
			case ON_ONLY_NEGATION:
				this.onOnlyNegationBehaviour();
				break;
			case REGULATOR:
				this.regulatorBehaviour();
				break;
			case THERMOSTAT:
				this.thermostatBehaviour();
				break;
			case VIRTUAL_PWM:
				this.virtualPwmBehaviour();
				break;
			case SUNBLIND_BUTTON:
				this.sunblindButtonBehaviour();
				break;
			case ON_ONLY_ACTIVATION:
				this.onOnlyActivationBehaviour();
				break;
			case REVERSE_SWITCH:
				this.reverseSwitchBehaviour();
				break;
			case PWM_WATCHER:
				this.pwmWatcherBehaviour();
				break;
			case TIMED_BUTTON:
				this.timedButtonBehaviour();
				break;
			case OVERCONTROL_BUTTON:
				this.overcontrolButtonBehaviour();
				break;
			case AIR_COND_INPUT_WATCHER:
				this.airCondIWBehaviour();
				break;
			case AIR_COND_VIRTUAL_WATCHER:
				this.airCondVWBehaviour();
				break;
		default:
			break;
		}
	}

	@Override
	public boolean shouldExecute() {
		return true;
	}
	
	private void buttonBehaviour() {
		try {
			EwcManager em = this.ewcManager;
			EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
			EwcUnit inputEwc = (EwcUnitInput) em.getEwcUnitBySoftwareId(this.inputEWC);
			
			short valueON;
			short valueOFF;
			
			try {
				valueON = Short.parseShort(this.getMetadata().getValue("valueON"));
				valueOFF = Short.parseShort(this.getMetadata().getValue("valueOFF"));
			} catch(NumberFormatException e) {
				valueON = 1;
				valueOFF = 0;
			}
			
			if(inputEwc.getStateValue() == SwitchState.ON.toInt()) {
				if(outputEwc.getStateValue() == valueOFF && this.turnOnConditions.isTrue()) {
					outputEwc.setStateValue(valueON);
				} else if(outputEwc.getStateValue() == valueON) {
					if(this.delay > 0)
						Thread.sleep(this.delay);
					if(this.turnOffConditions.isTrue())
						outputEwc.setStateValue(valueOFF);
				}
			}
		} catch (Exception e) {
			Messages.error("Error occured!");
			Messages.error(Messages.getStackTrace(e));
		}
	}
	
	private void switchBehaviour() {
		try {
			EwcManager em = this.ewcManager;
			EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
			EwcUnit inputEwc = (EwcUnitInput) em.getEwcUnitBySoftwareId(this.inputEWC);
			
			short valueON;
			short valueOFF;
			
			try {
				valueON = Short.parseShort(this.getMetadata().getValue("valueON"));
				valueOFF = Short.parseShort(this.getMetadata().getValue("valueOFF"));
			} catch(NumberFormatException e) {
				valueON = 1;
				valueOFF = 0;
			}
			
			if(inputEwc.getStateValue() == SwitchState.ON.toInt()) {
				if(outputEwc.getStateValue() == valueOFF) {
					if(this.turnOnConditions.isTrue())
						outputEwc.setStateValue(valueON);
				}
			} else if(inputEwc.getStateValue() == SwitchState.OFF.toInt()) {
				if(outputEwc.getStateValue() == valueON) {
					if(this.delay > 0)
						Thread.sleep(this.delay);
					if(this.turnOffConditions.isTrue())
						outputEwc.setStateValue(valueOFF);
				}
			}
		} catch (Exception e) {
			Messages.error("Error occured!");
			Messages.error(Messages.getStackTrace(e));
		}
	}
	
	private void timedBehaviour() {
		try {
			EwcManager em = this.ewcManager;
			EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
			
			short valueON;
			short valueOFF;
			
			try {
				valueON = Short.parseShort(this.getMetadata().getValue("valueON"));
				valueOFF = Short.parseShort(this.getMetadata().getValue("valueOFF"));
			} catch(NumberFormatException e) {
				valueON = 1;
				valueOFF = 0;
			}
			
			if(outputEwc.getStateValue() == valueOFF && this.turnOnConditions.isTrue()) {
				outputEwc.setStateValue(valueON);
				if(delay > 0)
					Thread.sleep(delay);
				outputEwc.setStateValue(valueOFF);
			}
		} catch (Exception e) {
			Messages.error("Error occured!");
			Messages.error(Messages.getStackTrace(e));
		}
	}
	
	private void pirSensorBehaviour() {
		try {
			EwcManager em = this.ewcManager;
			EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
			EwcUnit inputEwc = (EwcUnitInput) em.getEwcUnitBySoftwareId(this.inputEWC);
			
			short valueON;
			short valueOFF;
			
			try {
				valueON = Short.parseShort(this.getMetadata().getValue("valueON"));
				valueOFF = Short.parseShort(this.getMetadata().getValue("valueOFF"));
			} catch(NumberFormatException e) {
				valueON = 1;
				valueOFF = 0;
			}
			
			if(!outputEwc.isOvercontroled()) {
				if(inputEwc.getStateValue() == SwitchState.OFF.toInt()) {
					if(this.turnOnConditions.isTrue()) {
						outputEwc.setStateValue(valueON);
						this.pirBehaviourDelayCounter++;
					}
				} else {
					if(this.delay > 0) {
						Thread.sleep(this.delay);
					}
					if(this.turnOffConditions.isTrue())
						this.pirBehaviourDelayCounter--;
					if(this.pirBehaviourDelayCounter < 0) 
						this.pirBehaviourDelayCounter = 0;
					if(this.pirBehaviourDelayCounter == 0) {
						outputEwc.setStateValue(valueOFF);
					}
				}
			}
		} catch (Exception e) {
			Messages.error("Error occured!");
			Messages.error(Messages.getStackTrace(e));
		}
	}

	private void negationBehaviour() {
		EwcManager em = this.ewcManager;
		EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
		EwcUnit inputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.inputEWC);

		if(inputEwc.getStateValue() == 1)
			if(this.turnOffConditions.isTrue())
				outputEwc.setStateValue((short)0);
		if(inputEwc.getStateValue() == 0)
			if(this.turnOnConditions.isTrue())
				outputEwc.setStateValue((short)1);
	}

	private void onOnlyNegationBehaviour() {
		EwcManager em = this.ewcManager;
		EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
		EwcUnit inputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.inputEWC);

		if(inputEwc.getStateValue() == SwitchState.ON.toInt())
			if(this.turnOffConditions.isTrue())
				outputEwc.setStateValue((short)SwitchState.OFF.toInt());
	}

	boolean regulator_doLogic = false;
	boolean regulator_doingLogic = false;
	boolean regulator_increasing = true;
	int regulator_currentValue = 0;
	int regulator_lastValue = 0;
	long regulator_startTime = 0;
	
	public void setLastRegulatorValue(int val) {
		this.regulator_lastValue = val;
	}
	
	private void regulatorBehaviour() {
		EwcManager em = this.ewcManager;
		EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
		EwcUnit inputEwc = (EwcUnitInput) em.getEwcUnitBySoftwareId(this.inputEWC);

		this.regulator_currentValue = outputEwc.getStateValue();
		
		if(outputEwc.getValueType() != EwcValueType.PWM)
			return;
		
		if(inputEwc.getStateValue() == SwitchState.ON.toInt()) {
			if(this.regulator_doingLogic)
				this.regulator_doLogic = false;
			else {
				if(this.turnOnConditions.isTrue()) {
					this.regulator_doLogic = true;
					this.regulator_doingLogic = true;
					this.regulator_startTime = System.currentTimeMillis();
					while(true) {
						if(!this.regulator_doLogic) {
							if(this.turnOffConditions.isTrue())
								break;
							else
								this.regulator_doLogic = true;
						}
						if(System.currentTimeMillis() - this.regulator_startTime < 1000)
							continue;
						if(this.regulator_increasing) {
							this.regulator_currentValue++;
							if(this.regulator_currentValue >= 100) {
								this.regulator_currentValue = 100;
								this.regulator_increasing = false;
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						} else {
							this.regulator_currentValue--;
							if(this.regulator_currentValue <= 0) {
								this.regulator_currentValue = 0;
								this.regulator_increasing = true;
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						outputEwc.setStateValue((short)this.regulator_currentValue);
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				if(System.currentTimeMillis() - this.regulator_startTime < 1000) {
					if(outputEwc.getStateValue() <= 0) //<=10
						outputEwc.setStateValue((short)this.regulator_lastValue);
					else
						outputEwc.setStateValue((short)0);
				} else {
					this.regulator_lastValue = this.regulator_currentValue;
				}
				
				this.regulator_doingLogic = false;
			}
		} else if(inputEwc.getStateValue() == SwitchState.OFF.toInt()) {
			this.regulator_doLogic = false;
		}
	}
	
	private void thermostatBehaviour() {
		EwcManager em = this.ewcManager;
		EwcUnitOutput outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
		EwcUnitInput inputEwc = (EwcUnitInput) em.getEwcUnitBySoftwareId(this.inputEWC);
		
		double hystUp;
		double hystDown;
		
		try {
			hystUp = Double.parseDouble(this.getMetadata().getValue("hystUp"));
			hystDown = Double.parseDouble(this.getMetadata().getValue("hystDown"));
		} catch(NumberFormatException e) {
			hystUp = 1.0;
			hystDown = 1.0;
		}
		
		if(inputEwc.getTemperatureCelsius() < (inputEwc.getTargetTemperatureCelsius() - hystDown)) {
			if(outputEwc.getStateValue() == SwitchState.OFF.toInt())
				if(this.turnOnConditions.isTrue())
					outputEwc.setStateValue((short)SwitchState.ON.toInt());
		}
		
		if(inputEwc.getTemperatureCelsius() > (inputEwc.getTargetTemperatureCelsius() + hystUp)) {
			if(outputEwc.getStateValue() == SwitchState.ON.toInt())
				if(this.turnOffConditions.isTrue())
					outputEwc.setStateValue((short)SwitchState.OFF.toInt());
		}
	}
	
	private void virtualPwmBehaviour() {
		EwcManager em = this.ewcManager;
		EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
		EwcUnit inputEwc = (EwcUnitInput) em.getEwcUnitBySoftwareId(this.inputEWC);
		
		if(inputEwc.getStateValue() >= 0 && inputEwc.getStateValue() <= 100) {
			outputEwc.setStateValue(inputEwc.getStateValue());
		}
	}
	
	private void sunblindButtonBehaviour() {
		try {
			EwcManager em = this.ewcManager;
			EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
			EwcUnit inputEwc = (EwcUnitInput) em.getEwcUnitBySoftwareId(this.inputEWC);
			
			short valueON;
			short valueOFF;
			
			try {
				valueON = Short.parseShort(this.getMetadata().getValue("valueON"));
				valueOFF = Short.parseShort(this.getMetadata().getValue("valueOFF"));
			} catch(NumberFormatException e) {
				valueON = 1;
				valueOFF = 0;
			}
			
			if(inputEwc.getStateValue() == SwitchState.ON.toInt()) {
				if(outputEwc.getStateValue() == valueOFF) {
					//Thread.sleep(1000);
					if(this.turnOnConditions.isTrue())
						outputEwc.setStateValue(valueON);
				} else {
					if(this.turnOffConditions.isTrue())
						outputEwc.setStateValue(valueOFF);
				}
			} else if(inputEwc.getStateValue() == SwitchState.OFF.toInt()) {
				if(outputEwc.getStateValue() == valueON) {
					if(inputEwc.getLastActiveTime() >= delay) {
						Thread.sleep(120000);
						if(this.turnOffConditions.isTrue())
							outputEwc.setStateValue(valueOFF);
					} else {
						if(this.turnOffConditions.isTrue())
							outputEwc.setStateValue(valueOFF);
					}
				}
			}
		} catch (Exception e) {
			Messages.error("Error occured!");
			Messages.error(Messages.getStackTrace(e));
		}
	}

	private void onOnlyActivationBehaviour() {
		EwcManager em = this.ewcManager;
		EwcUnit inputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.inputEWC);
		EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);

		if(inputEwc.getStateValue() == SwitchState.ON.toInt())
			if(this.turnOffConditions.isTrue())
				outputEwc.setStateValue((short)SwitchState.ON.toInt());
	}

	private void reverseSwitchBehaviour() {
		try {
			EwcManager em = this.ewcManager;
			EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
			EwcUnit inputEwc = (EwcUnitInput) em.getEwcUnitBySoftwareId(this.inputEWC);
			
			short valueON;
			short valueOFF;
			
			try {
				valueON = Short.parseShort(this.getMetadata().getValue("valueON"));
				valueOFF = Short.parseShort(this.getMetadata().getValue("valueOFF"));
			} catch(NumberFormatException e) {
				valueON = 1;
				valueOFF = 0;
			}
			
			if(inputEwc.getStateValue() == SwitchState.ON.toInt()) {
				if(outputEwc.getStateValue() == valueON) {
					if(this.turnOnConditions.isTrue())
						outputEwc.setStateValue(valueOFF);
				}
			} else if(inputEwc.getStateValue() == SwitchState.OFF.toInt()) {
				if(outputEwc.getStateValue() == valueOFF) {
					if(this.delay > 0)
						Thread.sleep(this.delay);
					if(this.turnOffConditions.isTrue())
						outputEwc.setStateValue(valueON);
				}
			}
		} catch (Exception e) {
			Messages.error("Error occured!");
			Messages.error(Messages.getStackTrace(e));
		}
	}
	
	private void pwmWatcherBehaviour() {
		EwcManager em = this.ewcManager;
		EwcUnit outputEwc = em.getEwcUnitBySoftwareId(this.outputEWC);
		EwcUnit inputEwc = em.getEwcUnitBySoftwareId(this.inputEWC);

		this.regulator_currentValue = outputEwc.getStateValue();
		
		if(inputEwc.getValueType() != EwcValueType.PWM) {
			Messages.warning("(PWM Watcher) EWC Input " + inputEwc.getSoftwareId() + " is not PWM type!");
			return;
		}
		if(outputEwc.getValueType() != EwcValueType.DIGITAL) {
			Messages.warning("(PWM Watcher) EWC Output " + outputEwc.getSoftwareId() + " is not DIGITAL type!");
			return;
		}
		if(this.turnOnConditions.isTrue()) {
			outputEwc.setStateValue((short)SwitchState.ON.toInt());
		}
		if(this.turnOffConditions.isTrue()) {
			outputEwc.setStateValue((short)SwitchState.OFF.toInt());
		}
	}

	private boolean timedButtonBehaviour_shouldStop;
	private short timedButtonBehaviour_valueOFF;
	
	private void timedButtonBehaviour() {
		try {
			EwcManager em = this.ewcManager;
			EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
			EwcUnit inputEwc = (EwcUnitInput) em.getEwcUnitBySoftwareId(this.inputEWC);
			
			short valueON;
			short valueOFF;
			
			try {
				valueON = Short.parseShort(this.getMetadata().getValue("valueON"));
				valueOFF = Short.parseShort(this.getMetadata().getValue("valueOFF"));
			} catch(NumberFormatException e) {
				valueON = 1;
				valueOFF = 0;
			}
			
			this.timedButtonBehaviour_valueOFF = valueOFF;
			
			if(inputEwc.getStateValue() == SwitchState.ON.toInt()) {
				if(outputEwc.getStateValue() == valueOFF && this.turnOnConditions.isTrue()) {
					outputEwc.setStateValue(valueON);
					this.timedButtonBehaviour_shouldStop = false;
					new Thread(() -> {
						for(int i = 0; i < this.delay; i++) {
							try {
								Thread.sleep(1);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if(this.timedButtonBehaviour_shouldStop)
								return;
						}
						if(this.turnOffConditions.isTrue())
							outputEwc.setStateValue(this.timedButtonBehaviour_valueOFF);
					}).start();
				} else if(outputEwc.getStateValue() == valueON) {
					if(this.turnOffConditions.isTrue()) {
						outputEwc.setStateValue(valueOFF);
						this.timedButtonBehaviour_shouldStop = true;
					}
				}
			}
		} catch (Exception e) {
			Messages.error("Error occured!");
			Messages.error(Messages.getStackTrace(e));
		}
	}
	
	private void overcontrolButtonBehaviour() {
		try {
			EwcManager em = this.ewcManager;
			EwcUnit outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
			EwcUnit inputEwc = (EwcUnitInput) em.getEwcUnitBySoftwareId(this.inputEWC);
			
			short valueON;
			short valueOFF;
			
			try {
				valueON = Short.parseShort(this.getMetadata().getValue("valueON"));
				valueOFF = Short.parseShort(this.getMetadata().getValue("valueOFF"));
			} catch(NumberFormatException e) {
				valueON = 1;
				valueOFF = 0;
			}
			
			if(inputEwc.getStateValue() == SwitchState.ON.toInt()) {
				if(outputEwc.getStateValue() == valueOFF && this.turnOnConditions.isTrue()) {
					outputEwc.setStateValue(valueON);
					outputEwc.setOvercontroled(true);
				} else if(outputEwc.getStateValue() == valueON) {
					if(this.delay > 0)
						Thread.sleep(this.delay);
					if(this.turnOffConditions.isTrue()) {
						outputEwc.setStateValue(valueOFF);
						outputEwc.setOvercontroled(false);
					}
				}
			}
		} catch (Exception e) {
			Messages.error("Error occured!");
			Messages.error(Messages.getStackTrace(e));
		}
	}
	
	private void airCondIWBehaviour() {
		try {
			EwcManager em = this.ewcManager;
			EwcUnit outputEwc = em.getEwcUnitBySoftwareId(this.outputEWC);
			EwcUnit inputEwc = em.getEwcUnitBySoftwareId(this.inputEWC);
			
			if(!outputEwc.isVirtual()) {
				Messages.warning("(Air Condition IWB): OUTPUT " + outputEwc.getInputOutputId() + " is not virtual!");
				return;
			}
			
			if(inputEwc.getStateValue() == SwitchState.ON.toInt())
				outputEwc.setStateValueWithoutChange((short)SwitchState.ON.toInt());
			else {
				Thread.sleep(1200);
				if(inputEwc.getStateValue() == SwitchState.OFF.toInt())
					outputEwc.setStateValueWithoutChange((short)SwitchState.OFF.toInt());
			}
			
		} catch (Exception e) {
			Messages.error("Error occured!");
			Messages.error(Messages.getStackTrace(e));
		}
	}
	
	private void airCondVWBehaviour() {
		try {
			EwcManager em = this.ewcManager;
			EwcUnit outputEwc = em.getEwcUnitBySoftwareId(this.outputEWC);
			//EwcUnit inputEwc = em.getEwcUnitBySoftwareId(this.inputEWC);
			
			if(outputEwc.getStateValue() == SwitchState.ON.toInt())
				return;
			
			outputEwc.setStateValue((short)SwitchState.ON.toInt());
			Thread.sleep(2000);
			outputEwc.setStateValue((short)SwitchState.OFF.toInt());
			
		} catch (Exception e) {
			Messages.error("Error occured!");
			Messages.error(Messages.getStackTrace(e));
		}
	}
}
