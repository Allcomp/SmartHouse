/**
 * Copyright (c) 2015, Václav Vilímek
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	- Redistributions of source code must retain the above copyright notice, this list 
 * 	  of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright , this list 
 *    of conditions and the following disclaimer in the documentation and/or other materials 
 *    provided with the distribution.
 *  - Neither the name of the ALLCOMP a.s. nor the of its contributors may be used to endorse 
 *    or promote products from this software without specific prior written permission.
 *    
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL VÁCLAV VILÍMEK BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package cz.allcomp.shs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import com.sun.corba.se.spi.logging.CORBALogDomains;

import cz.allcomp.shs.allcomplib.common.Utility;
import cz.allcomp.shs.allcomplib.transducers.EwcAccess;
import cz.allcomp.shs.allcomplib.transducers.EwcAccessHelper;
import cz.allcomp.shs.cfg.Configuration;
import cz.allcomp.shs.database.SqlCommands;
import cz.allcomp.shs.database.StableMysqlConnection;
import cz.allcomp.shs.ewc.EwcHardwareType;
import cz.allcomp.shs.ewc.EwcManager;
import cz.allcomp.shs.ewc.SecuritySystem;
import cz.allcomp.shs.ewc.WorkingSimulator;
import cz.allcomp.shs.files.FileHandler;
import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.net.NetController;
import cz.allcomp.shs.net.NetServer;
import cz.allcomp.shs.net.WebServer;
import cz.allcomp.shs.net.commands.CommandHandler;
import cz.allcomp.shs.net.commands.CommandManager;
import cz.allcomp.shs.net.commands.CommandOutput;

public class SmartServer extends Thread {

	private Configuration mainConfig;
	private Configuration databaseConfig;
	private Configuration hardwareConfig;
	
	private NetServer netServer;
	private EwcManager ewcManager;
	private StableMysqlConnection database;
	private EwcAccess ewcAccess;
	private CommandManager commandManager;
	private NetController netController;
	private WebServer webServer;
	private HashMap<Integer, SecuritySystem> securitySystems;
	private WorkingSimulator workingSimulator;
	
	private String[] runParams;
	
	private boolean running;
	private boolean databaseActive;
	
	private Set<Integer> activeEwcs;

	public static final String VERSION = "3.2.2";
	
	public static final String[] VERSION_NAMES = {"Eros", "Thaumas", "Boreas"};
	public static final String VERSION_NAME = VERSION_NAMES[2];
	
	public SmartServer(final String[] runParams) {
		this.runParams = runParams;
		this.running = false;
		this.commandManager = new CommandManager(this);
		this.databaseActive = false;
		this.activeEwcs = new HashSet<>();
		this.securitySystems = new HashMap<Integer, SecuritySystem>();
		this.workingSimulator = new WorkingSimulator(this);
	}
	
	private void init() {
		this.loadConfig();
		this.setupMessages();
		this.registerCommands();
		
		if(this.databaseActive) {
			Messages.info("SmartServer version: " + VERSION_NAME + " " + VERSION + "...");
			Messages.info("Establishing connection to MySQL database");
			Messages.info(">> host: " + this.databaseConfig.get("host"));
			Messages.info(">> user: " + this.databaseConfig.get("user"));
			Messages.info(">> password: " + this.databaseConfig.get("password"));
			Messages.info(">> name: " + this.databaseConfig.get("name"));
			Messages.info(">> port: " + Integer.parseInt(this.databaseConfig.get("port")));
			this.database = new StableMysqlConnection(
				this.databaseConfig.get("host"), 
				this.databaseConfig.get("user"), 
				this.databaseConfig.get("password"), 
				this.databaseConfig.get("name"), 
				Integer.parseInt(this.databaseConfig.get("port"))
			);
		}
		
		this.ewcManager = new EwcManager(this);
		
		this.loadSecuritySystems();
		
		this.netServer = new NetServer(this, Integer.parseInt(this.mainConfig.get("netserver_port"))); //Integer.parseInt(this.mainConfig.get("netserver_port"))
		this.netController = new NetController(this.netServer);
		
		this.webServer = new WebServer(this, Integer.parseInt(this.mainConfig.get("webserver_port")));
	}
	
	private boolean shouldStop;
	
	private void loadSecuritySystems() {
		long securityActivationDelay = Long.parseLong(this.mainConfig.get("security_activation_delay"));
		//int notificationNumberToAlarm = Integer.parseInt(this.mainConfig.get("notification_number_to_alarm"));
		
		ResultSet rs = this.database.executeQuery(SqlCommands.LOAD_SECURITY_SYSTEMS);
		try {
			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int notification_tolerance = rs.getInt("notification_tolerance");
				this.securitySystems.put(id, new SecuritySystem(id, name, this.ewcManager, securityActivationDelay, notification_tolerance));
			}
		} catch (SQLException e) {
			Messages.error("Failed to read ResultSet.");
			Messages.error(Messages.getStackTrace(e));
		}
	}
	
	public void setDatabaseActive(boolean val) {
		this.databaseActive = val;
	}
	
	public boolean isDatabaseActive() {
		return this.databaseActive;
	}
	
	public Configuration getMainConfig() {
		return this.mainConfig;
	}
	
	public Configuration getDatabaseConfig() {
		return this.databaseConfig;
	}
	
	public Configuration getHardwareConfig() {
		return this.hardwareConfig;
	}
	
	public NetServer getNetServer() {
		return this.netServer;
	}
	
	public EwcManager getEwcManager() {
		return this.ewcManager;
	}
	
	public StableMysqlConnection getDatabase() {
		return this.database;
	}
	
	public EwcAccess getEwcAccess() {
		return this.ewcAccess;
	}
	
	public CommandManager getCommandManager() {
		return this.commandManager;
	}
	
	public NetController getNetController() {
		return this.netController;
	}
	
	public WebServer getWebServer() {
		return this.webServer;
	}
	
	public SecuritySystem getSecuritySystem(int id) {
		return this.securitySystems.get(id);
	}
	
	public HashMap<Integer, SecuritySystem> getSecuritySystems() {
		return this.securitySystems;
	}
	
	public WorkingSimulator getWorkingSimulator() {
		return this.workingSimulator;
	}
	
	public void startSecuritySystem(int id) {
		if(this.securitySystems.get(id) == null)
			return;
		if(!this.securitySystems.get(id).isRunning())
			new Thread(this.securitySystems.get(id)).start();
	}
	
	public void stopSecuritySystem(int id) {
		if(this.securitySystems.get(id) == null)
			return;
		if(this.securitySystems.get(id).isRunning())
			this.securitySystems.get(id).signalStop();
	}
	
	public void startSecuritySystems() {
		for(int sid : this.securitySystems.keySet())
			this.startSecuritySystem(sid);
	}
	
	public void stopSecuritySystems() {
		for(int sid : this.securitySystems.keySet())
			this.stopSecuritySystem(sid);
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	@Override
	public void run() {
		this.init();
		
		this.running = true;
		this.shouldStop = false;
		
		this.initEwcAccess();
		
		this.ewcManager.start();
		this.netServer.start();
		this.netController.start();
		this.webServer.start();
		
		while(true) {
			if(this.shouldStop)
				break;
			else
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					Messages.warning("Could not sleep a thread.");
					Messages.warning(Messages.getStackTrace(e));
				}
		}
		
		this.webServer.signalStop();
		this.netController.signalStop();
		this.netServer.signalStop();
		this.ewcManager.signalStop();
		
		this.running = false;
	}
	
	private void setupMessages() {
		try {
			Messages.SHOW_ERRORS = Boolean.parseBoolean(this.mainConfig.get("show_errors"));
		} catch (Exception e) {}
		try {
			Messages.SHOW_WARNINGS = Boolean.parseBoolean(this.mainConfig.get("show_warnings"));
		} catch (Exception e) {}
		try {
			Messages.SHOW_INFO = Boolean.parseBoolean(this.mainConfig.get("show_info"));
		} catch (Exception e) {}
		try {
			Messages.LOG_ERRORS = Boolean.parseBoolean(this.mainConfig.get("log_errors"));
		} catch (Exception e) {}
		try {
			Messages.LOG_WARNINGS = Boolean.parseBoolean(this.mainConfig.get("log_warnings"));
		} catch (Exception e) {}
		try {
			Messages.LOG_INFO = Boolean.parseBoolean(this.mainConfig.get("log_info"));
		} catch (Exception e) {}
	}

	private void registerCommands() {
		this.registerCommand("output", new CommandOutput());
	}
	
	public void registerCommand(String cmd, CommandHandler handler) {
		this.commandManager.registerCommand(cmd, handler);
	}

	private static final String IOR2 = "IOR:000000000000001e49444c3a7472616e736475636572732f4577634163636573733a312e30000000000000010000000000000082000102000000000a3132372e302e302e3100041c00000031afabcb0000000020e7cacce500000001000000000000000100000008526f6f74504f410000000008000000010000000014000000000000020000000100000020000000000001000100000002050100010001002000010109000000010001010000000026000000020002";
    private static final String IOR1 = "IOR:000000000000001e49444c3a7472616e736475636572732f4577634163636573733a312e300000000000000100000000000000b8000102000000000a3132372e302e302e3100041c00000020667265650000000800000000000000000000000c00000008526f6f74504f410000000001000000010000007400000000000100010000000c000100010001000200010003000100040001000500010006000100070001000800010009000100200001010905010001000101090000000c000100010001000200010003000100040001000500010006000100070001000800010009000100200001010905010001";
	
    /*public static final byte[] DEFAULT_EWC = new byte[]{
			 0xC,0xC,0xC,0xC	// 4 x DIN
			,0xC,0xC,0xC,0xC	// 4 x DIN //TCC = 0xD
			,0xC,0xC,0xC,0xC	// 4 x DIN //ECC = 0x10
			,0x11,0x11,0x11,0x11	// 4 x EPP
			,0x12,0x12,0x12,0x12	// 4 x AFRICA
			,0x13,0x13,0x13,0x13	// 4 x UDM
			
			,-1,-1,-1,-1	// 4 x N.C.
			,-1,-1,-1,-1	// 4 x N.C.
			,-1,-1,-1,-1	// 4 x N.C.
			,-1,-1,-1,-1	// 4 x N.C.
			,-1,-1,-1,-1	// 4 x N.C.
			,-1,-1,-1,-1	// 4 x N.C.
			,-1,-1,-1,-1	// 4 x N.C.
			,-1,-1,-1,-1	// 4 x N.C.
			,-1,-1,-1	// 35 x N.C.

			,0x4					// RFIDREAD
			,0x5					// SWBOARD
			,0x6					// VALVES
			,-1						// N.C.
			,0x3					// UPS
	};
	*/
    
	private void initEwcAccess() {
		Properties props = new Properties();
        props.put("org.omg.CORBA.ORBInitialHost", this.mainConfig.get("corba_ip")); //127.0.0.1
        props.put("org.omg.CORBA.ORBInitialPort", this.mainConfig.get("corba_port")); //1050
        ORB orb = ORB.init(this.runParams, props);
        ((com.sun.corba.se.spi.orb.ORB) orb).getLogger(CORBALogDomains.RPC_TRANSPORT).setLevel(Level.OFF);
        try {
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            this.ewcAccess = EwcAccessHelper.narrow(ncRef.resolve_str("EwcAccess"));
            //Messages.info("Obtained a handle on server object: " + ewcAccess);
		} catch (Exception e) {
			if (e instanceof org.omg.CORBA.COMM_FAILURE) {
                Messages.error("No name service found.");
                Messages.error(Messages.getStackTrace(e));
            } else {
            	Messages.error("Cannot resolve \"EwcAccess\".");
                Messages.error(Messages.getStackTrace(e));
            }
			Messages.info("Trying backup IOR (127.0.0.1:1052).");
			this.ewcAccess = EwcAccessHelper.narrow(orb.string_to_object(IOR1));
		}
		int swVer = 0;
		try {
		    swVer = this.ewcAccess.gSw((short) 0);
		} catch (Exception e) {
			Messages.info("Trying backup IOR (127.0.0.1:1052).");
		    this.ewcAccess = EwcAccessHelper.narrow(orb.string_to_object(IOR2));
		    try {
		    	swVer = this.ewcAccess.gSw((short) 0);
		    } catch (Exception e2) {
		    	Messages.error("Unable to connect to EwcAccess server object. \nExiting.");
				e.printStackTrace();
				System.exit(1);
		    }
		}
		Messages.info("Found EwcAccess server version " + Utility.swver2string(swVer));

        try {
            // Get reference to rootPOA and activate the POAManager
            /*POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();
            //Use the POAManager to get the reference to the callback object from the servant:
            // Get object reference from the servant
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(this.ewcManager);
            // Narrow the reference
            Notice callback = NoticeHelper.narrow(ref);*/

            Set<Integer> ewcss = this.getActiveEwcs();
            this.ewcManager.setActiveEwcs(ewcss);
            /*if(this.ewcOutputPatch == null)
            	this.ewcOutputPatch = new EwcOutputPatch(this, ewcss);
            else {
            	this.ewcOutputPatch.signalStop();
            	while(this.ewcOutputPatch.isRunning()) {}
            	this.ewcOutputPatch = null;
            	this.ewcOutputPatch = new EwcOutputPatch(this, ewcss);
            }*/
            
            byte[] defaultEwcFromConfig = new byte[128];
            for(int i = 1; i <= 64; i++) {
            	String s = this.hardwareConfig.get(String.valueOf(i));
            	if(s != null)
            		defaultEwcFromConfig[i-1] = EwcHardwareType.getByString(s).getId();
            	else
            		defaultEwcFromConfig[i-1] = EwcHardwareType.NC.getId();
            }
            
            byte[] types = new byte[defaultEwcFromConfig.length]; //=new byte[SmartServer.DEFAULT_EWC.length];
            for(int i=0; i<types.length; i++)
            	types[i]=-1;
            for (Integer ewc : ewcss) {
            	types[ewc-1] = defaultEwcFromConfig[ewc - 1]; //=DEFAULT_EWC[ewc-1];
            }
            
            ewcAccess.setTypes((byte)0, types);
            
            /*for (Integer ewc : ewcss) {
                ewcAccess.registerNotice((short) (int) ewc, callback);
            }*/
            
            Messages.info("Registered notify implementation for EWCs.");
            
        } catch (Exception e) {
            Messages.error("Unable to register notify implementation object. \nExiting.");
            Messages.error(Messages.getStackTrace(e));
        }
	}
	
	public void setActiveEwcs(Set<Integer> activeEwcs) {
		this.activeEwcs = activeEwcs;
	}
	
	/*public Set<Integer> getActiveEwcs() {
		if(this.databaseActive) {
	        Set<Integer> addresses = new HashSet<>();
	        
	        StableMysqlConnection database = this.getDatabase();
			ResultSet rs = database.executeQuery(SqlCommands.LOAD_EWCS);
			try {
				while(rs.next()) {
					String hwAddr = rs.getString("hardware_address");
					if(hwAddr.startsWith("EWC"))
						addresses.add(numberOfEwc(hwAddr.charAt(3), hwAddr.charAt(4)));
				}
			} catch (SQLException e) {
				Messages.error("Failed to read ResultSet.");
				Messages.error(Messages.getStackTrace(e));
			}
			
			return addresses;
		} else {
			return this.activeEwcs;
		}
	}*/
	
	/*private int numberOfEwc(char e, char i) {
        if (Character.getNumericValue(e) == 0) {
            return Character.getNumericValue(i);

        } else {
            return (Character.getNumericValue(i) + (Character.getNumericValue(e) * 10));
        }

    }*/
	
	public Set<Integer> getActiveEwcs() {
		if(this.databaseActive) {
	        Set<Integer> addresses = new HashSet<>();
	        
	        List<String> lines = FileHandler.readFileToList(this.hardwareConfig.getFile());
	    	
	    	for(String line : lines) {
	    		String[] lineParts = line.split(Configuration.INDEX_VALUE_SEPARATOR);
	    		if(lineParts.length >= 2) {
	    			try {
						int ewcNum = Integer.parseInt(lineParts[0]);
						addresses.add(ewcNum);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
	    		}
	    	}
			
			return addresses;
		} else {
			return this.activeEwcs;
		}
	}

	public void signalStop() {
		this.shouldStop = true;
	}
	
	public void forceStop() {
		this.netServer.signalStop();
		this.ewcManager.signalStop();
		this.saveConfig();
	}
	
	private void loadConfig() {
		this.mainConfig = new Configuration("config/main.cfg");
		this.databaseConfig = new Configuration("config/database.cfg");
		this.hardwareConfig = new Configuration("config/hardware.cfg");
	}
	
	private void saveConfig() {
		this.mainConfig.save();
		this.databaseConfig.save();
		this.hardwareConfig.save();
	}
}
