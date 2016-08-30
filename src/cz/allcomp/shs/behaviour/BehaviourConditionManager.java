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

import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.logging.Messages;

public class BehaviourConditionManager implements Runnable {

	private Thread logicThread;
	private boolean running;
	private boolean shouldStop;
	
	private SmartServer server;
	
	public BehaviourConditionManager(SmartServer server) {
		this.logicThread = new Thread(this);
		this.running = false;
		this.shouldStop = false;
		this.server = server;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public boolean isBroken() {
		return !this.logicThread.isAlive() && this.running;
	}
	
	@Deprecated
	public void forceStop() {
		this.logicThread.stop();
		this.running = false;
	}
	
	public void signalStop() {
		this.shouldStop = true;
	}
	
	public void start() {
		if(!this.running) {
			this.logicThread = new Thread(this);
			this.logicThread.start();
		}
	}
	
	@Override
	public void run() {
		this.running = true;
		this.shouldStop = false;
		
		List<Behaviour> behaviour = this.server.getEwcManager().getBehaviour();
		
		while(!this.shouldStop) {
			Messages.info("Condition evaluating...");
			for(Behaviour b : behaviour) {
				b.getTurnOnConditions().evaluateCondition();
				b.getTurnOffConditions().evaluateCondition();
			}
			Messages.info("Condition evaluate complete.");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Messages.warning(Messages.getStackTrace(e));
			}
		}
		
		this.running = false;
	}

}
