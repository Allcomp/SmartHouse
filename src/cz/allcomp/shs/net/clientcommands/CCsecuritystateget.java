package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCsecuritystateget extends ClientCommand {

	public CCsecuritystateget(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 1)
			this.setResponse(server.getSecuritySystem(Integer.parseInt(args[0])).isRunning() ? "1" : "0");
		else {
			this.setResponse(ClientStateMessages.WRONG_NUM_OF_ARGS);
			retVal = false;
		}
		
		return retVal;
	}

	@Override
	public ClientCommand copy() {
		return new CCsecuritystateget(this.getCommand());
	}

}
