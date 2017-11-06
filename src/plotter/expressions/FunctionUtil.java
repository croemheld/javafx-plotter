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
import java.util.List;
import java.util.Map;

public class FunctionUtil {

	public static abstract class Function {
		/**
		 * Name of this function.
		 */
		private String name;
		/**
		 * Number of parameters expected for this function.
		 */
		private int numParams;

		/**
		 * Creates a new function with given name and parameter count.
		 *
		 * @param name
		 *            The name of the function.
		 * @param numParams
		 *            The number of parameters for this function.
		 */
		public Function(String name, int numParams) {
			this.name = name.toUpperCase();
			this.numParams = numParams;
		}

		public String getName() {
			return name;
		}

		public int getNumParams() {
			return numParams;
		}

		/**
		 * Implementation for this function.
		 *
		 * @param parameters
		 *            Parameters will be passed by the expression evaluator as a
		 *            {@link List} of {@link ComplexNumber} values.
		 * @return The function must return a new {@link ComplexNumber} value as a
		 *         computing result.
		 */
		public abstract ComplexNumber eval(List<ComplexNumber> parameters);
	}

	@SuppressWarnings("serial")
	private static Map<String, Function> functions = new HashMap<String, Function>() {
		{
			put("NOT", (new Function("NOT", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					boolean zero = parameters.get(0).compareTo(ComplexNumber.ZERO) == 0;
					return zero ? ComplexNumber.ONE : ComplexNumber.ZERO;
				}
			}));

			put("SIN", (new Function("SIN", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).sin();
				}
			}));

			put("COS", (new Function("COS", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).cos();
				}
			}));

			put("TAN", (new Function("TAN", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).tan();
				}
			}));

			put("SEC", (new Function("SEC", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).sec();
				}
			}));

			put("COT", (new Function("COT", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).cot();
				}
			}));

			put("CSC", (new Function("CSC", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).csc();
				}
			}));

			put("SINH", (new Function("SINH", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).sinh();
				}
			}));

			put("COSH", (new Function("COSH", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).cosh();
				}
			}));

			put("TANH", (new Function("TANH", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).tanh();
				}
			}));

			put("ARCSIN", (new Function("ARCSIN", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).arcsin();
				}
			}));

			put("ARCCOS", (new Function("ARCCOS", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).arccos();
				}
			}));

			put("ARCTAN", (new Function("ARCTAN", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).arctan();
				}
			}));

			put("RAD", (new Function("RAD", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).rad();
				}
			}));

			put("DEG", (new Function("DEG", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).deg();
				}
			}));

			put("MAX", (new Function("MAX", 2) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					ComplexNumber v1 = parameters.get(0);
					ComplexNumber v2 = parameters.get(1);
					return v1.compareTo(v2) > 0 ? v1 : v2;
				}
			}));

			put("MIN", (new Function("MIN", 2) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					ComplexNumber v1 = parameters.get(0);
					ComplexNumber v2 = parameters.get(1);
					return v1.compareTo(v2) < 0 ? v1 : v2;
				}
			}));

			put("ABS", (new Function("ABS", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).abs();
				}
			}));

			put("LN", (new Function("LN", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).ln();
				}
			}));

			put("LOG2", (new Function("LOG2", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).log2();
				}
			}));

			put("LOG10", (new Function("LOG10", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).log10();
				}
			}));

			// put("ROUND", (new Function("ROUND", 2) {
			// @Override
			// public ComplexNumber eval(List<ComplexNumber> parameters) {
			// ComplexNumber toRound = parameters.get(1);
			// int precision = parameters.get(0).intValue();
			// return toRound.setScale(precision, mc.getRoundingMode());
			// }
			// }));

			put("SQRT", (new Function("SQRT", 1) {
				@Override
				public ComplexNumber eval(List<ComplexNumber> parameters) {
					return parameters.get(0).sqrt();
				}
			}));
		}
	};

	public static boolean containsKey(String key) {
		return functions.containsKey(key);
	}

	public static Function getFunction(String key) {
		return functions.get(key);
	}
}
