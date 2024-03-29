/*
 *  AUTHOR: Jonathan Harrison
 *  LAST MODIFIED: 6/25/2013
 *  
 *  CSCE 315 SUMMER 2013
 *  PROJECT 2
 *  
 *  This class defines the AST (abstract syntax tree) for the 
 *  	BASIC compiler. 
 */

package com.jondh.project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
	ArrayList<String> output = new ArrayList<String>();
	String strBuffer = "";
	ASTnode returnNode = null;
	ASTnode root;
	
	BasicData data1;
	BasicEvaluator eval1;
	
    public ASTtree(){
    	data1 = new BasicData();
    	eval1 = new BasicEvaluator(data1);
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
    		if(printNode == null) break;
    		printReturn.add(printNode.programLine);
    		if(printNode.rightnode != null){
    			returnNode.add(printNode); //get node to go back to
    			printNode = printNode.rightnode;
    			printReturn.add(printNode.programLine);
    		}
    	}
    	return printReturn;
    }
    
	public ArrayList<String> run(){
		root.eval();
		return output;
	}
    
    //This class defines the nodes of the tree; it contains
	//	subclass implementations of each node to be used in
	//	the tree.
	public class ASTnode {
		ASTnode leftnode = null;
		ASTnode rightnode = null;
		int linenumber = 0;
		String programLine = "";
		
		public void eval(){
			if(leftnode == null || errorCheck()){
				output.add(strBuffer);
			}
			else{
				leftnode.eval();
			}
		}
		
		protected void evalNode(ASTnode node_){
			if(node_ == null || errorCheck()){
				output.add(strBuffer);
			}
			else{
				node_.eval();
			}
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
		public void eval(){
			boolean dataInserted = true;
			for(int i = 0; i < variables.size(); i++){
				if(variables.get(i).indexOf('(') == 1){
					String size = variables.get(i).substring(1);
					size = size.replace("(", "");
					size = size.replace(")", "");
					String varList = variables.get(i).charAt(0)+"";
					int sizeList = (int) (eval1.evalExpr(size) - 0);
					dataInserted = data1.insertList(varList, sizeList, data1.getData()); 
				}
				else{
					dataInserted = data1.updateVar(variables.get(i), data1.getData()); 
				}
			}
			if(dataInserted){
				evalNode(leftnode);
			}
			else{
				output.add(strBuffer);
			}
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
		
		public void eval(){
			if(equal.indexOf('(') == 1){
				String varList = equal.charAt(0)+"";
				String size = equal.substring(1);
				size = size.replace("(", "");
				size = size.replace(")", "");
				int index = eval1.evalExpr(size).intValue();
				data1.insertList(varList, index, eval1.evalExpr(expr));
			}
			else{
				data1.updateVar(equal, eval1.evalExpr(expr));
			}
			evalNode(leftnode);
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
		
		public void eval(){
			ArrayList<String> evalSplit = new ArrayList<String>();
			for(int j = 0; j < items.size(); j++){
				if(items.get(j).statement == ""){
					output.add(strBuffer);
					strBuffer = "";
				}
				else{
				evalSplit = eval1.splitExpression(items.get(j).statement);
				
				for(int i = 0; i < evalSplit.size(); i++){
					if(evalSplit.get(i).charAt(0) == '"'){
						strBuffer += evalSplit.get(i).replace("\"", "");
					}
					else{
						strBuffer += formatOut(eval1.evalExpr(evalSplit.get(i)));
					}
				}
				if(items.get(j).lineType == ','){
					for(int k = 0; k < strBuffer.length()%15; k++){
						strBuffer += ' ';
					}
					if(strBuffer.length() >= 75){
						output.add(strBuffer);
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
						output.add(strBuffer);
						strBuffer = "";
					}
				}
				else{
					output.add(strBuffer);
					strBuffer = "";
				}
				}
			}
			
			evalNode(leftnode);
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
		
		public void eval(){
			if(nodes.containsKey(gotoNode)){
				nodes.get(gotoNode).eval();
			}
			else{ 
				output.add(strBuffer);
				output.add("UNDEFINED NUMBER");
			}
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
		
		public void eval(){
			boolean cond = eval1.evalExprB(conditional);
			
			if(cond){
				
				if(nodes.containsKey(gotoNode)){
					nodes.get(gotoNode).eval();
				}
				else{
					output.add(strBuffer);
					output.add("UNDEFINED NUMBER");
				}
			}
			else{
				evalNode(leftnode);
			}
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
		String toCond_;
		Double until;
		Double stepBy;
		boolean initial = true;
		
		ASTfor(String nVar, String nIni, String toCond, Double step, int lnNum){
			forVar = nVar;
			initialValue = nIni;
			toCond_ = toCond;
			stepBy = step;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine = linenumber + " FOR " + forVar + " = " + initialValue + 
					" TO " + toCond;
			if(step != 1.0){
				programLine += " STEP " + step;
			}
		}
		
		public void eval(){
			if(initial == true){ //Initial input var to expression
				forNodes.put(forVar, this);
				Double evalExpr = eval1.evalExpr(initialValue)-stepBy;
				data1.updateVar(forVar, evalExpr);
				initial = false;
			}
			until = eval1.evalExpr(toCond_);
			Double curVal = data1.getVar(forVar)+stepBy;
			if(stepBy>0 && curVal<=until){
				data1.updateVar(forVar, curVal);
				evalNode(rightnode);
			}
			else if(stepBy<0 && curVal>=until){
				data1.updateVar(forVar, curVal);
				evalNode(rightnode);
			}
			else{
				initial = true;
				evalNode(leftnode);
			}
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
		
		public void eval(){
			if(forNodes.containsKey(forVar)){
				forNodes.get(forVar).eval();
			}
			else{
				output.add(strBuffer);
				output.add("NOT MATCH WITH FOR");
			}
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
		
		public void eval(){
			returnNode = leftnode;
			if(nodes.containsKey(gotoLine)){
				nodes.get(gotoLine).eval();
			}
			else{
				output.add(strBuffer);
				output.add("UNDEFINED NUMBER");
			}
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
		
		public void eval(){
			if(returnNode == null){
				output.add(strBuffer);
				output.add("ILLEGAL RETURN");
			}
			else{
				ASTnode tempReturn = returnNode;
				//returnNode = null;
				tempReturn.eval();
			}
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
		
		ASTdef(String leftEq, String fun, int lnNum){
			// get position of the Letter that defines the function
			int pos = leftEq.indexOf("FN")+2;
			// get inputted function variable & remove surrounding paraenthesis
			String funVar_ = eval1.getNextExpression(pos+1, leftEq);
			funVar_ = funVar_.substring(1, funVar_.length()-1);
			funLet = leftEq.charAt(pos)+"";
			funVar = funVar_;
			funct = fun;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine = linenumber + " DEF FN" + funLet + "(" +
						funVar + ")" + " = " + funct;
		}
		
		public void eval(){
			data1.insertFormula(funLet, funVar, funct);
			evalNode(leftnode);
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
		
		public void eval(){
			output.add(strBuffer);
		}
	}
	
	public class printStruct{
		String statement;
		char lineType;
		
		printStruct(String pr, char l){
			statement = pr;
			lineType = l;
		}
	}
	
	
	private boolean errorCheck(){
		ArrayList<String> errors_ = new ArrayList<String>();
		errors_ = data1.getErrors();
		if(errors_.size() == 0){
			return false;
		}
		else{
			for(int i = 0; i < errors_.size(); i++){
				output.add(errors_.get(i));
			}
			return true;
		}
	}
}


