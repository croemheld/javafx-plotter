package plotter.expressions;

public class Expression {

	private String definition;
	private byte[] code;
	private ComplexNumber[] stack;
	private ComplexNumber[] constants;

	/* Parsing variables */
	private int pos = 0;
	private int constantCt = 0;
	private int codeSize = 0;

	/**
	 * Construct an expression, given its definition as a string. This will throw an
	 * IllegalArgumentException if the string does not contain a legal expression.
	 *
	 * @param definition
	 */
	public Expression(String definition) {
		parse(definition);
	}

	/**
	 * Computes the value of this expression, when the variable x has a specified
	 * value. If the expression is undefined for the specified value of x, then
	 * Double.NaN is returned.
	 *
	 * @param x
	 * @return Result of value
	 */
	public ComplexNumber value(double x) {
		ComplexNumber y = eval(x);
		System.out.println("[ " + definition + " ] value for x = " + x + ": f(" + x + ") = (" + y.getReal() + ", "
				+ y.getImaginary() + ")");
		return y;
	}

	/**
	 * Evaluate this expression for this value of the variable.
	 *
	 * @param variable
	 * @return Result of expression
	 */
	private ComplexNumber eval(double variable) {
		int top = 0;
		for (int i = 0; i < codeSize; i++) {
			if (code[i] >= 0) {
				stack[top++] = constants[code[i]];
			} else if (code[i] >= PlotOperations.POWER) {
				ComplexNumber y = stack[--top];
				ComplexNumber x = stack[--top];
				ComplexNumber ans = PlotOperations.binOp(code[i], x, y);
				stack[top++] = ans;
			} else if (code[i] == PlotOperations.VARIABLE) {
				stack[top++] = new ComplexNumber(variable);
			} else {
				ComplexNumber x = stack[--top];
				ComplexNumber ans = PlotOperations.unOp(code[i], x);
				stack[top++] = ans;
			}
		}

		if (stack[0].isInfinite()) {
			return ComplexNumber.NaN;
		} else {
			return stack[0];
		}
	}

	/**
	 * Throws an {@link IllegalAccessException} when parsing failed.
	 *
	 * @param message
	 */
	private void error(String message) {
		throw new IllegalArgumentException("Parse error: " + message + " (Position in data = " + pos + ".)");
	}

	/**
	 *
	 * @return The stack usage depending on the functions used in the calculations.
	 */
	private int computeStackUsage() {
		int s = 0;
		int max = 0;
		for (int i = 0; i < codeSize; i++) {
			if (code[i] >= 0 || code[i] == PlotOperations.VARIABLE) {
				s++;
				if (s > max) {
					max = s;
				}
			} else if (code[i] >= PlotOperations.POWER) {
				s--;
			}
		}

		return max;
	}

	/**
	 * Initial handling of the input expression.
	 *
	 * @param definition
	 */
	private void parse(String definition) {
		if (definition == null || definition.trim().equals("")) {
			error("No data provided to Expr constructor");
		}

		this.definition = definition;
		this.code = new byte[definition.length()];
		this.constants = new ComplexNumber[definition.length()];

		parseExpression();
		skip();

		if (next() != 0) {
			error("Extra data found after the end of the expression.");
		}

		int stackSize = computeStackUsage();

		this.stack = new ComplexNumber[stackSize];

		byte[] c = new byte[codeSize];
		System.arraycopy(code, 0, c, 0, codeSize);

		this.code = c;

		ComplexNumber[] A = new ComplexNumber[constantCt];
		System.arraycopy(constants, 0, A, 0, constantCt);

		this.constants = A;
	}

	/**
	 *
	 * @return The char at the current position of the definition.
	 */
	private char next() {
		if (pos >= definition.length()) {
			return 0;
		} else {
			return definition.charAt(pos);
		}
	}

	/**
	 * Skip all whitespaces in the definition.
	 */
	private void skip() {
		while (Character.isWhitespace(next())) {
			pos++;
		}
	}

