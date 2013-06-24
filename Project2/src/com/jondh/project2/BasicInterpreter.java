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
		DATA, DEF, DIM, END, FOR, GO, GOSUB, GOTO, IF, LET, 
		NEXT, PRINT, READ, RETURN, STOP
	}

	protected void saveToAST(Vector<String> codeLines) {
		LineType lineType = LineType.DATA;
		tree.root = root;
		topRoot = root;
		for (int iter = 0; iter < codeLines.size(); ++iter) {
			lineText = codeLines.get(iter);
			lineType = findLineType();

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
		//topRoot.run();
		return;
	}

	private void readToAST() {
		//ASTtree.ASTnode newRoot = tree.new ASTnode();
		System.out.println("LineType: "+lineType);
		if (lineType == LineType.IF) readIf();
		else if (lineType == LineType.DIM) readDim();
		else if (lineType == LineType.FOR) readFor();
		else if (lineType == LineType.LET) readLet();
		else if (lineType == LineType.DEF) readDef();
		else if (lineType == LineType.NEXT) readNext();
		else if (lineType == LineType.PRINT) readPrint();
		else if (lineType == LineType.DATA) readData();
		else if (lineType == LineType.READ) readRead();
		else if (lineType == LineType.GO || lineType == LineType.GOSUB) 
			readGoToGoSub();
		else if (lineType == LineType.END || lineType == LineType.RETURN)
			readEndReturn();
		/*if (lineType == LineType.FOR) {
			root.rightnode = newRoot;
		}
		else root.leftnode = newRoot;
		root = newRoot;*/
	}

	private void readData() {
		ArrayList<Double> dataList = new ArrayList<Double>();
		String dataStr = "";
		double dataNum = 0.0;
		int commaIndex = lineText.indexOf(",");
		
		while (commaIndex != -1 && commaIndex < lineText.length()) {
			dataStr = lineText.substring(0, commaIndex).trim();
			lineText = lineText.substring(commaIndex+1).trim();
			dataNum = Double.valueOf(dataStr);
			dataList.add(dataNum);
			commaIndex = lineText.indexOf(",");
		}

		dataNum = Double.valueOf(lineText.trim());
		dataList.add(dataNum);

		ASTtree.ASTdata dataState = tree.new ASTdata(dataList,lineNum);
		root.leftnode = dataState;
		root = dataState;
	}
	
	private void readRead() {
		ArrayList<String> readList = new ArrayList<String>();
		String readStr = "";
		int commaIndex = lineText.indexOf(",");
		
		while (commaIndex != -1 && commaIndex < lineText.length()) {
			readStr = lineText.substring(0, commaIndex).trim();
			lineText = lineText.substring(commaIndex+1).trim();
			readList.add(readStr);
			commaIndex = lineText.indexOf(",");
		}

		readList.add(lineText.trim());
		ASTtree.ASTread readState = tree.new ASTread(readList,lineNum);
		root.leftnode = readState;
		root = readState;
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
		int stepIndex = lineText.indexOf("STEP");
		int equalIndex = lineText.indexOf("=");
		int toIndex = lineText.indexOf("TO");
		String stepStr = "";
		String varStr = "";
		String numStr = "";
		String toStr = "";
		double stepNum = 1.0;

		if (stepIndex > 0) {
			stepStr = lineText.substring(stepIndex, lineText.length()).trim();
			lineText = lineText.substring(0, stepIndex-1).trim();
			stepNum = Double.valueOf(stepStr);
		}
		varStr = lineText.substring(0, equalIndex-1).trim();
		numStr = lineText.substring(equalIndex+1, toIndex-1).trim();
		toStr = lineText.substring(toIndex+2).trim();

		System.out.println("FOR "+varStr+" = "+numStr+" TO "+toStr+" STEP "+stepNum);
		ASTtree.ASTfor forState = tree.new ASTfor(varStr,numStr,toStr,stepNum,lineNum);
		root.rightnode = forState;
		root = forState;
	}

	private void readLet() {
		//ASTfor(String nVar, String nIni, String toCond, Double step, int lnNum){
		int equalIndex = lineText.indexOf("=");
		String eqLeft = lineText.substring(0, equalIndex).trim();
		String eqRight = lineText.substring(0, equalIndex).trim();

		ASTtree.ASTlet letState = tree.new ASTlet(eqLeft, eqRight, lineNum);
		root.leftnode = letState;
		root = letState;
	}

	private void readDef() {
		int equalIndex = lineText.indexOf("=");
		String eqLeft = lineText.substring(0, equalIndex).trim();
		String eqRight = lineText.substring(0, equalIndex).trim();

		ASTtree.ASTdef defState = tree.new ASTdef(eqLeft, eqRight, lineNum);
		root.leftnode = defState;
		root = defState;
	}

	private void readNext() {
		ASTtree.ASTnext nextState = tree.new ASTnext(lineText, lineNum);
		root.leftnode = nextState;
		root = nextState;
	}

	private void readGoToGoSub() {
		int goTo = 0;
		if (lineType == LineType.GO || lineType == LineType.GOTO) {
			ASTtree.ASTgoto goToState = tree.new ASTgoto(30,60);
			root.leftnode = goToState;
			root = goToState;
		}
		else {
			goTo = Integer.valueOf(lineText);
			ASTtree.ASTgosub goSubState = tree.new ASTgosub(goTo, lineNum);
			root.leftnode = goSubState;
			root = goSubState;
		}
	}

	private void readPrint() {
		ArrayList<printStruct> printList = new ArrayList<printStruct>();
		printStruct elem;
		char printFormat = ' ';
		String printStr = "";
		int index = findCommaColon();

		while (index != -1 && index < lineText.length()) {
			printFormat = lineText.charAt(index);
			printStr = lineText.substring(0, index).trim();
			elem = tree.new printStruct(printStr, printFormat);
			printList.add(elem);
			lineText = lineText.substring(index+1);
			index = findCommaColon();
		}
		printFormat = testLastChar();

		lineText = lineText.trim();
		elem = tree.new printStruct(lineText, printFormat);
		printList.add(elem);
		ASTtree.ASTprint printState = tree.new ASTprint(printList, lineNum);
		root.leftnode = printState;
		root = printState;
	}

	private char testLastChar() {
		char printChar = ' ';
		if (lineText.endsWith(",")) {
			printChar = ',';
			lineText = lineText.substring(0, lineText.length()-1).trim();
		}
		else if (lineText.endsWith(";")) {
			printChar = ';';
			lineText = lineText.substring(0, lineText.length()-1).trim();
		}
		else printChar = ' ';
		return printChar;
	}

	private int findCommaColon() {
		int commaIndex = lineText.indexOf(",");
		int colonIndex = lineText.indexOf(";");
		if (commaIndex != -1 || commaIndex != -1) {
			if (commaIndex == -1) return colonIndex;
			else if (colonIndex == -1) return commaIndex;
			else if (commaIndex < colonIndex) return commaIndex;
			else return colonIndex;
		}
		else return -1;
	}

	private void readEndReturn() {
		if (lineType == LineType.END) {
			ASTtree.ASTend endState = tree.new ASTend(lineNum);
			root.leftnode = endState;
			root = endState;
		}
		else {
			ASTtree.ASTreturn returnState = tree.new ASTreturn(lineNum);
			root.leftnode = returnState;
			root = returnState;
		}
	}

	private LineType findLineType() {
		String lineNumStr = " ";
		String lineTypeStr = " ";
		int spaceIndex = lineText.indexOf(" ");
		lineNumStr = lineText.substring(0, spaceIndex);
		lineNum = Integer.valueOf(lineNumStr);
		System.out.println("Line Number: " + lineNum);
		lineText = lineText.substring(spaceIndex + 1);

		spaceIndex = lineText.indexOf(" ");
		if (spaceIndex != -1) {
			lineTypeStr = lineText.substring(0,spaceIndex);
			lineText = lineText.substring(spaceIndex).trim();
		}
		else lineTypeStr = lineText;
		lineType = LineType.valueOf(lineTypeStr.trim());
		return lineType;
	}
}
