package plotter.expressions;

public class ComplexNumber {

	@SuppressWarnings("serial")
	private class ComplexArithmeticException extends RuntimeException {

		public ComplexArithmeticException(String message) {
			super(message);
		}

	}

	private double real;
	private double imaginary;

	public static ComplexNumber I = new ComplexNumber(0, 1);
	public static ComplexNumber ONE = new ComplexNumber(1, 0);
	public static ComplexNumber ZERO = new ComplexNumber(0, 0);
	public static ComplexNumber NaN = new ComplexNumber(Double.NaN, Double.NaN);
	public static ComplexNumber MAX_VALUE = new ComplexNumber(Double.MAX_VALUE, Double.MAX_VALUE);
	public static ComplexNumber MIN_VALUE = new ComplexNumber(Double.MIN_VALUE, Double.MIN_VALUE);
	public static ComplexNumber POSITIVE_INFINITY = new ComplexNumber(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	public static ComplexNumber NEGATIVE_INFINITY = new ComplexNumber(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
	public static ComplexNumber PI = new ComplexNumber(Math.PI);

	public ComplexNumber(double real) {
		this(real, 0);
	}

	public ComplexNumber(String string) {
		this(Double.parseDouble(string), 0);
	}

	public ComplexNumber(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}

	public ComplexNumber(ComplexNumber w) {
		this.real = w.getReal();
		this.imaginary = w.getImaginary();
	}

	public double getReal() {
		return real;
	}

	public double getImaginary() {
		return imaginary;
	}

	public ComplexNumber getModulus() {
		if (real != 0 || imaginary != 0) {
			return new ComplexNumber(Math.sqrt(real * real + imaginary * imaginary));
		} else {
			return ZERO;
		}
	}

	public double getArgument() {
		return Math.atan2(imaginary, real);
	}

	public ComplexNumber getConjugate() {
		return new ComplexNumber(real, -imaginary);
	}

	public ComplexNumber add(ComplexNumber w) {
		return new ComplexNumber(real + w.getReal(), imaginary + w.getImaginary());
	}

	public ComplexNumber subtract(ComplexNumber w) {
		return new ComplexNumber(real - w.getReal(), imaginary - w.getImaginary());
	}

	public ComplexNumber multiply(ComplexNumber w) {
		return new ComplexNumber(real * w.getReal() - imaginary * w.getImaginary(),
				real * w.getImaginary() + imaginary * w.getReal());
	}

	public ComplexNumber divide(ComplexNumber w) {
		double den = Math.pow(w.getModulus().getReal(), 2);
		return new ComplexNumber((real * w.getReal() + imaginary * w.getImaginary()) / den,
				(imaginary * w.getReal() - real * w.getImaginary()) / den);
	}

	public ComplexNumber remainder(ComplexNumber w) {
		ComplexNumber z = new ComplexNumber(Math.round(this.divide(w).getReal()),
				Math.round(this.divide(w).getImaginary()));
		return this.subtract(z.multiply(w));
	}

	public ComplexNumber power(ComplexNumber w) {
		double ex = Math.pow(Math.E, w.getReal());
		return new ComplexNumber(ex * Math.cos(w.getImaginary()), ex * Math.sin(w.getImaginary()));
	}

	public ComplexNumber abs() {
		return getModulus();
	}

	public ComplexNumber exp() {
		return new ComplexNumber(Math.exp(real) * Math.cos(imaginary), Math.exp(real) * Math.sin(imaginary));
	}

	public ComplexNumber ln() {
		return new ComplexNumber(Math.log(getModulus().getReal()), getArgument());
	}

	public ComplexNumber log2() {
		return ln().divide(new ComplexNumber(2));
	}

	public ComplexNumber log10() {
		return ln().divide(new ComplexNumber(10));
	}

	public ComplexNumber sqrt() {
		double r = Math.sqrt(this.getModulus().getReal());
		double theta = this.getArgument() / 2;
		return new ComplexNumber(r * Math.cos(theta), r * Math.sin(theta));
	}

	private double cosh(double theta) {
		return (Math.exp(theta) + Math.exp(-theta)) / 2;
	}

	private double sinh(double theta) {
		return (Math.exp(theta) - Math.exp(-theta)) / 2;
	}

	public ComplexNumber sin() {
		return new ComplexNumber(cosh(imaginary) * Math.sin(real), sinh(imaginary) * Math.cos(real));
	}

	public ComplexNumber cos() {
		return new ComplexNumber(cosh(imaginary) * Math.cos(real), -sinh(imaginary) * Math.sin(real));
	}

	public ComplexNumber sinh() {
		return new ComplexNumber(sinh(real) * Math.cos(imaginary), cosh(real) * Math.sin(imaginary));
	}

	public ComplexNumber cosh() {
		return new ComplexNumber(cosh(real) * Math.cos(imaginary), sinh(real) * Math.sin(imaginary));
	}

	public ComplexNumber tan() {
		return (sin()).divide(cos());
	}

	public ComplexNumber tanh() {
		return (sinh()).divide(cosh());
	}

	public ComplexNumber arcsin() {
		ComplexNumber w = ONE.subtract(multiply(this)).sqrt().add(multiply(I));
		return new ComplexNumber(I.multiply(w.ln()).multiply(ONE.negative()));
	}

	public ComplexNumber arccos() {
		ComplexNumber w = multiply(this).subtract(ONE).sqrt().add(this).ln().multiply(new ComplexNumber(0, -1));
		if (w.getReal() >= 0) {
			return new ComplexNumber(w.getReal(), -w.getImaginary());
		} else {
			return w.negative();
		}
	}

	public ComplexNumber arctan() {
		ComplexNumber frac = multiply(I).add(ONE).divide(multiply(I).multiply(ONE.negative()).add(ONE));
		return new ComplexNumber(I.multiply(new ComplexNumber(-0.5).multiply(frac.ln())));
	}

	public ComplexNumber cot() {
		return new ComplexNumber(1, 0).divide(tan());
	}

	public ComplexNumber sec() {
		return new ComplexNumber(1, 0).divide(cos());
	}

	public ComplexNumber csc() {
		return new ComplexNumber(1, 0).divide(sin());
	}

	public ComplexNumber negative() {
		return new ComplexNumber(-real, -imaginary);
	}

	public ComplexNumber rad() {
		if (!isRealNumber()) {
			throw new ComplexArithmeticException("Cannot calculate radians of complex number");
		}

		return new ComplexNumber(Math.toRadians(getReal()));
	}

	public ComplexNumber deg() {
		if (!isRealNumber()) {
			throw new ComplexArithmeticException("Cannot calculate degrees of complex number");
		}

		return new ComplexNumber(Math.toDegrees(getReal()));
	}

	public boolean equals(ComplexNumber w) {
		return w.getReal() == real && w.getImaginary() == imaginary;
	}

	public boolean isRealNumber() {
		return imaginary == 0;
	}

	public boolean isNaN() {
		return equals(NaN);
	}

	public boolean isInfinite() {
		return equals(POSITIVE_INFINITY) || equals(NEGATIVE_INFINITY);
	}

	/* Only if this and w are real numbers */
	public int compareTo(ComplexNumber w) {
		if (!isRealNumber() || !w.isRealNumber()) {
			throw new ComplexArithmeticException("Cannot compare two complex numbers");
		}

		return (getReal() == w.getReal()) ? 0 : getReal() > w.getReal() ? 1 : -1;
	}

}