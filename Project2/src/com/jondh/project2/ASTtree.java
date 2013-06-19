package com.jondh.project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/* UPDATE: READ, LET, DATA, PRINT, GOTO, IF THEN, FOR, NEXT
 * 		   implemented to some degree.
 * Current structure of tree: Every node by default only has
 * 		a leftnode, leading to a linear tree. The FOR statement
 * 		has a rightnode which is the loop portion of the statement.
 */


//This is the main class that will contain the values for
//	the data and what the variables are equal to;
public class ASTtree {
	ArrayList<Double> data = new ArrayList<Double>();
	Map<String, Double> var = new HashMap<String, Double>();
	Map<Integer, ASTnode> nodes = new HashMap<Integer, ASTnode>();
	Map<String, ASTnode> forNodes = new HashMap<String, ASTnode>();
	ASTnode root;
	
	ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("JavaScript");
	
    public ASTtree(){
    	ASTtree.ASTnode r = new ASTnode();
    	root = r;
    }
    
    public void print(){
    	ArrayList<ASTnode> returnNode = new ArrayList<ASTnode>();
    	ASTtree.ASTnode printNode = new ASTnode();
    	printNode = root;
    	while(printNode.leftnode!=null || returnNode.size()!=0){
    		if(printNode.leftnode == null){
    			printNode = returnNode.remove(0);
    		}
    		printNode = printNode.leftnode;
    		System.out.println(printNode.programLine);
    		if(printNode.rightnode != null){
    			returnNode.add(printNode); //get node to go back to
    			printNode = printNode.rightnode;
        		System.out.println(printNode.programLine);
    		}
    	}
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
		
		public boolean print(){
			if(leftnode == null) return false;
			leftnode.print();
			return true;
		}
	}
	
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
			for(int i = 0; i < variables.size(); i++){
				if(data.size()==0) return false;
				var.put(variables.get(i),data.remove(0));
			}
			if(leftnode == null) return false;
			leftnode.eval();
//			if(rightnode == null) return false;
//			rightnode.print();
			return true;
		}
		
		public boolean print(){
			System.out.print(linenumber+" READ ");
			for(int i = 0; i < variables.size(); i++){
				System.out.print(variables.get(i));
				if(i<variables.size()-1) System.out.print(", ");
			}
			System.out.print("\n");
			if(leftnode == null) return false;
			leftnode.print();
			//while(leftnode.print()){
//				if(rightnode == null) return false;
//				rightnode.print();
			//}
			return true;
		}
	}
	
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
			Double evalExpr = 0.0;
			try {
				evalExpr = (Double) engine.eval(putValuesIn(expr));
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			var.put(equal, evalExpr);
			if(leftnode != null){
				leftnode.eval();
			}
//			if(rightnode == null) return false;
//			rightnode.eval();
			return true;
		}
		
		public boolean print(){
			System.out.println(linenumber+" LET "+equal+" = "+expr);
			if(leftnode == null) return false;
			leftnode.print();
			//while(leftnode.print()){
//				if(rightnode == null) return false;
//				rightnode.print();
		//	}
			return true;
		}
	}

	public class ASTdata extends ASTnode{
		ArrayList<Double> input = new ArrayList<Double>();
		
		// The constructor puts the data into the data list
		//	so that it is avilable from the beginning of the 
		//	program.
		ASTdata(ArrayList<Double> dataIn, int lnNum){
			linenumber = lnNum;
			input = dataIn;
			nodes.put(lnNum, this);
			programLine += linenumber+" DATA ";
			for(int i = 0; i < dataIn.size(); i++){
				data.add(dataIn.get(i));
				programLine += dataIn.get(i);
				if(i < dataIn.size()-1){
					programLine += ", ";
				}
			}
		}
		public boolean print(){
			System.out.print(linenumber+" DATA ");
			for(int i = 0; i < input.size(); i++){
				System.out.print(input.get(i));
				if(i < input.size()-1) System.out.print(", ");
			}
			System.out.print("\n");
			if(leftnode == null) return false;
			leftnode.print();
			return true;
		}
	}

	public class ASTprint extends ASTnode{
		String expr = "";
		ArrayList<String> variables = new ArrayList<String>();
		
		ASTprint(String statement, int lnNum){
			expr = statement;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine += linenumber+" PRINT \""+expr+"\"";
		}
		public ASTprint(ArrayList<String> SvarsIn, int lnNum){
			variables = SvarsIn;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			programLine += linenumber+" PRINT ";
			for(int i = 0; i < variables.size(); i++){
				programLine += variables.get(i);
				if(i < variables.size()-1) programLine += ", ";
			}
		}
		public boolean eval(){
			if(expr == ""){
				for(int i = 0; i < variables.size(); i++){
					if(!var.containsKey(variables.get(i))){ return false; } // TODO error: variable not initialized
					System.out.print(variables.get(i)+" = ");
					System.out.print(var.get(variables.get(i))+"\n");
				}
			}
			else{
				System.out.print(expr + "\n");
			}
			if(leftnode == null) return false;
			leftnode.eval();
			return true;
		}
		public boolean print(){
			if(expr == ""){
				System.out.print(linenumber+" PRINT ");
				for(int i = 0; i < variables.size(); i++){
					System.out.print(variables.get(i));
					if(i < variables.size()-1) System.out.print(", ");
				}
				System.out.print("\n");
			}
			else{
				System.out.println(linenumber+" PRINT \""+expr+"\"");
			}
			if(leftnode == null) return false;
			leftnode.print();
//			if(rightnode == null) return false;
//			rightnode.print();
			return true;
		}
	}

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
			else{
				System.out.println("The node "+gotoNode+" does not exist. From GOTO");
				return false;
			}
			return true;
		}
		
		public boolean print(){
			System.out.println(linenumber+" GOTO "+gotoNode);
			if(leftnode == null) return false;
			leftnode.print();
//			if(rightnode == null) return false;
//			rightnode.print();
			return true;
		}
	}

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
				if((Boolean) engine.eval(putValuesIn(conditional))){
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
//				if(rightnode == null) return false;
//				rightnode.print();
			}
			return true;
		}
		
		public boolean print(){
			System.out.println(linenumber+" IF "+conditional+" THEN "+gotoNode);
			if(leftnode == null) return false;
			leftnode.print();
//			if(rightnode == null) return false;
//			rightnode.print();
			return true;
		}
	}
	
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
			until = 0.0;
			try {
				until = (Double) engine.eval(putValuesIn(toCond));
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stepBy = step;
			linenumber = lnNum;
			nodes.put(lnNum, this);
			forNodes.put(nVar, this);
			programLine = linenumber + " FOR " + forVar + " = " + initialValue + 
					" TO " + until + " STEP " + step;
		}
		
		public boolean eval(){
			if(initial == true){ //Initial input var to expression
				Double evalExpr = 0.0;
				try {
					evalExpr = (Double) engine.eval(putValuesIn(initialValue))-stepBy;
				} catch (ScriptException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				var.put(forVar, evalExpr);
				initial = false;
			}
			Double curVal = var.get(forVar)+stepBy;
			if(stepBy>0 && curVal<=until){
				var.put(forVar, curVal);
				if(rightnode == null) return false;
				rightnode.eval();
			}
			else if(stepBy<0 && curVal>=until){
				var.put(forVar, curVal);
				if(rightnode == null) return false;
				rightnode.eval();
			}
			else{
				if(leftnode == null) return false;
				leftnode.eval();
			}
			return true;
		}
		
		public boolean print(){
			if(rightnode == null) return false;
			rightnode.eval();
			return true;
		}
	}
	
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
	// This takes an input expression with variables and
	// outputs the expression with the varaiables converted to
	// their values.
	protected String putValuesIn(String expr){
		String convert = "";
		for(int i = 0; i < expr.length(); i++){
			String varAt = "";
			if(expr.charAt(i)>='A' && expr.charAt(i)<='Z'){
				varAt += expr.charAt(i);
				if(i < expr.length()-1){
					if(expr.charAt(i+1)>='0' && expr.charAt(i+1)<='9'){
						varAt += expr.charAt(i+1);
						i++;
					}
				}
				if(var.containsKey(varAt)){
					convert += " "+var.get(varAt);
				}
				else{
					convert += "NaN";
				}
			}
			else{
				convert += expr.charAt(i);
			}
		}
		return convert;
	}
}

