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

package cz.allcomp.shs.database;

public class SqlCommands {
	public static String LOAD_EWCS = "SELECT * FROM `ewc_properties`;";
	public static String LOAD_SECURITY_SYSTEMS = "SELECT * FROM `security_systems`;";
	public static String LOAD_BEHAVIOUR_SIGNAL = "SELECT * FROM `ewc_behaviour_signal`;";
	public static String LOAD_BEHAVIOUR_SECURITY = "SELECT * FROM `ewc_behaviour_security`;";
	public static String LOAD_BEHAVIOUR_PLANNED = "SELECT * FROM `ewc_behaviour_planned`;";
	public static String LOG_EWCUNIT_STATE = "INSERT INTO `ewc_states` (`id`,`ewc_id`,`time`,`value`) "
			+ "VALUES (NULL, '%softwareId%', '%time%', '%value%');";
	public static String GET_EWC_LAST_STATE_VALUE = "SELECT * FROM `ewc_states` WHERE `ewc_id`=%id% ORDER BY `time` DESC LIMIT 1;";
	public static String THERMOSTAT_COUNT = "SELECT COUNT(*) FROM `ewc_thermostats` WHERE `ewc` LIKE '%ewc%';";
	public static String THERMOSTAT_INSERT = "INSERT INTO `ewc_thermostats` (`id`, `ewc`, `value`, `active`) VALUES (NULL, '%ewc%', '%value%', '0');";
	public static String THERMOSTAT_UPDATE = "UPDATE `ewc_thermostats` SET `value`='%value%' WHERE `ewc`=%ewc%;";
	public static String THERMOSTAT_GET = "SELECT * FROM `ewc_thermostats` WHERE `ewc`=%ewc% ORDER BY `ewc` DESC LIMIT 1;";
	public static String THERMOSTAT_SET_ACTIVE = "UPDATE `ewc_thermostats` SET `active`='%value%' WHERE `ewc`=%ewc%;";
	public static String LOAD_SIMULATOR_UNITS = "SELECT * FROM `ewc_states` WHERE `time` <= '%endTime%' AND `time` >= '%startTime%' ORDER BY `time` ASC LIMIT %startRow%,%numRows%;";
}
