package cz.allcomp.shs.behaviour;

import java.util.ArrayList;
import java.util.List;

import cz.allcomp.shs.device.EwcManager;
import cz.allcomp.shs.device.EwcUnit;

public class Macro {

	private List<MacroEntry> entries;
	
	public List<MacroEntry> getEntries() {
		return this.entries;
	}
	
	public static Macro get(String macroString, EwcManager ewcManager) {
		List<MacroEntry> entries = new ArrayList<>();
		String[] entryStrings = macroString.split(";");
		for(String entryString : entryStrings) {
			String[] paramPairs = entryString.split(",");
			
			MacroEntryType type = MacroEntryType.UNKNOWN;
			long delay = 0;
			long duration = 0;
			long pause = 0;
			boolean noChange = false;
			boolean overcontrol = false;
			boolean reverse = false;
			EwcUnit io = null;
			int repeat = 0;
			short value = 0;
			
			for(String paramPair : paramPairs) {
				String[] param = paramPair.split("=");
				if(param.length == 2) {
					if(param[0].equalsIgnoreCase("type"))
						type = MacroEntryType.get(param[1]);
					if(param[0].equalsIgnoreCase("delay"))
						try {
							delay = Long.parseLong(param[1]);
						} catch (NumberFormatException e) {}
					if(param[0].equalsIgnoreCase("duration"))
						try {
							duration = Long.parseLong(param[1]);
						} catch (NumberFormatException e) {}
					if(param[0].equalsIgnoreCase("pause"))
						try {
							pause = Long.parseLong(param[1]);
						} catch (NumberFormatException e) {}
					if(param[0].equalsIgnoreCase("noChange"))
						try {
							noChange = Boolean.parseBoolean(param[1]);
						} catch (NumberFormatException e) {}
					if(param[0].equalsIgnoreCase("overcontrol"))
						try {
							overcontrol = Boolean.parseBoolean(param[1]);
						} catch (NumberFormatException e) {}
					if(param[0].equalsIgnoreCase("reverse"))
						try {
							reverse = Boolean.parseBoolean(param[1]);
						} catch (NumberFormatException e) {}
					if(param[0].equalsIgnoreCase("io"))
						try {
							int id = Integer.parseInt(param[1]);
							io = ewcManager.getEwcUnitBySoftwareId(id);
						} catch (NumberFormatException e) {}
					if(param[0].equalsIgnoreCase("repeat"))
						try {
							repeat = Integer.parseInt(param[1]);
						} catch (NumberFormatException e) {}
					if(param[0].equalsIgnoreCase("value"))
						try {
							value = Short.parseShort(param[1]);
						} catch (NumberFormatException e) {}
				}
			}
			
			switch(type) {
				case VALUE:
					if(io != null)
						entries.add(new MacroEntryTValue(type, delay, value, io, noChange));
					break;
				case PULSE:
					if(io != null)
						entries.add(new MacroEntryTPulse(type, duration, io, reverse, duration, overcontrol, repeat, noChange, pause));
					break;
				default:
					break;
			}
		}
		return new Macro(entries);
	}
	
	private Macro(List<MacroEntry> entries) {
		this.entries = entries;
	}
	
	public void execute() {
		for(MacroEntry e : this.entries)
			new Thread(e).start();
	}
}
