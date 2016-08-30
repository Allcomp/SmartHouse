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

import cz.allcomp.shs.ewc.EwcManager;

public abstract class Behaviour implements Runnable {
	
	private static int GLOBAL_ID_COUNTER = 0;
	
	protected final short outputEWC;
	protected final BehaviourMetadata metadata;
	protected final BehaviourConditions turnOnConditions;
	protected final BehaviourConditions turnOffConditions;
	
	protected boolean executed;
	
	protected EwcManager ewcManager;
	
	private int globalId;

	public Behaviour(EwcManager ewcManager, short outputEWC, BehaviourMetadata metadata, BehaviourConditions turnOnConditions, 
			BehaviourConditions turnOffConditions) {
		this.outputEWC = outputEWC;
		this.metadata = metadata;
		this.turnOnConditions = turnOnConditions;
		this.turnOffConditions = turnOffConditions;
		this.executed = false;
		this.globalId = GLOBAL_ID_COUNTER++;
		this.ewcManager = ewcManager;
	}
	
	public final short getOutputEWC() {
		return this.outputEWC;
	}

	public final BehaviourMetadata getMetadata() {
		return this.metadata;
	}

	public final BehaviourConditions getTurnOnConditions() {
		return this.turnOnConditions;
	}

	public final BehaviourConditions getTurnOffConditions() {
		return this.turnOffConditions;
	}
	
	public int getGlobalId() {
		return this.globalId;
	}
	
	@Override
	public final void run() {
		if(this.shouldExecute()) {
			this.executed = true;
			this.execute();
			this.executed = false;
		}
	}
	
	public final boolean isExecuted() {
		return this.executed;
	}
	
	public abstract void execute();
	public abstract boolean shouldExecute();
}
