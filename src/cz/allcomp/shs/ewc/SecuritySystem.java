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

import java.util.ArrayList;
import java.util.List;

import cz.allcomp.shs.behaviour.Behaviour;
import cz.allcomp.shs.behaviour.CalledBehaviour;
import cz.allcomp.shs.logging.Messages;

public class SecuritySystem implements Runnable {
	
	private final int id;
	private final String name;
	
	private boolean running;
	private boolean shouldStop;

	private final long securityActivationDelay;
	private final int notificationNumberToAlarm;
	
	private List<EwcUnitInput> securityInputs;
	private List<CalledBehaviour> securityBehaviour;
	
	private int notificationCount;
	private int lastNofiticationCount;
	private boolean alarmActivated;
	
	private EwcManager ewcManager;
	
	public SecuritySystem(int id, String name, EwcManager ewcManager, long securityActivationDelay, int notificationNumberToAlarm) {
		this.running = false;
		this.shouldStop = false;
		this.securityInputs = new ArrayList<>();
		this.securityBehaviour = new ArrayList<>();
		this.securityActivationDelay = securityActivationDelay;
		this.lastNofiticationCount = 0;
		this.notificationCount = 0;
		this.notificationNumberToAlarm = notificationNumberToAlarm;
		this.alarmActivated = false;
		this.ewcManager = ewcManager;
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void signalStop() {
		this.shouldStop = true;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public List<EwcUnitInput> getSecurityInputs() {
		return this.securityInputs;
	}
	
	public List<CalledBehaviour> getSecurityBehaviour() {
		return this.securityBehaviour;
	}
	
	public void notifyStateChange(EwcUnit ewc) {
		if(ewc.getSecurityIds().contains(this.getId()))
			if(ewc instanceof EwcUnitInput)
				if(this.isRunning())
					this.notificationCount++;
	}
	
	public void loadSecurityInputs() {
		for(EwcUnit ewc : this.ewcManager.getEwcUnits()) {
			if(ewc != null)
				if(ewc instanceof EwcUnitInput)
					if(((EwcUnitInput)ewc).getSecurityIds().contains(this.getId()))
						this.securityInputs.add((EwcUnitInput)ewc);
		}
	}
	
	public void loadSecurityBehaviour() {
		for(Behaviour b : this.ewcManager.getBehaviour()) {
			if(b != null)
				if(b instanceof CalledBehaviour)
					if(((CalledBehaviour) b).getSecurityId() == this.getId())
						this.securityBehaviour.add((CalledBehaviour)b);
		}
	}
	
	public int getRemainingNotificationsToAlarm() {
		int remainingNotifications = this.notificationNumberToAlarm - this.notificationCount;
		return remainingNotifications >= 0 ? remainingNotifications : 0;
	}
	
	@Override
	public void run() {
		if(this.running)
			return;
		
		this.running = true;
		this.shouldStop = false;
		
		Messages.info("Found " + this.securityInputs.size() + " security inputs.");
		
		Messages.info("Security system enabled, you have " + ((double)this.securityActivationDelay/1000.0) + " seconds to leave your house.");
		
		try {
			Thread.sleep(this.securityActivationDelay);
		} catch (InterruptedException e) {
			Messages.warning("Could not sleep a thread!");
			Messages.warning(Messages.getStackTrace(e));
		}
		
		Messages.info("Security sector '"+this.name+"' activated.");
		
		while(!this.shouldStop) {
			
			if(this.notificationCount != this.lastNofiticationCount) {
				if(this.notificationCount >= this.notificationNumberToAlarm) {
					if(!this.alarmActivated) {
						Messages.warning("Security system detected suspicious event, activating alarm...");
						
						for(CalledBehaviour b : this.securityBehaviour)
							new Thread(b).start();
						
						this.alarmActivated = true;
						Messages.info("Alarm activated!");
					} else {
						Messages.warning("Security system detected suspicious event, but alarm is already activated!");
					}
				} else {
					Messages.warning("Security system detected suspicious event!");
					int remainingNotifications = this.notificationNumberToAlarm - this.notificationCount;
					Messages.info("Remaining number of notifications: " 
						+ (remainingNotifications >= 0 ? remainingNotifications : 0));
				}
				this.lastNofiticationCount = this.notificationCount;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Messages.warning("Could not sleep a thread!");
				Messages.warning(Messages.getStackTrace(e));
			}
		}
		
		Messages.info("Deactivating security system...");
		
		for(CalledBehaviour b : this.securityBehaviour)
			b.interrupt();
		
		this.alarmActivated = false;
		//this.securityBehaviour.clear();
		
		Messages.info("Security system deactivated.");
		
		this.running = false;
	}
}