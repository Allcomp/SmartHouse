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

package cz.allcomp.shs.behaviour;

import java.util.HashMap;

public class BehaviourMetadata {
	
	private HashMap<String,String> dataMap;
	private String data;
	
	public BehaviourMetadata(String data) {
		this.data = data;
		dataMap = new HashMap<>();
		String[] keyVals = data.split(";");
		for(String s : keyVals) {
			String[] splitKeyVal = s.split("=");
			if(splitKeyVal.length == 2)
				dataMap.put(splitKeyVal[0], splitKeyVal[1]);
		}
	}
	
	@Override
	public String toString() {
		return this.data;
	}
	
	public String getValue(String key) {
		if(dataMap.containsKey(key))
			return dataMap.get(key);
		else
			return null;
	}
	
	public int getInt(String key, int defaultValue) {
		if(dataMap.containsKey(key))
			try {
				return Integer.parseInt(dataMap.get(key));
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		else
			return defaultValue;
	}
	
	public short getShort(String key, short defaultValue) {
		if(dataMap.containsKey(key))
			try {
				return Short.parseShort(dataMap.get(key));
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		else
			return defaultValue;
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		if(dataMap.containsKey(key))
			try {
				return Boolean.parseBoolean(dataMap.get(key));
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		else
			return defaultValue;
	}
	
	public long getLong(String key, long defaultValue) {
		if(dataMap.containsKey(key))
			try {
				return Long.parseLong(dataMap.get(key));
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		else
			return defaultValue;
	}
	
	public float getFloat(String key, float defaultValue) {
		if(dataMap.containsKey(key))
			try {
				return Float.parseFloat(dataMap.get(key));
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		else
			return defaultValue;
	}
	
	public double getDouble(String key, double defaultValue) {
		if(dataMap.containsKey(key))
			try {
				return Double.parseDouble(dataMap.get(key));
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		else
			return defaultValue;
	}
	
	public String getString(String key, String defaultValue) {
		if(dataMap.containsKey(key))
			return dataMap.get(key);
		else
			return defaultValue;
	}
}
