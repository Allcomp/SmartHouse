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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.logging.Messages;

public class WebServer implements Runnable {
	
	private int port;
	private Thread logicThread;
	
	private boolean running;
	private boolean shouldStop;
	
	private SmartServer server;
	
	public WebServer(SmartServer server, int port) {
		this.port = port;
		this.running = false;
		this.shouldStop = false;
		this.logicThread = new Thread(this);
		this.server = server;
	}
	
	public int getPort() {
		return this.port;
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
	
	public boolean isBroken() {
		return !this.logicThread.isAlive() && this.running;
	}
	
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public void run() {
		this.running = true;
		this.shouldStop = false;
		
		ServerSocket listenSocket = null;
		try {
			listenSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(listenSocket != null) {
			while(!this.shouldStop) {
				if(Boolean.parseBoolean(server.getMainConfig().get("log_web_requests")))
					Messages.info("<WebServer> Waiting for request on port " + this.port);
			   	try {
			   		Socket connectionSocket = listenSocket.accept();
					new Thread(new WebClientHandler(this.server, connectionSocket)).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				listenSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.running = false;
	}
}
