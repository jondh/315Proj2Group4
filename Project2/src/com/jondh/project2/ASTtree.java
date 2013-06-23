package com.jondh.project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/* 
 * Current structure of tree: Every node (except FOR) should only use its
 * 		leftnode, leading to a linear tree. The FOR statement uses its
 * 		rightnode for the loop portion of the statement.
 */


//This is the main class that will contain the values for
//	the data and what the variables are equal to;
public class ASTtree {
	Map<Integer, ASTnode> nodes = new HashMap<Integer, ASTnode>();
	Map<String, ASTnode> forNodes = new HashMap<String, ASTnode>();
	String strBuffer = "";
	ASTnode returnNode = null;
	ASTnode root;
	
	BasicData data1;
	
	ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("JavaScript");
	
    public ASTtree(){
    	data1 = new BasicData();
    	ASTtree.ASTnode r = new ASTnode();
    	root = r;
    }
    
    /* 
     * This function returns the complete program in order of the
     * 	nodes of the tree.
     */
    public ArrayList<String> print(){
    	// Initializing variables
    	ArrayList<String> printReturn = new ArrayList<String>();
    	ArrayList<ASTnode> returnNode = new ArrayList<ASTnode>();
    	ASTtree.ASTnode printNode = new ASTnode();
    	printNode = root;
    	if(printNode.leftnode==null) return null; // empty tree, exit
    	// Go through tree until left-most branch is complete, if a rightnode
    	//	is found, that branch is completely run before returning.
    	while(printNode.leftnode!=null || returnNode.size()!=0){
    		if(printNode.leftnode == null){
    			printNode = returnNode.remove(returnNode.size()-1);
    		}
    		printNode = printNode.leftnode;
    		//System.out.println(printNode.programLine);
    		printReturn.add(printNode.programLine);
    		if(printNode.rightnode != null){
    			returnNode.add(printNode); //get node to go back to
    			printNode = printNode.rightnode;
        		//System.out.println(printNode.programLine);
    			printReturn.add(printNode.programLine);
    		}
    	}
    	return printReturn;
    }
    
	public void run(){
		root.eval();
	}
    
    //This class defines the nodes of the tree; it contains
	//	subclass implementations of each node to be used in
	//	the tree.
	public class ASTnode {
		ASTnode leftnode = null;
		ASTnode rightnode = null;
		int linenumber = 0;
		String programLine = "";
		
		public boolean eval(){
			if(leftnode == null) return false;
			leftnode.eval();
			return true;
		}
	}
	
	/*
	 *  This class reads data values into variables. The variables should
	 *  	be imported in the constructor contained in an ArrayList of 
	 *  	strings where each string is a variable. This class assumes
	 *  	that each string contains exactly one valid BASIC variable.
	 *  	The linenumber should also be included in the constructor.
	 */
	public class ASTread extends ASTnode{
		ArrayList<String> variables = new ArrayList<String>();
		//--Constructor--//
		//_This constructor sets each var in the list equal
		//to the next value in data_//
		ASTread(ArrayList<String> vars, int lnNum){
			linenumber = lnNum;
			variables = vars;
			nodes.put(lnNum, this);
			programLine = linenumber + " READ ";
			for(int i = 0; i < variables.size(); i++){
				programLine += variables.get(i);
				if(i<variables.size()-1){
					programLine += ", ";
				}
			}
		}
		public boolean eval(){
			boolean dataInserted = true;
			for(int i = 0; i < variables.size(); i++){
				if(variables.get(i).indexOf('(') == 1){
					String size = variables.get(i).substring(1);
					size = size.replace("(", "");
					size = size.replace(")", "");
					String varList = variables.get(i).charAt(0)+"";
					int sizeList = (int) (evalExpr(size) - 0);
					dataInserted = data1.insertList(varList, sizeList, data1.getData()); 
				}
				else{
					dataInserted = data1.updateVar(variables.get(i), data1.getData()); 
				}
			}
			if(leftnode == null || !dataInserted){
					//TODO return string buffer
				return false;
			}
			leftnode.eval();
			return true;
		}
	}
	
	/*
	 *  This class evaluates an expression and puts a variable equal to it.
	 *  	The constructor needs a string that represents the variable that
	 *  	the expression is equal to (leftEq), a string that is the expression
	 *  	that is to be evaluated (rightEq), and the linenumber.
	 */
	public class ASTlet extends ASTnode{
		String equal;
		String expr;
		
		ASTlet(String leftEq, String rightEq, int lnNum){
			linenumber = lnNum;
			equal = leftEq;
			expr = rightEq;
			nodes.put(lnNum, this);
			programLine = linenumber+" LET "+equal+" = "+expr;
		}
		
