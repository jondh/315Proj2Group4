/*
 *  AUTHOR: Matthew Kocmoud AND Jonathan Harrison
 *  LAST MODIFIED: 6/25/2013
 *  
 *  CSCE 315 SUMMER 2013
 *  PROJECT 2
 *  
 *  This class defines the GUI used by this BASIC compiler. It
 *  	gets BASIC lines and passes them onto the interpreter as
 *  	well as displaying the BASIC code and output.
 */

package com.jondh.project2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class Interface extends BasicInterpreter {

	public class GUI extends JFrame implements DocumentListener, ActionListener {
		
		JTextArea textArea = new JTextArea(10, 30);
		JTextField entryBar = new JTextField();
		JLabel statusText = new JLabel("Enter a line of BASIC code above: ");

		JButton listProgram = new JButton("List Program");
		JButton runProgram = new JButton("Run Program");
		final static String ENTER = "enter line";
		private Vector<String> textLines = new Vector<String>();

		final Highlighter hilit;
		final Highlighter.HighlightPainter painter;
		Color  HILIT_COLOR = Color.LIGHT_GRAY;
		Color  ERROR_COLOR = Color.PINK;
		final Color entryBg;
		String identicalLine = "";
		JPanel content;

		public GUI() {
			setTextButtonsContent();

			hilit = new DefaultHighlighter();
			painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
			textArea.setHighlighter(hilit);
			Font font = new Font("Courier New", 0, 12);
	        textArea.setFont(font);
			entryBg = entryBar.getBackground();
			entryBar.getDocument().addDocumentListener(this);
			statusText.setHorizontalAlignment(getX()/2);
			
			this.setContentPane(content);
			this.setTitle("TextAreaDemo B");
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.pack();

			InputMap im = entryBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			ActionMap am = entryBar.getActionMap();
			im.put(KeyStroke.getKeyStroke("ENTER"), ENTER);
			am.put(ENTER, new EnterText());
		}

		private void setTextButtonsContent() {
			textArea.setText(vectorToString(textLines));
			textArea.setColumns(75);
			textArea.setLineWrap(true);
			textArea.setRows(20);
			textArea.setWrapStyleWord(true);
			textArea.setEditable(false);
			
			JScrollPane scrollingArea = new JScrollPane(textArea);
			content = new JPanel();
			content.setLayout(new BorderLayout());
			content.add(scrollingArea, BorderLayout.PAGE_START);
			content.add(entryBar, BorderLayout.CENTER);
			content.add(statusText, BorderLayout.SOUTH);
			content.add(listProgram, BorderLayout.BEFORE_LINE_BEGINS);
			content.add(runProgram, BorderLayout.AFTER_LINE_ENDS);
			
			listProgram.addActionListener(this);
			runProgram.addActionListener(this);
			listProgram.setToolTipText("Click this to list the program");
			runProgram.setToolTipText("Click this to run the program");
			listProgram.setMnemonic(KeyEvent.VK_M);
			runProgram.setMnemonic(KeyEvent.VK_N);
			listProgram.setActionCommand("LISTPROGRAM");
			runProgram.setActionCommand("RUNPROGRAM");
		}

		class EnterText extends AbstractAction {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<String> inputLines = new ArrayList<String>();
				inputLines = pasteCode(entryBar.getText());
				for(int i = 0; i < inputLines.size(); i++){
					if (checkText(inputLines.get(i))) {
						textLines.add(inputLines.get(i));
						textLines = sortVectStrings(textLines);
						textArea.setText(vectorToString(textLines));
						entryBar.setText("");
					}
					else {
						statusText.setText("Incorrect line above: try again");
					}
				}
			}
		}
		
		/*
		 *  This function takes input code and splits it
		 *  	into individual BASIC lines. This assumes
		 *  	that all the input is correct
		 */
		private ArrayList<String> pasteCode(String str){
			// List the possible BASIC functions
			ArrayList<String> func = new ArrayList<String>();
			func.add("READ"); 
			func.add("LET");
			func.add("PRINT");
			func.add("GOSUB");
			func.add("GO TO");
			func.add("DATA");
			func.add("RETURN");
			func.add("END");
			func.add("IF");
			func.add("FOR");
			func.add("NEXT");
			func.add("DEF");
			
			/*
			 *  This function finds every position in the input where
			 *  	a BASIC function is used
			 */
			ArrayList<Integer> funcPos = new ArrayList<Integer>();
			for(int i = 0; i < func.size(); i++){
				int curPos = 0;
				while(str.indexOf(func.get(i), curPos+1) > 0){
					curPos = str.indexOf(func.get(i), curPos+1);
					funcPos.add(curPos);
				}
			}
			
			/*
			 *  This functions goes through the entire input and searches
			 *  	for a number followed by a spaced followed by two capital
			 *  	letters (ie ## LL). It then checks the position of the first
			 *  	letter with the positions found above to verify the beginning
			 *  	of a new line. It also checks for quotes and ignores what is 
			 *  	inside them
			 */
			ArrayList<Integer> newLinePos = new ArrayList<Integer>();
			boolean inQuote = false;
			for(int i = 0; i < str.length(); i++){
				int newLine = -1;
				if(isNumber(str.charAt(i))){
					newLine = i;
				}
				if(str.charAt(i) == '"' && !inQuote){
					inQuote = true;
				}
				else if(str.charAt(i) == '"' && inQuote){
					inQuote = false;
				}
				boolean iMoved = false;
				// go through all numbers at pos
				while(isNumber(str.charAt(i))){
					iMoved = true;
					i++;
					if(i > str.length()-3) break;
				}
				// if i moved, check again for quotes
				if(str.charAt(i) == '"' && !inQuote && iMoved){
					inQuote = true;
				}
				else if(str.charAt(i) == '"' && inQuote && iMoved){
					inQuote = false;
				}
				if(i > str.length()-3) break;
				if(str.charAt(i) != ' '){
					newLine = -1;
				}
				else{ // if space after number
				    i++;
					if(i > str.length() - 3) break;
					if(isLetter(str.charAt(i)) && isLetter(str.charAt(i+1))){
						if(funcPos.contains(i) && !inQuote){
							newLinePos.add(newLine);
						}
					}
					else if(str.charAt(i) == '"' && !inQuote){
						inQuote = true;
					}
					else if(str.charAt(i) == '"' && inQuote){
						inQuote = false;
					}
				}
			}
			// split input into ArrayList according to newLine positions
			ArrayList<String> lines_ = new ArrayList<String>();
			if(newLinePos.size() > 1){
				lines_.add(str.substring(0, newLinePos.get(1)-1).trim());
				
				for(int i = 2; i < newLinePos.size(); i++){
					lines_.add(str.substring(newLinePos.get(i-1)-1, newLinePos.get(i)-1).trim());
				}
				lines_.add(str.substring(newLinePos.get(newLinePos.size()-1)-1).trim());
			}
			else{
				lines_.add(str.trim());
			}
			return lines_;
		}

		private boolean isNumber(char c){
			if((c>='0' && c<='9')){
				return true;
			}
			else return false;
		}
		
		private boolean isLetter(char c){
			if((c>='A' && c<='Z')){
				return true;
			}
			else return false;
		}
		
		public String vectorToString(Vector<String> vect) {
			String vectString = "";
			for(int i = 0; i < vect.size(); i++) {
				String currentString = vect.get(i);
				if(currentString.length() > 75){
					currentString = currentString.substring(0, 74);
				}
				vectString += vect.get(i) + "\n";
			}
			return vectString;
		}

		/*
		 *  This function sorts a vector of strings accurately based on
		 *  the line number. This sorting: 2,19,21 Normal sorting: 19,2,21
		 */
		public Vector<String> sortVectStrings(Vector<String> vect) {
			Collections.sort(vect, new Comparator<String>()
					{
				public int compare(String s1, String s2) {
					String num1 = " ";
					String num2 = " ";
					int index1 = s1.indexOf(" ");
					int index2 = s2.indexOf(" ");

					num1 = s1.substring(0, index1);
					num2 = s2.substring(0, index2);

					if (num1.equalsIgnoreCase(num2)) {
						identicalLine = s2;
						statusText.setText("Deleting an identical line number");
					}
					else 
						statusText.setText("Enter a line of BASIC code above: ");
					return Integer.valueOf(num1).compareTo(Integer.valueOf(num2));
				}
					});
			if (identicalLine.length() != 0) {
				for (int i = 0; i < vect.size(); i++) {
					if (vect.elementAt(i) == identicalLine) {
						vect.removeElementAt(i);
						identicalLine = "";
					}
				}
			}
			return vect;
		}

		public boolean checkText(String line_) {
			String inputText = line_;

			boolean correctData = true;
			boolean hasLineNum = checkLineNum(inputText);
			boolean hasFuncName = checkFuncName(inputText);

			hilit.removeAllHighlights();

			if (hasLineNum && hasFuncName) {
				entryBar.setBackground(entryBg);
			}
			
			else if(hasLineNum){
				if(removeLine(inputText)){
					textLines = sortVectStrings(textLines);
					textArea.setText(vectorToString(textLines));
					entryBar.setText("");
					correctData = false;
				}
				else{
					entryBar.setBackground(ERROR_COLOR);
					correctData = false;
				}
			}
			else {
				entryBar.setBackground(ERROR_COLOR);
				correctData = false;
			}
			return correctData;
		}
		
		/*
		 *  This function takes an input string (linenumber) and
		 *  	searches for this lines number in the text, if found,
		 *  	remove the line number from the program.
		 */
		private boolean removeLine(String inNum){
			String num_ = "";
			for(int i = 0; i < inNum.length(); i++){
				if(inNum.charAt(i)>='0' && inNum.charAt(i)<='9'){
					num_ += inNum.charAt(i);
				}
				else break;
			}
			for(int i = 0; i < textLines.size(); i++){
				if(textLines.get(i).indexOf(num_) == 0){
					textLines.remove(textLines.get(i));
					return true;
				}
			}
			return false;
		}
		
		private boolean checkFuncName(String inputText) {
			boolean okFuncName = true;
			int index1 = inputText.indexOf(" ");
			String funcName = inputText.substring(index1+1);
			String funcNames = "DATA,DEF,END,FOR,GO,GOTO,GOSUB,IF," +
					"LET,NEXT,PRINT,READ,RETURN,";
			int index2 = funcName.indexOf(" ");
			int funcSize = funcName.length();
			if (index2 != -1) {
				funcName = funcName.substring(0, Math.min(index2, funcSize));
			}
			else {
				funcName = funcName.substring(0, funcSize);
			}
			funcName += ",";
			okFuncName = funcNames.contains(funcName) && checkFuncReqs(funcName, inputText);
			
			return okFuncName;
		}

		private boolean checkFuncReqs(String funcName, String inputText) {
			if (funcName.contentEquals("FOR,")) {
				return inputText.contains("=") && inputText.contains("TO");
			}
			else if (funcName.contentEquals("IF,")) {
				return inputText.contains("THEN");
			}
			else if (funcName.contentEquals("GO,")) {
				return inputText.contains("TO");
			}
			else if (funcName.contentEquals("LET,")) {
				return inputText.contains("=");
			}
			return true;
		}

		private boolean checkLineNum(String inputText) {
			int spaceIndex1 = inputText.indexOf(" ");
			if (spaceIndex1 > 0 && 
					Character.isDigit(inputText.charAt(spaceIndex1 - 1)) &&
					Character.isLetter(inputText.charAt(spaceIndex1 + 1))) {
				return true;
			}
			for(int i = 0; i < inputText.length(); i++){
				if(inputText.charAt(i)<'0' || inputText.charAt(i)>'9'){
					return false;
				}
			}
			return true;
		}

		public void main() {
			JFrame win = new GUI();
			win.setResizable(false);
			win.setVisible(true);
		}

		public void changedUpdate(DocumentEvent e) {
		}

		public void insertUpdate(DocumentEvent e) {
		}

		public void removeUpdate(DocumentEvent e) {
		}

		/*
		 * This function details what is done when the buttons 'List Program'
		 * or 'Run Program' are pressed.
		 */
		public void actionPerformed(ActionEvent e) {
			if ("LISTPROGRAM".equals(e.getActionCommand())) {
				ArrayList<String> program_ = saveToAST(textLines);
				Vector<String> programVector = new Vector<String>();
				
				for(int i = 0; i < program_.size(); i++){
					programVector.add(program_.get(i));
				}
				textArea.setText(vectorToString(programVector));
			}
			else { // 'Run Program' pressed
				saveToAST(textLines); // be sure AST is non-empty
				Vector<String> textLinesCopy = runFromAST();
				
				textArea.setText(vectorToString(textLinesCopy));
				runFromAST();
			}
		}
	}
	
	public Interface() {
		super();
		GUI gui = new GUI();
		gui.main();
	}
}
