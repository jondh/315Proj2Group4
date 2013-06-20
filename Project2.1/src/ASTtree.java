

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/* UPDATE: READ, LET, DATA, and PRINT implemented
 *  Let eval() puts out a dummy value
 *  I'm not sure about the eval() and print() iteration yet
 * TODO: put a map with line number and node for looping purposes,
 * 		 implement errors
 */


//This is the main class that will contain the values for
//	the data and what the variables are equal to;
public class ASTtree {
	ArrayList<Double> data = new ArrayList<Double>();
	Map<String, Double> var = new HashMap<String, Double>();
	Map<Integer, ASTnode> nodes = new HashMap<Integer, ASTnode>();
	ASTnode root = null;
	
	ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("JavaScript");
	
	//This class defines the nodes of the tree; it contains
	//	subclass implementations of each node to be used in
	//	the tree.
	public class ASTnode {
		ASTnode leftnode = null;
		ASTnode rightnode = null;
		int linenumber = 0;
		
		public boolean eval(){
			if(leftnode == null) return false;
			leftnode.eval();
			//while(leftnode.eval()){
				if(rightnode == null) return false;
				rightnode.eval();
			//}
			return true;
		}
		
		public boolean print(){
			if(leftnode == null) return false;
			leftnode.print();
			//while(leftnode.print()){
				if(rightnode == null) return false;
				rightnode.print();
			//}
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
		}
		public boolean eval(){
			for(int i = 0; i < variables.size(); i++){
				if(data.size()==0) return false;
				var.put(variables.get(i),data.remove(0));
			}
			if(leftnode == null) return false;
			leftnode.eval();
			if(rightnode == null) return false;
			rightnode.print();
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
				if(rightnode == null) return false;
				rightnode.print();
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
			if(rightnode == null) return false;
			rightnode.print();
			return true;
		}
		
		public boolean print(){
			System.out.println(linenumber+" LET "+equal+" = "+expr);
			if(leftnode == null) return false;
			leftnode.print();
			//while(leftnode.print()){
				if(rightnode == null) return false;
				rightnode.print();
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
			for(int i = 0; i < dataIn.size(); i++){
				data.add(dataIn.get(i));
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
			//while(leftnode.print()){
				if(rightnode == null) return false;
				rightnode.print();
			//}
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
		}
		public ASTprint(ArrayList<String> SvarsIn, int lnNum){
			variables = SvarsIn;
			linenumber = lnNum;
			nodes.put(lnNum, this);
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
			if(rightnode == null) return false;
			rightnode.print();
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
			if(rightnode == null) return false;
			rightnode.print();
			return true;
		}
	}

	public class ASTgoto extends ASTnode{
		int gotoNode = 0;
		
		ASTgoto(int go, int lnNum){
			gotoNode = go;
			linenumber = lnNum;
			nodes.put(lnNum, this);
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
			if(rightnode == null) return false;
			rightnode.print();
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
				if(rightnode == null) return false;
				rightnode.print();
			}
			return true;
		}
		
		public boolean print(){
			System.out.println(linenumber+" IF "+conditional+" THEN "+gotoNode);
			if(leftnode == null) return false;
			leftnode.print();
			if(rightnode == null) return false;
			rightnode.print();
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

