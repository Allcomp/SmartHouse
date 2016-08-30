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

import java.net.InetAddress;

import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.net.NetServer;

public abstract class Packet {
	public static enum PacketType
	{
		INVALID(-1), STOPSERVER(00), MESSAGE(01), COMMAND(02), LOGIN(03),
		LOGIN_SUCCESSFUL(04), LOGIN_FAILED(05), ACCESS_DENIED(06);
		
		private int packetId;
		
		private PacketType(int packetId)
		{
			this.packetId = packetId;
		}
		
		public int getId()
		{
			return packetId;
		}
	}
	
	public byte packetId;
	
	public Packet(int packetId)
	{
		this.packetId = (byte) packetId;
	}
	
	public abstract void writeData(NetServer server, InetAddress address, int port);
	
	public String readData(byte[] data)
	{
		String message = new String(data).trim();
		return message.substring(2);
	}
	
	public abstract byte[] getData();
	
	public boolean isPacketEmpty() {
		return this.getDataInString().length() == 0;
	}
	
	public String getDataInString() {
		return new String(this.getData()).trim();
	}
	
	public static PacketType lookupPacket(int id)
	{
		for(PacketType p : PacketType.values())
		{
			if(p.getId() == id)
				return p;
		}
		return PacketType.INVALID;
	}
	
	public static PacketType lookupPacket(String id) {
		try {
			return lookupPacket(Integer.parseInt(id));
		} catch(NumberFormatException e) {
			Messages.warning(Messages.getStackTrace(e));
			return PacketType.INVALID;
		}
	}
	
	public static int getPacketId(String packetData) {
		String strId = packetData.substring(0, 2);
		try {
			int id = Integer.parseInt(strId);
			return id;
		} catch (NumberFormatException e) {
			Messages.warning("Packet ID is not valid.");
			Messages.warning(Messages.getStackTrace(e));
			return (-1);
		}
	}
}
