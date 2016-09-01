package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.device.EwcUnit;
import cz.allcomp.shs.device.EwcUnitInput;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCthermsactive extends ClientCommand {

	public CCthermsactive(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 1) {
			try {
				String[] pairs = args[0].split("-");
				for(String p : pairs) {
					String[] splittedPair = p.split(":");
					if(splittedPair.length == 2) {
						int id = Integer.parseInt(splittedPair[0]);
						EwcUnit ewc = server.getEwcManager().getEwcUnitBySoftwareId(id);
						if(ewc != null) {
							int val = Integer.parseInt(splittedPair[1]);
							if(ewc instanceof EwcUnitInput) {
								if(val == 1) {
									((EwcUnitInput)ewc).activateThermostat();
									this.setResponse(ClientStateMessages.OK);
								} else if(val == 0) {
									((EwcUnitInput)ewc).deactivateThermostat();
									this.setResponse(ClientStateMessages.OK);
								} else {
									this.setResponse(ClientStateMessages.NOT_LOGICAL_VALUE);
									retVal = false;
								}
							} else {
								this.setResponse(ClientStateMessages.NOT_EWC_INPUT);
								retVal = false;
							}
						} else {
							this.setResponse(ClientStateMessages.EWC_NULL);
							retVal = false;
						}
					} else {
						this.setResponse(ClientStateMessages.WRONG_FORMAT);
						retVal = false;
					}
				}
			} catch (NumberFormatException e) {
				this.setResponse(ClientStateMessages.NOT_NUMBER);
				retVal = false;
			}
		} else {
			this.setResponse(ClientStateMessages.WRONG_NUM_OF_ARGS);
			retVal = false;
		}
		
		return retVal;
	}

	@Override
	public ClientCommand copy() {
		return new CCthermsactive(this.getCommand());
	}

}