		public boolean eval(){
			if(equal.indexOf('(') == 1){
				String varList = equal.charAt(0)+"";
				String size = equal.substring(1);
				size = size.replace("(", "");
				size = size.replace(")", "");
				int index = evalExpr(size).intValue();
				data1.insertList(varList, index, evalExpr(expr));
			}
			else{
				data1.updateVar(equal, evalExpr(expr));
			}
			if(leftnode != null){
				leftnode.eval();
			}
			return true;
		}
	}

	/*
	 *  This class puts data into the data buffer to be used during read.
	 *  	The constructor expects an ArrayList of doubles corresponding
	 *  	to the data and its order. It also expects the linenumber.
	 */
	public class ASTdata extends ASTnode{
		ArrayList<Double> input = new ArrayList<Double>();
		
		ASTdata(ArrayList<Double> dataIn, int lnNum){
			linenumber = lnNum;
			input = dataIn;
			nodes.put(lnNum, this);
			programLine += linenumber+" DATA ";
			data1.insertDataArray(dataIn);
			for(int i = 0; i < dataIn.size(); i++){
				programLine += dataIn.get(i);
				if(i < dataIn.size()-1){
					programLine += ", ";
				}
			}
		}
	}

	/*
	 *  This class prints out things according to BASIC standards. 
	 *  	The constructor expects an ArrayList of printStruct and 
	 *  	a linenumber. printStruct is a class defined in the class 
	 *  	ASTtree, so an ASTtree is needed to use it. EX.
	 *  	ASTtree.ASTprintStruct prName = tree.new ASTprintStruct(String expression, char endType);
	 *  		where endType is a comma, semicolon or space
	 */
	public class ASTprint extends ASTnode{
		ArrayList<printStruct> items = new ArrayList<printStruct>();
		
		ASTprint(ArrayList<printStruct> stateList, int lnNum){
			items = stateList;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine += linenumber+" PRINT ";
			for(int i = 0; i < items.size(); i++){
				programLine += items.get(i).statement + items.get(i).lineType;
			}
		}
		
		public boolean eval(){
			ArrayList<String> evalSplit = new ArrayList<String>();
			for(int j = 0; j < items.size(); j++){
				evalSplit = splitExpression(items.get(j).statement);
				
				for(int i = 0; i < evalSplit.size(); i++){
					if(evalSplit.get(i).charAt(0) == '"'){
						strBuffer += evalSplit.get(i).replace("\"", "");
					}
					else{
						strBuffer += formatOut(evalExpr(evalSplit.get(i)));
					}
				}
				if(items.get(j).lineType == ','){
					for(int k = 0; k < strBuffer.length()%15; k++){
						strBuffer += ' ';
					}
					if(strBuffer.length() >= 75){
						System.out.println(strBuffer);
						strBuffer = "";
					}
				}
				else if(items.get(j).lineType == ';'){
					int k = 0;
					while(k < 3 || strBuffer.length()%3 != 0){
						strBuffer += ' ';
						k++;
					}
					if(strBuffer.length() >= 70){
						System.out.println(strBuffer);
						strBuffer = "";
					}
				}
				else{
					System.out.println(strBuffer);
					strBuffer = "";
				}
			}
			
			if(leftnode == null) return false;
			leftnode.eval();
			return true;
		}
		
		private String formatOut(Double in){
			String num = String.format("%.7G", in);
			num = num.replace("+", "");
			// This delets all the trailing zeros after decimal
			// reference -> user mdm on stackoverflow
			//	url: http://stackoverflow.com/questions/14984664/remove-trailing-zero-in-java
			if(num.indexOf('.') >= 0){
				num = num.replaceAll("\\.?0+$", "");
			}
			return num;
		}
	}

	/*
	 *  This class gos to an inputted line. This constructor contains
	 *  	the line to go to (int) as well as its own linenumber.
	 */
	public class ASTgoto extends ASTnode{
		int gotoNode = 0;
		
		ASTgoto(int go, int lnNum){
			gotoNode = go;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine = linenumber + " GOTO " + go;
		}
		
		public boolean eval(){
			if(nodes.containsKey(gotoNode)){
				nodes.get(gotoNode).eval();
			}
			else{ //TODO goto error
				System.out.println("The node "+gotoNode+" does not exist. From GOTO");
				return false;
			}
			return true;
		}
	}

	/*
	 *  This class goes to a line if a condition is met. The constructor 
	 *  	expects the statement to be tested (string), the line to go
	 *  	to (int), and its own linenumber. The string to be tested should
	 *  	contain a valid BASIC relation and valid BASIC variables.
	 */
	public class ASTif extends ASTnode{
		String conditional = "";
		int gotoNode = 0;
		
