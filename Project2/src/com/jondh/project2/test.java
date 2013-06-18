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
		// The test program from http://www.cs.bris.ac.uk/~dave/basic.pdf
		ArrayList<String> vars = new ArrayList<String>();
		ArrayList<String> vars30 = new ArrayList<String>();
		ArrayList<Double> data70 = new ArrayList<Double>();
		ArrayList<Double> data80 = new ArrayList<Double>();
		ArrayList<Double> data85 = new ArrayList<Double>();
		ArrayList<String> pr55 = new ArrayList<String>();
		vars.add("A1");
		vars.add("A2");
		vars.add("A3");
		vars.add("A4");
		vars30.add("B1");
		vars30.add("B2");
		data70.add(1.0);
		data70.add(2.0);
		data70.add(4.0);
		data80.add(2.0);
		data80.add(-7.0);
		data80.add(5.0);
		data85.add(1.0);
		data85.add(3.0);
		data85.add(4.0);
		data85.add(-7.0);
		pr55.add("X1");
		pr55.add("X2");
		ASTtree tree = new ASTtree();
		ASTtree.ASTnode root = tree.new ASTnode();
		tree.root = root;
		ASTtree.ASTread read10 = tree.new ASTread(vars, 10);
		ASTtree.ASTlet let15 = tree.new ASTlet("D","A1*A4-A3*A2",15);
		ASTtree.ASTif if20 = tree.new ASTif("D == 0", 65, 20);
		ASTtree.ASTread read30 = tree.new ASTread(vars30,30);
		ASTtree.ASTlet let37 = tree.new ASTlet("X1","(B1*A4-B2*A2)/D",37);
		ASTtree.ASTlet let42 = tree.new ASTlet("X2","(A1*B2-A3*B1)/D",42);
		ASTtree.ASTprint print55 = tree.new ASTprint(pr55,55);
		ASTtree.ASTgoto goto60 = tree.new ASTgoto(30,60);
		ASTtree.ASTprint print65 = tree.new ASTprint("NO UNIQUE SOLUTION",65);
		ASTtree.ASTdata d70 = tree.new ASTdata(data70,70);
		ASTtree.ASTdata d80 = tree.new ASTdata(data80,80);
		ASTtree.ASTdata d85 = tree.new ASTdata(data85,85);
		
		root.leftnode = read10;
		read10.leftnode = let15;
		let15.leftnode = if20;
		if20.leftnode = read30;
		read30.leftnode = let37;
		let37.leftnode = let42;
		let42.leftnode = print55;
		print55.leftnode = goto60;
		goto60.leftnode = print65;
		print65.leftnode = d70;
		d70.leftnode = d80;
		d80.leftnode = d85;
		
		root.eval();
		root.print();
	}

}