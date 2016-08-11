package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.net.ClientCommand;

public class CCgetversionandname extends ClientCommand {

	public CCgetversionandname(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		this.setResponse(SmartServer.VERSION_NAME + " " + SmartServer.VERSION);
		
		return true;
	}

	@Override
	public ClientCommand copy() {
		return new CCgetversionandname(this.getCommand());
	}

}
