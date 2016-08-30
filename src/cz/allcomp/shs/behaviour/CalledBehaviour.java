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
import cz.allcomp.shs.ewc.EwcUnitOutput;
import cz.allcomp.shs.logging.Messages;

public class CalledBehaviour extends Behaviour {

	protected int delay;
	protected CalledBehaviourType type;
	private boolean shouldStop;
	private int securityId;
	
	public CalledBehaviour(EwcManager ewcManager, int delay, CalledBehaviourType type, short outputEWC, 
			BehaviourMetadata metadata, BehaviourConditions turnOnConditions, 
			BehaviourConditions turnOffConditions, int securityId) {
		super(ewcManager, outputEWC, metadata, turnOnConditions, turnOffConditions);
		
		this.delay = delay;
		this.type = type;
		this.shouldStop = false;
		this.securityId = securityId;
	}
	
	public CalledBehaviourType getType() {
		return this.type;
	}
	
	public int getDelay() {
		return this.delay;
	}
	
	public int getSecurityId() {
		return this.securityId;
	}
	
	public void interrupt() {
		this.shouldStop = true;
		EwcUnit output = this.ewcManager.getEwcUnitBySoftwareId(this.getOutputEWC());
		if(output != null)
			if(output instanceof EwcUnitOutput) {
				try {
					short valOff = Short.parseShort(this.metadata.getValue("valueOFF"));
					output.setStateValue(valOff);
				} catch (NumberFormatException e) {
					Messages.warning("Could not get value valueOFF from metadata!");
					Messages.warning(Messages.getStackTrace(e));
				}
			}
	}

	@Override
	public void execute() {
		this.shouldStop = false;
		
		if(this.delay > 0) {
			long numOfPeriods = (long)((double)this.delay/100.0);
			
			for(int i = 0; i < numOfPeriods; i++) {
				try {
					Thread.sleep(100);
					if(this.shouldStop)
						return;
				} catch (InterruptedException e) {
					Messages.warning("Could not sleep a thread!");
					Messages.warning(Messages.getStackTrace(e));
				}
			}
		}
		
		switch(this.type) {
			case ACTIVATE:
				this.activateBehaviour();
				break;
			case TIMED:
				this.timedBehaviour();
				break;
			case SIREN:
				this.sirenBehaviour();
				break;
		default:
			break;
		}
	}
	
	private void activateBehaviour() {
		EwcUnit output = this.ewcManager.getEwcUnitBySoftwareId(this.getOutputEWC());
		if(output != null)
			if(output instanceof EwcUnitOutput) {
				try {
					short valOn = Short.parseShort(this.metadata.getValue("valueON"));
					output.setStateValue(valOn);
				} catch (NumberFormatException e) {
					Messages.warning("Could not get value valueON from metadata!");
					Messages.warning(Messages.getStackTrace(e));
				}
			}
	}
	
	private void timedBehaviour() {
		EwcUnit output = this.ewcManager.getEwcUnitBySoftwareId(this.getOutputEWC());
		try {
			int duration = Integer.parseInt(this.metadata.getValue("duration"));
			if(output != null)
				if(output instanceof EwcUnitOutput) {
					try {
						short valOn = Short.parseShort(this.metadata.getValue("valueON"));
						short valOff = Short.parseShort(this.metadata.getValue("valueOFF"));
						output.setStateValue(valOn);
						
						if(duration > 0) {

							long numOfPeriods = (long)((double)duration/100.0);
							for(int i = 0; i < numOfPeriods; i++) {
								try {
									Thread.sleep(100);
									if(this.shouldStop)
										return;
								} catch (InterruptedException e) {
									Messages.warning("Could not sleep a thread!");
									Messages.warning(Messages.getStackTrace(e));
								}
							}
							
							output.setStateValue(valOff);
							
						} else {
							output.setStateValue(valOff);
						}
						
					} catch (NumberFormatException e) {
						Messages.warning("Could not get value valueON from metadata!");
						Messages.warning(Messages.getStackTrace(e));
					}
				}
		} catch (NumberFormatException e) {
			Messages.warning("Could not convert String to Integer!");
			Messages.warning(Messages.getStackTrace(e));
		}
		
	}
	
	private void sirenBehaviour() {
		EwcUnit output = this.ewcManager.getEwcUnitBySoftwareId(this.getOutputEWC());
		try {
			int timeOn = Integer.parseInt(this.metadata.getValue("timeON"));
			int timeOff = Integer.parseInt(this.metadata.getValue("timeOFF"));
			
			if(output != null)
				if(output instanceof EwcUnitOutput) {
					try {
						short valOn = Short.parseShort(this.metadata.getValue("valueON"));
						short valOff = Short.parseShort(this.metadata.getValue("valueOFF"));
						output.setStateValue(valOn);
						
						if(timeOn > 0 && timeOff > 0) {

							while(!this.shouldStop) {
							
								output.setStateValue(valOn);
								
								long numOfPeriodsTimeOn = (long)((double)timeOn/100.0);
								for(int i = 0; i < numOfPeriodsTimeOn; i++) {
									try {
										Thread.sleep(100);
										if(this.shouldStop)
											return;
									} catch (InterruptedException e) {
										Messages.warning("Could not sleep a thread!");
										Messages.warning(Messages.getStackTrace(e));
									}
								}
								
								output.setStateValue(valOff);
								
								long numOfPeriodsTimeOff = (long)((double)timeOff/100.0);
								for(int i = 0; i < numOfPeriodsTimeOff; i++) {
									try {
										Thread.sleep(100);
										if(this.shouldStop)
											return;
									} catch (InterruptedException e) {
										Messages.warning("Could not sleep a thread!");
										Messages.warning(Messages.getStackTrace(e));
									}
								}
							}
							
						} else {
							output.setStateValue(valOff);
						}
						
					} catch (NumberFormatException e) {
						Messages.warning("Could not get value valueON from metadata!");
						Messages.warning(Messages.getStackTrace(e));
					}
				}
		} catch (NumberFormatException e) {
			Messages.warning("Could not convert String to Integer!");
			Messages.warning(Messages.getStackTrace(e));
		}
	}

	@Override
	public boolean shouldExecute() {
		return true;
	}
}