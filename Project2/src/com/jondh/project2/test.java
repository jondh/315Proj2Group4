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
		ASTtree tree = new ASTtree();
		// The test program from http://www.cs.bris.ac.uk/~dave/basic.pdf
		ArrayList<String> vars = new ArrayList<String>();
		ArrayList<String> vars30 = new ArrayList<String>();
		ArrayList<Double> data70 = new ArrayList<Double>();
		ArrayList<Double> data80 = new ArrayList<Double>();
		ArrayList<Double> data85 = new ArrayList<Double>();
		ArrayList<ASTtree.printStruct> pr55 = new ArrayList<ASTtree.printStruct>();
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
		ASTtree.printStruct ps0 = tree.new printStruct("\"X1 = \" X1",';');
		ASTtree.printStruct ps1 = tree.new printStruct("\"X2 = \" X2",' ');
		pr55.add(ps0);
		pr55.add(ps1);
		
//		ASTtree.ASTnode root = tree.new ASTnode();
//		tree.root = root;
		ASTtree.ASTread read10 = tree.new ASTread(vars, 10);
		ASTtree.ASTlet let15 = tree.new ASTlet("D","A1*A4-A3*A2",15);
		ASTtree.ASTif if20 = tree.new ASTif("D == 0", 65, 20);
		ASTtree.ASTread read30 = tree.new ASTread(vars30,30);
		ASTtree.ASTlet let37 = tree.new ASTlet("X1","(B1*A4-B2*A2)/D",37);
		ASTtree.ASTlet let42 = tree.new ASTlet("X2","(A1*B2-A3*B1)/D",42);
		ASTtree.ASTprint print55 = tree.new ASTprint(pr55,55);
		ASTtree.ASTgoto goto60 = tree.new ASTgoto(30,60);
		ASTtree.ASTprint print65 = tree.new ASTprint(pr55,65);
		ASTtree.ASTdata d70 = tree.new ASTdata(data70,70);
		ASTtree.ASTdata d80 = tree.new ASTdata(data80,80);
		ASTtree.ASTdata d85 = tree.new ASTdata(data85,85);
		ASTtree.ASTfor for90 = tree.new ASTfor("I","0","5",1.0,90);
		ArrayList<String> pr95 = new ArrayList<String>();
		pr95.add("I");
		ASTtree.ASTprint print95 = tree.new ASTprint(pr55,95);
		ASTtree.ASTfor for96 = tree.new ASTfor("J","0","COS(0)",1.0,96);
		ArrayList<String> pr97 = new ArrayList<String>();
		pr97.add("J");
		ASTtree.ASTprint print97 = tree.new ASTprint(pr55,97);
		ASTtree.ASTnext next98 = tree.new ASTnext("J",98);
		ASTtree.ASTnext next100 = tree.new ASTnext("I",100);
		ASTtree.ASTprint print105 = tree.new ASTprint(pr55,105);
		
		tree.root.leftnode = read10;
		read10.leftnode = let15;
		let15.leftnode = if20;
		if20.leftnode = read30;
		read30.leftnode = let37;
		let37.leftnode = let42;
		let42.leftnode = print55;
		print55.leftnode = print65;
		print55.leftnode = goto60;
		goto60.leftnode = print65;
		print65.leftnode = d70;
		d70.leftnode = d80;
		d80.leftnode = d85;
//		d85.leftnode = for90;
		for90.rightnode = print95;
		print95.leftnode = for96;
		for96.leftnode = next100;
		for96.rightnode = print97;
		print97.leftnode = next98;
		for90.leftnode = print105;
		
		tree.run();
		
		ArrayList<String> program = new ArrayList<String>();
		program = tree.print();
		for(int i = 0; i < program.size(); i++){
			System.out.println(program.get(i));
		}
		ScriptEngineManager mgr = new ScriptEngineManager();
	    ScriptEngine engine = mgr.getEngineByName("JavaScript");
	    System.out.println(engine.eval("Math.round(5.45/2)"));
	}

}
