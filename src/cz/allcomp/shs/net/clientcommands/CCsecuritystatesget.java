package cz.allcomp.shs.net.clientcommands;

import java.util.HashMap;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.device.SecuritySystem;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCsecuritystatesget extends ClientCommand {

	public CCsecuritystatesget(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 0) {
			String res = "";
			HashMap<Integer, SecuritySystem> secSystems = server.getSecuritySystems();
			for(int sid : secSystems.keySet())
				res += "-" + sid + ":" + (secSystems.get(sid).isRunning() ? "1" : "0");
			if(res.length() > 0)
				res = res.substring(1);
			this.setResponse(res);
		} else {
			this.setResponse(ClientStateMessages.WRONG_NUM_OF_ARGS);
			retVal = false;
		}
		
		return retVal;
	}

	@Override
	public ClientCommand copy() {
		return new CCsecuritystatesget(this.getCommand());
	}

}
