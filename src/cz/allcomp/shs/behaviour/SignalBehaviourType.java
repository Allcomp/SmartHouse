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

public enum SignalBehaviourType {
	UNKNOWN(-1),
	BUTTON(0), /* button... */
	SWITCH(1), /* switch... */
	TIMED(2), /* delayed turn off */
	PIR_SENSOR(3), /* PIR sensor... */
	NEGATION(4), /* output = ~input */
	ON_ONLY_NEGATION(5), /* output = ~input, if input is 1 */
	REGULATOR(6), /* Used for regulating lights etc */
	THERMOSTAT(7), /* thermostat... */
	VIRTUAL_PWM(8), /* virtual pwm input */
	SUNBLIND_BUTTON(9), /* sublind control */
	ON_ONLY_ACTIVATION(10),
	REVERSE_SWITCH(11),
	PWM_WATCHER(12),
	TIMED_BUTTON(13),
	OVERCONTROL_BUTTON(14),
	AIR_COND_INPUT_WATCHER(100),
	AIR_COND_VIRTUAL_WATCHER(101);
	
	private int intVal;
	
	private SignalBehaviourType(int intVal) {
		this.intVal = intVal;
	}
	
	public int toInt() {
		return this.intVal;
	}
	
	public static SignalBehaviourType getByInt(int intVal) {
		for(SignalBehaviourType bt : SignalBehaviourType.values())
			if(bt.toInt() == intVal)
				return bt;
		return UNKNOWN;
	}
}
