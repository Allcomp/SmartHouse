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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.device.EwcUnit;
import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.net.packets.Packet;
import cz.allcomp.shs.net.packets.Packet.PacketType;
import cz.allcomp.shs.net.packets.Packet01Message;
import cz.allcomp.shs.net.packets.Packet02Command;
import cz.allcomp.shs.net.packets.Packet03Login;
import cz.allcomp.shs.net.packets.Packet04LoginSuccessful;
import cz.allcomp.shs.net.packets.Packet05LoginFailed;
import cz.allcomp.shs.net.packets.Packet06AccessDenied;
import cz.allcomp.shs.net.packets.Packet07InvalidPacket;
import cz.allcomp.shs.net.users.User;

public class NetServer implements Runnable {

	private int port;
	public int getPort() {
		return port;
	}

	private List<User> users;
	
	private DatagramSocket serverSocket;
	private boolean shouldStop;
	private boolean running;
	private long stopTime;
	
	private byte[] receiveData;
	
	private Thread logicThread;
	private SmartServer server;
	
	public NetServer(SmartServer server, int port, int timeout) {
		this.logicThread = new Thread(this);
		this.server = server;
		this.users = new ArrayList<>();
		this.port = port;
		this.shouldStop = false;
		this.running = false;
		this.stopTime = 0;
		try {
			this.serverSocket = new DatagramSocket(port);
			this.serverSocket.setSoTimeout(timeout);
			Messages.info("Server bound to port " + port + ".");
		} catch (SocketException e) {
			Messages.error("Failed to bind port " + port + " to server!");
		}
	}
	
	public NetServer(SmartServer server, int port) {
		this(server, port, 2000);
	}
	
	public List<User> getUsers() {
		return this.users;
	}
	
	public DatagramSocket getSocket() {
		return this.serverSocket;
	}
	
	public void signalStop() {
		this.shouldStop = true;
	}
	
	@Deprecated
	public void forceStop() {
		this.logicThread.stop();
		this.running = false;
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
	
	public boolean isBroken() {
		return !this.logicThread.isAlive() && this.running;
	}
	
	@Override
	public void run() {
		this.running = true;
		this.shouldStop = false;
		while(true) {
			//Messages.info("NetServer > ready for next packet");
			if(this.shouldStop)
				if(this.stopTime <= System.currentTimeMillis())
					break;
			
			receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				serverSocket.receive(receivePacket);
				Messages.info("NetServer > packet received");
				this.handleData(receivePacket.getAddress(), receivePacket.getPort(), receivePacket.getData());
			} catch (IOException e) {
				//Messages.info("NetServer.java:79 >>");
				//Messages.warning("Could not receive a packet.");
			}
			//serverSocket.send(sendPacket);
		}
		this.running = false;
	}
	
	public void handleData(InetAddress ipAddress, int port, byte[] data) {
		String packetContent = new String(data).trim();
		PacketType type = Packet.lookupPacket(Packet.getPacketId(packetContent));
		
		if(type == PacketType.LOGIN) {
			this.handleLoginPacket(data, ipAddress, port);
		} else {
			if(!this.isIpConnected(ipAddress)) {
				switch(type) {
					case STOPSERVER:
						this.handleStopServerPacket(ipAddress, port);
						break;
					case MESSAGE:
						this.handleMessagePacket(data, ipAddress);
						break;
					case COMMAND:
						this.handleCommandPacket(data, ipAddress, port);
						break;
					default:
						this.handleInvalidPacket(ipAddress, port);
						break;
				}
			} else {
				Packet06AccessDenied accDeniedPacket = new Packet06AccessDenied("");
				accDeniedPacket.writeData(this, ipAddress, port);
			}
		}
	}
	
	private void handleInvalidPacket(InetAddress ipAddress, int port) {
		Packet07InvalidPacket answerPacket = new Packet07InvalidPacket("");
		answerPacket.writeData(this, ipAddress, port);
	}
	
	private void handleStopServerPacket(InetAddress ipAddress, int port) {
		Messages.info("NetServer > handling stop server packet");
		Packet01Message answerPacket = new Packet01Message("stopping");
		answerPacket.writeData(this, ipAddress, port);
		this.server.signalStop();
	}
	
	private void handleMessagePacket(byte[] data, InetAddress ipAddress) {
		Messages.info("NetServer > handling message packet");
		Packet01Message receivedPacket = new Packet01Message(data);
		Messages.info("Message from " + ipAddress.getHostAddress() + ": "
				+ receivedPacket.getMessage());
	}
	
	private void handleCommandPacket(byte[] data, InetAddress ipAddress, int port) {
		Messages.info("NetServer > handling command packet");
		Packet02Command receivedPacket = new Packet02Command(data);
		
		if(receivedPacket.getCommand().equalsIgnoreCase("states")) {
			String states = "";
			for(EwcUnit ewc : this.server.getEwcManager().getEwcUnits())
				states += "-" + ewc.getSoftwareId() + ":" + ewc.getStateValue();
			states = states.substring(1);
			Packet01Message msgPacket = new Packet01Message(states);
			msgPacket.writeData(this, ipAddress, port);
		} else {
		
		this.server.getCommandManager().executeCommand(
				receivedPacket.getCommand(), receivedPacket.getArguments());
		}
	}
	
	private boolean isUserConnected(String userId) {
		for(User u : this.users)
			if(u.getUserId().equalsIgnoreCase(userId))
				return true;
		return false;
	}
	
	private boolean isIpConnected(InetAddress ipAddress) {
		for(User u : this.users)
			if(u.getIpAddress().equals(ipAddress))
				return true;
		return false;
	}
	
	private void addUser(User user) {
		this.users.add(user);
	}
	
	/*private void removeUser(User user) {
		this.users.remove(user);
	}*/
	
	private void handleLoginPacket(byte[] data, InetAddress ipAddress, int port) {
		Packet03Login receivePacket = new Packet03Login(data);
		if(!this.isUserConnected(receivePacket.getUserId())) {
			this.addUser(new User(
					receivePacket.getUserId(), receivePacket.getPassword(),
					ipAddress, port));
			Packet04LoginSuccessful answerPacket = new Packet04LoginSuccessful("");
			answerPacket.writeData(this, ipAddress, port);
		} else {
			Packet05LoginFailed answerPacket = new Packet05LoginFailed("already connected");
			answerPacket.writeData(this, ipAddress, port);
		}
			
	}
}
