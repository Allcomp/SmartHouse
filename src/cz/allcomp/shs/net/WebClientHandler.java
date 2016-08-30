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

package cz.allcomp.shs.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import cz.allcomp.shs.SmartServer;
import cz.allcomp.shs.logging.Messages;
import cz.allcomp.shs.net.clientcommands.CCallsecuritydisable;
import cz.allcomp.shs.net.clientcommands.CCallsecurityenable;
import cz.allcomp.shs.net.clientcommands.CCgetpulsing;
import cz.allcomp.shs.net.clientcommands.CCgetversion;
import cz.allcomp.shs.net.clientcommands.CCgetversionandname;
import cz.allcomp.shs.net.clientcommands.CCgetversionname;
import cz.allcomp.shs.net.clientcommands.CCmacro;
import cz.allcomp.shs.net.clientcommands.CCoutput;
import cz.allcomp.shs.net.clientcommands.CCoutputs;
import cz.allcomp.shs.net.clientcommands.CCrestartewcmanager;
import cz.allcomp.shs.net.clientcommands.CCsecurityactiveinputsget;
import cz.allcomp.shs.net.clientcommands.CCsecuritydisable;
import cz.allcomp.shs.net.clientcommands.CCsecurityenable;
import cz.allcomp.shs.net.clientcommands.CCsecurityinputsget;
import cz.allcomp.shs.net.clientcommands.CCsecurityremainingnotificationsget;
import cz.allcomp.shs.net.clientcommands.CCsecuritystateget;
import cz.allcomp.shs.net.clientcommands.CCsecuritystatesget;
import cz.allcomp.shs.net.clientcommands.CCshutdown;
import cz.allcomp.shs.net.clientcommands.CCsimulationgettimes;
import cz.allcomp.shs.net.clientcommands.CCsimulationstart;
import cz.allcomp.shs.net.clientcommands.CCsimulationstate;
import cz.allcomp.shs.net.clientcommands.CCsimulationstop;
import cz.allcomp.shs.net.clientcommands.CCstartpulsing;
import cz.allcomp.shs.net.clientcommands.CCstate;
import cz.allcomp.shs.net.clientcommands.CCstates;
import cz.allcomp.shs.net.clientcommands.CCstoppulsing;
import cz.allcomp.shs.net.clientcommands.CCtemp;
import cz.allcomp.shs.net.clientcommands.CCtemps;
import cz.allcomp.shs.net.clientcommands.CCthermactive;
import cz.allcomp.shs.net.clientcommands.CCthermget;
import cz.allcomp.shs.net.clientcommands.CCthermsactive;
import cz.allcomp.shs.net.clientcommands.CCthermsactiveget;
import cz.allcomp.shs.net.clientcommands.CCthermset;
import cz.allcomp.shs.net.clientcommands.CCthermsget;
import cz.allcomp.shs.net.clientcommands.CCthermsset;

public class WebClientHandler implements Runnable {

	private Socket client;
	private SmartServer server;
	
	public WebClientHandler(SmartServer server, Socket client) {
		this.client = client;
		this.server = server;
		this.initClientCommands();
	}
	
	private void initClientCommands() {
		ClientCommand.register(new CCstates("states"));
		ClientCommand.register(new CCstate("state"));
		ClientCommand.register(new CCtemps("temps"));
		ClientCommand.register(new CCtemp("temp"));
		ClientCommand.register(new CCoutput("output"));
		ClientCommand.register(new CCoutputs("outputs"));
		ClientCommand.register(new CCthermset("thermset"));
		ClientCommand.register(new CCthermget("thermget"));
		ClientCommand.register(new CCthermsget("thermsget"));
		ClientCommand.register(new CCthermsset("thermsset"));
		ClientCommand.register(new CCthermsactive("thermsactive"));
		ClientCommand.register(new CCthermactive("thermactive"));
		ClientCommand.register(new CCthermsactiveget("thermsactiveget"));
		ClientCommand.register(new CCshutdown("shutdown"));
		ClientCommand.register(new CCsecuritystateget("securitystateget"));
		ClientCommand.register(new CCsecurityenable("securityenable"));
		ClientCommand.register(new CCsecuritydisable("securitydisable"));
		ClientCommand.register(new CCgetpulsing("getpulsing"));
		ClientCommand.register(new CCstartpulsing("startpulsing"));
		ClientCommand.register(new CCstoppulsing("stoppulsing"));
		ClientCommand.register(new CCgetversion("getversion"));
		ClientCommand.register(new CCgetversionname("getversionname"));
		ClientCommand.register(new CCgetversionandname("getversionandname"));
		ClientCommand.register(new CCrestartewcmanager("restartewcmanager"));
		ClientCommand.register(new CCallsecuritydisable("allsecuritydisable"));
		ClientCommand.register(new CCallsecurityenable("allsecurityenable"));
		ClientCommand.register(new CCsecuritystatesget("securitystatesget"));
		ClientCommand.register(new CCsecurityinputsget("securityinputsget"));
		ClientCommand.register(new CCsecurityremainingnotificationsget("securityremainingnotificationsget"));
		ClientCommand.register(new CCsecurityactiveinputsget("securityactiveinputsget"));
		ClientCommand.register(new CCsimulationstart("simulationstart"));
		ClientCommand.register(new CCsimulationstop("simulationstop"));
		ClientCommand.register(new CCsimulationstate("simulationstate"));
		ClientCommand.register(new CCsimulationgettimes("simulationgettimes"));
		ClientCommand.register(new CCmacro("macro"));
	}
	
