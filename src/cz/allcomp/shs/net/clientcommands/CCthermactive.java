package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.device.EwcUnit;
import cz.allcomp.shs.device.EwcUnitInput;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCthermactive extends ClientCommand {

	public CCthermactive(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 2) {
			short id;
			try {
				id = Short.parseShort(args[0]);
				EwcUnit ewc = server.getEwcManager().getEwcUnitBySoftwareId(id);
				int value = Integer.parseInt(args[1]);
				if(ewc != null)
					if(ewc instanceof EwcUnitInput) {
						EwcUnitInput in = (EwcUnitInput)ewc;
						if(value == 1) {
							in.activateThermostat();
							this.setResponse(ClientStateMessages.OK);
						} else if(value == 0) {
							in.deactivateThermostat();
							this.setResponse(ClientStateMessages.OK);
						} else {
							this.setResponse(ClientStateMessages.NOT_LOGICAL_VALUE);
							retVal = false;
						}
					} else {
						this.setResponse(ClientStateMessages.NOT_EWC_INPUT);
						retVal = false;
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
		return new CCthermactive(this.getCommand());
	}

}
