package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.device.EwcUnit;
import cz.allcomp.shs.device.EwcUnitInput;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCtemp extends ClientCommand {

	public CCtemp(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 1) {
			try {
				int swId = Integer.parseInt(args[0]);
				EwcUnit ewc = server.getEwcManager().getEwcUnitBySoftwareId(swId);
				if(ewc != null) {
					if(ewc instanceof EwcUnitInput)
						this.setResponse(((EwcUnitInput)ewc).getTemperatureCelsius()+"");
					else {
						this.setResponse(ClientStateMessages.NOT_EWC_INPUT);
						retVal = false;
					}
				} else {
					this.setResponse(ClientStateMessages.EWC_NULL);
					retVal = false;
				}
			} catch (NumberFormatException e) {
				this.setResponse(ClientStateMessages.NOT_NUMBER);
				retVal = false;
				e.printStackTrace();
			}
		} else {
			this.setResponse(ClientStateMessages.WRONG_NUM_OF_ARGS);
			retVal = false;
		}
		
		return retVal;
	}

	@Override
	public ClientCommand copy() {
		return new CCtemp(this.getCommand());
	}

}
