package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.net.ClientCommand;

public class CCgetversionname extends ClientCommand {

	public CCgetversionname(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		this.setResponse(SmartServer.VERSION_NAME);
		
		return true;
	}

	@Override
	public ClientCommand copy() {
		return new CCgetversionname(this.getCommand());
	}

}
