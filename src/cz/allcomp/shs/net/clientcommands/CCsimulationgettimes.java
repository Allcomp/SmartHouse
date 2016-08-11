package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.ewc.WorkingSimulator;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;
import cz.allcomp.shs.util.Time;

public class CCsimulationgettimes extends ClientCommand {

	public CCsimulationgettimes(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 0) {
			WorkingSimulator simulator = server.getWorkingSimulator();
			Time startTime = simulator.getStartTime();
			Time endTime = simulator.getEndTime();
			if(simulator.isRunning()) {
				if(startTime != null && endTime != null)
					this.setResponse(startTime.getTimeStamp() + ",," + endTime.getTimeStamp());
				else
					this.setResponse(ClientStateMessages.SIMULATOR_TIMES_NOT_SET);
			} else
				this.setResponse(ClientStateMessages.SIMULATOR_NOT_RUNNING);
		} else {
			this.setResponse(ClientStateMessages.WRONG_NUM_OF_ARGS);
			retVal = false;
		}
		
		return retVal;
	}

	@Override
	public ClientCommand copy() {
		return new CCsimulationgettimes(this.getCommand());
	}

}
