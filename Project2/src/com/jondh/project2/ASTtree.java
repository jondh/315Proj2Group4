package com.jondh.project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* UPDATE: READ, LET, DATA, and PRINT implemented
 *  Let eval() puts out a dummy value
 *  I'm not sure about the eval() and print() iteration yet
 * TODO: put a map with line number and node for looping putposes,
 * 		 implement errors
 */


//This is the main class that will contain the values for
//	the data and what the variables are equal to;
public class ASTtree {
	ArrayList<Double> data = new ArrayList<Double>();
	Map<String, Double> var = new HashMap<String, Double>();
	ASTnode root = null;
	
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
		}
		
		public boolean eval(){
			var.put(equal, 46.5);
			if(leftnode == null) return false;
			leftnode.eval();
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
		}
		public ASTprint(ArrayList<String> SvarsIn, int lnNum){
			variables = SvarsIn;
			linenumber = lnNum;
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
}

