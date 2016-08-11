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

package cz.allcomp.shs.net;

import cz.allcomp.shs.logging.Messages;

public class NetController implements Runnable {

	private Thread logicThread;
	
	private boolean running;
	private boolean shouldStop;
	
	private NetServer netServer;
	
	public NetController(NetServer netServer) {
		this.logicThread = new Thread(this);
		this.shouldStop = false;
		this.running = false;
		this.wasBroken = false;
		this.netServer = netServer;
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
	
	public boolean isRunning() {
		return this.running;
	}
	
	@Deprecated
	public void forceStop() {
		this.logicThread.stop();
		this.running = false;
	}
	
	private boolean wasBroken;
	
	@Override
	public void run() {
		this.shouldStop = false;
		this.running = true;
		
		while(!this.shouldStop) {
			NetServer net = this.netServer;
			if(net.isBroken()) {
				this.wasBroken = true;
				Messages.warning("NetServer broken. Trying to repair...");
				net.start();
			} else {
				if(this.wasBroken) {
					this.wasBroken = false;
					Messages.info("NetServer repaired.");
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Messages.error("Could not sleep a thread!");
				Messages.error(Messages.getStackTrace(e));
			}
		}
		
		this.running = false;
	}
	
}
