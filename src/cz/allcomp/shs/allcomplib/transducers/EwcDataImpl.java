/*
 * EwcDataImpl
 * 
 * Created on 4.7.2014, 17:51:50
 * 
 * Copyright 2014-2015 Petr Mikše. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cz.allcomp.shs.allcomplib.transducers;

/**
 * implements org.omg.CORBA.portable.StreamableValue is needed to workaround
 * a java CORBA implementation error; unableLocateValueHelper exception is 
 * thrown otherwise
 *
 * @author petr
 */
class EwcDataImpl extends EwcData implements org.omg.CORBA.portable.StreamableValue {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EwcDataImpl() {
    }

    @Override
    public int getEwcTypeSign() {
	return ewcTypeSign;
    }

    @Override
    public int getCommState() {
	if (status == FAKE_EWC){
	    //combination indicating FakeEWC (newer occurs otherwise)
	    return EwcStateSign.UNUSED;
	}
	if ((status & ADDR_VALID) == 0) {
	    return EwcStateSign.OFFLINE;
	}
	if ((status & SW_VALID) == 0) {
	    return EwcStateSign.NO_SW;
	}
	if ((status & HW_VALID) == 0) {
	    return EwcStateSign.NO_HW;
	}
	if ((status & HALL_VALID) == 0) {
	    return EwcStateSign.ONLINE;
	}
	if ((status & OUTS_VALID) == 0) {
	    return EwcStateSign.DROPOUT;
	}
	if ((status & BUS_OK) == 0) {
	    return EwcStateSign.WORKING;
	}
	return EwcStateSign.CORRECT;
    }

    @Override
    public int getStatus() {
	return status;
    }

    @Override
    public int getHwVersion() {
	return hwVersion;
    }

    @Override
    public int getSwVersion() {
	return swVersion;
    }

    @Override
    public long getLastTime() {
	return lastTime;
    }

    @Override
    public int getHallValue() {
	return hallIn[0];
    }

    @Override
    public boolean getPosIn(int i) {
	return (hallIn[i >>> 5] & (1 << (i & 31))) != 0;
    }

    @Override
    public short getPosInLength() {
	return posInLenght;
    }

    @Override
    public boolean getBack(int i) {
	int idx = i + backOutIndex;
	return (hallIn[idx >>> 5] & (1 << (idx & 31))) != 0;
    }

    @Override
    public short getAnalogOut(int i) {
	return analogOut[i];
    }

    @Override
    public int getAnalogOutLength() {
	return analogOut.length;
    }

    @Override
    public byte getSwitchOut(int i) {
	return switchOut[i];
    }

    @Override
    public int getSwitchOutLength() {
	return switchOut.length;
    }

    @Override
    public short getAnalogIn(int i) {
	return analogIn[i];
    }

    @Override
    public int getAnalogInLength() {
	return analogIn.length;
    }

    @Override
    public int getTimerIn(int i) {
	return timerIn[i];
    }

    @Override
    public int getTimerInLength() {
	return timerIn.length;
    }

    @Override
    public long getLongIn(int i) {
	return longIn[i];
    }

    @Override
    public int getLongInLength() {
	return longIn.length;
    }
}
