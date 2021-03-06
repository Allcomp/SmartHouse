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

package cz.allcomp.shs.allcomplib.transducers;


/**
* transducers/EwcAccess.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /home/petr/secret/ewa/AllNet.idl
* Sunday, 15 February 2015 11:26:13 o'clock CET
*/


/** 
  * The <code>EwcAccess</code> is the only interface to read (and write) data from the Ewc server.
  * <p> The Ewc address is specified by short (two bytes) value, while Ewc address is 255 maximum.
  * The higher byte is reserved to specify the Ewc net when more then one is present on single server.
  */
public interface EwcAccess extends EwcAccessOperations, org.omg.CORBA.Object, org.omg.CORBA.portable.IDLEntity 
{

  /** bitmask to the ID values to get Ewc type signature. */
  public static final int TYPE = (int)(1);

  /** bitmask to the ID values to get Ewc ID high word. */
  public static final int HWH = (int)(2);

  /** bitmask to the ID values to get Ewc ID low word (hardware type). */
  public static final int HW = (int)(4);

  /** bitmask to the ID values to get Ewc software version. */
  public static final int SW = (int)(8);

  /** bitmask to the ID values to get time from the last message received. */
  public static final int SENESCENCE = (int)(16);

  /** bitmask to the ID values to get Ewc communication status flags {@link gS(short ewc)} */
  public static final int STATUS = (int)(32);

  /** bitmask to the ID values to get Ewc Hall value (the first 32 digital inputs). */
  public static final int HALL = (int)(64);
} // interface EwcAccess