		ASTif(String statement, int go, int lnNum){
			gotoNode = go;
			conditional = statement;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine += linenumber+" IF "+conditional+" THEN "+gotoNode;
		}
		
		public boolean eval(){
			boolean cond = false;
			// Runs the conditional in javascript, throws scripting exception
		    try {
				if((Boolean) engine.eval(putValuesIn(conditional))){ //TODO
					cond = true;
				}
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(cond){ // TODO if inputted statement is true
				if(nodes.containsKey(gotoNode)){
					nodes.get(gotoNode).eval();
				}
				else{
					System.out.println("The node "+gotoNode+" does not exist. From IF THEN");
					return false;
				}
			}
			else{
				if(leftnode == null) return false;
				leftnode.eval();
			}
			return true;
		}
	}
	
	/*
	 *  This class initializes a variable to an exression, runs until the
	 *  	variable reaches a condition, and steps the variable by an amount
	 *  	each iteration. Paired with a NEXT (var) statement to iterate.
	 *  	The constructor expects a String containing only the variable(nVar),
	 *  	a String containing an expression for what the variable is initialized
	 *  	to (nIni), a String contaning an expression for what the variable
	 *  	is allowed to reach befor the loop stops (toCond), a Double contaning
	 *  	what the variable is stepped by (step), and its own linenumber.
	 */
	public class ASTfor extends ASTnode{
		/* This for loop runs the rightnode whenever the condition
		 * is true. The end of the righnode should contain a next
		 * statement for the loop to be executed. When the condition
		 * is not true, the leftnode is run.
		 */
		String forVar;
		String initialValue;
		Double until;
		Double stepBy;
		boolean initial = true;
		
		ASTfor(String nVar, String nIni, String toCond, Double step, int lnNum){
			forVar = nVar;
			initialValue = nIni;
			until = evalExpr(putValuesIn(toCond));
			stepBy = step;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			forNodes.put(nVar, this);
			programLine = linenumber + " FOR " + forVar + " = " + initialValue + 
					" TO " + until + " STEP " + step;
		}
		
		public boolean eval(){
			if(initial == true){ //Initial input var to expression
				Double evalExpr = evalExpr(putValuesIn(initialValue))-stepBy;
				data1.updateVar(forVar, evalExpr);
				initial = false;
			}
			Double curVal = data1.getVar(forVar)+stepBy;
			// TODO data1 check for error
			if(stepBy>0 && curVal<=until){
				data1.updateVar(forVar, curVal);
				if(rightnode == null) return false;
				rightnode.eval();
			}
			else if(stepBy<0 && curVal>=until){
				data1.updateVar(forVar, curVal);
				if(rightnode == null) return false;
				rightnode.eval();
			}
			else{
				initial = true;
				if(leftnode == null) return false;
				leftnode.eval();
			}
			return true;
		}
	}
	
	/*
	 *  This class returns the program execution to the FOR statement
	 *  associated with the inputted variable. The constructor expects
	 *  a String containing exactly the variable associted with the FOR
	 *  statement, and its own linenumber.
	 */
	public class ASTnext extends ASTnode{
		String forVar;
		
		ASTnext(String gotoVar, int lnNum){
			forVar = gotoVar;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine = linenumber + " NEXT " + forVar;
		}
		
		public boolean eval(){
			if(forNodes.containsKey(forVar)){
				forNodes.get(forVar).eval();
			}
			else{
				System.out.println(forVar + " not contained in FOR node");
				return false;
			}
			return true;
		}
	}
	
	/*
	 *  This class goes to a line and then comes back once a RETURN
	 *  statement is found. This constructor expects the line to go
	 *  to and its own linenumber.
	 */
	public class ASTgosub extends ASTnode{
		int gotoLine;
		
		ASTgosub(int go ,int lnNum){
			gotoLine = go;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine = linenumber + " GOSUB " + go;
		}
		
		public boolean eval(){
			returnNode = this;
			if(nodes.containsKey(gotoLine)){
				nodes.get(gotoLine).eval();
			}
			else{
				//TODO GOSUB error
				System.out.println("Error in GOSUB - Line doesn't exist");
				return false;
			}
			return true;
		}
	}
	
	/*
	 *  This class returns back to the last GOSUB line. The constructor
	 *  expects only its own linenumber.
	 */
	public class ASTreturn extends ASTnode{
		
		ASTreturn(int lnNum){
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine = linenumber + " RETURN";
		}
		
