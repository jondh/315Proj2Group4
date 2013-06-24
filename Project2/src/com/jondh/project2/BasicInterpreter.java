package com.jondh.project2;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jondh.project2.ASTtree.ASTif;
import com.jondh.project2.ASTtree.printStruct;

public class BasicInterpreter {

	ASTtree tree = new ASTtree();
	ASTtree.ASTnode topRoot = tree.new ASTnode();
	ASTtree.ASTnode root = tree.new ASTnode();
	LineType lineType;
	int lineNum = 0;
	String lineText;
	boolean forLoop = false;

	public static void main(String[] args) {
		new Interface();
		//ASTtree astTree = new ASTtree();
		/*
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
		 */
	}

	public enum LineType {
		DATA, DEF, DIM, END, FOR, GO, GOSUB, IF, LET, 
		NEXT, PRINT, READ, RETURN, STOP
	}

	protected void saveToAST(Vector<String> codeLines) {
		LineType lineType = LineType.END;
		tree.root = root;
		topRoot = root;
		for (int iter = 0; iter < codeLines.size(); ++iter) {
			lineText = codeLines.get(iter);
			findLineType();

			System.out.println("Line Type: " + lineType);
			readToAST();
		}
		ArrayList<String> ASTtext = tree.print();
		System.out.println("AST: ");
		for(int i = 0; i < ASTtext.size(); ++i) {
			System.out.println(ASTtext.get(i));
		}
		return;
	}

	protected void runFromAST() {
		topRoot.eval();
		return;
	}

	private void readToAST() {
		//ASTtree.ASTnode newRoot = tree.new ASTnode();
		if (lineType == LineType.IF) readIf();
		else if (lineType == LineType.DIM) readDim();
		else if (lineType == LineType.FOR) readFor();
		else if (lineType == LineType.LET) readLet();
		else if (lineType == LineType.DEF) readDef();
		else if (lineType == LineType.NEXT) readNext();
		else if (lineType == LineType.PRINT) readPrint();
		else if (lineType == LineType.GO || lineType == LineType.GOSUB) 
			readGoToGoSub();
		else if (lineType == LineType.DATA || (lineType == LineType.READ))
			readDataRead();
		else if (lineType == LineType.END || lineType == LineType.RETURN)
			readEndReturn();
		/*if (lineType == LineType.FOR) {
			root.rightnode = newRoot;
		}
		else root.leftnode = newRoot;
		root = newRoot;*/
	}

	private void readDataRead() {
		// TODO Auto-generated method stub
	}

	private void readIf() {
		String eval = "";
		String lineNumStr = "";
		int goToNum = 0;
		int thenIndex = lineText.indexOf("THEN");
		if (thenIndex != -1) {
			eval = lineText.substring(0, thenIndex).trim();
			lineNumStr = lineText.substring(thenIndex + 4, lineText.length());
			goToNum = Integer.valueOf(lineNumStr.trim());
			System.out.println("eval: " + eval + " goto: " + goToNum);
		}
		ASTtree.ASTif ifState = tree.new ASTif(eval, 65, lineNum);

	}

	private void readDim() {
		// TODO Auto-generated method stub

	}

	private void readFor() {
		//String nVar, String nIni, String toCond, Double step, int lnNum
		
		ASTtree.ASTfor forState = tree.new ASTfor("X", "1", "100", 1.0, lineNum);
		root.leftnode = forState;
		root = forState;
		forLoop = true;
	}

	private void readLet() {
		// TODO Auto-generated method stub
		//ASTfor(String nVar, String nIni, String toCond, Double step, int lnNum){
		int stepIndex = lineText.indexOf("STEP");
		String stepStr = "";
		double stepNum = 0.0;
		
		if (stepIndex > 0) {
			stepStr = lineText.substring(stepIndex, lineText.length());
			lineText = lineText.substring(0, stepIndex-1);
			stepNum = Double.valueOf(stepStr);
		}
		
		ASTtree.ASTlet letState = tree.new ASTlet("X1","(B1*A4-B2*A2)/D",37);
		root.leftnode = letState;
		root = letState;
	}

	private void readDef() {
		// TODO Auto-generated method stub

	}

	private void readNext() {
		// TODO Auto-generated method stub
		ASTtree.ASTnext nextState = tree.new ASTnext("X", lineNum);
		root.leftnode = nextState;
		root = nextState;
	}

	private void readGoToGoSub() {
		// TODO Auto-generated method stub
		if (lineType == LineType.GO) {
			ASTtree.ASTgoto goToState = tree.new ASTgoto(30,60);
			root.leftnode = goToState;
			root = goToState;
		}
		else {
			ASTtree.ASTgosub goSubState = tree.new ASTgosub(10, lineNum);
		}
	}

	private void readPrint() {
		// TODO Auto-generated method stub
		if (lineType == LineType.PRINT) {
			ArrayList<printStruct> pr55 = new ArrayList<printStruct>();
			printStruct elem = tree.new printStruct("SQR(X)", 'R');
			pr55.add(elem);
			ASTtree.ASTprint printState = tree.new ASTprint(pr55,55);
			root.leftnode = printState;
			root = printState;
		}
		//ASTtree.ASTread read30 = tree.new ASTread(vars30,30);
	}

	private void readEndReturn() {
		// TODO Auto-generated method stub

	}

	private void findLineType() {
		String lineNumStr = " ";
		String lineTypeStr = " ";
		int spaceIndex = lineText.indexOf(" ");
		lineNumStr = lineText.substring(0, spaceIndex);
		lineNum = Integer.valueOf(lineNumStr);
		System.out.println("Line Number: " + lineNum);
		lineText = lineText.substring(spaceIndex + 1);

		spaceIndex = lineText.indexOf(" ");
		if (spaceIndex == -1) 
			lineTypeStr = lineText;
		else {
			lineTypeStr = lineText.substring(0, spaceIndex);
		}

		lineType = LineType.valueOf(lineTypeStr.trim());
		return;
	}

}
