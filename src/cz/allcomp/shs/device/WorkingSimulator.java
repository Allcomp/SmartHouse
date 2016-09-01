package cz.allcomp.shs.device;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.database.SqlCommands;
import cz.allcomp.shs.database.StableMysqlConnection;
import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.util.Time;

public class WorkingSimulator implements Runnable {

	public static final int LOAD_UNITS_NUMBER_IN_TIME = 100;
	
	private final SmartServer server;
	private Time startTime, endTime;
	private Thread routineThread;
	private boolean running, shouldStop;
	private int currentUnitIndex, currentLoadTime;
	private int dayDifference;
	
	private List<SimulatorUnit> simulatorUnits;
	
	public WorkingSimulator(SmartServer server) {
		this.simulatorUnits = new ArrayList<>();
		this.server = server;
		this.startTime = null;
		this.endTime = null;
		this.running = false;
		this.shouldStop = true;
		this.currentUnitIndex = 0;
		this.currentLoadTime = 0;
		this.dayDifference = -1;
	}
	
	private void prepareSimulator(Time startTime, Time endTime, int loadTime) throws SQLException {
		this.startTime = startTime;
		this.endTime = endTime;
		this.simulatorUnits.clear();
		String sqlCmd = SqlCommands.LOAD_SIMULATOR_UNITS
				.replace("%startTime%", this.startTime.getTimeStamp()+"")
				.replace("%endTime%", this.endTime.getTimeStamp()+"")
				.replace("%startRow%", (loadTime*WorkingSimulator.LOAD_UNITS_NUMBER_IN_TIME)+"")
				.replace("%numRows%", WorkingSimulator.LOAD_UNITS_NUMBER_IN_TIME+"");
		//Messages.info(sqlCmd);
		StableMysqlConnection database = this.server.getDatabase();
		ResultSet rs = database.executeQuery(sqlCmd);
		while(rs.next()) {
			short swId = rs.getShort("ewc_id");
			long time = rs.getLong("time");
			short targetValue = rs.getShort("value");
			EwcManager ewcManager = this.server.getEwcManager();
			EwcUnit ewcUnit = ewcManager.getEwcUnitBySoftwareId(swId);
			if(!(ewcUnit instanceof EwcUnitOutput))
				continue;
			if(!((EwcUnitOutput)ewcUnit).canSimulate())
				continue;
			this.simulatorUnits.add(new SimulatorUnit((EwcUnitOutput)ewcUnit, time, targetValue));
		}
		if(this.simulatorUnits.size() == 0) {
			this.currentLoadTime = 0;
			if(!this.shouldStop) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Messages.warning(Messages.getStackTrace(e));
				}
				this.prepareSimulator(this.startTime, this.endTime, 0);
			}
		}
	}
	
	public void prepareSimulator(Time startTime, Time stopTime) throws SQLException {
		this.currentLoadTime = 0;
		this.currentUnitIndex = 0;
		this.prepareSimulator(startTime, stopTime, 0);
		Messages.info("WorkingSimulator prepared simulation.");
	}
	
	public void startSimulation() {
		if(startTime == null || endTime == null || simulatorUnits.size() == 0)
			return;
		if(this.running)
			return;
		
		Time currTime = Time.getTime();
		Time differenceTime = Time.getTime(currTime.getTimeStamp()-startTime.getTimeStamp());
		this.dayDifference = differenceTime.getDay()-1;
		
		this.routineThread = null;
		this.routineThread = new Thread(this);
		this.routineThread.start();
		Messages.info("WorkingSimulator started simulation.");
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public boolean isStopping() {
		return this.running && this.shouldStop;
	}
	
	public boolean isStopped() {
		return !this.running && this.shouldStop;
	}
	
	public boolean isStarting() {
		return !this.running && !this.shouldStop;
	}
	
	public boolean isStarted() {
		return this.running && !this.shouldStop;
	}
	
	public void stopSimulation() {
		this.shouldStop = true;
	}
	
	public void simulate(SimulatorUnit simulatorUnit) {
		simulatorUnit.getOutput().simulateStateValue(
				simulatorUnit.getTargetState());
	}
	
	public Time getStartTime() {
		return this.startTime;
	}
	
	public Time getEndTime() {
		return this.endTime;
	}

	@Override
	public void run() {
		this.shouldStop = false;
		this.running = true;
		
		while(!this.shouldStop) {
			
			if(this.currentUnitIndex >= this.simulatorUnits.size()) {
				this.currentUnitIndex = 0;
				this.currentLoadTime++;
				try {
					this.prepareSimulator(this.startTime, this.endTime, this.currentLoadTime);
				} catch (SQLException e) {
					Messages.error("Could not load simulator units!");
					Messages.error(Messages.getStackTrace(e));
					this.currentUnitIndex = WorkingSimulator.LOAD_UNITS_NUMBER_IN_TIME;
					this.currentLoadTime--;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						Messages.warning("Could not sleep a thread!");
						Messages.warning(Messages.getStackTrace(e1));
					}
					continue;
				}
			}
			
			SimulatorUnit currUnit = this.simulatorUnits.get(this.currentUnitIndex);
			Time currTime = Time.getTime();
			Time currUnitTime = Time.getTime(currUnit.getTime());
			
			Time differenceTime = Time.getTime(currTime.getTimeStamp()-currUnitTime.getTimeStamp());
			int currDayDifference = differenceTime.getDay()-1;
			
			Messages.info("<WorkingSimulator>");
			Messages.info("Current simulator unit: " + currUnit.getOutput().getSoftwareId() + ", " + currUnit.getTime() + ", " + currUnit.getTargetState());
			
			double currTimeHours = (double)currTime.getHour()
					+(double)((double)currTime.getMinutes()/60.0)
					+(double)((double)currTime.getSeconds()/3600.0);
			double currUnitTimeHours = (double)currUnitTime.getHour()
					+(double)((double)currUnitTime.getMinutes()/60.0)
					+(double)((double)currUnitTime.getSeconds()/3600.0);

			Messages.info("Hours: s-" + currTimeHours + ", u-" + currUnitTimeHours);
			Messages.info("Hour difference: " + (currUnitTimeHours - currTimeHours));
			Messages.info("Required hour difference: <= " + (1.0/3600.0));
			
			if(currUnitTimeHours - currTimeHours <= (1.0 / 3600.0)) {
				if(this.dayDifference == currDayDifference) {
					if(currUnitTimeHours - currTimeHours <= -(5.0 / 3600.0)) {
						this.currentUnitIndex++;
						Messages.info("Unit not simulated, too late.");
					} else {
						Messages.info("Unit simulated.");
						currUnit.simulate(this);
						this.currentUnitIndex++;
					}
				} else
					Messages.info("Unit not simulated, waiting for the right day.");
			} else {
				Messages.info("Unit not simulated, waiting for the right time.");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					Messages.warning("Could not sleep a thread!");
					Messages.warning(Messages.getStackTrace(e));
				}
			}
			Messages.info("</WorkingSimulator>");
		}
		
		this.running = false;
	}
}
