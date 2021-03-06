package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCsecuritydisable extends ClientCommand {

	public CCsecuritydisable(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase(server.getMainConfig().get("security_pin"))) {
				server.stopSecuritySystem(Integer.parseInt(args[1]));
				this.setResponse(ClientStateMessages.OK);
			} else {
				this.setResponse(ClientStateMessages.PIN_CODE_NOT_MATCH);
				retVal = false;
			}
		} else {
			this.setResponse(ClientStateMessages.WRONG_NUM_OF_ARGS);
			retVal = false;
		}
		
		return retVal;
	}

	@Override
	public ClientCommand copy() {
		return new CCsecuritydisable(this.getCommand());
	}

}
