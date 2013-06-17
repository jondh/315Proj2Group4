package com.jondh.project2;

import java.util.ArrayList;

//This is the main class that will contain the values for
//	the data and what the variables are equal to;
public class ASTtree {
	ArrayList<Double> data = new ArrayList<Double>();
	//ArrayList<ArrayList<Double>> var = new ArrayList<ArrayList<Double>>();
	Double[][] var = new Double[26][11];
	
	//This class defines the nodes of the tree; it contains
	//	subclass implementations of each node to be used in
	//	the tree.
	public class ASTnode {
		ASTnode leftnode = null;
		ASTnode rightnode = null;
		
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
		
		public class ASTread extends ASTnode{
			ASTnode leftnode = null;
			ASTnode rightnode = null;
			//--Constructor--//
			//_This constructor sets each var in the list equal
			//to the next value in data_//
			ASTread(ArrayList<String> vars){
				for(int i = 0; i < vars.size(); i++){
					if(data.size()==0){}
					else{
						int letter = (int)vars.get(i).charAt(0);
						int number = -1;
						if(vars.get(i).length() > 1){
							number = (int)vars.get(i).charAt(1);
						}
						var[letter][number+1] = data.remove(0);
					}
				}
			}
			
			public boolean print(){
				for(int i = 0; i < data.size(); i++){
					System.out.print(data.get(i));
				}
				if(leftnode == null) return false;
				while(leftnode.print()){
					if(rightnode == null) return false;
					rightnode.print();
				}
				return true;
			}
		}
	}
}
