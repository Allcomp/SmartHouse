package cz.allcomp.shs.net.clientcommands;

import java.util.HashMap;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.device.SecuritySystem;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCsecurityremainingnotificationsget extends ClientCommand {

	public CCsecurityremainingnotificationsget(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 1) {
			SecuritySystem ssystem = server.getSecuritySystem(Integer.parseInt(args[0]));
			this.setResponse(ssystem.getRemainingNotificationsToAlarm()+"");
		} else if(args.length == 0) {
			String response = "";
			HashMap<Integer, SecuritySystem> ssystems = server.getSecuritySystems();
			for(int sid : ssystems.keySet())
				response += "&"+sid+":"+ssystems.get(sid).getRemainingNotificationsToAlarm();
			if(response.length() > 0)
				response = response.substring(1);
			this.setResponse(response);
		} else {
			this.setResponse(ClientStateMessages.WRONG_NUM_OF_ARGS);
			retVal = false;
		}
		
		return retVal;
	}

	@Override
	public ClientCommand copy() {
		return new CCsecurityremainingnotificationsget(this.getCommand());
	}

}
