package cz.allcomp.shs.ewc;

import java.util.HashMap;
import java.util.Set;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.allcomplib.transducers.EwcNonExist;
import cz.allcomp.shs.logging.Messages;

public class EwcOutputPatch implements Runnable {
	
	private static final short ERROR_INPUT = 15;
	
	private Set<Integer> activeEwcs;
	private HashMap<Integer, Integer> lastValues;
	private SmartServer server;
	private boolean running, shouldStop;
	
	private Thread logicThread;
	
	public EwcOutputPatch(SmartServer server, Set<Integer> activeEwcs) {
		this.server = server;
		this.activeEwcs = activeEwcs;
		this.running = false;
		this.shouldStop = true;
		this.logicThread = new Thread(this);
		this.lastValues = new HashMap<Integer, Integer>();
		for(int i : this.activeEwcs)
			this.lastValues.put(i, 0);
	}
	
	private void checkProblem(int ewcId) throws EwcNonExist {
		String states = EwcManager.hallValueToUnsignedString(this.server.getEwcAccess().gHall((short)ewcId));
		
		short inputValue = Short.parseShort( states.charAt(31 - ERROR_INPUT)+"" );
		int lastValue = this.lastValues.get(ewcId);
		this.lastValues.put(ewcId, (int)inputValue);
		if(!(lastValue == 1 && inputValue == 0))
			return;
		
		EwcUnitOutput firstOutput = null;
		short firstOutputVal = 0;
		boolean someOutputOn = false;
		int counter = 0;
		for(EwcUnit ewc : server.getEwcManager().getEwcUnitsByEwcId((short)ewcId)) {
			if(ewc instanceof EwcUnitOutput) {
				short val = Short.parseShort( states.charAt(31 - ewc.getInputOutputId())+"" );
				if(counter == 0) {
					firstOutput = (EwcUnitOutput)ewc;
					firstOutputVal = val;
				}
				if(val == 1) {
					someOutputOn = true;
					break;
				}
			}
			counter++;
		}
		
		if(someOutputOn) {
			Messages.error("Problem found on ewc id " + ewcId + "! All outputs are inactive while the software values not! We are having fixed this problem in few seconds.");
			
			if(firstOutput == null) {
				Messages.error("Problem fixing failed. First output null!");
			} else {
				server.getEwcAccess().sOut((short)ewcId, firstOutput.getInputOutputId(), (byte)(1 - firstOutputVal));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Messages.warning(Messages.getStackTrace(e));
				}
				server.getEwcAccess().sOut((short)ewcId, firstOutput.getInputOutputId(), (byte)firstOutputVal);
				Messages.error("Problem should be fixed.");
			}
		}
	}
	
	public void start() {
		if(!this.running)
			this.logicThread.start();
	}
	
	public void signalStop() {
		this.shouldStop = true;
	}
	
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public void run() {
		this.shouldStop = false;
		this.running = true;
		
		while(!this.shouldStop) {
			for(int ewc : this.activeEwcs) {
				try {
					this.checkProblem(ewc);
				} catch (EwcNonExist e) {
					Messages.warning(Messages.getStackTrace(e));
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Messages.warning(Messages.getStackTrace(e));
			}
		}
		
		this.running = false;		
	}
}
