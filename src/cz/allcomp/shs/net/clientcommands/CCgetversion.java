package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.net.ClientCommand;

public class CCgetversion extends ClientCommand {

	public CCgetversion(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		this.setResponse(SmartServer.VERSION);
		
		return true;
	}

	@Override
	public ClientCommand copy() {
		return new CCgetversion(this.getCommand());
	}

}
