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

import cz.allcomp.shs.device.EwcManager;
import cz.allcomp.shs.device.EwcUnit;
import cz.allcomp.shs.device.EwcUnitInput;
import cz.allcomp.shs.util.Time;
import cz.allcomp.shs.util.Utility;

public class BehaviourConditions {

	private String condition;
	
	private boolean conditionTrue;
	
	private EwcManager ewcManager;
	
	public BehaviourConditions(EwcManager ewcManager, String condition) {
		if(condition.equals(""))
			condition = "0 == 0";
		if(condition.equals("true"))
			condition = "0 == 0";
		if(condition.equals("false"))
			condition = "0 == 1";
		this.condition = condition;
		this.conditionTrue = true;
		this.ewcManager = ewcManager;
	}
	
	public String getCondition() {
		return this.condition;
	}
	
	public void evaluateCondition() {
		this.conditionTrue = Utility.logicalExpression(this.parseCondition());
	}
	
	public boolean isTrue() {
		this.evaluateCondition();
		return this.conditionTrue;
	}
	
	public String parseCondition() {
		String condition = this.condition;
		if(this.condition.contains("%ewc")) {
			for(EwcUnit ewc : this.ewcManager.getEwcUnits()) {
				condition = condition.replace("%ewc" + ewc.getSoftwareId() + "%", ewc.getStateValue()+"");
			}
		}
		if(this.condition.contains("%tempc")) {
			for(EwcUnit ewc : this.ewcManager.getEwcUnits()) {
				if(ewc instanceof EwcUnitInput)
					condition = condition.replace("%tempc" + ewc.getSoftwareId() + "%", ((EwcUnitInput)ewc).getTemperatureCelsius() +"");
			}
		}
		if(this.condition.contains("%tempk")) {
			for(EwcUnit ewc : this.ewcManager.getEwcUnits()) {
				if(ewc instanceof EwcUnitInput)
					condition = condition.replace("%tempk" + ewc.getSoftwareId() + "%", ((EwcUnitInput)ewc).getTemperatureKelvin() +"");
			}
		}
		if(this.condition.contains("%tempf")) {
			for(EwcUnit ewc : this.ewcManager.getEwcUnits()) {
				if(ewc instanceof EwcUnitInput)
					condition = condition.replace("%tempf" + ewc.getSoftwareId() + "%", ((EwcUnitInput)ewc).getTemperatureFahrenheit() +"");
			}
		}
		Time time = Time.getTime();
		condition = condition.replace("%hour%", time.getHour()+"");
		condition = condition.replace("%minutes%", time.getMinutes()+"");
		condition = condition.replace("%seconds%", time.getSeconds()+"");
		condition = condition.replace("%month%", time.getMonth()+"");
		condition = condition.replace("%day%", time.getDay()+"");
		condition = condition.replace("%dayofweek%", time.getDayOfWeek()+"");
		condition = condition.replace("%year%", time.getYear()+"");
		return condition;
	}
}
