package cz.allcomp.shs.net;

import java.util.ArrayList;
import java.util.List;

import cz.allcomp.shs.SmartServer;

public abstract class ClientCommand {
	private String cmd, response;
	private static List<ClientCommand> cmds = new ArrayList<>();
	
	public abstract boolean execute(SmartServer server, String[] args);
	
	public ClientCommand(String cmd) {
		this.cmd = cmd;
	}
	
	public abstract ClientCommand copy();
	
	public static void register(ClientCommand cc) {
		cmds.add(cc);
	}
	
	public static ClientCommand get(String cmd) {
		for(ClientCommand c : cmds)
			if(c.getCommand().equals(cmd))
				return c;
		return null;
	}
	
	protected String getCommand() {
		return this.cmd;
	}
	
	public String getResponse() {
		return this.response;
	}
	
	protected void setResponse(String response) {
		this.response = response;
	}
}
