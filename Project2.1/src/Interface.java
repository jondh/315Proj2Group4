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
	public static class GUI extends JFrame implements DocumentListener, ActionListener {
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
			textLines.add("80 FOR X = 1 TO 100");
			textLines.add("20 PRINT X, SQR(X)");
			textLines.add("50 NEXT X");
			textLines.add("10 END");

			textArea.setText(vectorToString(textLines));
			textArea.setColumns(40);
			textArea.setLineWrap(true);
			textArea.setRows(20);
			textArea.setWrapStyleWord(true);
			textArea.setEditable(false);
			JScrollPane scrollingArea = new JScrollPane(textArea);

			hilit = new DefaultHighlighter();
			painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
			textArea.setHighlighter(hilit);
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
				vectString += vect.get(i) + "\n";
			}
			return vectString;
		}

		public Vector<String> sortVectStrings(Vector<String> vect) {
			Collections.sort(vect, new Comparator<String>()
					{
				@Override
				public int compare(String s1, String s2) {
					String num1 = " ";
					String num2 = " ";
					int index1 = s1.indexOf(" ");
					int index2 = s2.indexOf(" ");
					boolean hasNum1 = Character.isDigit(s1.charAt(0));
					boolean hasNum2 = Character.isDigit(s1.charAt(0));

					num1 = s1.substring(0, index1);
					num2 = s2.substring(0, index2);

					if (num1.equalsIgnoreCase(num2)) {
						System.out.println("Found identical number\n");
						identicalLine = s1;
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

			hilit.removeAllHighlights();

			if (hasLineNum && hasFuncName) {   // match found
				int end = inputText.length();
				//hilit.addHighlight(index, end, painter);
				textArea.setCaretPosition(end);
				entryBar.setBackground(entryBg);
			} else {
				entryBar.setBackground(ERROR_COLOR);
				correctData = false;
			}
			return correctData;
		}

		private boolean checkFuncName(String inputText) {
			int index1 = inputText.indexOf(" ");
			String funcName = inputText.substring(index1+1);
			String funcNames = "DATA,DEF,END,FOR,GO,IF," +
					"LET,NEXT,PRINT,READ,RETURN,STOP,";
			int index2 = funcName.indexOf(" ");
			int funcSize = funcName.length();
			if (index2 != -1) {
				funcName = funcName.substring(0, Math.min(index2, funcSize));
			}
			else {
				funcName = funcName.substring(0, funcSize);
			}
			funcName += ",";
			return funcNames.contains(funcName);
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

		//============================================================= main
		public static void main() {
			JFrame win = new GUI();
			win.setVisible(true);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if ("LISTPROGRAM".equals(e.getActionCommand())) {
				System.out.println("ListProgram clicked");
				textLines = deleteLineNums(textLines);
				//List<Token> tokens = tokenize(vectorToString(textLines));
				//for (int i = 0; i < tokens.size(); ++i) {
				//	System.out.println("text at " + i + ": " + tokens.get(i).text + " " + tokens.get(i).type);
				//}
				tokenizeString(vectorToString(textLines));
			}
			else System.out.println("RunProgram clicked");

		}

		private Vector<String> deleteLineNums(Vector<String> s1) {
			int index = 0; 
			for (int i = 0; i < s1.size(); ++i) {
				index = s1.get(i).indexOf(" ");
				s1.set(i, s1.get(i).substring(index+1));
			}
			return s1;
		}
	}
	
	public Interface() {
		super();
		GUI gui = new GUI();
		GUI.main();
	}
	
}
