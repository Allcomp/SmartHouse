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

import cz.allcomp.shs.logging.Messages;

public class EwcHardwareAddress {
	
	private short ewcId;
	private EwcType ewcType;
	private short inputOutputId;

	public EwcHardwareAddress(String hwAddress) {
		if(hwAddress.startsWith("EWC")) {
			hwAddress = hwAddress.replace("EWC", "");
			String[] params = hwAddress.split("_");
			if(params.length == 2) {
				ewcId = Short.parseShort(params[0]);
				if(params[1].contains(EwcType.INPUT.getMark().toUpperCase()))
					ewcType = EwcType.INPUT;
				else if(params[1].contains(EwcType.OUTPUT.getMark().toUpperCase()))
					ewcType = EwcType.OUTPUT;
				else
					ewcType = EwcType.UNKNOWN;
				inputOutputId = Short.parseShort(params[1].replace(ewcType.getMark().toUpperCase(), ""));
			} else {
				ewcId = -1;
				ewcType = EwcType.UNKNOWN;
				inputOutputId = -1;
			}
		} else if(hwAddress.startsWith("VIRTUAL")) {
			ewcId = 0;
			ewcType = EwcType.INPUT;
			try {
				inputOutputId = Short.parseShort(hwAddress.replace("VIRTUAL", ""));
			} catch (NumberFormatException e) {
				Messages.warning(Messages.getStackTrace(e));
				inputOutputId = -1;
			}
		} else {
			ewcId = -1;
			ewcType = EwcType.UNKNOWN;
			inputOutputId = -1;
		}
	}
	
	public EwcHardwareAddress(short ewcId, EwcType ewcType, short inputOutputId) {
		this.ewcId = ewcId;
		this.ewcType = ewcType;
		this.inputOutputId = inputOutputId;
	}
	
	public String getHardwareAddress() {
		String zero = "";
		if(ewcId < 10)
			zero = "0";
		return "EWC" + zero + ewcId + "_" + ewcType.getMark().toUpperCase() + inputOutputId;
	}
	
	public short getEWCId() {
		return ewcId;
	}

	public EwcType getEWCType() {
		return ewcType;
	}

	public short getInputOutputId() {
		return inputOutputId;
	}
}
