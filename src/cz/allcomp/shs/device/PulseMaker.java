package cz.allcomp.shs.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.allcomp.shs.allcomplib.transducers.EwcNonExist;
import cz.allcomp.shs.logging.Messages;

public class PulseMaker implements Runnable {

	private HashMap<Integer, PulseData> ewcs;
	private HashMap<Integer, PulseData> ewcsToAdd;
	private List<Integer> ewcsToRemove;
	private HashMap<Integer, EwcUnit> ewcCache;
	
	private Thread logicThread;
	private EwcManager ewcManager;
	private long counter = 0;

	private boolean shouldStop;
	private boolean running;
	
	public PulseMaker(EwcManager ewcManager) {
		this.ewcManager = ewcManager;
		this.shouldStop = true;
		this.running = false;
		this.ewcs = new HashMap<Integer, PulseData>();
		this.ewcsToAdd = new HashMap<Integer, PulseData>();
		this.ewcsToRemove = new ArrayList<Integer>();
		this.ewcCache = new HashMap<Integer, EwcUnit>();
		
		this.logicThread = new Thread(this);
	}
	
	public void startPulsing(int ewc, int frequency, float dutyCycle) {
		this.ewcCache.put(ewc, this.ewcManager.getEwcUnitBySoftwareId(ewc));
		this.ewcsToAdd.put(ewc, new PulseData(dutyCycle, frequency));
	}
	
	public void stopPulsing(int ewc) {
		this.ewcsToRemove.add(ewc);
	}
	
	public boolean isPulsing(int ewc) {
		return this.ewcs.containsKey(ewc);
	}

	@Override
	public void run() {

		if(this.running)
			return;
		this.shouldStop = false;
		this.running = true;
		
		while(!this.shouldStop) {
			
			if(counter % 50 == 0) {
				for(int ewc : ewcsToRemove) {
					if(ewcCache.containsKey(ewc)) {
						ewcCache.get(ewc).setStateValue((short)0);
						this.ewcs.remove(ewc);
					}
				}
				this.ewcsToRemove.clear();
				
				this.ewcs.putAll(this.ewcsToAdd);
				this.ewcsToAdd.clear();
			}
			
			for(int ewc : this.ewcs.keySet()) {
				PulseData pd = this.ewcs.get(ewc);
				if(pd.hasChanged()) {
					EwcUnit unit = this.ewcCache.get(ewc);
					//if(ewcAccess != null)
					try {
						this.ewcManager.getServer().getEwcAccess()
						.sOut(
								unit
								.getEwcId(), 
								unit
								.getInputOutputId(), 
								(byte)pd
								.getState()
								.toInt());
					} catch (EwcNonExist e) {
						e.printStackTrace();
					}
				}
					
				pd.update();
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Messages.info(Messages.getStackTrace(e));
			}

			counter++;
			if(counter + 1 >= Long.MAX_VALUE)
				counter = 0;
		}

		this.running = false;
	}
	
	public void start() {
		if(!this.running) {
			this.logicThread = new Thread(this);
			this.logicThread.start();
		}
	}
	
	public void signalStop() {
		this.shouldStop = true;
	}
	
	public boolean isRunning() {
		return this.running;
	}
}
