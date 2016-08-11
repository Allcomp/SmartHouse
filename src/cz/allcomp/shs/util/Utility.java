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

package cz.allcomp.shs.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import cz.allcomp.shs.logging.Messages;
import scireum.parsii.eval.Parser;
import scireum.parsii.tokenizer.ParseException;

public class Utility {
	public static boolean isInt(String s)
	{
	    for (char c : s.toCharArray())
	        if (!Character.isDigit(c) && c != '.')
	            return false;
	    return true;
	}
	
	public static boolean javascriptLogicalExpression(String expression) {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("JavaScript");
		try {
			Object resString = se.eval(expression);
			return Boolean.parseBoolean(resString.toString());
		} catch (Exception e) {
			Messages.warning(Messages.getStackTrace(e));
			return false;
		}
	}
	
	public static boolean logicalExpression(String expression) {
		boolean res;
		try {
			res = Parser.parse(expression.replace("==", "=")).evaluate() == 1;
		} catch (ParseException e) {
			Messages.error(Messages.getStackTrace(e));
			return false;
		}
		return res;
	}
}
