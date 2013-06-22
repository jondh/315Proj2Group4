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
		
		// Test of lists and user functions
//		ArrayList<String> rd20 = new ArrayList<String>();
//		rd20.add("P(I)");
//		ArrayList<ASTtree.printStruct> pr60 = new ArrayList<ASTtree.printStruct>();
//		ASTtree.printStruct ps01 = tree.new printStruct("\"P(0) = \" P(0) \" P(1) = \" P(1) \" P(2) = \" P(2) \" P(3) = \" P(3)" ,' ');
//		pr60.add(ps01);
//		ArrayList<Double> d1000 = new ArrayList<Double>();
//		d1000.add(10.0);
//		d1000.add(20.0);
//		d1000.add(30.0);
//		d1000.add(1303000.0);
//		
//		ASTtree.ASTfor for10 = tree.new ASTfor("I","0","3",1.0,10);
//		ASTtree.ASTread read20 = tree.new ASTread(rd20,20);
//		ASTtree.ASTnext next30 = tree.new ASTnext("I",30);
//		ASTtree.ASTlet let40 = tree.new ASTlet("P(1)", "-23.435E3", 40);
//		ASTtree.ASTdef def50 = tree.new ASTdef("X","X","(ABS(X*9)*10)",50);
//		ASTtree.ASTlet let55 = tree.new ASTlet("P(2)","FNX(-3)",55);
//		ASTtree.ASTprint print60 = tree.new ASTprint(pr60,60);
//		ASTtree.ASTdata data1000 = tree.new ASTdata(d1000,1000);
//		
//		tree.root.leftnode = for10;
//		for10.rightnode = read20;
//		read20.leftnode = next30;
//		for10.leftnode = let40;
//		let40.leftnode = def50;
//		def50.leftnode = let55;
//		let55.leftnode = print60;
//		print60.leftnode = data1000;
		
		// for 1 -> 100 print i*i*i
		ASTtree.printStruct ps0 = tree.new printStruct("I*I*I",';');
		ArrayList<ASTtree.printStruct> aps0 = new ArrayList<ASTtree.printStruct>();
		aps0.add(ps0);
		
		ASTtree.ASTfor for10 = tree.new ASTfor("I","1","100",1.0,10);
		ASTtree.ASTprint pr20 = tree.new ASTprint(aps0,20);
		ASTtree.ASTnext next30 = tree.new ASTnext("I",20);
		ASTtree.ASTend end40 = tree.new ASTend(40);
		
		tree.root.leftnode = for10;
		for10.rightnode = pr20;
		pr20.leftnode = next30;
		for10.leftnode = end40;
		
		// The test program from http://www.cs.bris.ac.uk/~dave/basic.pdf
//		ArrayList<String> vars = new ArrayList<String>();
//		ArrayList<String> vars30 = new ArrayList<String>();
//		ArrayList<Double> data70 = new ArrayList<Double>();
//		ArrayList<Double> data80 = new ArrayList<Double>();
//		ArrayList<Double> data85 = new ArrayList<Double>();
//		ArrayList<ASTtree.printStruct> pr55 = new ArrayList<ASTtree.printStruct>();
//		ArrayList<ASTtree.printStruct> pr95 = new ArrayList<ASTtree.printStruct>();
//		ArrayList<ASTtree.printStruct> pr97 = new ArrayList<ASTtree.printStruct>();
//		vars.add("A1");
//		vars.add("A2");
//		vars.add("A3");
//		vars.add("A4");
//		vars30.add("B1");
//		vars30.add("B2");
//		vars30.add("X(4)");
//		data70.add(1.0);
//		data70.add(2.0);
//		data70.add(4.0);
//		data80.add(2.0);
//		data80.add(-7.0);
//		data80.add(5.0);
//		data85.add(1.0);
//		data85.add(3.0);
//		data85.add(4.0);
//		data85.add(-7.0);
//		ASTtree.printStruct ps0 = tree.new printStruct("\"X1 = \" X1",',');
//		ASTtree.printStruct ps1 = tree.new printStruct("\"X2 = \" X2",',');
//		ASTtree.printStruct ps01 = tree.new printStruct("\"X(0) = \" X(0) \" X(1) = \" X(1) \" X(2) = \" X(2) \" X(3) = \" X(3)" ,' ');
//		ASTtree.printStruct ps2 = tree.new printStruct("I",',');
//		ASTtree.printStruct ps3 = tree.new printStruct("J",',');
//		pr55.add(ps0);
//		pr55.add(ps1);
//		pr55.add(ps01);
//		pr95.add(ps2);
//		pr97.add(ps3);
//		
//		ASTtree.ASTread read10 = tree.new ASTread(vars, 10);
//		ASTtree.ASTlet let15 = tree.new ASTlet("D","A1*A4-A3*A2",15);
//		ASTtree.ASTif if20 = tree.new ASTif("D = 0", 65, 20);
//		ASTtree.ASTread read30 = tree.new ASTread(vars30,30);
//		ASTtree.ASTlet let37 = tree.new ASTlet("X1","(B1^A4-B2*A2)/D",37);
//		ASTtree.ASTlet let42 = tree.new ASTlet("X2","(A1*B2-A3*B1)/D",42);
//		ASTtree.ASTprint print55 = tree.new ASTprint(pr55,55);
//		ASTtree.ASTgoto goto60 = tree.new ASTgoto(30,60);
//		ASTtree.ASTprint print65 = tree.new ASTprint(pr55,65);
//		ASTtree.ASTdata d70 = tree.new ASTdata(data70,70);
//		ASTtree.ASTdata d80 = tree.new ASTdata(data80,80);
//		ASTtree.ASTdata d85 = tree.new ASTdata(data85,85);
//		ASTtree.ASTfor for90 = tree.new ASTfor("I","0","5",1.0,90);
//		ASTtree.ASTprint print95 = tree.new ASTprint(pr95,95);
//		ASTtree.ASTfor for96 = tree.new ASTfor("J","0","COS(0)",1.0,96);
//		ASTtree.ASTprint print97 = tree.new ASTprint(pr97,97);
//		ASTtree.ASTnext next98 = tree.new ASTnext("J",98);
//		ASTtree.ASTnext next100 = tree.new ASTnext("I",100);
//		ASTtree.ASTprint print105 = tree.new ASTprint(pr55,105);
//		
//		tree.root.leftnode = read10;
//		read10.leftnode = let15;
//		let15.leftnode = if20;
//		if20.leftnode = read30;
//		read30.leftnode = let37;
//		let37.leftnode = let42;
//		let42.leftnode = print55;
//		print55.leftnode = d70;
////		print55.leftnode = goto60;
////		goto60.leftnode = print65;
////		print65.leftnode = d70;
//		d70.leftnode = d80;
//		d80.leftnode = d85;
//		d85.leftnode = for90;
//		for90.rightnode = print95;
//		print95.leftnode = for96;
//		for96.leftnode = next100;
//		for96.rightnode = print97;
//		print97.leftnode = next98;
//		for90.leftnode = print105;
		
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
