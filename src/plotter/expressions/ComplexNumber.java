package plotter.expressions;

public class ComplexNumber {

	@SuppressWarnings("serial")
	private class ComplexArithmeticException extends RuntimeException {

		public ComplexArithmeticException(String message) {
			super(message);
		}

	}

	/* The real and imaginary part */
	private double real;
	private double imaginary;

	/* Wether to interpret the value as boolean or not */
	private boolean asBoolean;

	public static ComplexNumber ZERO = new ComplexNumber(0, 0);
	public static ComplexNumber I = new ComplexNumber(0, 1);
	public static ComplexNumber NEGATIVE_I = new ComplexNumber(0, -1);
	public static ComplexNumber ONE = new ComplexNumber(1, 0);
	public static ComplexNumber NEGATIVE_ONE = new ComplexNumber(-1, 0);
	public static ComplexNumber NaN = new ComplexNumber(Double.NaN, Double.NaN);
	public static ComplexNumber MAX_VALUE = new ComplexNumber(Double.MAX_VALUE, Double.MAX_VALUE);
	public static ComplexNumber MIN_VALUE = new ComplexNumber(Double.MIN_VALUE, Double.MIN_VALUE);
	public static ComplexNumber POSITIVE_INFINITY = new ComplexNumber(Double.POSITIVE_INFINITY,
			Double.POSITIVE_INFINITY);
	public static ComplexNumber NEGATIVE_INFINITY = new ComplexNumber(Double.NEGATIVE_INFINITY,
			Double.NEGATIVE_INFINITY);
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
		this.asBoolean = false;
	}

	public ComplexNumber(ComplexNumber w) {
		this.real = w.getReal();
		this.imaginary = w.getImaginary();
	}

	/* Getters */

	public double getReal() {
		return real;
	}

	public double getImaginary() {
		return imaginary;
	}

	public ComplexNumber valueAsBoolean() {
		this.asBoolean = true;
		return this;
	}

	/* Complex number representations */

	public ComplexNumber polar() {
		return new ComplexNumber(modulusAsDouble(), argumentAsDouble());
	}

	public ComplexNumber cartesian() {
		return new ComplexNumber(real * StrictMath.cos(imaginary), real * StrictMath.sin(imaginary));
	}

	/* Trigonometry */

	public ComplexNumber modulus() {
		return new ComplexNumber(modulusAsDouble());
	}

	public double modulusAsDouble() {
		return Math.sqrt(real * real + imaginary * imaginary);
	}

	public ComplexNumber argument() {
		return new ComplexNumber(argumentAsDouble());
	}

	public double argumentAsDouble() {
		return Math.atan2(imaginary, real);
	}

	public ComplexNumber conjugate() {
		return new ComplexNumber(real, -imaginary);
	}

	/* Add */

	public ComplexNumber add(ComplexNumber w) {
		return new ComplexNumber(real + w.getReal(), imaginary + w.getImaginary());
	}

	public ComplexNumber add(double d) {
		return new ComplexNumber(real + d, imaginary);
	}

	/* Subtract */

	public ComplexNumber subtract(ComplexNumber w) {
		return new ComplexNumber(real - w.getReal(), imaginary - w.getImaginary());
	}

	public ComplexNumber subtract(double d) {
		return new ComplexNumber(real - d, imaginary);
	}

	/* Multiply */

	public ComplexNumber multiply(ComplexNumber w) {
		return new ComplexNumber(real * w.getReal() - imaginary * w.getImaginary(),
				real * w.getImaginary() + imaginary * w.getReal());
	}

	public ComplexNumber multiply(double d) {
		return new ComplexNumber(real * d, imaginary * d);
	}

	/* Divide and remainder */

	public ComplexNumber divide(ComplexNumber w) {
		double den = Math.pow(w.modulus().getReal(), 2);
		return new ComplexNumber((real * w.getReal() + imaginary * w.getImaginary()) / den,
				(imaginary * w.getReal() - real * w.getImaginary()) / den);
	}

	public ComplexNumber divide(double d) {
		return new ComplexNumber(real / d, imaginary / d);
	}

	public ComplexNumber remainder(ComplexNumber w) {
		ComplexNumber z = new ComplexNumber(Math.round(this.divide(w).getReal()),
				Math.round(this.divide(w).getImaginary()));
		return this.subtract(z.multiply(w));
	}

	/* Power */

	public ComplexNumber power(ComplexNumber w) {
		return ln().multiply(w).exp();
	}

	public ComplexNumber power(double d) {
		return ln().multiply(d).exp();
	}

	/* Absolute */

	public ComplexNumber abs() {
		return modulus();
	}

	/* Exponential and natural logarithm */

	public ComplexNumber exp() {
		double exp = Math.exp(real);
		return new ComplexNumber(exp * Math.cos(imaginary), exp * Math.sin(imaginary));
	}

	public ComplexNumber ln() {
		return new ComplexNumber(Math.log(modulusAsDouble()), argumentAsDouble());
	}

	/* Common logarithms */

	public ComplexNumber log2() {
		return log(2);
	}

	public ComplexNumber log10() {
		return log(10);
	}

	/* Logarithm with custom base */

	public ComplexNumber log(double base) {
		return ln().divide(base);
	}

	/* Inverse the ComplexNumber number */

	public ComplexNumber invert() {
		double r = real * real + imaginary + imaginary;
		return new ComplexNumber(real / r, -imaginary / r);
	}

	/* Square root */

	public ComplexNumber sqrt() {
		double r = Math.sqrt(modulusAsDouble());
		double theta = argumentAsDouble() / 2;
		return new ComplexNumber(r * Math.cos(theta), r * Math.sin(theta));
	}

	/* Sine, cosine and tangent */

	public ComplexNumber sin() {
		return new ComplexNumber(cosh(imaginary) * Math.sin(real), sinh(imaginary) * Math.cos(real));
	}

	public ComplexNumber cos() {
		return new ComplexNumber(cosh(imaginary) * Math.cos(real), -sinh(imaginary) * Math.sin(real));
	}

	public ComplexNumber tan() {
		return (sin()).divide(cos());
	}

	/* Sine, cosine and tangent hyperbolicus */

	private double cosh(double theta) {
		return (Math.exp(theta) + Math.exp(-theta)) / 2;
	}

	private double sinh(double theta) {
		return (Math.exp(theta) - Math.exp(-theta)) / 2;
	}

	public ComplexNumber sinh() {
		return new ComplexNumber(sinh(real) * Math.cos(imaginary), cosh(real) * Math.sin(imaginary));
	}

	public ComplexNumber cosh() {
		return new ComplexNumber(cosh(real) * Math.cos(imaginary), sinh(real) * Math.sin(imaginary));
	}

	public ComplexNumber tanh() {
		return (sinh()).divide(cosh());
	}

	/* Arcsine, arccosine and arctangent */

	public ComplexNumber arcsin() {
		ComplexNumber w = ONE.subtract(multiply(this)).sqrt().add(multiply(I));
		return new ComplexNumber(I.multiply(w.ln()).multiply(NEGATIVE_ONE));
	}

	public ComplexNumber arccos() {
		ComplexNumber w = multiply(this).subtract(ONE).sqrt().add(this).ln().multiply(NEGATIVE_I);
		if (w.getReal() >= 0) {
			return new ComplexNumber(w.getReal(), -w.getImaginary());
		} else {
			return w.negate();
		}
	}

	public ComplexNumber arctan() {
		return NEGATIVE_I.multiply(subtract(I).divide(negate().subtract(I)).ln()).divide(2.0);
	}

	/* Cotangent, secant and cosecant */

	public ComplexNumber cot() {
		return ONE.divide(tan());
	}

	public ComplexNumber sec() {
		return ONE.divide(cos());
	}

	public ComplexNumber csc() {
		return ONE.divide(sin());
	}

	/* Negate a complex number */

	public ComplexNumber negate() {
		return new ComplexNumber(-real, -imaginary);
	}

	/* Radians and Degrees conversions */

	public ComplexNumber rad() {
		if (!isReal()) {
			throw new ComplexArithmeticException("Cannot calculate radians of complex number");
		}

		return new ComplexNumber(Math.toRadians(getReal()));
	}

	public ComplexNumber deg() {
		if (!isReal()) {
			throw new ComplexArithmeticException("Cannot calculate degrees of complex number");
		}

		return new ComplexNumber(Math.toDegrees(getReal()));
	}

	/* Object methods */

	public boolean equals(ComplexNumber w) {
		return real == w.getReal() && imaginary == w.getImaginary();
	}

	public boolean equals(double d) {
		return real == d;
	}

	public boolean isReal() {
		return imaginary == 0;
	}

	public boolean isNaN() {
		return equals(NaN);
	}

	public boolean isInfinite() {
		return equals(POSITIVE_INFINITY) || equals(NEGATIVE_INFINITY);
	}

	/* Complex number as boolean */

	public boolean isAsBoolean() {
		return asBoolean;
	}

	public boolean booleanValue() {
		return equals(ONE);
	}

	/* Compare complex numbers */

	public int compareTo(ComplexNumber w) {
		if (!isReal() || !w.isReal()) {
			throw new ComplexArithmeticException("Cannot compare two complex numbers");
		}

		return (getReal() == w.getReal()) ? 0 : getReal() > w.getReal() ? 1 : -1;
	}

	/**
	 * Prints the ComplexNumber number in the format
	 *
	 * ("real" ± "imaginary"i)
	 */
	@Override
	public String toString() {
		return "(" + real + " " + (imaginary < 0 ? "-" : "+") + " " + Math.abs(imaginary) + "i)";
	}

}