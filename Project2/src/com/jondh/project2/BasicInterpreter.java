package com.jondh.project2;

import java.util.List;


public class BasicInterpreter {
	
	public static void main(String[] args) {
		System.out.println("Hello");
		Interface gui = new Interface();
		//ASTtree astTree = new ASTtree();
	}
	
	protected static void tokenizeString(String str) {
		new Tokenizer();
		List<Token> toks = Tokenizer.tokenize(str);
		for (int i = 0; i < toks.size(); ++i) {
			System.out.println("Token " + i + ": "+ toks.get(i).text + " " + toks.get(i).type);
		}
		return;
	}
}
