package com.jondh.project2;

import java.util.ArrayList;
import java.util.List;


public class Tokenizer extends BasicInterpreter {
	
	public Tokenizer() {
		//tokenize(str);
	}

	public static List<Token> tokenize(String source) {
		List<Token> tokens = new ArrayList<Token>();

		String token = "";
		TokenizeState state = TokenizeState.DEFAULT;

		// Many tokens are a single character, like operators and ().
		String charTokens = "\n=+-*/<>()";
		TokenType[] tokenTypes = { TokenType.LINE, TokenType.EQUALS,
				TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
				TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
				TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN
		};
		// Scan through the code one character at a time, building up the list
		// of tokens.
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			switch (state) {
			case DEFAULT:
				if (charTokens.indexOf(c) != -1) {
					tokens.add(new Token(Character.toString(c),
							tokenTypes[charTokens.indexOf(c)]));
				} else if (Character.isLetter(c)) {
					token += c;
					state = TokenizeState.WORD;
				} else if (Character.isDigit(c)) {
					token += c;
					state = TokenizeState.NUMBER;
				} else if (c == '"') {
					state = TokenizeState.STRING;
				} else if (c == '\'') {
					state = TokenizeState.COMMENT;
				}
				break;

			case WORD:
				if (Character.isLetterOrDigit(c)) {
					token += c;
				} else if (c == ':') {
					tokens.add(new Token(token, TokenType.LABEL));
					token = "";
					state = TokenizeState.DEFAULT;
				} else {
					tokens.add(new Token(token, TokenType.WORD));
					token = "";
					state = TokenizeState.DEFAULT;
					i--; // Reprocess this character in the default state.
				}
				break;

			case NUMBER:
				// HACK: Negative numbers and floating points aren't supported.
				// To get a negative number, just do 0 - <your number>.
				// To get a floating point, divide.
				if (Character.isDigit(c)) {
					token += c;
				} else {
					tokens.add(new Token(token, TokenType.NUMBER));
					token = "";
					state = TokenizeState.DEFAULT;
					i--; // Reprocess this character in the default state.
				}
				break;

			case STRING:
				if (c == '"') {
					tokens.add(new Token(token, TokenType.STRING));
					token = "";
					state = TokenizeState.DEFAULT;
				} else {
					token += c;
				}
				break;

			case COMMENT:
				if (c == '\n') {
					state = TokenizeState.DEFAULT;
				}
				break;
			}
		}

		// HACK: Silently ignore any in-progress token when we run out of
		// characters. This means that, for example, if a script has a string
		// that's missing the closing ", it will just ditch it.
		return tokens;
	}
}
