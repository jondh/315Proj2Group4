/*
 *  AUTHOR: Matthew Kocmoud
 *  LAST MODIFIED: 6/24/2013
 */
package com.jondh.project2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
		String defaultStatus = "Enter a line of BASIC code above: ";
		JLabel statusText = new JLabel(defaultStatus);

		JButton listProgram = new JButton("List Program");
		JButton runProgram = new JButton("Run Program");
		JPanel content;
		final static String ENTER = "enter line";
		private Vector<String> textLines = new Vector<String>();

		final Highlighter hilit;
		final Highlighter.HighlightPainter painter;
		Color  GCOLOR = Color.LIGHT_GRAY;
		Color  ERROR_COLOR = Color.PINK;
		final Color entryBg;
		String identicalLine = "";
		
		boolean pushedListProgram = false;

		public GUI() {			
			setTextValues();
			//set red error hilit
			hilit = new DefaultHighlighter();
			painter = new DefaultHighlighter.DefaultHighlightPainter(GCOLOR);
			textArea.setHighlighter(hilit);
			entryBg = entryBar.getBackground();
			entryBar.getDocument().addDocumentListener(this);
			
			setContentButtons();
			statusText.setHorizontalAlignment(getX()/2); // align to middle
			// set window characteristics.
			this.setContentPane(content);
			this.setTitle("Basic Interpreter");
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.pack();
			// listen for entered text
			InputMap im = 
					entryBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			ActionMap am = entryBar.getActionMap();
			im.put(KeyStroke.getKeyStroke("ENTER"), ENTER);
			am.put(ENTER, new EnterText());
		}
		
		public void main() {
			JFrame win = new GUI();
			win.setVisible(true);
		}
		
		// Get the content pane, define buttons
		private void setContentButtons() { 
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
			listProgram.setToolTipText("Click to list the program");
			runProgram.setToolTipText("Click to run the program");
			listProgram.setMnemonic(KeyEvent.VK_M);
			runProgram.setMnemonic(KeyEvent.VK_N);
			listProgram.setActionCommand("LISTPROGRAM");
			runProgram.setActionCommand("RUNPROGRAM");
		}

		// define main scrollable text box
		private void setTextValues() {
			textArea.setText(vectorToString(textLines));
			textArea.setColumns(40);
			textArea.setLineWrap(true);
			textArea.setRows(20);
			textArea.setWrapStyleWord(true);
			textArea.setEditable(false);
		}
		
		// when ENTER pressed, add to main text area
		class EnterText extends AbstractAction {
			public void actionPerformed(ActionEvent ev) {
				if (checkText()) {
					textLines.add(entryBar.getText());
					textLines = sortVectStrings(textLines);
					textArea.setText(vectorToString(textLines));
					entryBar.setText("");
				}
				else {
					statusText.setText("Incorrect line above: try again");
				}
			}
		}

		// a set way to outputting a vector
		public String vectorToString(Vector<String> vect) {
			String vectString = "";
			for(int i = 0; i < vect.size(); i++) {
				vectString += vect.get(i) + "\n";
			}
			return vectString;
		}

		// sort vectors using whole line number, not just first digit
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
					if (num1.equalsIgnoreCase(num2)) { // if same line number
						identicalLine = s1; // save for future elimination
						statusText.setText("Deleting a same line number");
					}
					else 
						statusText.setText(defaultStatus);
					return Integer.valueOf(num1)
							.compareTo(Integer.valueOf(num2));
				}
					});
			// remove duplicate line number, leave newer
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
		
		// make sure lines have line numbers, function names, requirements...
		public boolean checkText() {
			String inputText = entryBar.getText();
			boolean correctData = true;
			boolean hasLineNum = checkLineNum(inputText);
			boolean hasFuncName = checkFuncName(inputText);

			hilit.removeAllHighlights();

			if (hasLineNum && hasFuncName && !textArea.getText().isEmpty()) {
				int end = inputText.length();
				textArea.setCaretPosition(end);
				entryBar.setBackground(entryBg); // set default color
			} 
			else if (!textArea.getText().isEmpty()) {
				entryBar.setBackground(ERROR_COLOR); // error color
				correctData = false;
			}
			else if (hasLineNum && hasFuncName) {
			}
			else correctData = false;
			return correctData;
		}

		// ensure pre-defined name
		private boolean checkFuncName(String inputText) {
			boolean okFuncName = true;
			int index1 = inputText.indexOf(" ");
			String funcName = inputText.substring(index1+1);
			String funcNames = "DATA,DEF,END,FOR,GO,GOTO,GUSUB,IF," +
					"LET,NEXT,PRINT,READ,RETURN,";
			int index2 = funcName.indexOf(" ");
			int funcSize = funcName.length();
			
			if (index2 != -1) { // cut off at end of string or space
				funcName = funcName.substring(0, Math.min(index2, funcSize));
			}
			else {
				funcName = funcName.substring(0, funcSize);
			}
			funcName += ","; // to make sure lines contain the whole name
			okFuncName = funcNames.contains(funcName)
					&& checkFuncReqs(funcName, inputText);
			return okFuncName;
		}

		// ensure functions have needed parts
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
			return false;
		}

		// needed to override these
		public void changedUpdate(DocumentEvent e) {
		}

		public void insertUpdate(DocumentEvent e) {
		}

		public void removeUpdate(DocumentEvent e) {
		}

		
		public void actionPerformed(ActionEvent e) {
			// when List Program is clicked
			if ("LISTPROGRAM".equals(e.getActionCommand())
					&& !textLines.isEmpty()) { 
				statusText.setText("Now press 'Run Program' to run");
				textLines = saveToAST(textLines);
				textArea.setText(vectorToString(textLines));
				pushedListProgram = true;
			}
			else if (textLines.isEmpty()) 
				statusText.setText("First you need to enter code");
			else if (pushedListProgram){
				statusText.setText("Here is the program output");
				Vector<String> textLines2 = runFromAST();
				textArea.setText(vectorToString(textLines2));
				runFromAST();
			}
			else statusText.setText("You must first press 'List Program'");
		}
	}
	
	public Interface() {
		super();
		GUI gui = new GUI();
		gui.main();
	}
}
