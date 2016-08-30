package cz.allcomp.shs.net.clientcommands;

import java.util.ArrayList;
import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.ewc.EwcUnit;
import cz.allcomp.shs.ewc.EwcUnitInput;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCthermsactiveget extends ClientCommand {

	public CCthermsactiveget(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 0) {
			String states = "";
			for(EwcUnit ewc : server.getEwcManager().getEwcUnits())
				if(ewc instanceof EwcUnitInput)
					states += "-" + ewc.getSoftwareId() + ":" + (((EwcUnitInput)ewc).isThermostatActive() ? "1" : "0");
			if(states.length() > 0)
				states = states.substring(1);
			this.setResponse(states);
		} else if(args.length == 1) {
			String states = "";
			String[] swIdsString = args[0].split("-");
			List<Integer> swIds = new ArrayList<>();
			for(String s : swIdsString) {
				try {
					int id = Integer.parseInt(s);
					swIds.add(id);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			for(EwcUnit ewc : server.getEwcManager().getEwcUnits())
				if(swIds.contains(ewc.getSoftwareId()))
					if(ewc instanceof EwcUnitInput)
						states += "-" + ewc.getSoftwareId() + ":" + (((EwcUnitInput)ewc).isThermostatActive() ? "1" : "0");
			if(states.length() > 0)
				states = states.substring(1);
			this.setResponse(states);
		} else {
			this.setResponse(ClientStateMessages.WRONG_NUM_OF_ARGS);
			retVal = false;
		}
		
		return retVal;
	}

	@Override
	public ClientCommand copy() {
		return new CCthermsactiveget(this.getCommand());
	}

}
