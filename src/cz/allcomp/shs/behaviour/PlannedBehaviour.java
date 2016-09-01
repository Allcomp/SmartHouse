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

import cz.allcomp.shs.device.EwcManager;
import cz.allcomp.shs.device.EwcUnit;
import cz.allcomp.shs.device.EwcUnitOutput;
import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.states.SwitchState;
import cz.allcomp.shs.util.Time;
import cz.allcomp.shs.util.TimeHourMinute;

public class PlannedBehaviour extends Behaviour {

	protected String days;
	protected TimeHourMinute timeStart;
	protected TimeHourMinute timeStop;
	protected PlannedBehaviourType type;

	public PlannedBehaviour(EwcManager ewcManager, short outputEWC, PlannedBehaviourType type, String days, TimeHourMinute timeStart, 
			TimeHourMinute timeStop, BehaviourMetadata metadata, BehaviourConditions turnOnConditions, 
			BehaviourConditions turnOffConditions) {
		super(ewcManager, outputEWC, metadata, turnOnConditions, turnOffConditions);
		
		this.days = days;
		this.timeStart = timeStart;
		this.timeStop = timeStop;
		this.type = type;
	}
	
	public String getDays() {
		return days;
	}

	public TimeHourMinute getTimeStart() {
		return timeStart;
	}

	public TimeHourMinute getTimeStop() {
		return timeStop;
	}
	
	public PlannedBehaviourType getType() {
		return this.type;
	}

	@Override
	public void execute() {
		if(this.type == PlannedBehaviourType.ORDINARY) {
			long waitTime = TimeHourMinute.computeTimeDifferenceInMinutes(timeStart, timeStop)*60*1000;
			
			EwcManager em = this.ewcManager;
			EwcUnit outputEwc = em.getEwcUnitBySoftwareId(this.outputEWC);
			
			short valueOn = this.metadata.getShort("valueOn", SwitchState.ON.toShort());
			short valueOff = this.metadata.getShort("valueOff", SwitchState.OFF.toShort());
			
			if(this.turnOnConditions.isTrue())
				outputEwc.setStateValue(valueOn);
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				Messages.error("Could not sleep a thread!");
				Messages.error(Messages.getStackTrace(e));
			}
			if(this.turnOffConditions.isTrue())
				outputEwc.setStateValue(valueOff);
			
		} else if(this.type == PlannedBehaviourType.REPEATING) {
			long waitOn = (timeStart.getHour()*60 + timeStart.getMinute())*60*1000;
			long waitOff = (timeStop.getHour()*60 + timeStop.getMinute())*60*1000;
			EwcManager em = this.ewcManager;
			EwcUnitOutput outputEwc = (EwcUnitOutput) em.getEwcUnitBySoftwareId(this.outputEWC);
			
			short valueOn = this.metadata.getShort("valueOn", SwitchState.ON.toShort());
			short valueOff = this.metadata.getShort("valueOff", SwitchState.OFF.toShort());
			
			while(true) {
				Time time = Time.getTime();
				boolean dayEquals = this.getDays().charAt(time.getDayOfWeek()-1) == '1';
				if(!dayEquals)
					break;
				
				if(this.turnOnConditions.isTrue())
					outputEwc.setStateValue(valueOn);
				try {
					Thread.sleep(waitOn);
				} catch (InterruptedException e) {
					Messages.error("Could not sleep a thread!");
					Messages.error(Messages.getStackTrace(e));
				}
				if(this.turnOffConditions.isTrue())
					outputEwc.setStateValue(valueOff);
				try {
					Thread.sleep(waitOff);
				} catch (InterruptedException e) {
					Messages.error("Could not sleep a thread!");
					Messages.error(Messages.getStackTrace(e));
				}
			}
		}
	}

	@Override
	public boolean shouldExecute() {
		return !this.isExecuted();
	}
}
