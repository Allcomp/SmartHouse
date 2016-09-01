package cz.allcomp.shs.device;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public enum RpiPinSelect {

	UNKNOWN(-1, null),
	GPIO_0(11, RaspiPin.GPIO_00),
	GPIO_1(12, RaspiPin.GPIO_01),
	GPIO_2(13, RaspiPin.GPIO_02),
	GPIO_3(15, RaspiPin.GPIO_03),
	GPIO_4(16, RaspiPin.GPIO_04),
	GPIO_5(18, RaspiPin.GPIO_05),
	GPIO_6(22, RaspiPin.GPIO_06),
	GPIO_7(7, RaspiPin.GPIO_07),
	GPIO_8(3, RaspiPin.GPIO_08),
	GPIO_9(5, RaspiPin.GPIO_09),
	GPIO_10(24, RaspiPin.GPIO_10),
	GPIO_11(26, RaspiPin.GPIO_11),
	GPIO_12(19, RaspiPin.GPIO_12),
	GPIO_13(21, RaspiPin.GPIO_13),
	GPIO_14(23, RaspiPin.GPIO_14),
	GPIO_15(8, RaspiPin.GPIO_15),
	GPIO_16(10, RaspiPin.GPIO_16),
	GPIO_21(29, RaspiPin.GPIO_21),
	GPIO_22(31, RaspiPin.GPIO_22),
	GPIO_23(33, RaspiPin.GPIO_23),
	GPIO_24(35, RaspiPin.GPIO_24),
	GPIO_25(37, RaspiPin.GPIO_25),
	GPIO_26(32, RaspiPin.GPIO_26),
	GPIO_27(36, RaspiPin.GPIO_27),
	GPIO_28(38, RaspiPin.GPIO_28),
	GPIO_29(40, RaspiPin.GPIO_29),
	GPIO_30(27, RaspiPin.GPIO_30),
	GPIO_31(28, RaspiPin.GPIO_31);

	private int id;
	private Pin pin;
	
	private RpiPinSelect(int id, Pin pin) {
		this.id = id;
		this.pin = pin;
	}
	
	public Pin getPin() {
		return this.pin;
	}
	
	public int getId() {
		return this.id;
	}
	
	public static RpiPinSelect getById(int id) {
		for(RpiPinSelect p : RpiPinSelect.values())
			if(p.id == id)
				return p;
		return UNKNOWN;
	}
	
	public static RpiPinSelect getByPin(Pin pin) {
		for(RpiPinSelect p : RpiPinSelect.values())
			if(p.pin == pin)
				return p;
		return UNKNOWN;
	}
}
