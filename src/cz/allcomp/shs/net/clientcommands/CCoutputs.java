package cz.allcomp.shs.net.clientcommands;

import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.behaviour.SignalBehaviour;
import cz.allcomp.shs.ewc.EwcUnit;
import cz.allcomp.shs.ewc.EwcUnitOutput;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;
import cz.allcomp.shs.states.SwitchState;

public class CCoutputs extends ClientCommand {

	public CCoutputs(String cmd) {
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
							short val = Short.parseShort(splittedPair[1]);
							if(ewc instanceof EwcUnitOutput) {
								if(val==SwitchState.ON.toInt())
									ewc.setOvercontroled(true);
								else
									ewc.setOvercontroled(false);
								ewc.setStateValue(val);
								List<SignalBehaviour> bs = server.getEwcManager()
										.getBehaviourByOutputSoftwareId(ewc.getSoftwareId());
								for(SignalBehaviour b : bs)
									b.setLastRegulatorValue(val);
								this.setResponse(ClientStateMessages.OK);
							} else {
								this.setResponse(ClientStateMessages.NOT_EWC_OUTPUT);
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
		return new CCoutputs(this.getCommand());
	}

}
