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
	GUI gui;
	public class GUI extends JFrame implements DocumentListener, ActionListener {
		//============================================== instance variables
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

		//====================================================== constructor
		public GUI() {

			//... Set textarea's initial text, scrolling, and border.
//			textLines.add("10 FOR X = 1 TO 100");
//			textLines.add("20 PRINT X, SQR(X),");
//			textLines.add("30 NEXT X");
//			textLines.add("40 END");
			
//			textLines.add("10 READ A1, A2, A3, A4");
//			textLines.add("15 LET D = A1*A4-A3*A2");
//			textLines.add("20 IF D = 0 THEN 65");
//			textLines.add("30 READ B1, B2");
//			textLines.add("37 LET X1 = (B1*A4-B2*A2)/D");
//			textLines.add("42 LET X2= (A1*B2-A3*B1)/D");
//			textLines.add("55 PRINT X1, X2");
//			textLines.add("60 GO TO 30");
//			textLines.add("65 PRINT \"NO UNIQUE SOLUTION\"");
//			textLines.add("70 DATA 1, 2, 4");
//			textLines.add("80 DATA 2, -7, 5");
//			textLines.add("85 DATA 1, 3, 4, -7");
//			textLines.add("90 END");
			
//			textLines.add("10 LET X = 0");
//			textLines.add("20 LET X = X + 1");
//			textLines.add("30 PRINT X, SQR(X)");
//			textLines.add("40 IF X <= 100 THEN 20");
//			textLines.add("50 END");
			
//			textLines.add("10 READ A, B");
//			textLines.add("20 PRINT \"FIRST NO. =\" A, \"SECOND NO. =\"B");
//			textLines.add("30 DATA 2.3, -3.13");
//			textLines.add("40 END");
			
//			textLines.add("10 FOR I = 1 TO 12 STEP 0.5");
//			textLines.add("20 PRINT I,");
//			textLines.add("30 NEXT I");
//			textLines.add("40 END");
			
//			textLines.add("10 FOR N = 1 TO 7");
//			textLines.add("15 PRINT \"N = \"N");
//			textLines.add("20 FOR I = 1 TO N");
//			textLines.add("30 PRINT I^N,");
//			textLines.add("40 NEXT I");
//			textLines.add("50 PRINT   ");
//			textLines.add("60 PRINT");
//			textLines.add("70 NEXT N");
//			textLines.add("80 END");
			
//			textLines.add("10 FOR I = 1 TO 100");
//			textLines.add("20 PRINT I*I*I;");
//			textLines.add("30 NEXT I");
//			textLines.add("40 END");
			
//			textLines.add("10 FOR I = 1 TO 3");
//			textLines.add("20 READ P(I)");
//			textLines.add("25 PRINT I");
//			textLines.add("30 NEXT I");
//			textLines.add("40 FOR K = 1 TO 3");
//			textLines.add("60 READ S(K)");
//			textLines.add("80 NEXT K");
//			textLines.add("90 FOR J = 1 TO 3");
//			textLines.add("100 FOR L = 1 TO 3");
//			textLines.add("105 LET B3 = P(L)");
//			textLines.add("110 PRINT B3");
//			textLines.add("120 NEXT L");
//			textLines.add("130 NEXT J");
//			textLines.add("135 DATA 1, 2, 3, 4, 5, 6, 7, 8");
//			textLines.add("140 END");
			
//			textLines.add("10 FOR I = 1 TO 20");
//			textLines.add("20 PRINT INT(10*RND(X));");
//			textLines.add("30 NEXT I");
//			textLines.add("40 END");
			
//			textLines.add("10 DEF FNF(Z) = SIN(Z*P)");
//			textLines.add("20 LET P = 3.13159265/180");
//			textLines.add("30 FOR X = 0 TO 90");
//			textLines.add("40 PRINT X, FNF(X)");
//			textLines.add("50 NEXT X");
//			textLines.add("60 END");
			
//			textLines.add("10 LET X = 1");
//			textLines.add("20 PRINT X");
//			textLines.add("30 GOSUB 100");
//			textLines.add("40 PRINT X");
//			textLines.add("50 END");
//			textLines.add("100 LET X = X*3");
//			textLines.add("110 RETURN");
			
			textLines.add("10 PRINT \"A\", \"B\", \"C\", \"GCD\"");
			textLines.add("20 READ A, B, C");
			textLines.add("30 LET X = A");
			textLines.add("40 LET Y = B");
			textLines.add("50 GOSUB 200");
			textLines.add("60 LET X = G");
			textLines.add("70 LET Y = C");
			textLines.add("80 GOSUB 200");
			textLines.add("90 PRINT A, B, C, G");
			textLines.add("100 GO TO 20");
			textLines.add("110 DATA 60, 90, 120");
			textLines.add("120 DATA 38456, 64872, 98765");
			textLines.add("130 DATA 32, 384, 72");
			textLines.add("200 LET Q = INT(X/Y)");
			textLines.add("210 LET R = X-Q*Y");
			textLines.add("220 IF R = 0.0 THEN 300");
			textLines.add("230 LET X = Y");
			textLines.add("240 LET Y = R");
			textLines.add("250 GO TO 200");
			textLines.add("300 LET G = Y");
			textLines.add("310 RETURN");
			textLines.add("999 END");
			
//			textLines.add("");
//			textLines.add("");
			

			textArea.setText(vectorToString(textLines));
			textArea.setColumns(75);
			textArea.setLineWrap(true);
			textArea.setRows(20);
			textArea.setWrapStyleWord(true);
			textArea.setEditable(false);
			JScrollPane scrollingArea = new JScrollPane(textArea);

			hilit = new DefaultHighlighter();
			painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
			textArea.setHighlighter(hilit);
			Font font = new Font("Courier New", 0, 12);
	        textArea.setFont(font);
			entryBg = entryBar.getBackground();
			entryBar.getDocument().addDocumentListener(this);

			//... Get the content pane, set layout, add to center
			JPanel content = new JPanel();
			content.setLayout(new BorderLayout());
			content.add(scrollingArea, BorderLayout.PAGE_START);
			content.add(entryBar, BorderLayout.CENTER);
			content.add(statusText, BorderLayout.SOUTH);
			content.add(listProgram, BorderLayout.BEFORE_LINE_BEGINS);
			content.add(runProgram, BorderLayout.AFTER_LINE_ENDS);
			statusText.setHorizontalAlignment(getX()/2);
			//... Set window characteristics.
			this.setContentPane(content);
			this.setTitle("TextAreaDemo B");
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.pack();

			listProgram.addActionListener(this);
			runProgram.addActionListener(this);
			listProgram.setToolTipText("Click this button to list the program");
			runProgram.setToolTipText("Click this button to run the program");
			listProgram.setMnemonic(KeyEvent.VK_M);
			runProgram.setMnemonic(KeyEvent.VK_N);
			listProgram.setActionCommand("LISTPROGRAM");
			runProgram.setActionCommand("RUNPROGRAM");

			InputMap im = entryBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			ActionMap am = entryBar.getActionMap();
			im.put(KeyStroke.getKeyStroke("ENTER"), ENTER);
			am.put(ENTER, new EnterText());
		}

		class EnterText extends AbstractAction {
			public void actionPerformed(ActionEvent ev) {
				if (checkText()) {
					textLines.add(entryBar.getText());
					textLines = sortVectStrings(textLines);
					textArea.setText(vectorToString(textLines));
					//mainText = _resultArea.getText();
					entryBar.setText("");
					//inputBar.setBackground(entryBg);
				}
				else {
					statusText.setText("Incorrect line above: try again");
				}
			}
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
						System.out.println("Found identical number\n");
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
						System.out.println("identicalLine: " + i);
						vect.removeElementAt(i);
						identicalLine = "";
					}
				}
			}
			return vect;
		}

		public boolean checkText() {
			String inputText = entryBar.getText();


			boolean correctData = true;
			boolean hasLineNum = checkLineNum(inputText);
			boolean hasFuncName = checkFuncName(inputText);

			System.out.println(hasLineNum + " " + hasFuncName);
			hilit.removeAllHighlights();

			if (hasLineNum && hasFuncName && !textArea.getText().isEmpty()) {   // match found
				int end = inputText.length();
				//hilit.addHighlight(index, end, painter);
				textArea.setCaretPosition(end);
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
			else if(!textArea.getText().isEmpty()){
				entryBar.setBackground(ERROR_COLOR);
				correctData = false;
			}
			else {
				entryBar.setBackground(ERROR_COLOR);
				correctData = false;
			}
			return correctData;
		}

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
					System.out.println(i);
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
			String funcNames = "DATA,DEF,END,FOR,GO,GOTO,GUSUB,IF," +
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
			if (spaceIndex1 >= 0 && 
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

		//============================================================= main
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

		public void actionPerformed(ActionEvent e) {
			if ("LISTPROGRAM".equals(e.getActionCommand())) {
				System.out.println("ListProgram clicked");
				ArrayList<String> program_ = saveToAST(textLines);
				Vector<String> programVector = new Vector<String>();
				for(int i = 0; i < program_.size(); i++){
					programVector.add(program_.get(i));
				}
				textArea.setText(vectorToString(programVector));
			}
			else {
				saveToAST(textLines);
				System.out.println("RunProgram clicked");
				Vector<String> textLines2 = runFromAST();
				
				textArea.setText(vectorToString(textLines2));
				runFromAST();
			}
		}
	}
	
	public Interface() {
		super();
		gui = new GUI();
		gui.main();
	}
	
	public void setText(Vector vect) {
		gui.textLines = vect; 
		//gui.main();
		//gui.textArea.setText("Hello");
		//gui.repaint();
	}
}
