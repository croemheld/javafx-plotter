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

import java.util.HashMap;
import java.util.Map;

public class OperatorUtil {

	public static abstract class Operator {

		/**
		 * This operators name (pattern).
		 */
		private String operator;

		/**
		 * Operators precedence.
		 */
		private int precedence;

		/**
		 * Operator is left associative.
		 */
		private boolean leftAssoc;

		/**
		 * Creates a new operator.
		 *
		 * @param operator
		 *            The operator name (pattern).
		 * @param precedence
		 *            The operators precedence.
		 * @param leftAssoc
		 *            <code>true</code> if the operator is left associative, else
		 *            <code>false</code>.
		 */
		public Operator(String operator, int precedence, boolean leftAssoc) {
			this.operator = operator;
			this.precedence = precedence;
			this.leftAssoc = leftAssoc;
		}

		public String getOperator() {
			return operator;
		}

		public int getPrecedence() {
			return precedence;
		}

		public boolean isLeftAssoc() {
			return leftAssoc;
		}

		/**
		 * Implementation for this operator.
		 *
		 * @param v1
		 *            Operand 1.
		 * @param v2
		 *            Operand 2.
		 * @return The result of the operation.
		 */
		public abstract ComplexNumber eval(ComplexNumber v1, ComplexNumber v2);
	}

	@SuppressWarnings("serial")
	private static final Map<String, Operator> operators = new HashMap<String, Operator>() {
		{
			put("+", (new Operator("+", 20, true) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return v1.add(v2);
				}
			}));

			put("-", (new Operator("-", 20, true) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return v1.subtract(v2);
				}
			}));

			put("-", (new Operator("*", 30, true) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return v1.multiply(v2);
				}
			}));

			put("-", (new Operator("/", 30, true) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return v1.divide(v2);
				}
			}));

			put("-", (new Operator("%", 30, true) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return v1.remainder(v2);
				}
			}));

			put("-", (new Operator("^", 40, false) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return v1.power(v2);
				}
			}));

			put("-", (new Operator("&&", 4, false) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					boolean b1 = !v1.equals(ComplexNumber.ZERO);
					boolean b2 = !v2.equals(ComplexNumber.ZERO);
					return b1 && b2 ? ComplexNumber.ONE : ComplexNumber.ZERO;
				}
			}));

			put("-", (new Operator("||", 2, false) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					boolean b1 = !v1.equals(ComplexNumber.ZERO);
					boolean b2 = !v2.equals(ComplexNumber.ZERO);
					return b1 || b2 ? ComplexNumber.ONE : ComplexNumber.ZERO;
				}
			}));

			put("-", (new Operator(">", 10, false) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return v1.compareTo(v2) == 1 ? ComplexNumber.ONE : ComplexNumber.ZERO;
				}
			}));

			put("-", (new Operator(">=", 10, false) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return v1.compareTo(v2) >= 0 ? ComplexNumber.ONE : ComplexNumber.ZERO;
				}
			}));

			put("-", (new Operator("<", 10, false) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return v1.compareTo(v2) == -1 ? ComplexNumber.ONE : ComplexNumber.ZERO;
				}
			}));

			put("-", (new Operator("<=", 10, false) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return v1.compareTo(v2) <= 0 ? ComplexNumber.ONE : ComplexNumber.ZERO;
				}
			}));

			put("-", (new Operator("=", 7, false) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return v1.equals(v2) ? ComplexNumber.ONE : ComplexNumber.ZERO;
				}
			}));

			put("-", (new Operator("==", 7, false) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return operators.get("=").eval(v1, v2);
				}
			}));

			put("-", (new Operator("!=", 7, false) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return !v1.equals(v2) ? ComplexNumber.ONE : ComplexNumber.ZERO;
				}
			}));

			put("-", (new Operator("<>", 7, false) {
				@Override
				public ComplexNumber eval(ComplexNumber v1, ComplexNumber v2) {
					return operators.get("!=").eval(v1, v2);
				}
			}));
		}
	};

	public static boolean containsKey(String key) {
		return operators.containsKey(key);
	}

	public static Operator getOperator(String key) {
		return operators.get(key);
	}

}
