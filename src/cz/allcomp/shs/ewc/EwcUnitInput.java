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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.allcomplib.transducers.EwcNonExist;
import cz.allcomp.shs.behaviour.SignalBehaviour;
import cz.allcomp.shs.behaviour.SignalBehaviourType;
import cz.allcomp.shs.database.SqlCommands;
import cz.allcomp.shs.logging.Messages;

public class EwcUnitInput extends EwcUnit implements Runnable {
	
	private long updateCounter;

	private double temperatureCelsius;
	private double targetTemperatureCelsius;
	private final short activeStateValue;
	private boolean thermostatActive;
	
	public EwcUnitInput(SmartServer server, EwcType type, int softwareId, String description, short ewcId,
			short inputOutputId, short stateValue, EwcValueType valueType, List<Integer> securityIds, boolean virtual,
			short activeStateValue) {
		super(server, type, softwareId, description, ewcId, inputOutputId, stateValue, valueType, securityIds, virtual);
		
		this.updateCounter = 0;
		this.temperatureCelsius = 0;
		this.targetTemperatureCelsius = 0;
		this.thermostatActive = false;
		this.activeStateValue = activeStateValue;
	}

	@Override
	public void update() {
		if(this.getValueType() == EwcValueType.TEMPERATURE) {
			if(updateCounter % 200 == 0) {
				try {
					this.stateValue = 1;
					int temperature = this.server.getEwcAccess().gTIn(this.getEwcId(), this.getInputOutputId());
					double temperatureKelvin = (double)((double)temperature/100.0);
					double temperatureCelsius = temperatureKelvin - 273.15;
					this.temperatureCelsius = temperatureCelsius;
					
					if(updateCounter % 300 == 0)
						Messages.info("Input EWC Unit (" + this.getSoftwareId() + ") temperature updated: " + this.temperatureCelsius + " °C");
					
					EwcManager em = this.server.getEwcManager();
					List<SignalBehaviour> behaviourList = em.getBehaviourByInputSoftwareId(this.getSoftwareId());
					
					for(SignalBehaviour sb : behaviourList) {
						if(sb.getType() == SignalBehaviourType.THERMOSTAT)
							if(!this.thermostatActive)
								continue;
						new Thread(sb).start();
					}
					
				} catch (EwcNonExist e) {}
			}
		} else if(this.getValueType() == EwcValueType.DIGITAL) {
			if(this.lastStateValue != this.stateValue) {
				this.lastStateValue = this.stateValue;
				Messages.info("Input EWC Unit (" + this.getSoftwareId() + ") state changed: " + this.stateValue);
				
				EwcManager em = this.server.getEwcManager();
				List<SignalBehaviour> behaviourList = em.getBehaviourByInputSoftwareId(this.getSoftwareId());
				
				for(SignalBehaviour sb : behaviourList)
					new Thread(sb).start();
			}
		} else if(this.getValueType() == EwcValueType.PWM) {
			if(this.lastStateValue != this.stateValue) {
				this.lastStateValue = this.stateValue;
				Messages.info("Input EWC Unit (" + this.getSoftwareId() + ") value changed: " + this.stateValue);
				
				EwcManager em = this.server.getEwcManager();
				List<SignalBehaviour> behaviourList = em.getBehaviourByInputSoftwareId(this.getSoftwareId());
				
				for(SignalBehaviour sb : behaviourList)
					new Thread(sb).start();
			}
		}
		updateCounter++;
	}
	
	public short getActiveStateValue() {
		return this.activeStateValue;
	}
	
	public boolean isThermostatActive() {
		return this.thermostatActive;
	}
	
	public void activateThermostat() {
		this.thermostatActive = true;
		new Thread(this).start();
	}
	
	public void deactivateThermostat() {
		this.thermostatActive = false;
		EwcManager ewcManager = this.server.getEwcManager();
		List<SignalBehaviour> behaviourList = ewcManager.getBehaviourByInputSoftwareId(this.getSoftwareId());
		for(SignalBehaviour sb : behaviourList) {
			EwcUnit output = ewcManager.getEwcUnitBySoftwareId(sb.getOutputEWC());
			output.setStateValue((short)0);
		}
		new Thread(this).start();
	}
	
	public double getTemperatureCelsius() {
		return this.temperatureCelsius;
	}
	
	public double getTemperatureKelvin() {
		return this.temperatureCelsius + 273.15;
	}
	
	public double getTemperatureFahrenheit() {
		return this.temperatureCelsius * 1.8 + 32;
	}
	
	public void setTargetTemperatureCelsius(double temp) {
		this.targetTemperatureCelsius = temp;
		new Thread(this).start();
	}

	public void setTargetTemperatureKelvin(double temp) {
		this.setTargetTemperatureCelsius(temp - 273.15);
	}

	public void setTargetTemperatureFahrenheit(double temp) {
		this.setTargetTemperatureCelsius((temp - 32.0)/1.8);
	}
	
	public double getTargetTemperatureCelsius() {
		return this.targetTemperatureCelsius;
	}
	
	public double getTargetTemperatureKelvin() {
		return this.targetTemperatureCelsius + 273.15;
	}
	
	public double getTargetTemperatureFahrenheit() {
		return this.targetTemperatureCelsius * 1.8 + 32;
	}

	@Override
	public void run() {
		this.saveThermostatToDatabase();
	}
	
	private void saveThermostatToDatabase() {
		if(this.server.isDatabaseActive()) {
			int count = this.getThermostatCountInDatabase();
			if(count == 1) {
				this.server.getDatabase().executeUpdate(SqlCommands.THERMOSTAT_UPDATE
						.replace("%ewc%", this.getSoftwareId() + "")
						.replace("%value%", this.targetTemperatureCelsius + ""));
				this.server.getDatabase().executeUpdate(SqlCommands.THERMOSTAT_SET_ACTIVE
						.replace("%ewc%", this.getSoftwareId() + "")
						.replace("%value%", this.thermostatActive ? "1" : "0"));
			} else if(count == 0) {
				this.server.getDatabase().executeUpdate(SqlCommands.THERMOSTAT_INSERT
						.replace("%ewc%", this.getSoftwareId() + "")
						.replace("%value%", this.targetTemperatureCelsius + ""));
			}
		}
	}
	
	public void loadThermostatFromDatabase() {
		if(this.server.isDatabaseActive()) {
			ResultSet rs = this.server.getDatabase()
					.executeQuery(SqlCommands.THERMOSTAT_GET.replace("%ewc%", this.getSoftwareId() + ""));
			try {
				while(rs.next()) {
					this.targetTemperatureCelsius = rs.getDouble("value");
					this.thermostatActive = rs.getInt("active") == 1;
					break;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getThermostatCountInDatabase() {
		if(this.server.isDatabaseActive()) {
			ResultSet rs = this.server.getDatabase()
					.executeQuery(SqlCommands.THERMOSTAT_COUNT.replace("%ewc%", this.getSoftwareId()+""));
			try {
				while(rs.next()) {
					return rs.getInt("COUNT(*)");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
}
