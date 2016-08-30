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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MathExpressionParser {
    // Associativity constants for operators  
    private static final int LEFT_ASSOC  = 0;  
    private static final int RIGHT_ASSOC = 1;  
   
    // Operators  
    private static final Map<String, int[]> OPERATORS = new HashMap<String, int[]>();  
    static   
    {  
        // Map<"token", []{precendence, associativity}>  
        OPERATORS.put("+", new int[] { 0, LEFT_ASSOC });  
        OPERATORS.put("-", new int[] { 0, LEFT_ASSOC });  
        OPERATORS.put("*", new int[] { 5, LEFT_ASSOC });  
        OPERATORS.put("/", new int[] { 5, LEFT_ASSOC });          
    }  
   
    // Test if token is an operator  
    private static boolean isOperator(String token)   
    {  
        return OPERATORS.containsKey(token);  
    }  
   
    // Test associativity of operator token  
    private static boolean isAssociative(String token, int type)   
    {  
        if (!isOperator(token))   
        {  
            throw new IllegalArgumentException("Invalid token: " + token);  
        }  
          
        if (OPERATORS.get(token)[1] == type) {  
            return true;  
        }  
        return false;  
    }  
   
    // Compare precedence of operators.      
    private static final int cmpPrecedence(String token1, String token2)   
    {  
        if (!isOperator(token1) || !isOperator(token2))   
        {  
            throw new IllegalArgumentException("Invalid tokens: " + token1  
                    + " " + token2);  
        }  
        return OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0];  
    }  
   
    // Convert infix expression format into reverse Polish notation  
    public static String[] infixToRPN(String[] inputTokens)   
    {  
        ArrayList<String> out = new ArrayList<String>();  
        Stack<String> stack = new Stack<String>();  
          
        // For each token  
        for (String token : inputTokens)   
        {  
            // If token is an operator  
            if (isOperator(token))   
            {    
                // While stack not empty AND stack top element   
                // is an operator  
                while (!stack.empty() && isOperator(stack.peek()))   
                {                      
                    if ((isAssociative(token, LEFT_ASSOC)         &&   
                         cmpPrecedence(token, stack.peek()) <= 0) ||   
                        (isAssociative(token, RIGHT_ASSOC)        &&   
                         cmpPrecedence(token, stack.peek()) < 0))   
                    {  
                        out.add(stack.pop());     
                        continue;  
                    }  
                    break;  
                }  
                // Push the new operator on the stack  
                stack.push(token);  
            }   
            // If token is a left bracket '('  
            else if (token.equals("("))   
            {  
                stack.push(token);  //   
            }   
            // If token is a right bracket ')'  
            else if (token.equals(")"))   
            {                  
                while (!stack.empty() && !stack.peek().equals("("))   
                {  
                    out.add(stack.pop());   
                }  
                stack.pop();   
            }   
            // If token is a number  
            else   
            {  
                out.add(token);   
            }  
        }  
        while (!stack.empty())  
        {  
            out.add(stack.pop());   
        }  
        String[] output = new String[out.size()];  
        return out.toArray(output);  
    }  
      
    public static double RPNtoDouble(String[] tokens)  
    {          
        Stack<String> stack = new Stack<String>();  
          
        // For each token   
        for (String token : tokens)   
        {  
            // If the token is a value push it onto the stack  
            if (!isOperator(token))   
            {  
                stack.push(token);                  
            }  
            else  
            {  
                // Token is an operator: pop top two entries  
                Double d2 = Double.valueOf( stack.pop() );  
                Double d1 = Double.valueOf( stack.pop() );  
                  
                //Get the result  
                Double result = token.compareTo("+") == 0 ? d1 + d2 :   
                                token.compareTo("-") == 0 ? d1 - d2 :  
                                token.compareTo("*") == 0 ? d1 * d2 :  
                                                            d1 / d2;                 
                                  
                // Push result onto stack  
                stack.push( String.valueOf( result ));                                                  
            }
        }          
          
        return Double.valueOf(stack.pop());  
    }  
   
    public static void main(String[] args) {  
        String[] input = "( 1 + 1 + 1 ) - ( 1 + 1 )".split(" ");         
        String[] output = infixToRPN(input);  
          
        // Build output RPN string minus the commas  
         for (String token : output) {  
            System.out.print(token + " ");  
        }  
         System.out.println();
          
        // Feed the RPN string to RPNtoDouble to give result  
        Double result = RPNtoDouble( output );       
        System.out.println(result);
    }  
}
