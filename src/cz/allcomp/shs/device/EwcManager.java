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

package cz.allcomp.shs.device;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.allcomplib.transducers.EwcAccess;
import cz.allcomp.shs.allcomplib.transducers.EwcNonExist;
import cz.allcomp.shs.behaviour.Behaviour;
import cz.allcomp.shs.behaviour.BehaviourConditions;
import cz.allcomp.shs.behaviour.BehaviourMetadata;
import cz.allcomp.shs.behaviour.CalledBehaviour;
import cz.allcomp.shs.behaviour.CalledBehaviourType;
import cz.allcomp.shs.behaviour.PlannedBehaviour;
import cz.allcomp.shs.behaviour.PlannedBehaviourManager;
import cz.allcomp.shs.behaviour.PlannedBehaviourType;
import cz.allcomp.shs.behaviour.SignalBehaviour;
import cz.allcomp.shs.behaviour.SignalBehaviourType;
import cz.allcomp.shs.database.SqlCommands;
import cz.allcomp.shs.database.StableMysqlConnection;
import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.util.StringEncDec;
import cz.allcomp.shs.util.TimeHourMinute;

public class EwcManager implements Runnable {
	private List<EwcUnit> ewcs;
	private List<Behaviour> behaviour;
	private Set<Integer> activeEwcs;
	private List<GSM> gsmModules;
	private final GpioController gpioRaspi;
	
	private boolean shouldStop, running;

	private PlannedBehaviourManager plannedBehaviourManager;
	//private BehaviourConditionManager behaviourConditionManager;
	private Thread logicThread;
	
	private SmartServer server;
	private PulseMaker pulseMaker;
	
