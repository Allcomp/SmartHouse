package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCstartpulsing extends ClientCommand {

	public CCstartpulsing(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {
		
		boolean retVal = true;
		
		if(args.length == 3) {
			short id;
			try {
				id = Short.parseShort(args[0]);
				int frequency = Integer.parseInt(args[1]);
				float dutyCycle = Float.parseFloat(args[2]);
				server.getEwcManager().getPulseMaker().startPulsing(id, frequency, dutyCycle);
				this.setResponse(ClientStateMessages.OK);
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
		return new CCstartpulsing(this.getCommand());
	}

}
