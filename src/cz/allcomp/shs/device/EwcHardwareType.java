package cz.allcomp.shs.device;

import cz.allcomp.shs.logging.Messages;

public enum EwcHardwareType {
	NC((byte)-1),
	QUADRUPLEX((byte)0x0),
	UEWC((byte)0x1),
	UPS((byte)0x3),
	RFIDREAD((byte)0x4),
	SWBOARD((byte)0x5),
	VALVES((byte)0x6),
	THERMOEWC((byte)0x7),
	EVICKO((byte)0x8),
	DIN1010((byte)0xC),
	TCC((byte)0xD),
	ECC((byte)0x10),
	EPP((byte)0x11),
	AFRICA((byte)0x12),
	UDM((byte)0x13),
	KNOB((byte)0x20),
	KNOBDIS((byte)0x21),
	SUPPLY((byte)0x22),
	TIMER((byte)0x23);
	
	private byte id;
	
	private EwcHardwareType(byte id) {
		this.id = id;
	}
	
	public byte getId() {
		return this.id;
	}
	
	public static EwcHardwareType getById(byte id) {
		for(EwcHardwareType type : EwcHardwareType.values())
			if(type.getId() == id)
				return type;
		return NC;
	}
	
	public static EwcHardwareType getByString(String s) {
		for(EwcHardwareType type : EwcHardwareType.values())
			if(type.name().equalsIgnoreCase(s))
				return type;
		try {
			byte id = Byte.parseByte(s);
			return EwcHardwareType.getById(id);
		} catch (NumberFormatException e) {
			Messages.warning("Ewc hardware type or id is not valid!");
			Messages.warning(Messages.getStackTrace(e));
		}
		return NC;
	}
}
