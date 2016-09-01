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

package cz.allcomp.shs.device;

import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.behaviour.SignalBehaviour;

public abstract class EwcUnit {
	
	private final EwcType type;
	private final int softwareId;
	private final String description;
	private final short ewcId;
	private final short inputOutputId;
	private final EwcValueType valueType;
	private final List<Integer> securityIds;
	private final boolean virtual;
	private boolean blockedForSignalBehaviour;

	protected short stateValue;
	protected short lastStateValue;
	
	protected boolean preventDatabaseLogging;
	
	protected long lastActiveTime,lastInactiveTime,timeCounterStart;
	
	protected SmartServer server;
	
	protected boolean overcontroled;
	
	public EwcUnit(SmartServer server, EwcType type, int softwareId, String description, short ewcId, short inputOutputId,
			short stateValue, EwcValueType valueType, List<Integer> securityIds, boolean virtual) {
		this.type = type;
		this.softwareId = softwareId;
		this.description = description;
		this.ewcId = ewcId;
		this.inputOutputId = inputOutputId;
		this.stateValue = stateValue;
		this.lastStateValue = stateValue;
		this.valueType = valueType;
		this.securityIds = securityIds;
		this.virtual = virtual;
		this.server = server;
		this.lastActiveTime = 0;
		this.lastInactiveTime = 0;
		this.timeCounterStart = System.currentTimeMillis();
		this.overcontroled = false;
		this.preventDatabaseLogging = false;
		this.blockedForSignalBehaviour = false;
	}
	
	public abstract void update();
	
	public void setBlockedForSignalBehaviour(boolean val) {
		this.blockedForSignalBehaviour = val;
	}
	
	public boolean isBlockedForSignalBehaviour() {
		return this.blockedForSignalBehaviour;
	}
	
	public void setStateValue(short value) {
		lastStateValue = stateValue;
		stateValue = value;
		if(stateValue != lastStateValue) {
			
			if(lastStateValue == 0) {
				this.lastInactiveTime = System.currentTimeMillis() - this.timeCounterStart;
			} else if(lastStateValue == 1) {
				this.lastActiveTime = System.currentTimeMillis() - this.timeCounterStart;
			}
			
			this.timeCounterStart = System.currentTimeMillis();
			
			this.notifySecuritySystem();
		}
	}
	
	public void setStateValueWithoutChange(short value) {
		lastStateValue = value;
		stateValue = value;
	}
	
	public void simulateStateValue(short value) {
		lastStateValue = stateValue;
		stateValue = value;
		if(stateValue != lastStateValue) {
			
			if(lastStateValue == 0) {
				this.lastInactiveTime = System.currentTimeMillis() - this.timeCounterStart;
			} else if(lastStateValue == 1) {
				this.lastActiveTime = System.currentTimeMillis() - this.timeCounterStart;
			}
			
			this.timeCounterStart = System.currentTimeMillis();
			this.preventDatabaseLogging = true;
		}
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public EwcType getType() {
		return type;
	}

	public int getSoftwareId() {
		return softwareId;
	}

	public short getEwcId() {
		return ewcId;
	}

	public short getInputOutputId() {
		return inputOutputId;
	}
	
	public List<Integer> getSecurityIds() {
		return this.securityIds;
	}
	
	public boolean isVirtual() {
		return this.virtual;
	}

	public short getStateValue() {
		return stateValue;
	}

	public int getLastStateValue() {
		return lastStateValue;
	}

	public EwcValueType getValueType() {
		return valueType;
	}
	
	public long getLastActiveTime() {
		return this.lastActiveTime;
	}
	
	public long getLastInactiveTime() {
		return this.lastInactiveTime;
	}
	
	public boolean isSecurityIO() {
		return this.getSecurityIds().size() != 0;
	}
	
	protected List<SignalBehaviour> getAssignedBehaviour() {
		return this.server.getEwcManager().getBehaviourByInputSoftwareId(this.softwareId);
	}
	
	private void notifySecuritySystem() {
		for(int sid : this.getSecurityIds())
			if(this.server.getSecuritySystem(sid) != null)
				this.server.getSecuritySystem(sid).notifyStateChange(this);
	}

	public boolean isOvercontroled() {
		return overcontroled;
	}

	public void setOvercontroled(boolean overcontroled) {
		this.overcontroled = overcontroled;
	}
}