		public boolean eval(){
			returnNode.eval();
			return true;
		}
		
	}
	/*
	 *  This class defines a function. The constructor takes the
	 *  	letter the function is defined by (ie X or N for FNX or
	 *  	FNN), the variable of the function (ie X or Y for FNR(X)
	 *  	or FNR(Y)), the actual user defined function, and its own
	 *  	line number.
	 */
	public class ASTdef extends ASTnode{
		String funct;
		String funVar;
		String funLet;
		
		ASTdef(String l, String v, String fun, int lnNum){
			funLet = l;
			funVar = v;
			funct = fun;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine = linenumber + " DEF FN" + funLet + "(" +
						funVar + ")" + " = " + funct;
		}
		
		public boolean eval(){
			data1.insertFormula(funLet, funVar, funct);
			if(leftnode == null) return false;
			leftnode.eval();
			return true;
		}
	}
	
	/*
	 *  The class ends execution. Its constructor expects only its
	 *  linenumber.
	 */
	public class ASTend extends ASTnode{
		ASTend(int lnNum){
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine = linenumber + " END";
		}
		
		public boolean eval(){
			System.out.println(strBuffer);
			return false;
		}
	}
	
	protected Double evalExpr(String expr){
		Double evalExpr = 0.0;
		try {
			evalExpr = (Double) engine.eval(putValuesIn(expr));
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return evalExpr;
	}
	
	/*
	 * 	This function takes in a string with variables and functions.
	 * 	 It returns the same string with the variables replaced with 
	 *   their current values and the function replaces with equivalent
	 *   functions readable by JavaScript.
	 */
	protected String putValuesIn(String expr){
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
					convert += data1.getList(varAt, listIndex);
				}
				else{
					convert += data1.getVar(varAt);
					// TODO illegal formula check
				}
				
			}
			else{
				convert += expr.charAt(i);
			}
		}
		return replaceFunctions(convert);
	}

	
	
	private class FuncData{
		String funcIn;
		String funcOut;
		
		FuncData(String in, String out){
			funcIn = in;
			funcOut = out;
		}
	}
	
	protected String replaceFunctions(String inString){
		ArrayList<FuncData> functionMap = new ArrayList<FuncData>();
		functionMap = getReplaced();
		inString = replaceRND(inString);
		inString = replaceExp(inString);
		inString = replaceUser(inString);
		for(int i = 0; i < functionMap.size(); i++){
			inString = inString.replace(functionMap.get(i).funcIn, functionMap.get(i).funcOut);
		}
		return inString;
	}
	
	protected String replaceUser(String inString){
		while(inString.indexOf("FN") >= 0){
			int pos = inString.indexOf("FN");
			String funLet = inString.charAt(pos+2) + "";
			// Get the input for the function
			String funInput = getNextExpression(pos+3, inString);
			String formula_ = data1.getFormula(funLet, funInput);
			inString = inString.replace("FN"+funLet+funInput, formula_);
			// TODO undefined function check
		}
		return inString;
	}
	
	protected String replaceExp(String inString){
		while(inString.indexOf('^') > 0){
			int pos = inString.indexOf('^');
			String prevEpr = getPrevExpression(pos, inString);
			String nextEpr = getNextExpression(pos, inString);
			inString = inString.replace(prevEpr+'^'+nextEpr, "Math.pow("+prevEpr+","+nextEpr+")");
		}
		return inString;
	}
	
	protected ArrayList<FuncData> getReplaced(){
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
		FuncData data9 = new FuncData("=","==");
		functionMap.add(data9);
		FuncData data10 = new FuncData("<>","!=");
		functionMap.add(data10);
		return functionMap;
	}
	
	protected String getNextExpression(int pos, String inString){
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
	// pos is position of carrot, assumes a vaild expression before
	// the carrot
	protected String getPrevExpression(int pos, String inString){
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
	
	protected String replaceRND(String inString){
		while(inString.indexOf("RND") >= 0){
			int strAt = inString.indexOf("RND") + 3;
			String replaceStr = "RND";
			replaceStr += getNextExpression(strAt, inString);
			inString = inString.replace(replaceStr, "Math.random()");
		}
		return inString;
	
	}
	
	public boolean isNumber(char c){
		if((c>='0' && c<='9') || c=='.'){
			return true;
		}
		else return false;
	}
	public boolean isLetter(char c){
		if((c>='A' && c<='Z') || (c>='a' && c<='z')){
			return true;
		}
		else return false;
	}

	protected ArrayList<String> splitExpression(String expr){
		ArrayList<String> parts = new ArrayList<String>();
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
	
	public class printStruct{
		String statement;
		char lineType;
		
		printStruct(String pr, char l){
			statement = pr;
			lineType = l;
		}
	}
}


