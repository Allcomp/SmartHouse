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

import java.util.ArrayList;
import java.util.List;

import cz.allcomp.shs.ewc.EwcManager;
import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.util.Time;

public class PlannedBehaviourManager implements Runnable {

	private boolean shouldStop, running;
	
	private List<PlannedBehaviour> behaviour;
	
	private EwcManager ewcManager;
	
	private Thread logicThread;
	
	public PlannedBehaviourManager(EwcManager ewcManager) {
		this.behaviour = new ArrayList<>();
		this.ewcManager = ewcManager;
		this.shouldStop = true;
		this.running = false;
		this.logicThread = new Thread(this);
	}
	
	public void start() {
		if(!this.running) {
			this.logicThread = new Thread(this);
			this.logicThread.start();
		}
	}
	
	@Deprecated
	public void forceStop() {
		this.logicThread.stop();
		this.running = false;
	}
	
	public void signalStop() {
		this.shouldStop = true;
	}
	
	@Override
	public void run() {
		this.shouldStop = false;
		this.running = true;
		this.getPlannedBehaviourFromEwcManager();
		while(!this.shouldStop) {
			for(PlannedBehaviour pb : this.behaviour) {
				Time time = Time.getTime();
				boolean dayEquals = pb.getDays().charAt(time.getDayOfWeek()-1) == '1';
				if(pb.getType() == PlannedBehaviourType.ORDINARY) {
					boolean hourEquals = pb.getTimeStart().getHour() == time.getHour();
					boolean minuteEquals = pb.getTimeStart().getMinute() == time.getMinutes();
					if(dayEquals && hourEquals && minuteEquals)
						new Thread(pb).start();
				} else if(pb.getType() == PlannedBehaviourType.REPEATING) {
					if(dayEquals && time.getHour() == 0 && time.getMinutes() == 0)
						new Thread(pb).start();
				}
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Messages.warning("Could not sleep a thread!");
				Messages.warning(Messages.getStackTrace(e));
			}
		}
		this.running = false;
	}
	
	private void getPlannedBehaviourFromEwcManager() {
		for(Behaviour b : this.ewcManager.getBehaviour()) {
			if(b instanceof PlannedBehaviour) {
				this.behaviour.add((PlannedBehaviour) b);
			}
		}
	}
}
