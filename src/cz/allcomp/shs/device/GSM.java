package cz.allcomp.shs.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import cz.allcomp.shs.logging.Messages;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class GSM {
	
    private final String port;
    private InputStream inStream;
    private OutputStream outStream;
    private SerialPort serialPort;
    private final RpiPinSelect pinId;
    private final short id;
    private GpioPinDigitalOutput pin;
    private boolean enabled;
    
    public GSM(String port, int speed, short id, RpiPinSelect pin) throws NoSuchPortException, PortInUseException, 
    		UnsupportedCommOperationException, IOException {
    	System.setProperty("gnu.io.rxtx.SerialPorts", port);
    	this.port = port;
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(port);
        this.serialPort = (SerialPort)portId.open("GSM", 5000);
        this.serialPort.setSerialPortParams(speed, SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        this.serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        this.inStream = serialPort.getInputStream();
        this.outStream = serialPort.getOutputStream();
        this.pinId = pin;
        this.id = id;
        this.enabled = false;
    }
    
    public String setCMGF(int val) throws IOException, InterruptedException {
    	if(val > 1)
    		val = 1;
    	if(val < 0)
    		val = 0;
    	this.send("AT+CMGF="+val);
		Thread.sleep(100);
		String ret = this.receive();
		Thread.sleep(100);
		return ret;
    }
    
    public RpiPinSelect getPinId() {
    	return this.pinId;
    }
    
    public short getId() {
    	return this.id;
    }
    
    public boolean isEnabled() {
    	return this.enabled;
    }
    
    public String sendMessage(String phoneNumber, String message) throws IOException, InterruptedException {
    	String sendString = "\""+phoneNumber+"\"\r";
		this.send("AT+CMGS="+sendString);
		Thread.sleep(100);
		this.send(message+"\032");
		Thread.sleep(500);
		String ret = this.receive();
		Thread.sleep(100);
		return ret;
    }
    
    public String getPort() {
    	return this.port;
    }
    
    public void send(String s) throws IOException {
    	if(!s.endsWith((char)13+""))
    		s += (char)13+"";
        byte[] bytes = s.getBytes();
        this.outStream.write(bytes, 0, bytes.length);
    }
    
    public String receive() throws IOException {
    	byte[] answer = new byte[300];
    	String res = "";
    	int answerLength = inStream.read(answer);
    	for(int i = 0; i < answerLength; i++)
    		res += (char)answer[i];
    	String[] strngs = res.split("\r\n");
    	res = "";
    	for(String s : strngs)
    		res+=s+"\n";
    	return res;
    }
    
    public void close() throws IOException {
    	this.inStream.close();
    	this.outStream.close();
    	this.serialPort.close();
    }
    
    public void connectPin(GpioController gpio) {
    	if(this.pinId.getPin() == null) {
    		Messages.error("<GSM "+this.getId()+"> Could not connect to Raspi Pin!");
    		return;
    	}
    	pin = gpio.provisionDigitalOutputPin(this.pinId.getPin(), "GSM"+this.id, PinState.HIGH);
    	pin.setShutdownOptions(true, PinState.HIGH);
    	Messages.info("<GSM "+this.getId()+"> Connected to Raspi Pin number "+this.getPinId().getId()+"!");
    }
    
    public void enable() {
    	if(this.enabled)
    		return;
    	this.enabled = true;
    	this.pin.low();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Messages.error(Messages.getStackTrace(e));
		}
    	this.pin.high();
    }
    
    public void disable() {
    	if(!this.enabled)
    		return;
    	this.enabled = false;
    	this.pin.low();
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Messages.error(Messages.getStackTrace(e));
		}
    	this.pin.high();
    }
}
