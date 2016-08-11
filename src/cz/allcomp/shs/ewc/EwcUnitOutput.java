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

package cz.allcomp.shs.ewc;

import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.allcomplib.transducers.EwcNonExist;
import cz.allcomp.shs.behaviour.SignalBehaviour;
import cz.allcomp.shs.database.SqlCommands;
import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.util.Time;

public class EwcUnitOutput extends EwcUnit implements Runnable {

	public EwcUnitOutput(SmartServer server, EwcType type, int softwareId, String description, short ewcId,
			short inputOutputId, short stateValue, EwcValueType valueType, List<Integer> securityIds,
			boolean simulate) {
		super(server, type, softwareId, description, ewcId, inputOutputId, stateValue, valueType, securityIds, false);
		
		lastPwmWrite = 0;
		this.simulate = simulate;
	}

	private long lastPwmWrite;
	private final boolean simulate;
	
	@Override
	public void update() {
		if(this.getLastStateValue() != this.getStateValue()) {
			Messages.info("Output EWC Unit (" + this.getSoftwareId() + ") state changed: " + this.stateValue);
			
			if(this.preventDatabaseLogging)
				Messages.info("This state will not be logged in the database. (simulated state)");
			
			this.lastStateValue = this.stateValue;
			
			EwcManager em = this.server.getEwcManager();
			List<SignalBehaviour> behaviourList = em.getBehaviourByInputSoftwareId(this.getSoftwareId());

			for(SignalBehaviour sb : behaviourList) {
				new Thread(sb).start();
			}
	        
			new Thread(this).start();
		}
	}
	
	@Override
	public void run() {
        try {
        	if(this.getValueType() == EwcValueType.DIGITAL) {
				this.server.getEwcAccess().sOut(
					this.getEwcId(), this.getInputOutputId(), (byte)this.getStateValue());
        	} else if(this.getValueType() == EwcValueType.PWM) {
        		this.server.getEwcAccess().sAOut(
					this.getEwcId(), this.getInputOutputId(), this.getStateValue());
	        }
		} catch (EwcNonExist e) {
			Messages.info("EwcUnitOutput.java:32 >>");
			Messages.error("Ewc " + this.getEwcId() + " does not exixt!");
			Messages.error(Messages.getStackTrace(e));
		}
        
        if(this.getValueType() == EwcValueType.PWM) {
    		if(System.currentTimeMillis() - this.lastPwmWrite >= 2000) {
    			this.lastPwmWrite = System.currentTimeMillis();
    			this.logState();
    		}
        } else
        	this.logState();
        
        this.preventDatabaseLogging = false;
	}
	
	private void logState() {
		if(this.preventDatabaseLogging)
			return;
		if(this.server.isDatabaseActive()) {
			this.server.getDatabase().executeUpdate(
				SqlCommands.LOG_EWCUNIT_STATE
					.replace("%softwareId%", this.getSoftwareId()+"")
					.replace("%time%", Time.getTime().getTimeStamp()+"")
					.replace("%value%", this.getStateValue()+"")
			);
		}
	}
	
	public boolean canSimulate() {
		return this.simulate;
	}
}
