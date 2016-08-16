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

package cz.allcomp.shs.states;

public enum SwitchState {
	UNKNOWN(-1, "unknown"), OFF(0, "off"), ON(1, "on");
	
	private int intVal;
	private String stringVal;
	
	private SwitchState(int intVal, String stringVal) {
		this.intVal = intVal;
		this.stringVal = stringVal;
	}
	
	public int toInt() {
		return this.intVal;
	}
	
	public short toShort() {
		return (short)this.intVal;
	}
	
	@Override
	public String toString() {
		return this.stringVal;
	}
	
	public static SwitchState getByInt(int val) {
		for(SwitchState ss : SwitchState.values()) {
			if(ss.toInt() == val)
				return ss;
		}
		return UNKNOWN;
	}
	
	public static SwitchState getByString(String val) {
		for(SwitchState ss : SwitchState.values()) {
			if(ss.toString().equalsIgnoreCase(val))
				return ss;
		}
		return UNKNOWN;
	}
}
