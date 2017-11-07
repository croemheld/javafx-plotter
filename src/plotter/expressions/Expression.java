/*
 * Copyright 2012 Udo Klimaschewski
 *
 * http://UdoJava.com/
 * http://about.me/udo.klimaschewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package plotter.expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import plotter.expressions.FunctionUtil.Function;
import plotter.expressions.OperatorUtil.Operator;

public class Expression {

	/**
	 * The original infix expression.
	 */
	private String expression = null;

	/**
	 * All defined variables with name and value.
	 */
	@SuppressWarnings("serial")
	private Map<String, ComplexNumber> variables = new HashMap<String, ComplexNumber>() {
		{
			put("PI", ComplexNumber.PI);
		}
	};

	/**
	 * What character to use for decimal separators.
	 */
	private final char decimalSeparator = '.';

	/**
	 * What character to use for minus sign (negative values).
	 */
	private final char minusSign = '-';

	/**
	 * The expression evaluators exception class.
	 */
	@SuppressWarnings("serial")
	public class ExpressionException extends RuntimeException {

		public ExpressionException(String message) {
			super(message);
		}
	}

	/**
	 * Expression tokenizer that allows to iterate over a {@link String} expression
	 * token by token. Blank characters will be skipped.
	 */
	private class Tokenizer implements Iterator<String> {

		/**
		 * Actual position in expression string.
		 */
		private int pos = 0;
		/**
		 * The original input expression.
		 */
		private String input;
		/**
		 * The previous token or <code>null</code> if none.
		 */
		private String previousToken;

		/**
		 * Creates a new tokenizer for an expression.
		 *
		 * @param input
		 *            The expression string.
		 */
		public Tokenizer(String input) {
			this.input = input;
		}

		@Override
		public boolean hasNext() {
			return (pos < input.length());
		}

		/**
		 * Peek at the next character, without advancing the iterator.
		 *
		 * @return The next character or character 0, if at end of string.
		 */
		private char peekNextChar() {
			if (pos < (input.length() - 1)) {
				return input.charAt(pos + 1);
			} else {
				return 0;
			}
		}

		@Override
		public String next() {
			StringBuilder token = new StringBuilder();

			if (pos >= input.length()) {
				return previousToken = null;
			}

			char ch = input.charAt(pos);
			while (Character.isWhitespace(ch) && pos < input.length()) {
				ch = input.charAt(++pos);
			}

			if (Character.isDigit(ch)) {
				while ((Character.isDigit(ch) || ch == decimalSeparator) && (pos < input.length())) {
					token.append(input.charAt(pos++));
					ch = pos == input.length() ? 0 : input.charAt(pos);
				}
			} else if (ch == minusSign && Character.isDigit(peekNextChar()) && ("(".equals(previousToken)
					|| ",".equals(previousToken) || previousToken == null || OperatorUtil.containsKey(previousToken))) {
				token.append(minusSign);
				pos++;
				token.append(next());
			} else if (Character.isLetter(ch)) {
				while ((Character.isLetter(ch) || Character.isDigit(ch) || (ch == '_')) && (pos < input.length())) {
					token.append(input.charAt(pos++));
					ch = pos == input.length() ? 0 : input.charAt(pos);
				}
			} else if (ch == '(' || ch == ')' || ch == ',') {
				token.append(ch);
				pos++;
			} else {
				while (!Character.isLetter(ch) && !Character.isDigit(ch) && !Character.isWhitespace(ch) && ch != '('
						&& ch != ')' && ch != ',' && (pos < input.length())) {
					token.append(input.charAt(pos));
					pos++;
					ch = pos == input.length() ? 0 : input.charAt(pos);
					if (ch == minusSign) {
						break;
					}
				}

				if (!OperatorUtil.containsKey(token.toString())) {
					throw new ExpressionException(
							"Unknown operator '" + token + "' at position " + (pos - token.length() + 1));
				}
			}

			return previousToken = token.toString();
		}

		@Override
		public void remove() {
			throw new ExpressionException("remove() not supported");
		}

	}

	/**
	 * Creates a new expression instance from an expression string.
	 *
	 * @param expression
	 *            The expression. E.g. <code>"2.4*sin(3)/(2-4)"</code> or
	 *            <code>"sin(y)>0 & max(z, 3)>3"</code>
	 */
	public Expression(String expression) {
		this.expression = expression;
	}

	/**
	 * Is the string a number?
	 *
	 * @param st
	 *            The string.
	 * @return <code>true</code>, if the input string is a number.
	 */
	private boolean isNumber(String st) {
		if (st.charAt(0) == minusSign && st.length() == 1)
			return false;
		for (char ch : st.toCharArray()) {
			if (!Character.isDigit(ch) && ch != minusSign && ch != decimalSeparator)
				return false;
		}
		return true;
	}

	/**
	 * Implementation of the <i>Shunting Yard</i> algorithm to transform an infix
	 * expression to a RPN expression.
	 *
	 * @param expression
	 *            The input expression in infix.
	 * @return A RPN representation of the expression, with each token as a list
	 *         member.
	 */
	private List<String> shuntingYard(String expression) {
		List<String> outputQueue = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();

		Tokenizer tokenizer = new Tokenizer(expression);

		String lastFunction = null;
		while (tokenizer.hasNext()) {
			String token = tokenizer.next();
			if (isNumber(token)) {
				outputQueue.add(token);
			} else if (variables.containsKey(token)) {
				outputQueue.add(Double.toString(variables.get(token).getReal()));
			} else if (FunctionUtil.containsKey(token.toUpperCase())) {
				stack.push(token);
				lastFunction = token;
			} else if (Character.isLetter(token.charAt(0))) {
				stack.push(token);
			} else if (",".equals(token)) {
				while (!stack.isEmpty() && !"(".equals(stack.peek())) {
					outputQueue.add(stack.pop());
				}

				if (stack.isEmpty()) {
					throw new ExpressionException("Parse error for function '" + lastFunction + "'");
				}
			} else if (OperatorUtil.containsKey(token)) {
				Operator o1 = OperatorUtil.getOperator(token);
				String token2 = stack.isEmpty() ? null : stack.peek();

				while (OperatorUtil.containsKey(token2)
						&& ((o1.isLeftAssoc() && o1.getPrecedence() <= OperatorUtil.getOperator(token2).getPrecedence())
								|| (o1.getPrecedence() < OperatorUtil.getOperator(token2).getPrecedence()))) {
					outputQueue.add(stack.pop());
					token2 = stack.isEmpty() ? null : stack.peek();
				}

				stack.push(token);
			} else if ("(".equals(token)) {
				stack.push(token);
			} else if (")".equals(token)) {
				while (!stack.isEmpty() && !"(".equals(stack.peek())) {
					outputQueue.add(stack.pop());
				}

				if (stack.isEmpty()) {
					throw new RuntimeException("Mismatched parentheses");
				}

				stack.pop();
				if (!stack.isEmpty() && FunctionUtil.containsKey(stack.peek().toUpperCase())) {
					outputQueue.add(stack.pop());
				}
			}
		}
		while (!stack.isEmpty()) {
			String element = stack.pop();

			if ("(".equals(element) || ")".equals(element)) {
				throw new RuntimeException("Mismatched parentheses");
			}

			if (!OperatorUtil.containsKey(element)) {
				throw new RuntimeException("Unknown operator or function: " + element);
			}

			outputQueue.add(element);
		}
		return outputQueue;
	}

	public ComplexNumber eval(double value) {
		return with("x", value).eval();
	}

	/**
	 * Evaluates the expression.
	 *
	 * @return The result of the expression.
	 */
	public ComplexNumber eval() {
		Stack<ComplexNumber> stack = new Stack<ComplexNumber>();

		for (String token : getRPN()) {
			if (OperatorUtil.containsKey(token)) {
				ComplexNumber v1 = stack.pop();
				ComplexNumber v2 = stack.pop();
				stack.push(OperatorUtil.getOperator(token).eval(v2, v1));
			} else if (FunctionUtil.containsKey(token.toUpperCase())) {
				Function f = FunctionUtil.getFunction(token.toUpperCase());
				ArrayList<ComplexNumber> p = new ArrayList<ComplexNumber>(f.getNumParams());

				for (int i = 0; i < f.getNumParams(); i++) {
					p.add(stack.pop());
				}

				ComplexNumber fResult = f.eval(p);
				stack.push(fResult);
			} else {
				stack.push(new ComplexNumber(token));
			}
		}

		return stack.pop();
	}

	/**
	 * Sets a variable value.
	 *
	 * @param variable
	 *            The variable name.
	 * @param value
	 *            The variable value.
	 * @return The expression, allows to chain methods.
	 */
	private Expression setVariable(String variable, double value) {
		if (variables.containsKey(variable)) {
			variables.remove(variable);
		}

		variables.put(variable, new ComplexNumber(value));
		return this;
	}

	/**
	 * Sets a variable value.
	 *
	 * @param variable
	 *            The variable to set.
	 * @param value
	 *            The variable value.
	 * @return The expression, allows to chain methods.
	 */
	private Expression with(String variable, double value) {
		return setVariable(variable, value);
	}

	/**
	 * Get an iterator for this expression, allows iterating over an expression
	 * token by token.
	 *
	 * @return A new iterator instance for this expression.
	 */
	public Iterator<String> getExpressionTokenizer() {
		return new Tokenizer(this.expression);
	}

	/**
	 * Cached access to the RPN notation of this expression, ensures only one
	 * calculation of the RPN per expression instance. If no cached instance exists,
	 * a new one will be created and put to the cache.
	 *
	 * For our purposes we simply return the result from the shunting yard algorithm
	 * since we need to evaluate the result for a range of x coordinates.
	 *
	 * @return The cached RPN instance.
	 */
	private List<String> getRPN() {
		return shuntingYard(this.expression);
	}

	/**
	 * Get a string representation of the RPN (Reverse Polish Notation) for this
	 * expression.
	 *
	 * @return A string with the RPN representation for this expression.
	 */
	public String toRPN() {
		String result = new String();
		for (String st : getRPN()) {
			result = result.isEmpty() ? result : result + " ";
			result += st;
		}
		return result;
	}

	@Override
	public String toString() {
		return expression;
	}

}