	public EwcManager(SmartServer server) {
		ewcs = new ArrayList<EwcUnit>();
		behaviour = new ArrayList<Behaviour>();
		gsmModules = new ArrayList<GSM>();
		this.logicThread = new Thread(this);
		this.plannedBehaviourManager = new PlannedBehaviourManager(this);
		//this.behaviourConditionManager = new BehaviourConditionManager();
		this.shouldStop = true;
		this.server = server;
		this.activeEwcs = new HashSet<>();
		this.pulseMaker = new PulseMaker(this);
		this.running = false;
		this.gpioRaspi = GpioFactory.getInstance();
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public boolean isStopping() {
		return this.running && this.shouldStop;
	}
	
	public EwcManager(List<EwcUnit> ewcs) {
		this.ewcs = ewcs;
		this.logicThread = new Thread(this);
		this.shouldStop = false;
		this.gpioRaspi = GpioFactory.getInstance();
	}
	
	public void setActiveEwcs(Set<Integer> activeEwcs) {
		this.activeEwcs = activeEwcs;
	}
	
	private void addEwcUnit(EwcUnit... ewcs) {
		for(EwcUnit ewc : ewcs)
			this.ewcs.add(ewc);
	}
	
	public void setGSMModules(List<GSM> gsmModules) {
		this.gsmModules = gsmModules;
	}
	
	public GSM getGSMModuleById(short id) {
		for(GSM gsm : this.gsmModules)
			if(gsm.getId() == id)
				return gsm;
		return null;
	}
	
	private void addBehaviour(Behaviour... behaviour) {
		for(Behaviour b : behaviour)
			this.behaviour.add(b);
	}
	
	public List<Behaviour> getBehaviour() {
		return this.behaviour;
	}
	
	public List<SignalBehaviour> getBehaviourByInputSoftwareId(int id) {
		List<SignalBehaviour> res = new ArrayList<SignalBehaviour>();
		for(Behaviour b : this.behaviour) {
			if(b instanceof SignalBehaviour) {
				if(((SignalBehaviour)b).getInputEWC() == id)
					res.add((SignalBehaviour)b);
			}
		}
		return res;
	}
	
	public List<SignalBehaviour> getBehaviourByOutputSoftwareId(int id) {
		List<SignalBehaviour> res = new ArrayList<SignalBehaviour>();
		for(Behaviour b : this.behaviour) {
			if(b instanceof SignalBehaviour) {
				if(((SignalBehaviour)b).getOutputEWC() == id)
					res.add((SignalBehaviour)b);
			}
		}
		return res;
	}
	
	private void handleSecuritySystems() {
		HashMap<Integer, SecuritySystem> ssystems = this.server.getSecuritySystems();
		for(int sid : ssystems.keySet()) {
			SecuritySystem ss = ssystems.get(sid);
			ss.loadSecurityBehaviour();
			ss.loadSecurityInputs();
		}
	}
	
	private void loadEwcsFromDatabase() {
		StableMysqlConnection database = this.server.getDatabase();
		ResultSet rs = database.executeQuery(SqlCommands.LOAD_EWCS);
		int inputEwcCount = 0;
		int outputEwcCount = 0;
		try {
			while(rs.next()) {
				EwcHardwareAddress hwAddress = new EwcHardwareAddress(
					rs.getString("hardware_address")
				);
				int softwareId = rs.getInt("software_address");
				EwcValueType valueType = EwcValueType.getByInt(rs.getInt("value_type"));
				List<Integer> securityIds = new ArrayList<Integer>();
				String sidStrings[] = rs.getString("security").split(",");
				for(String s : sidStrings) {
					try {
						securityIds.add(Integer.parseInt(s));
					} catch (NumberFormatException e) {
						Messages.warning("Could not convert security ids for ewc " + softwareId + "!");
					}
				}
				String description = StringEncDec.encode(rs.getString("description"));
				if(hwAddress.getEWCType() == EwcType.INPUT) {
					short activeStateValue = rs.getShort("active_state");
					inputEwcCount++;
					this.addEwcUnit(new EwcUnitInput(
						this.server, EwcType.INPUT, softwareId, description, hwAddress.getEWCId(),
						hwAddress.getInputOutputId(), (short)0,
						valueType, securityIds, hwAddress.getEWCId() == 0, activeStateValue
					));
				} else if(hwAddress.getEWCType() == EwcType.OUTPUT) {
					boolean simulate = rs.getInt("simulate") == 1;
					outputEwcCount++;
					this.addEwcUnit(new EwcUnitOutput(
						this.server, EwcType.OUTPUT, softwareId, description, hwAddress.getEWCId(),
						hwAddress.getInputOutputId(), (short)0,
						valueType, securityIds, simulate
					));
				}
			}
		} catch (SQLException e) {
			Messages.error("Failed to read ResultSet.");
			Messages.error(Messages.getStackTrace(e));
		}
		
		Messages.info("Loaded " + inputEwcCount + " input ewc units.");
		Messages.info("Loaded " + outputEwcCount + " output ewc units.");
	}
	
	private void loadSignalBehaviourFromDatabase() {
		StableMysqlConnection database = this.server.getDatabase();
		ResultSet rs = database.executeQuery(SqlCommands.LOAD_BEHAVIOUR_SIGNAL);
		int count = 0;
		try {
			while(rs.next()) {
				count++;
				
				short inputEwc = rs.getShort("input_ewc");
				int delay = rs.getInt("delay");
				SignalBehaviourType type = SignalBehaviourType.getByInt(rs.getInt("type"));
				short outputEwc = rs.getShort("output_ewc");
				BehaviourMetadata metadata = new BehaviourMetadata(rs.getString("metadata"));
				BehaviourConditions turnOnConditions = new BehaviourConditions(this, rs.getString("turn_on_conditions"));
				BehaviourConditions turnOffConditions = new BehaviourConditions(this, rs.getString("turn_off_conditions"));
				
				this.addBehaviour(new SignalBehaviour(
					this, inputEwc, delay, type, outputEwc, metadata, turnOnConditions, turnOffConditions
				));
			}
		} catch (SQLException e) {
			Messages.error("Failed to read ResultSet.");
			Messages.error(Messages.getStackTrace(e));
		}
		Messages.info("Loaded " + count + " signal behaviour.");
	}
	
	private void loadSecurityBehaviourFromDatabase() {
		StableMysqlConnection database = this.server.getDatabase();
		ResultSet rs = database.executeQuery(SqlCommands.LOAD_BEHAVIOUR_SECURITY);
		int count = 0;
		try {
			while(rs.next()) {
				count++;
				
				int delay = rs.getInt("delay");
				CalledBehaviourType type = CalledBehaviourType.getByInt(rs.getInt("type"));
				short outputEwc = rs.getShort("output_ewc");
				BehaviourMetadata metadata = new BehaviourMetadata(rs.getString("metadata"));
				BehaviourConditions turnOnConditions = new BehaviourConditions(this, rs.getString("turn_on_conditions"));
				BehaviourConditions turnOffConditions = new BehaviourConditions(this, rs.getString("turn_off_conditions"));
				int securityId = rs.getInt("security_id");
				
				this.addBehaviour(new CalledBehaviour(
					this, delay, type, outputEwc, metadata, turnOnConditions, turnOffConditions, securityId
				));
			}
		} catch (SQLException e) {
			Messages.error("Failed to read ResultSet.");
			Messages.error(Messages.getStackTrace(e));
		}
		Messages.info("Loaded " + count + " security behaviour.");
	}
	
	private void loadPlannedBehaviourFromDatabase() {
		StableMysqlConnection database = this.server.getDatabase();
		ResultSet rs = database.executeQuery(SqlCommands.LOAD_BEHAVIOUR_PLANNED);
		int count = 0;
		try {
			while(rs.next()) {
				count++;

				short outputEwc = rs.getShort("output_ewc");
				PlannedBehaviourType type = PlannedBehaviourType.getByInt(rs.getInt("type"));
				String days = rs.getString("days");
				TimeHourMinute timeStart = new TimeHourMinute(rs.getString("time_start"));
				TimeHourMinute timeStop = new TimeHourMinute(rs.getString("time_stop"));
				BehaviourMetadata metadata = new BehaviourMetadata(rs.getString("metadata"));
				BehaviourConditions turnOnConditions = new BehaviourConditions(this, rs.getString("turn_on_conditions"));
				BehaviourConditions turnOffConditions = new BehaviourConditions(this, rs.getString("turn_off_conditions"));
				
				this.addBehaviour(new PlannedBehaviour(
					this, outputEwc, type, days, timeStart, timeStop, metadata, turnOnConditions, turnOffConditions
				));
			}
		} catch (SQLException e) {
			Messages.info("EwcManager.java:161 >>");
			Messages.error("Failed to read ResultSet.");
			Messages.error(Messages.getStackTrace(e));
		}
		Messages.info("Loaded " + count + " planned behaviour.");
	}
	
	public List<EwcUnit> getEwcUnits() {
		return this.ewcs;
	}
	
	public List<GSM> getGSMModules() {
		return this.gsmModules;
	}
	
	public EwcUnit getEwcUnitBySoftwareId(int id) {
		for(EwcUnit ewc : this.ewcs)
			if(ewc.getSoftwareId() == id)
				return ewc;
		return null;
	}
	
	public List<EwcUnit> getEwcUnitsByEwcId(short id) {
		List<EwcUnit> ewcs = new ArrayList<EwcUnit>();
		for(EwcUnit ewc : this.ewcs)
			if(ewc.getEwcId() == id)
				ewcs.add(ewc);
		return ewcs;
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
		Messages.info("Trying to start EwcManager...");
		if(!this.running) {
			this.logicThread = null;
			this.logicThread = new Thread(this);
			this.logicThread.start();
			Messages.info("EwcManager started.");
		}
	}
	
	public SmartServer getServer() {
		return this.server;
	}
	
	public PulseMaker getPulseMaker() {
		return this.pulseMaker;
	}
	
	@Override
	public void run() {
		this.running = true;
		this.shouldStop = false;
		Messages.info("Loading ewcs from database...");
		this.loadEwcsFromDatabase();
		Messages.info("Loading signal behaviour from database...");
		this.loadSignalBehaviourFromDatabase();
		Messages.info("Loading security behaviour from database...");
		this.loadSecurityBehaviourFromDatabase();
		Messages.info("Loading planned behaviour from database...");
		this.loadPlannedBehaviourFromDatabase();
		Messages.info("Loading ewc state values from database...");
		this.loadEwcStateValuesFromDatabase();
		Messages.info("Loading input state values from net...");
		this.loadInputStateValuesFromNet();
		Messages.info("Loading thermostats from database...");
		this.loadThermostatsFromDatabase();
		Messages.info("Starting planned behaviour manager...");
		this.plannedBehaviourManager.start();
		Messages.info("Planned behaviour manager started.");
		//this.behaviourConditionManager.start();
		this.handleSecuritySystems();
		Messages.info("Security inputs loaded.");

		Messages.info("Starting Pulse Maker...");
		this.pulseMaker.start();
		Messages.info("Pulse Maker started.");
		
		Messages.info("Enabling GSM modules...");
		new Thread(()->{
			for(GSM gsm : this.gsmModules) {
				gsm.connectPin(this.gpioRaspi);
				gsm.enable();
			}
			Messages.info("GSM modules enabled.");
		}).start();
		
		while(!this.shouldStop) {
			
			/*for (Integer ewc : this.activeEwcs) {
            	try {
					this.hall(ewc.shortValue(), this.server.getEwcAccess().gHall(ewc.shortValue()));
				} catch (Exception e) {
					e.printStackTrace();
				}
            }*/
			this.gtId(this.activeEwcs);
			
			for(EwcUnit ewc : this.ewcs) {
				ewc.update();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Messages.info("EwcManager.java:214 >>");
				Messages.warning("Could not sleep a thread!");
				Messages.warning(Messages.getStackTrace(e));
			}
		}
		
		Messages.info("Stopping Pulse Maker...");
		this.pulseMaker.signalStop();
		Messages.info("Pulse Maker stopped.");
		
		//this.behaviourConditionManager.signalStop();
		Messages.info("Stopping planned behaviour manager...");
		this.plannedBehaviourManager.signalStop();
		Messages.info("Planned behaviour manager stopped.");
		
		this.ewcs.clear();
		Messages.info("Ewcs cleared...");
		this.behaviour.clear();
		Messages.info("Behaviour cleared...");
		
		this.running = false;
		Messages.info("EwcManager stoped.");
	}
	
	// ----------------------------------------------------------------------------

	public void hall(short ewcId, int value) {
		String states = EwcManager.hallValueToUnsignedString(value);
		//Messages.info("Hall called > " + ewcId + " : " + states);
		for(EwcUnit ewc : this.getEwcUnitsByEwcId(ewcId)) {
			if(ewc instanceof EwcUnitInput && !ewc.isVirtual()) {
				short val = Short.parseShort( states.charAt(31 - ewc.getInputOutputId())+"" );
				ewc.setStateValue(val);
				//Messages.info("Hall set > " + ewc.getInputOutputId() + " : " + val);
			}
		}
	}

	public void gtId(Set<Integer> ewcs) { //replaces gHall
		//Messages.info("Calling gtInLen");
		long ewcCode = convertSetEwcsToLong(ewcs);
		//Messages.info("Got ewc code: " + ewcCode);
		int[] allEwcStates = this.server.getEwcAccess().gtId(ewcCode, /*EwcAccess.STATUS | */EwcAccess.HALL);

		//Messages.info("num ewc states: " + allEwcStates.length);
		int counter = 0;
		//Messages.info("Short values:");
		//for(int s : allEwcStates)
		//	Messages.info(s+"");
		for(int ewc = 1; ewc <= 128; ewc++) {
			if(!ewcs.contains(new Integer(ewc))) continue;
			String states = EwcManager.hallValueToUnsignedString(allEwcStates[counter]);
			//Messages.info("Hall called > " + ewc + " : " + states);
			for(EwcUnit ewcUnit : this.getEwcUnitsByEwcId((short)ewc)) {
				if(ewcUnit instanceof EwcUnitInput && !ewcUnit.isVirtual()) {
					short val = Short.parseShort( states.charAt(31 - ewcUnit.getInputOutputId())+"" );
					ewcUnit.setStateValue(val);
					//Messages.info("Hall set > " + ewc.getInputOutputId() + " : " + val);
				}
			}
			counter++;
		}
	}
	
	private static long convertSetEwcsToLong(Set<Integer> ewcs) {
		long result = 0;
		for(int i : ewcs)
			result = result | (1 << (i - 1));
		return result;
	}
	
	private void loadEwcStateValuesFromDatabase() {
		for(EwcUnit ewc : this.ewcs) {
			ResultSet rs = this.server.getDatabase().executeQuery(
					SqlCommands.GET_EWC_LAST_STATE_VALUE.replace("%id%", ewc.getSoftwareId()+""));
			try {
				while(rs.next()) {
					ewc.setStateValue(rs.getShort("value"));
					break;
				}
			} catch (SQLException e) {
				ewc.setStateValue((short)0);
				Messages.error(Messages.getStackTrace(e));
			}
		}
	}
	
	private void loadThermostatsFromDatabase() {
		for(EwcUnit ewc : this.ewcs) {
			if(ewc instanceof EwcUnitInput) {
				EwcUnitInput in = (EwcUnitInput)ewc;
				int count = in.getThermostatCountInDatabase();
				if(count != 0) {
					in.loadThermostatFromDatabase();
				}
			}
		}
	}
	
	private void loadInputStateValuesFromNet() {
		for(EwcUnit ewc : this.ewcs) {
			if(ewc instanceof EwcUnitInput) {
				try {
					if(!ewc.isVirtual()) {
						boolean val = this.server.getEwcAccess().getIn(ewc.getEwcId(), ewc.getInputOutputId());
						ewc.setStateValue((short)(val ? 1 : 0));
					}
					//Messages.info("Reading input value for ewc unit (" + ewc.getSoftwareId() + "): " + (val ? 1 : 0));
				} catch (EwcNonExist e) {
					EwcHardwareAddress addr = new EwcHardwareAddress(
							ewc.getEwcId(), ewc.getType(), ewc.getInputOutputId());
					Messages.error("EWC Unit " + addr.getHardwareAddress() + " not found!");
					Messages.error(Messages.getStackTrace(e));
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void loadEwcStateValuesFromNet() {
		for(EwcUnit ewc : this.ewcs) {
			if(ewc instanceof EwcUnitInput) {
				try {
					if(!ewc.isVirtual()) {
						boolean val = this.server.getEwcAccess().getIn(ewc.getEwcId(), ewc.getInputOutputId());
						ewc.setStateValue((short)(val ? 1 : 0));
					}
					//Messages.info("Reading input value for ewc unit (" + ewc.getSoftwareId() + "): " + (val ? 1 : 0));
				} catch (EwcNonExist e) {
					Messages.info("EwcManager.java:247 >>");
					EwcHardwareAddress addr = new EwcHardwareAddress(
							ewc.getEwcId(), ewc.getType(), ewc.getInputOutputId());
					Messages.error("EWC Unit " + addr.getHardwareAddress() + " not found!");
				}
			}
			if(ewc instanceof EwcUnitOutput) {
				try {
					if(!ewc.isVirtual()) {
						boolean val = this.server.getEwcAccess().gOut(ewc.getEwcId(), ewc.getInputOutputId()) != 0;
						ewc.setStateValue((short)(val ? 1 : 0));
					}
					//Messages.info("Reading output value for ewc unit (" + ewc.getSoftwareId() + "): " + (val ? 1 : 0));
				} catch (EwcNonExist e) {
					Messages.info("EwcManager.java:247 >>");
					EwcHardwareAddress addr = new EwcHardwareAddress(
							ewc.getEwcId(), ewc.getType(), ewc.getInputOutputId());
					Messages.error("EWC Unit " + addr.getHardwareAddress() + " not found!");
				}
			}
		}
	}
	
	public static String hallValueToUnsignedString(int i) {
        char[] buf = new char[32];
        for (int charPos = 31; charPos >= 0; charPos--) {
            buf[charPos] = (char) ((i & 1) + '0');
            i >>>= 1;
        }

        return new String(buf, 0, 32);
    }
}