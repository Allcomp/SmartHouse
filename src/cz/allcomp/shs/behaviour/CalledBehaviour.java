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

import java.io.IOException;
import java.text.Normalizer;

import cz.allcomp.shs.device.EwcManager;
import cz.allcomp.shs.device.EwcUnit;
import cz.allcomp.shs.device.GSM;
import cz.allcomp.shs.device.SecuritySystem;
import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.states.SwitchState;

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
		if(output != null) {
			short valOff = this.metadata.getShort("valueOff", SwitchState.OFF.toShort());
			output.setStateValue(valOff);
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
			case GSM_SMS:
				this.gsmSMSBehaviour();
				break;
		default:
			break;
		}
	}

	private void activateBehaviour() {
		EwcUnit output = this.ewcManager.getEwcUnitBySoftwareId(this.getOutputEWC());
		if(output == null) {
			Messages.error("<CalledBehaviour->Activate> Output " + this.getOutputEWC() + " does not exist!");
			return;
		}
		short valOn = this.metadata.getShort("valueOn", SwitchState.ON.toShort());
		output.setStateValue(valOn);
	}
	
	private void timedBehaviour() {
		EwcUnit output = this.ewcManager.getEwcUnitBySoftwareId(this.getOutputEWC());
		if(output == null) {
			Messages.error("<CalledBehaviour->Timed> Output " + this.getOutputEWC() + " does not exist!");
			return;
		}

		int duration = this.metadata.getInt("duration", 0);
		short valOn = this.metadata.getShort("valueOn", SwitchState.ON.toShort());
		short valOff = this.metadata.getShort("valueOff", SwitchState.OFF.toShort());
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
	}
	
	private void sirenBehaviour() {
		EwcUnit output = this.ewcManager.getEwcUnitBySoftwareId(this.getOutputEWC());
		if(output == null) {
			Messages.error("<CalledBehaviour->Siren> Output " + this.getOutputEWC() + " does not exist!");
			return;
		}
		
		long timeOn = this.metadata.getLong("timeOn", 0);
		long timeOff = this.metadata.getLong("timeOff", 0);
		
		short valOn = this.metadata.getShort("valueOn", SwitchState.ON.toShort());
		short valOff = this.metadata.getShort("valueOff", SwitchState.OFF.toShort());
		
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
	}
	
	private void gsmSMSBehaviour() {
		GSM gsm = this.ewcManager.getGSMModuleById(this.getOutputEWC());
		if(gsm == null) {
			Messages.error("<CalledBehaviour->GSM_SMS> GSM " + this.getOutputEWC() + " does not exist!");
			return;
		}
		
		String phoneNumber = this.metadata.getString("phoneNumber", "");
		String message = this.metadata.getString("message", "SmartHouse suspicious activity!");
		
		SecuritySystem ss = this.ewcManager.getServer().getSecuritySystem(this.getSecurityId());
		if(ss.getFirstInput() != null)
			message = message.replace("%firstInputDescription%", ss.getFirstInput().getDescription()+"");
		//remove accents
		message = Normalizer.normalize(message, Normalizer.Form.NFD);
		message = message.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		
		long numOfPeriodsTime = (long)((double)this.delay/100.0);
		for(int i = 0; i < numOfPeriodsTime; i++) {
			try {
				Thread.sleep(100);
				if(this.shouldStop)
					return;
			} catch (InterruptedException e) {
				Messages.warning("Could not sleep a thread!");
				Messages.warning(Messages.getStackTrace(e));
			}
		}		

		if(!phoneNumber.equals(""))
			try {
				gsm.setCMGF(1);
				gsm.sendMessage(phoneNumber, message);
			} catch (IOException | InterruptedException e) {
				Messages.error(Messages.getStackTrace(e));
			}
	}

	@Override
	public boolean shouldExecute() {
		return true;
	}
}