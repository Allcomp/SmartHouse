package cz.allcomp.shs.net.clientcommands;

import java.util.HashMap;
import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.ewc.EwcUnitInput;
import cz.allcomp.shs.ewc.SecuritySystem;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCsecurityactiveinputsget extends ClientCommand {

	public CCsecurityactiveinputsget(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 1) {
			SecuritySystem ssystem = server.getSecuritySystem(Integer.parseInt(args[0]));
			List<EwcUnitInput> inputs = ssystem.getSecurityInputs();
			String response = "";
			for(EwcUnitInput in : inputs)
				if(in.getStateValue() == in.getActiveStateValue())
					response+="-"+in.getSoftwareId();
			if(response.length() > 0)
				response = response.substring(1);
			this.setResponse(response);
		} else if(args.length == 0) {
			String response = "";
			HashMap<Integer, SecuritySystem> ssystems = server.getSecuritySystems();
			for(int sid : ssystems.keySet()) {
				response += "&"+sid+":";
				List<EwcUnitInput> inputs = ssystems.get(sid).getSecurityInputs();
				String securityIns = "";
				for(EwcUnitInput in : inputs)
					if(in.getStateValue() == in.getActiveStateValue())
						securityIns+="-"+in.getSoftwareId();
				if(securityIns.length() > 0)
					securityIns = securityIns.substring(1);
				response+=securityIns;
			}
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
		return new CCsecurityactiveinputsget(this.getCommand());
	}

}
