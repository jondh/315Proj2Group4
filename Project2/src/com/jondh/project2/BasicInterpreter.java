/*
 *  AUTHOR: Matthew Kocmoud AND Jonathan Harrison
 *  LAST MODIFIED: 6/25/2013
 *  
 *  CSCE 315 SUMMER 2013
 *  PROJECT 2
 *  
 *  This class interprets the input data and puts it
 *  	into the AST with the appropriate structure.
 */

package com.jondh.project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.jondh.project2.ASTtree.printStruct;

public class BasicInterpreter {

	ASTtree tree;
	ASTtree.ASTnode topRoot; 
	ASTtree.ASTnode forRoot;
	ASTtree.ASTnode root;
	Map<String, ASTtree.ASTnode> forNodeMap = 
			new HashMap<String, ASTtree.ASTnode>();
	LineType lineType;
	static Interface gui;
	int lineNum = 0;
	String lineText;
	boolean forLoop = false;

	public static void main(String[] args) {
		gui = new Interface();
	}

	public enum LineType {
		DATA, DEF, DIM, END, FOR, GO, GOSUB, GOTO, IF, LET, 
		NEXT, PRINT, READ, RETURN, STOP
	}

	protected ArrayList<String> saveToAST(Vector<String> codeLines) {
		LineType lineType = LineType.DATA;
		
		tree = new ASTtree();
		topRoot = tree.new ASTnode();
		root = tree.new ASTnode();
		forRoot = tree.new ASTnode();
		root = tree.root;
		
		for (int iter = 0; iter < codeLines.size(); ++iter) {
			lineText = codeLines.get(iter);
			lineType = findLineType();

			readToAST();
		}
		ArrayList<String> ASTtext = tree.print();
		return ASTtext;
	}

	protected Vector<String> runFromAST() {		
		ArrayList<String> output = tree.run();
		Vector<String> textLines = new Vector<String>();
		
		for (int i = 0; i < output.size(); ++i) {
			textLines.add(output.get(i));
		}
		return textLines;
	}

	private void readToAST() {
		if (lineType == LineType.DATA) readData();
		else if (lineType == LineType.DEF) readDef();
		else if (lineType == LineType.FOR) readFor();
		else if (lineType == LineType.IF) readIf();
		else if (lineType == LineType.LET) readLet();
		else if (lineType == LineType.NEXT) readNext();
		else if (lineType == LineType.PRINT) readPrint();
		else if (lineType == LineType.READ) readRead();
		else if (lineType == LineType.GO || lineType == LineType.GOSUB) 
			readGoToGoSub();
		else if (lineType == LineType.END || lineType == LineType.RETURN)
			readEndReturn();
	}

