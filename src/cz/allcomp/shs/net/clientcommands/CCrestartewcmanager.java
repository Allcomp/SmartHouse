package cz.allcomp.shs.net.clientcommands;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.net.ClientCommand;
import cz.allcomp.shs.net.ClientStateMessages;

public class CCrestartewcmanager extends ClientCommand {

	public CCrestartewcmanager(String cmd) {
		super(cmd);
	}

	@Override
	public boolean execute(SmartServer server, String[] args) {

		server.getEwcManager().signalStop();
		this.setResponse(ClientStateMessages.OK);
		new Thread(()->{
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.getEwcManager().start();
		}).start();
		return true;
	}

	@Override
	public ClientCommand copy() {
		return new CCrestartewcmanager(this.getCommand());
	}

}