	/**
	 * Parsing the expression.
	 */
	private void parseExpression() {
		boolean neg = false;
		skip();
		if (next() == '+' || next() == '-') {
			neg = (next() == '-');
			pos++;

			skip();
		}

		parseTerm();

		if (neg) {
			code[codeSize++] = PlotOperations.UNARYMINUS;
		}

		skip();

		while (next() == '+' || next() == '-') {
			char op = next();
			pos++;

			parseTerm();

			code[codeSize++] = (op == '+') ? PlotOperations.PLUS : PlotOperations.MINUS;
			skip();
		}
	}

	/**
	 * Parse the term.
	 */
	private void parseTerm() {
		parseFactor();
		skip();

		while (next() == '*' || next() == '/') {
			char op = next();
			pos++;

			parseFactor();

			code[codeSize++] = (op == '*') ? PlotOperations.TIMES : PlotOperations.DIVIDE;

			skip();
		}
	}

	/**
	 * Parse the factor.
	 */
	private void parseFactor() {
		parsePrimary();
		skip();

		while (next() == '^') {
			pos++;

			parsePrimary();

			code[codeSize++] = PlotOperations.POWER;

			skip();
		}
	}

	/**
	 * Parse the variables.
	 */
	private void parsePrimary() {
		skip();
		char ch = next();

		if (ch == 'x' || ch == 'X') {
			pos++;
			code[codeSize++] = PlotOperations.VARIABLE;
		} else if (Character.isLetter(ch)) {
			parseWord();
		} else if (Character.isDigit(ch) || ch == '.') {
			parseNumber();
		} else if (ch == '(') {
			pos++;

			parseExpression();
			skip();

			if (next() != ')') {
				error("Exprected a right parenthesis.");
			}

			pos++;
		} else if (ch == ')') {
			error("Unmatched right parenthesis.");
		} else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^') {
			error("Operator '" + ch + "' found in an unexpected position.");
		} else if (ch == 0) {
			error("Unexpected end of data in the middle of an expression.");
		} else {
			error("Illegal character '" + ch + "' found in data.");
		}
	}

	/**
	 * Parse the word.
	 */
	private void parseWord() {
		String w = "";

		while (Character.isLetterOrDigit(next())) {
			w += next();
			pos++;
		}

		w = w.toLowerCase();

		for (int i = 0; i < PlotOperations.functionNames.length; i++) {
			if (w.equals(PlotOperations.functionNames[i])) {
				skip();

				if (next() != '(')
					error("Function name '" + w + "' must be followed by its parameter in parentheses.");
				pos++;

				parseExpression();
				skip();

				if (next() != ')') {
					error("Missing right parenthesis after parameter of function '" + w + "'.");
				}

				pos++;
				code[codeSize++] = (byte) (PlotOperations.SIN - i);

				return;
			}
		}
		error("Unknown word '" + w + "' found in data.");
	}

	/**
	 * Parse the number.
	 */
	private void parseNumber() {
		String w = "";

		while (Character.isDigit(next())) {
			w += next();
			pos++;
		}

		if (next() == '.') {
			w += next();
			pos++;

			while (Character.isDigit(next())) {
				w += next();
				pos++;
			}
		}
		if (w.equals(".")) {
			error("Illegal number found, consisting of decimal point only.");
		}

		if (next() == 'E' || next() == 'e') {
			w += next();
			pos++;

			if (next() == '+' || next() == '-') {
				w += next();
				pos++;
			}

			if (!Character.isDigit(next())) {
				error("Illegal number found, with no digits in its exponent.");
			}

			while (Character.isDigit(next())) {
				w += next();
				pos++;
			}
		}

		ComplexNumber d = ComplexNumber.NaN;

		try {
			d = new ComplexNumber(Double.valueOf(w).doubleValue());
		} catch (Exception e) {
			// Nothing for now
		}

		if (d.isNaN()) {
			error("Illegal number '" + w + "' found in data.");
		}

		code[codeSize++] = (byte) constantCt;
		constants[constantCt++] = d;
	}

	/**
	 * Return the original definition string of this expression. This is the same
	 * string that was provided in the constructor.
	 */
	@Override
	public String toString() {
		return definition;
	}

}
