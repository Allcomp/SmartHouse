package cz.allcomp.shs.net.clientcommands;

import java.util.HashMap;
import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.ewc.EwcUnitInput;
import cz.allcomp.shs.ewc.SecuritySystem;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCsecurityinputsget extends ClientCommand {

	public CCsecurityinputsget(String cmd) {
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
				response+="#&"+in.getSoftwareId()+":"+in.getDescription();
			if(response.length() > 1)
				response = response.substring(2);
			this.setResponse(response);
		} else if(args.length == 0) {
			String response = "";
			HashMap<Integer, SecuritySystem> ssystems = server.getSecuritySystems();
			for(int sid : ssystems.keySet()) {
				response += "?#system?"+sid+"?#scon?";
				SecuritySystem ss = ssystems.get(sid);
				List<EwcUnitInput> inputs = ss.getSecurityInputs();
				String inputsString = "";
				for(EwcUnitInput in : inputs)
					inputsString+="?#ins?"+in.getSoftwareId()+"?#inv?"+in.getDescription();
				if(inputsString.length() > 1)
					inputsString = inputsString.substring(6);
				response+=inputsString;
			}
			if(response.length() > 1)
				response = response.substring(9);
			this.setResponse(response);
		} else {
			this.setResponse(ClientStateMessages.WRONG_NUM_OF_ARGS);
			retVal = false;
		}
		
		return retVal;
	}

	@Override
	public ClientCommand copy() {
		return new CCsecurityinputsget(this.getCommand());
	}

}
