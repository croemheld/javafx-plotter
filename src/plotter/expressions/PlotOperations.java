package plotter.expressions;

public class PlotOperations {

	public PlotOperations() {
		// No instantiation
	}

	/* Supported functions */
	public static String[] functionNames = { "sin", "cos", "tan", "cot", "sec", "csc", "arcsin", "arccos", "arctan",
			"exp", "ln", "log10", "log2", "abs", "sqrt" };

	public static final byte PLUS = -1;
	public static final byte MINUS = -2;
	public static final byte TIMES = -3;
	public static final byte DIVIDE = -4;
	public static final byte POWER = -5;
	public static final byte SIN = -6;
	public static final byte COS = -7;
	public static final byte TAN = -8;
	public static final byte COT = -9;
	public static final byte SEC = -10;
	public static final byte CSC = -11;
	public static final byte ARCSIN = -12;
	public static final byte ARCCOS = -13;
	public static final byte ARCTAN = -14;
	public static final byte EXP = -15;
	public static final byte LN = -16;
	public static final byte LOG10 = -17;
	public static final byte LOG2 = -18;
	public static final byte ABS = -19;
	public static final byte SQRT = -20;
	public static final byte UNARYMINUS = -21;
	public static final byte VARIABLE = -22;

	public static ComplexNumber binOp(byte instruction, ComplexNumber x, ComplexNumber y) {
		ComplexNumber ans = ComplexNumber.NaN;

		switch (instruction) {
		case PLUS:
			ans = x.add(y);
			break;
		case MINUS:
			ans = x.subtract(y);
			break;
		case TIMES:
			ans = x.multiply(y);
			break;
		case DIVIDE:
			ans = x.divide(y);
			break;
		case POWER:
			ans = x.power(y);
			break;
		}

		if (ans.isNaN()) {
			return ans;
		}

		return ans;
	}

	public static ComplexNumber unOp(byte instruction, ComplexNumber x) {
		ComplexNumber ans = ComplexNumber.NaN;
		switch (instruction) {
		case SIN:
			ans = x.sin();
			break;
		case COS:
			ans = x.cos();
			break;
		case TAN:
			ans = x.tan();
			break;
		case COT:
			ans = x.cot();
			break;
		case SEC:
			ans = x.sec();
			break;
		case CSC:
			ans = x.csc();
			break;
		case ARCSIN:
			ans = x.arcsin();
			break;
		case ARCCOS:
			ans = x.arccos();
			break;
		case ARCTAN:
			ans = x.arctan();
			break;
		case EXP:
			ans = x.exp();
			break;
		case LN:
			ans = x.ln();
			break;
		case LOG2:
			ans = x.log2();
			break;
		case LOG10:
			ans = x.log10();
			break;
		case ABS:
			ans = x.getModulus();
			break;
		case SQRT:
			ans = x.sqrt();
			break;
		case UNARYMINUS:
			ans = x.negative();
			break;
		}

		if (ans.isNaN()) {
			return ans;
		}

		return ans;
	}
}
