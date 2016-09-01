package cz.allcomp.shs.net.clientcommands;

import java.util.ArrayList;
import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.device.EwcUnit;
import cz.allcomp.shs.device.PulseMaker;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCgetpulsing extends ClientCommand {

	public CCgetpulsing(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 0) {
			String states = "";
			PulseMaker pulseMaker = server.getEwcManager().getPulseMaker();
			for(EwcUnit ewc : server.getEwcManager().getEwcUnits())
				states += "-" + ewc.getSoftwareId() + ":" + (pulseMaker.isPulsing(ewc.getSoftwareId()) ? "1" : "0");
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
			PulseMaker pulseMaker = server.getEwcManager().getPulseMaker();
			for(EwcUnit ewc : server.getEwcManager().getEwcUnits())
				if(swIds.contains(ewc.getSoftwareId()))
					states += "-" + ewc.getSoftwareId() + ":" + (pulseMaker.isPulsing(ewc.getSoftwareId()) ? "1" : "0");
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
		return new CCgetpulsing(this.getCommand());
	}

}
