package com.jondh.project2;

import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class test {

	/**
	 * @param args
	 * @throws ScriptException 
	 */
	public static void main(String[] args) throws ScriptException {
		ArrayList<String> vars = new ArrayList<String>();
		vars.add("D");
		ASTtree tree = new ASTtree();
		ASTtree.ASTnode node = tree.new ASTnode();
		tree.root = node;
		ASTtree.ASTread read = tree.new ASTread(vars, 10);
		ASTtree.ASTlet let = tree.new ASTlet("D","535+6",20);
		node.leftnode = read;
		node.rightnode = let;
		node.eval();
		node.print();
//		ScriptEngineManager mgr = new ScriptEngineManager();
//	    ScriptEngine engine = mgr.getEngineByName("JavaScript");
//	    System.out.println(engine.eval("7-5*(8-4)"));
	}

}