	/*
	 * These functions extract data needed to create AST nodes
	 */
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
		if (forLoop) {
			root.rightnode = dataState;
			forLoop = false;
		}
		else root.leftnode = dataState;
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
		if (forLoop) {
			root.rightnode = readState;
			forLoop = false;
		}
		else root.leftnode = readState;
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
		}
		ASTtree.ASTif ifState = tree.new ASTif(eval, goToNum, lineNum);
		if (forLoop) {
			root.rightnode = ifState;
			forLoop = false;
		}
		else root.leftnode = ifState;
		root = ifState;
	}

	private void readFor() {
		int stepInd = lineText.indexOf("STEP");
		int equalInd = lineText.indexOf("=");
		int toIndex = lineText.indexOf("TO");
		String stepStr = "";
		String varStr = "";
		String numStr = "";
		String toStr = "";
		double stepNum = 1.0;

		if (stepInd > 0) {
			stepStr = lineText.substring(stepInd+4, lineText.length()).trim();
			lineText = lineText.substring(0, stepInd-1).trim();
			stepNum = Double.valueOf(stepStr);
		}
		
		varStr = lineText.substring(0, equalInd-1).trim();
		numStr = lineText.substring(equalInd+1, toIndex-1).trim();
		toStr = lineText.substring(toIndex+2).trim();
		ASTtree.ASTfor forState = tree.new ASTfor(varStr,numStr,toStr,stepNum,lineNum);
		forNodeMap.put(varStr, forState);
		if (forLoop) {
			root.rightnode = forState;
			forLoop = false;
		}
		else root.leftnode = forState;
		root = forState;
		forLoop = true;
	}

	private void readLet() {
		int equalIndex = lineText.indexOf("=");
		String eqLeft = lineText.substring(0, equalIndex).trim();
		String eqRight = lineText.substring(equalIndex+1, lineText.length()).trim();

		ASTtree.ASTlet letState = tree.new ASTlet(eqLeft, eqRight, lineNum);
		if (forLoop) {
			root.rightnode = letState;
			forLoop = false;
		}
		else root.leftnode = letState;
		root = letState;
	}

	private void readDef() {
		int equalIndex = lineText.indexOf("=");
		String eqLeft = lineText.substring(0, equalIndex).trim();
		String eqRight = lineText.substring(equalIndex+1, lineText.length()).trim();
		ASTtree.ASTdef defState = tree.new ASTdef(eqLeft, eqRight, lineNum);
		if (forLoop) {
			root.rightnode = defState;
			forLoop = false;
		}
		else root.leftnode = defState;
		root = defState;
	}

	private void readNext() {
		ASTtree.ASTnext nextState = tree.new ASTnext(lineText, lineNum);
		if (forLoop) {
			root.rightnode = nextState;
			forLoop = false;
		}
		else root.leftnode = nextState;
		if(forNodeMap.containsKey(lineText)){
			root = forNodeMap.get(lineText);
		}
		forLoop = false;
	}

	private void readGoToGoSub() {
		int goTo = 0;
		if (lineType == LineType.GO || lineType == LineType.GOTO) {
			goTo = Integer.valueOf(lineText.substring(2).trim());
			ASTtree.ASTgoto goToState = tree.new ASTgoto(goTo, lineNum);
			if (forLoop) {
				root.rightnode = goToState;
				forLoop = false;
			}
			else root.leftnode = goToState;
			root = goToState;
		}
		else {
			goTo = Integer.valueOf(lineText);
			ASTtree.ASTgosub goSubState = tree.new ASTgosub(goTo, lineNum);
			if (forLoop) {
				root.rightnode = goSubState;
				forLoop = false;
			}
			else root.leftnode = goSubState;
			root = goSubState;
		}
	}

	private void readPrint() {
		ArrayList<printStruct> printList = new ArrayList<printStruct>();
		printStruct elem;
		char printFormat = ' ';
		String printStr = "";
		
		if(lineText.equals("PRINT") || lineText.trim().length() == 0){
			elem = tree.new printStruct(printStr, printFormat);
			printList.add(elem);
		}
		else{
			int index = findCommaColon();
	
			while (index != -1 && index < lineText.length()) {
				printFormat = lineText.charAt(index);
				printStr = lineText.substring(0, index).trim();
				elem = tree.new printStruct(printStr, printFormat);
				printList.add(elem);
				lineText = lineText.substring(index+1);
				index = findCommaColon();
			}
			if(index == -1 && index < lineText.length()-1){
				printFormat = testLastChar();
				lineText = lineText.trim();
				elem = tree.new printStruct(lineText, printFormat);
				printList.add(elem);
			}
		}
		ASTtree.ASTprint printState = tree.new ASTprint(printList, lineNum);
		if (forLoop) {
			root.rightnode = printState;
			forLoop = false;
		}
		else root.leftnode = printState;
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

	/*
	 * This function returns the index of a comma or colon, whichever first,
	 * or -1 if neither found
	 */
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
			if (forLoop) {
				root.rightnode = endState;
				forLoop = false;
			}
			else root.leftnode = endState;
			root = endState;
		}
		else {
			ASTtree.ASTreturn returnState = tree.new ASTreturn(lineNum);
			if (forLoop) {
				root.rightnode = returnState;
				forLoop = false;
			}
			else root.leftnode = returnState;
			root = returnState;
		}
	}

	/*
	 * This function extracts the line type, while eating the type name, to
	 * save effort in future extractions
	 */
	private LineType findLineType() {
		String lineNumStr = " ";
		String lineTypeStr = " ";
		int spaceIndex = lineText.indexOf(" ");
		lineNumStr = lineText.substring(0, spaceIndex);
		lineNum = Integer.valueOf(lineNumStr);
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
