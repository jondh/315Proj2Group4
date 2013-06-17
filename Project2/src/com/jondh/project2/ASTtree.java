package com.jondh.project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
			while(leftnode.eval()){
				if(rightnode == null) return false;
				rightnode.eval();
			}
			return true;
		}
		
		public boolean print(){
			if(leftnode == null) return false;
			while(leftnode.print()){
				if(rightnode == null) return false;
				rightnode.print();
			}
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
			return true;
		}
		
		public boolean print(){
			System.out.print(linenumber+" READ ");
			for(int i = 0; i < variables.size(); i++){
				System.out.print(variables.get(i));
			}
			if(leftnode == null) return false;
			while(leftnode.print()){
				if(rightnode == null) return false;
				rightnode.print();
			}
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
			return true;
		}
		
		public boolean print(){
			System.out.println(linenumber+" LET "+equal+" = "+expr);
			if(leftnode == null) return false;
			while(leftnode.print()){
				if(rightnode == null) return false;
				rightnode.print();
			}
			return true;
		}
	}
}