	@Override
	public void run() {
		try {
			BufferedReader inFromClient = new BufferedReader(
					new InputStreamReader(client.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(client.getOutputStream());
			List<String> data = this.getData(inFromClient);
			
			if(data.size() > 0) {
				if(Boolean.parseBoolean(server.getMainConfig().get("log_web_requests"))) {
					Messages.info("<WebServer> Received data:");
					for(String s : data) {
						Messages.info("<WebServer> -> " + s);
					}
				}
				
				String response = this.getResponse(data);
				if(Boolean.parseBoolean(server.getMainConfig().get("log_web_requests")))
					Messages.info("<WebServer> Sending response: " + response);
				if(!response.equals(""))
					this.sendResponse(response, outToClient);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				this.client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getResponse(List<String> data) {
		if(data.size() > 1) {
			String response = "";
			String methodData[] = data.get(0).split(":");
			String methodName = "";
			String methodId = "";
			if(methodData.length > 0)
				methodName = methodData[0];
			if(methodData.length > 1)
				methodId = methodData[1];
			String command = data.get(1);
			String[] args;
			
			if(data.size() > 2) {
				args = new String[data.size()-2];
				for(int i = 2; i < data.size(); i++)
					args[i-2] = data.get(i);
			} else {
				args = new String[]{};
			}
			
			/*if(command.equalsIgnoreCase("states")) {
				if(args.length == 0) {
					String states = "";
					for(EwcUnit ewc : this.server.getEwcManager().getEwcUnits())
						states += "-" + ewc.getSoftwareId() + ":" + ewc.getStateValue();
					if(states.length() > 0)
						states = states.substring(1);
					response = states;
				} else if(args.length == 1) {
					String states = "";
					String[] swIdsString = args[0].split("-");
					List<Integer> swIds = new ArrayList<>();
					for(String s : swIdsString) {
						try {
							int id = Integer.parseInt(s);
							swIds.add(id);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
					for(EwcUnit ewc : this.server.getEwcManager().getEwcUnits())
						if(swIds.contains(ewc.getSoftwareId()))
							states += "-" + ewc.getSoftwareId() + ":" + ewc.getStateValue();
					if(states.length() > 0)
						states = states.substring(1);
					response = states;
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("state")) {
				if(args.length == 1) {
					try {
						int swId = Integer.parseInt(args[0]);
						EwcUnit ewc = this.server.getEwcManager().getEwcUnitBySoftwareId(swId);
						if(ewc != null)
							response = ewc.getStateValue()+"";
						else
							response = "x";
					} catch (NumberFormatException e) {
						response = "x";
						e.printStackTrace();
					}
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("temps")) {
				if(args.length == 0) {
					String states = "";
					for(EwcUnit ewc : this.server.getEwcManager().getEwcUnits())
						if(ewc instanceof EwcUnitInput)
							states += "-" + ewc.getSoftwareId() + ":" + ((EwcUnitInput)ewc).getTemperatureCelsius();
					states = states.substring(1);
					response = states;
				} else if(args.length == 1) {
					String states = "";
					String[] swIdsString = args[0].split("-");
					List<Integer> swIds = new ArrayList<>();
					for(String s : swIdsString) {
						try {
							int id = Integer.parseInt(s);
							swIds.add(id);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
					for(EwcUnit ewc : this.server.getEwcManager().getEwcUnits())
						if(ewc instanceof EwcUnitInput)
							if(swIds.contains(ewc.getSoftwareId()))
								states += "-" + ewc.getSoftwareId() + ":" + ((EwcUnitInput)ewc).getTemperatureCelsius();
					if(states.length() > 0)
						states = states.substring(1);
					response = states;
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("temp")) {
				if(args.length == 1) {
					try {
						int swId = Integer.parseInt(args[0]);
						EwcUnit ewc = this.server.getEwcManager().getEwcUnitBySoftwareId(swId);
						if(ewc != null && ewc instanceof EwcUnitInput)
							response = ((EwcUnitInput)ewc).getTemperatureCelsius()+"";
						else
							response = "x";
					} catch (NumberFormatException e) {
						response = "x";
						e.printStackTrace();
					}
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("output")) {
				if(args.length == 2) {
					short id;
					try {
						id = Short.parseShort(args[0]);
						EwcUnit ewc = this.server.getEwcManager().getEwcUnitBySoftwareId(id);
						short value = Short.parseShort(args[1]);
						if(ewc != null)
							if(ewc instanceof EwcUnitOutput) {
								if(value==SwitchState.ON.toInt())
									ewc.setOvercontroled(true);
								else
									ewc.setOvercontroled(false);
								ewc.setStateValue(value);
								List<SignalBehaviour> bs = this.server.getEwcManager()
										.getBehaviourByOutputSoftwareId(ewc.getSoftwareId());
								for(SignalBehaviour b : bs)
									b.setLastRegulatorValue(value);
								response = "ok";
							} else 
								response = "x";
					} catch (NumberFormatException e) {
						response = "x";
					}
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("outputs")) {
				if(args.length == 1) {
					try {
						String[] pairs = args[0].split("-");
						for(String p : pairs) {
							String[] splittedPair = p.split(":");
							if(splittedPair.length == 2) {
								int id = Integer.parseInt(splittedPair[0]);
								EwcUnit ewc = this.server.getEwcManager().getEwcUnitBySoftwareId(id);
								if(ewc != null) {
									short val = Short.parseShort(splittedPair[1]);
									if(ewc instanceof EwcUnitOutput) {
										if(val==SwitchState.ON.toInt())
											ewc.setOvercontroled(true);
										else
											ewc.setOvercontroled(false);
										ewc.setStateValue(val);
										List<SignalBehaviour> bs = this.server.getEwcManager()
												.getBehaviourByOutputSoftwareId(ewc.getSoftwareId());
										for(SignalBehaviour b : bs)
											b.setLastRegulatorValue(val);
										response = "ok";
									} else
										response = "x";
								} else
									response = "x";
							} else
								response = "x";
						}
					} catch (NumberFormatException e) {
						response = "x";
					}
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("thermset")) {
				if(args.length == 2) {
					short id;
					try {
						id = Short.parseShort(args[0]);
						EwcUnit ewc = this.server.getEwcManager().getEwcUnitBySoftwareId(id);
						double value = Double.parseDouble(args[1]);
						if(ewc != null)
							if(ewc instanceof EwcUnitInput) {
								EwcUnitInput in = (EwcUnitInput)ewc;
								in.setTargetTemperatureCelsius(value);
								response = "ok";
							} else
								response = "x";
					} catch (NumberFormatException e) {
						response = "x";
					}
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("thermget")) {
				if(args.length == 1) {
					try {
						int swId = Integer.parseInt(args[0]);
						EwcUnit ewc = this.server.getEwcManager().getEwcUnitBySoftwareId(swId);
						if(ewc != null) {
							if(ewc instanceof EwcUnitInput)
								response = ((EwcUnitInput)ewc).getTargetTemperatureCelsius() + "";
							else
								response = "x";
						} else
							response = "x";
					} catch (NumberFormatException e) {
						response = "x";
						e.printStackTrace();
					}
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("thermsget")) {
				if(args.length == 0) {
					String states = "";
					for(EwcUnit ewc : this.server.getEwcManager().getEwcUnits())
						if(ewc instanceof EwcUnitInput)
							states += "-" + ewc.getSoftwareId() + ":" + ((EwcUnitInput)ewc).getTargetTemperatureCelsius();
					states = states.substring(1);
					response = states;
				} else if(args.length == 1) {
					String states = "";
					String[] swIdsString = args[0].split("-");
					List<Integer> swIds = new ArrayList<>();
					for(String s : swIdsString) {
						try {
							int id = Integer.parseInt(s);
							swIds.add(id);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
					for(EwcUnit ewc : this.server.getEwcManager().getEwcUnits())
						if(ewc instanceof EwcUnitInput)
							if(swIds.contains(ewc.getSoftwareId()))
								states += "-" + ewc.getSoftwareId() + ":" + ((EwcUnitInput)ewc).getTargetTemperatureCelsius();
					if(states.length() > 0)
						states = states.substring(1);
					response = states;
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("thermsset")) {
				if(args.length == 1) {
					try {
						String[] pairs = args[0].split("-");
						for(String p : pairs) {
							String[] splittedPair = p.split(":");
							if(splittedPair.length == 2) {
								int id = Integer.parseInt(splittedPair[0]);
								EwcUnit ewc = this.server.getEwcManager().getEwcUnitBySoftwareId(id);
								if(ewc != null) {
									double val = Double.parseDouble(splittedPair[1]);
									if(ewc instanceof EwcUnitInput) {
										((EwcUnitInput)ewc).setTargetTemperatureCelsius(val);
										response = "ok";
									} else
										response = "x";
								} else
									response = "x";
							} else
								response = "x";
						}
					} catch (NumberFormatException e) {
						response = "x";
					}
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("thermsactive")) {
				if(args.length == 1) {
					try {
						String[] pairs = args[0].split("-");
						for(String p : pairs) {
							String[] splittedPair = p.split(":");
							if(splittedPair.length == 2) {
								int id = Integer.parseInt(splittedPair[0]);
								EwcUnit ewc = this.server.getEwcManager().getEwcUnitBySoftwareId(id);
								if(ewc != null) {
									int val = Integer.parseInt(splittedPair[1]);
									if(ewc instanceof EwcUnitInput) {
										if(val == 1) {
											((EwcUnitInput)ewc).activateThermostat();
											response = "ok";
										} else if(val == 0) {
											((EwcUnitInput)ewc).deactivateThermostat();
											response = "ok";
										} else
											response = "x";
									} else
										response = "x";
								} else
									response = "x";
							} else
								response = "x";
						}
					} catch (NumberFormatException e) {
						response = "x";
					}
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("thermactive")) {
				if(args.length == 2) {
					short id;
					try {
						id = Short.parseShort(args[0]);
						EwcUnit ewc = this.server.getEwcManager().getEwcUnitBySoftwareId(id);
						int value = Integer.parseInt(args[1]);
						if(ewc != null)
							if(ewc instanceof EwcUnitInput) {
								EwcUnitInput in = (EwcUnitInput)ewc;
								if(value == 1) {
									in.activateThermostat();
									response = "ok"; 
								} else if(value == 0) {
									in.deactivateThermostat();
									response = "ok"; 
								} else
									response = "x";
							} else
								response = "x";
					} catch (NumberFormatException e) {
						response = "x";
					}
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("thermsactiveget")) {
				if(args.length == 0) {
					String states = "";
					for(EwcUnit ewc : this.server.getEwcManager().getEwcUnits())
						if(ewc instanceof EwcUnitInput)
							states += "-" + ewc.getSoftwareId() + ":" + (((EwcUnitInput)ewc).isThermostatActive() ? "1" : "0");
					if(states.length() > 0)
						states = states.substring(1);
					response = states;
				} else if(args.length == 1) {
					String states = "";
					String[] swIdsString = args[0].split("-");
					List<Integer> swIds = new ArrayList<>();
					for(String s : swIdsString) {
						try {
							int id = Integer.parseInt(s);
							swIds.add(id);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
					for(EwcUnit ewc : this.server.getEwcManager().getEwcUnits())
						if(swIds.contains(ewc.getSoftwareId()))
							if(ewc instanceof EwcUnitInput)
								states += "-" + ewc.getSoftwareId() + ":" + (((EwcUnitInput)ewc).isThermostatActive() ? "1" : "0");
					if(states.length() > 0)
						states = states.substring(1);
					response = states;
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("shutdown")) {
				this.server.signalStop();
				response = "ok";
			} else if(command.equalsIgnoreCase("securitystateget")) {
				
				if(args.length == 0)
					response = this.server.getSecuritySystem().isRunning() ? "1" : "0";
				else
					response = "x";
				
			} else if(command.equalsIgnoreCase("securityenable")) {
				
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase(this.server.getMainConfig().get("security_pin"))) {
						this.server.startSecuritySystem();
						response = "ok";
					} else
						response = "x";
				} else
					response = "x";
				
			} else if(command.equalsIgnoreCase("securitydisable")) {
				
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase(this.server.getMainConfig().get("security_pin"))) {
						this.server.stopSecuritySystem();
						response = "ok";
					} else
						response = "x";
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("getpulsing")) {
				if(args.length == 0) {
					String states = "";
					PulseMaker pulseMaker = this.server.getEwcManager().getPulseMaker();
					for(EwcUnit ewc : this.server.getEwcManager().getEwcUnits())
						states += "-" + ewc.getSoftwareId() + ":" + (pulseMaker.isPulsing(ewc.getSoftwareId()) ? "1" : "0");
					if(states.length() > 0)
						states = states.substring(1);
					response = states;
				} else if(args.length == 1) {
					String states = "";
					String[] swIdsString = args[0].split("-");
					List<Integer> swIds = new ArrayList<>();
					for(String s : swIdsString) {
						try {
							int id = Integer.parseInt(s);
							swIds.add(id);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
					PulseMaker pulseMaker = this.server.getEwcManager().getPulseMaker();
					for(EwcUnit ewc : this.server.getEwcManager().getEwcUnits())
						if(swIds.contains(ewc.getSoftwareId()))
							states += "-" + ewc.getSoftwareId() + ":" + (pulseMaker.isPulsing(ewc.getSoftwareId()) ? "1" : "0");
					if(states.length() > 0)
						states = states.substring(1);
					response = states;
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("startpulsing")) {
				if(args.length == 3) {
					short id;
					try {
						id = Short.parseShort(args[0]);
						int frequency = Integer.parseInt(args[1]);
						float dutyCycle = Float.parseFloat(args[2]);
						this.server.getEwcManager().getPulseMaker().startPulsing(id, frequency, dutyCycle);
						response = "ok";
					} catch (NumberFormatException e) {
						response = "x";
					}
				} else
					response = "x";
			} else if(command.equalsIgnoreCase("stoppulsing")) {
				if(args.length == 1) {
					short id;
					try {
						id = Short.parseShort(args[0]);
						this.server.getEwcManager().getPulseMaker().stopPulsing(id);
						response = "ok";
					} catch (NumberFormatException e) {
						response = "x";
					}
				} else
					response = "x";
			}*/
			
			try {
				ClientCommand cc = ClientCommand.get(command).copy();
				cc.execute(this.server, args);
				
				if(methodName.length() != 0) {
					String idString = methodId.equals("") ? "" : ", '" + methodId + "'";
					response = methodName + "('" + cc.getResponse() + "'" + idString + ")";
				} else {
					response = cc.getResponse();
				}
			} catch (Exception e) {
				Messages.warning("WebClientHandler is too busy!");
			}
			return response;
		}
		return "";
	}

	public void sendResponse(String response, DataOutputStream outToClient) throws IOException {
		outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
		/*if (fileName.endsWith(".jpg"))
			outToClient.writeBytes ("Content-Type: image/jpeg\r\n");
		if (fileName.endsWith(".gif"))
			outToClient.writeBytes ("Content-Type: image/gif\r\n");*/
	 	outToClient.writeBytes("Content-Length: " + response.getBytes().length + "\r\n");
	 	outToClient.writeBytes("\r\n");
	 	outToClient.write(response.getBytes(), 0, response.getBytes().length);
	}
	
	public List<String> getData(BufferedReader inFromClient) throws IOException {
		String requestMessageLine = inFromClient.readLine();
		if(Boolean.parseBoolean(server.getMainConfig().get("log_web_requests")))
			Messages.info("<WebServer> Request: " + requestMessageLine);
		
		if(requestMessageLine == null)
			return new ArrayList<String>();
		
		StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);
		
		if(tokenizedLine != null) {
			if (tokenizedLine.nextToken().equals("GET")) {
				String requestDataString = tokenizedLine.nextToken();
				if(requestDataString.startsWith("/")) {
					requestDataString = requestDataString.substring(1);
					requestDataString = URLDecoder.decode(requestDataString, "UTF-8");
					List<String> data = Arrays.asList(requestDataString.split("/"));
					return data;
				}
			}
		}
		return new ArrayList<String>();
	}

}
