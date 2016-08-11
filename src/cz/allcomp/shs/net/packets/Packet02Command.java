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

package cz.allcomp.shs.net.packets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.net.NetServer;

public class Packet02Command extends Packet {
	private String command;
	private String[] arguments;
	
	public Packet02Command(byte[] data) {
		super(02);
		
		String[] words = readData(data).split(" ");
		if(words.length > 0) {
			this.command = words[0];
			this.arguments = new String[words.length-1];
		}
		for(int i = 1; i < words.length; i++)
			this.arguments[i-1] = words[i];
	}
	
	public Packet02Command(String command, String[] args) {
		super(02);
		
		this.command = command;
		this.arguments = args;
	}
	
	@Override
	public void writeData(NetServer server, InetAddress address, int port) {
		try {
			server.getSocket().send(new DatagramPacket(getData(), getData().length, address, port));
		} catch (IOException e) {
			Messages.warning("Could not send 'Message' packet!");
			Messages.warning(Messages.getStackTrace(e));
		}
	}

	@Override
	public byte[] getData() {
		String text = this.command;
		for(int i = 0; i < this.arguments.length; i++)
			text += " " + this.arguments[i];
		return ("02" + text).getBytes();
	}

	public String getCommand() {
		return this.command;
	}
	
	public String[] getArguments() {
		return this.arguments;
	}
}
