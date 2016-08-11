package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.ewc.EwcUnit;
import cz.allcomp.shs.ewc.EwcUnitInput;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCthermsset extends ClientCommand {

	public CCthermsset(String cmd) {
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
							double val = Double.parseDouble(splittedPair[1]);
							if(ewc instanceof EwcUnitInput) {
								((EwcUnitInput)ewc).setTargetTemperatureCelsius(val);
								this.setResponse(ClientStateMessages.OK);
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
		return new CCthermsset(this.getCommand());
	}

}
