package cz.allcomp.shs.net.clientcommands;

import java.sql.SQLException;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.ewc.WorkingSimulator;
import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;
import cz.allcomp.shs.util.Time;

public class CCsimulationstart extends ClientCommand {

	public CCsimulationstart(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 2) {
			WorkingSimulator simulator = server.getWorkingSimulator();
			
			try {
				final Time timeStart = Time.getTime(Long.parseLong(args[0]));
				final Time timeStop = Time.getTime(Long.parseLong(args[1]));
				this.setResponse(ClientStateMessages.OK);
				new Thread(()->{
					if(timeStart != null && timeStop != null) {
						try {
							simulator.prepareSimulator(timeStart, timeStop);
							simulator.startSimulation();
						} catch (NumberFormatException | SQLException e) {
							Messages.warning(Messages.getStackTrace(e));
						}
					}
				}).start();
			} catch (NumberFormatException e1) {
				this.setResponse(ClientStateMessages.NOT_NUMBER);
			}
		} else {
			this.setResponse(ClientStateMessages.WRONG_NUM_OF_ARGS);
			retVal = false;
		}
		
		return retVal;
	}

	@Override
	public ClientCommand copy() {
		return new CCsimulationstart(this.getCommand());
	}

}
