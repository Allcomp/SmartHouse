package cz.allcomp.shs.net.clientcommands;

import java.util.List;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.behaviour.SignalBehaviour;
import cz.allcomp.shs.ewc.EwcUnit;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;
import cz.allcomp.shs.states.SwitchState;

public class CCoutput extends ClientCommand {

	public CCoutput(String cmd) {
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
				short value = Short.parseShort(args[1]);
				if(ewc != null) {
					if(value==SwitchState.ON.toInt())
						ewc.setOvercontroled(true);
					else
						ewc.setOvercontroled(false);
					ewc.setStateValue(value);
					List<SignalBehaviour> bs = server.getEwcManager()
							.getBehaviourByOutputSoftwareId(ewc.getSoftwareId());
					for(SignalBehaviour b : bs)
						b.setLastRegulatorValue(value);
					this.setResponse(ClientStateMessages.OK);
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
		return new CCoutput(this.getCommand());
	}

}
