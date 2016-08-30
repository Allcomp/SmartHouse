package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCshutdown extends ClientCommand {

	public CCshutdown(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {

		server.signalStop();
		this.setResponse(ClientStateMessages.OK);
		
		return true;
	}

	@Override
	public ClientCommand copy() {
		return new CCshutdown(this.getCommand());
	}

}
