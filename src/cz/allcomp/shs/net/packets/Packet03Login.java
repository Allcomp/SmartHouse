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

public class Packet03Login extends Packet {
	private String userId;
	private String password;
	
	public Packet03Login(byte[] data) {
		super(03);
		
		String[] words = readData(data).split("%");
		if(words.length == 2) {
			this.userId = words[0];
			this.password = words[1];
		}
	}
	
	public Packet03Login(String userId, String password) {
		super(03);
		
		this.userId = userId;
		this.password = password;
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
		return ("03" + this.userId + "%" + this.password).getBytes();
	}
	
	public String getUserId() {
		return this.userId;
	}
	
	public String getPassword() {
		return this.password;
	}
}
