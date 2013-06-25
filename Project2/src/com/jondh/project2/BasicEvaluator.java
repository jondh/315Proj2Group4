/*
 *  AUTHOR: Jonathan Harrison
 *  LAST MODIFIED: 6/24/2013
 */

package com.jondh.project2;

import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class BasicEvaluator {
    ScriptEngine engine;
    BasicData data1;
    
    /*
     * The constructor starts the JavaScipt engine and
     * 		links the inputted BasicData class.
     */
	BasicEvaluator(BasicData data_){
		ScriptEngineManager mgr = new ScriptEngineManager();
		engine = mgr.getEngineByName("JavaScript");
		data1 = data_;
	}
	
	/*
	 *  The purpose of this class is to define a BASIC function
	 *  	(funcIn) and its equivalent JavaScript function (funcOut)
	 */
	private class FuncData{
		String funcIn;
		String funcOut;
		
		FuncData(String in, String out){
			funcIn = in;
			funcOut = out;
		}
	}
	
	/*
	 *  This function evaluates an exrpession in BASIC by converting
	 *  	it to legal javascript and then returns the result as 
	 *  	a double
	 */
	public Double evalExpr(String expr){
		Double evalExpr = 0.0;
		try {
			evalExpr = (Double) engine.eval(putValuesIn(expr));
		} catch (ScriptException e) {
			data1.illegalFormula = true;
			e.printStackTrace();
		}
		return evalExpr;
	}
	
	/*
	 *  This function evaluates a relation in BASIC by converting
	 *  	it to legal javascript and then retutns the result as
	 *  	a boolean
	 */
	public boolean evalExprB(String expr){
		Boolean evalExpr = false;
		try {
			evalExpr = (Boolean) engine.eval(putValuesIn(expr));
		} catch (ScriptException e) {
			data1.illegalFormula = true;
			e.printStackTrace();
		}
		return evalExpr;
	}
	
	/*
	 *  This function takes an String of one or more "expressions"
	 *  	(a BASIC string or variable) and splits the string into
	 *  	strings of each string or variables and returns an arraylist
	 *  	of these
	 */
	public ArrayList<String> splitExpression(String expr){
		ArrayList<String> parts = new ArrayList<String>();
		if(expr.length() == 0){
			parts.add("");
		}
		for(int i = 0; i < expr.length(); i++){
			while(expr.charAt(i) == ' ') i++;
			if(expr.charAt(i) == '"'){
				String quote = "" + expr.charAt(i);
				i++;
				while(expr.charAt(i) != '"'){
					quote += expr.charAt(i);
					i++;
					if(i == expr.length()-1) break;
				}
				quote += expr.charAt(i);
				parts.add(quote);
			}
			else{
				String ex = "";
				while(expr.charAt(i)!='"' && i<expr.length()-1 && expr.charAt(i)!=' '){
					ex += expr.charAt(i);
					i++;
				}
				ex += expr.charAt(i);
				parts.add(ex);
			}
		}
		return parts;
	}
	
	/*
	 * 	This function takes in a string with BASIC variables and functions.
	 * 	 It returns the same string with the variables replaced with 
	 *   their current values and the function replaces with equivalent
	 *   functions readable by JavaScript.
	 */
	private String putValuesIn(String expr){
		String convert = "";
		boolean function = false;
		for(int i = 0; i < expr.length(); i++){
			String varAt = "";
			String listExpr = "";
			boolean listVariable = false;
			boolean scientific = false;
			int listIndex = 0;
			if(function && expr.charAt(i)=='('){
				function = false;
			}
			if(i < expr.length()-1){
				if(expr.charAt(i)>='A' && expr.charAt(i)<='Z' &&
						expr.charAt(i+1)>='A' && expr.charAt(i+1)<='Z'){
					function = true;
				}
				else if(expr.charAt(i)=='E' && (isNumber(expr.charAt(i+1)) ||
							expr.charAt(i)=='-')){
					scientific = true;
				}
			}
			if(expr.charAt(i)>='A' && expr.charAt(i)<='Z' && !function && !scientific){
				varAt += expr.charAt(i);
				if(i < expr.length()-1){
					if(expr.charAt(i+1)>='0' && expr.charAt(i+1)<='9'){
						varAt += expr.charAt(i+1);
						i++;
					}
					else if(expr.charAt(i+1) == '('){
						i+=2;
						while(expr.charAt(i) != ')'){
							listExpr += expr.charAt(i);
							i++;
						}
						listVariable = true;
						listIndex = evalExpr(listExpr).intValue();
					}
				}
				if(listVariable){
					convert += " " + data1.getList(varAt, listIndex);
				}
				else{
					convert += " " + data1.getVar(varAt);
				}
				
			}
			else{
				convert += expr.charAt(i);
			}
		}
		return replaceFunctions(convert);
	}
	
	/*
	 *  This function takes a string with BASIC functions and input
	 *  	and converts them to JavaScript function.
	 */
	private String replaceFunctions(String inString){
		ArrayList<FuncData> functionMap = new ArrayList<FuncData>();
		functionMap = getReplaced();
		inString = replaceEquals(inString);
		inString = replaceRND(inString);
		inString = replaceExp(inString);
		inString = replaceUser(inString);
		for(int i = 0; i < functionMap.size(); i++){
			inString = inString.replace(functionMap.get(i).funcIn, functionMap.get(i).funcOut);
		}
		return inString;
	}
	
	private String replaceEquals(String inString){
		if(inString.indexOf('=')>0){
			int pos = inString.indexOf('=');
			if(inString.charAt(pos-1)!='<' && inString.charAt(pos-1)!='>'){
				inString = inString.replace("=", "");
			}
		}
		return inString;
	}
	
	/*
	 *  This function takes a string with user defined BASIC functions
	 *  	(ie. FNX(Y)). It replaces the function with its definition
	 *  	and replaces the varibale with what was inputted.
	 */
	private String replaceUser(String inString){
		while(inString.indexOf("FN") >= 0){
			int pos = inString.indexOf("FN");
			String funLet = inString.charAt(pos+2) + "";
			// Get the input for the function
			String funInput = getNextExpression(pos+3, inString);
			String formula_ = data1.getFormula(funLet, funInput);
			inString = inString.replace("FN"+funLet+funInput, formula_);
		}
		return inString;
	}
	
	/*
	 *  This function replaces the BASIC exponent operator ^
	 *  	with the equivalent JavaScript function Math.pow()
	 */
	private String replaceExp(String inString){
		while(inString.indexOf('^') > 0){
			int pos = inString.indexOf('^');
			String prevEpr = getPrevExpression(pos, inString);
			String nextEpr = getNextExpression(pos, inString);
			inString = inString.replace(prevEpr+'^'+nextEpr, "Math.pow("+prevEpr+","+nextEpr+")");
		}
		return inString;
	}
	
	/*
	 *  This function defines an ArrayList of BASIC functions paired
	 *  	with their approiate JavaScript conversions
	 */
	private ArrayList<FuncData> getReplaced(){
		ArrayList<FuncData> functionMap = new ArrayList<FuncData>();
		FuncData data0 = new FuncData("SIN","Math.sin");
		functionMap.add(data0);
		FuncData data1 = new FuncData("COS","Math.cos");
		functionMap.add(data1);
		FuncData data2 = new FuncData("TAN","Math.tan");
		functionMap.add(data2);
		FuncData data3 = new FuncData("ATN","Math.atan");
		functionMap.add(data3);
		FuncData data4 = new FuncData("EXP(","Math.pow(Math.E,");
		functionMap.add(data4);
		FuncData data5 = new FuncData("ABS","Math.abs");
		functionMap.add(data5);
		FuncData data6 = new FuncData("LOG","Math.log");
		functionMap.add(data6);
		FuncData data7 = new FuncData("SQR","Math.sqrt");
		functionMap.add(data7);
		FuncData data8 = new FuncData("INT","Math.round");
		functionMap.add(data8);
//		FuncData data9 = new FuncData("=","==");
//		functionMap.add(data9);
		FuncData data10 = new FuncData("<>","!=");
		functionMap.add(data10);
		return functionMap;
	}
	
	/*
	 *  This function takes an input string (math expression) and
	 *  	a position in the String. It then returns the next
	 *  	mathematical expression after this position.
	 */
	public String getNextExpression(int pos, String inString){
		String nextEpr = "";
		if(inString.charAt(pos)=='^'){
			pos++;
			while(inString.charAt(pos)==' '){
				nextEpr += inString.charAt(pos);
				pos++;
			}
		}
		
		if(inString.charAt(pos)=='('){
			int pare = 1;
			nextEpr += inString.charAt(pos);
			pos++;
			while(pare != 0){
				nextEpr += inString.charAt(pos);
				if(inString.charAt(pos) == '('){
					pare++;
				}
				else if(inString.charAt(pos) == ')'){
					pare--;
				}
				pos++;
			}
		}
		else{
			while(inString.charAt(pos) == ' '){
				pos++;
			}
			while(isLetter(inString.charAt(pos)) ||
					isNumber(inString.charAt(pos))){
				nextEpr += inString.charAt(pos);
				pos++;
			}
		}
		return nextEpr;
	}
	
	/*
	 *  This function takes an input string (math expression) and
	 *  	a position in the String. It then returns the previous
	 *  	mathematical expression after this position.
	 */
	private String getPrevExpression(int pos, String inString){
		String prevEpr = "";
		pos--;
		while(inString.charAt(pos) == ' '){
			prevEpr = inString.charAt(pos) + prevEpr;
			pos--;
		}
		if(inString.charAt(pos)==')'){
			int pare = 1;
			prevEpr = inString.charAt(pos) + prevEpr;
			pos--;
			while(pare != 0){
				prevEpr = inString.charAt(pos) + prevEpr;
				if(inString.charAt(pos) == ')'){
					pare++;
				}
				else if(inString.charAt(pos) == '('){
					pare--;
				}
				pos--;
			}
		}
		else{
			boolean negative = false;
			while(isLetter(inString.charAt(pos)) ||
					isNumber(inString.charAt(pos))){
				prevEpr = inString.charAt(pos) + prevEpr;
				pos--;
				// check for negative number
				if(pos>0){ 
					if(inString.charAt(pos-1)=='-'){
						negative = true;
					}
				}
			}
			if(negative){
				prevEpr = '-' + prevEpr;
			}
		}
		return prevEpr;
	}
	
	/*
	 *  This function replaces the BASIC function RND and any inputted
	 *  	values with the equivalent JavaScript functin Math.random()
	 */
	private String replaceRND(String inString){
		while(inString.indexOf("RND") >= 0){
			int strAt = inString.indexOf("RND") + 3;
			String replaceStr = "RND";
			replaceStr += getNextExpression(strAt, inString);
			inString = inString.replace(replaceStr, "Math.random()");
		}
		return inString;
	
	}
	
	/*
	 *  These functions check whether the inputted char
	 *  	is a number or letter and returns a boolean
	 */
	private boolean isNumber(char c){
		if((c>='0' && c<='9') || c=='.'){
			return true;
		}
		else return false;
	}
	private boolean isLetter(char c){
		if((c>='A' && c<='Z') || (c>='a' && c<='z')){
			return true;
		}
		else return false;
	}
}
