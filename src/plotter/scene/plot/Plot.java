package plotter.scene.plot;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import plotter.expressions.ComplexNumber;
import plotter.expressions.Expression;
import plotter.scene.axes.Axes;

public class Plot extends Pane {

	private Axes axes;
	private Path realPath;
	private Path imaginaryPath;
	private Expression expression;
	private Tooltip tooltip = new Tooltip();
	private boolean isTooltipActive = false;

	public Plot(Axes axes, Expression expression) {
		this.axes = axes;
		this.expression = expression;

		draw();

		setOnMouseMoved(event -> showCoordinates(event));
		setOnMouseClicked(event -> toggleCoordinates(event));
	}

	public void draw() {
		if (expression == null) {
			getChildren().setAll(axes);
			return;
		}

		double lowerBound = axes.getHorizontalAxis().getLowerBound();
		double upperBound = axes.getHorizontalAxis().getUpperBound();

		setPath(lowerBound, upperBound);

		getChildren().setAll(axes, realPath, imaginaryPath);
	}

	public void redraw() {
		getChildren().setAll(axes, realPath, imaginaryPath);
	}

	private void setPath(double lowerBound, double upperBound) {
		realPath = new Path();
		imaginaryPath = new Path();

		boolean isReal = setRealPath(lowerBound, upperBound);

		if (!isReal) {
			setImaginaryPath(lowerBound, upperBound);
		}

		setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
		setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
		setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
	}

	private boolean setRealPath(double x, double upperBound) {
		boolean isReal = true;

		setStroke(realPath, Color.ORANGE);
		ComplexNumber y = expression.with("x", x).eval();

		isReal = isReal && y.isRealNumber();
		realPath.getElements().add(new MoveTo(mapHorizontal(x), mapVertical(y.getReal())));
		x = x + PlotUtil.X_STEPS;

		while (x < upperBound) {
			y = expression.with("x", x).eval();
			isReal = isReal && y.isRealNumber();

			LineTo lineTo = new LineTo(mapHorizontal(x), mapVertical(y.getReal()));
			realPath.getElements().add(lineTo);

			x = x + PlotUtil.X_STEPS;
		}

		return isReal;
	}

	private void setImaginaryPath(double x, double upperBound) {
		setStroke(imaginaryPath, Color.DEEPSKYBLUE);
		ComplexNumber y = expression.with("x", x).eval();

		imaginaryPath.getElements().add(new MoveTo(mapHorizontal(x), mapVertical(y.getImaginary())));
		x = x + PlotUtil.X_STEPS;

		while (x < upperBound) {
			y = expression.with("x", x).eval();
			LineTo lineTo = new LineTo(mapHorizontal(x), mapVertical(y.getImaginary()));
			imaginaryPath.getElements().add(lineTo);

			x = x + PlotUtil.X_STEPS;
		}
	}

	private void setStroke(Path path, Color color) {
		Rectangle rectangle = new Rectangle(0, 0, axes.getPrefWidth(), axes.getPrefHeight());
		path.setStroke(color.deriveColor(0, 1, 1, 1));
		path.setStrokeWidth(1);
		path.setClip(rectangle);
	}

	private double mapHorizontal(double x) {
		double tx = axes.getPrefWidth() / 2;
		double sx = axes.getPrefWidth()
				/ (axes.getHorizontalAxis().getUpperBound() - axes.getHorizontalAxis().getLowerBound());

		return x * sx + tx;
	}

	private double mapVertical(double y) {
		double ty = axes.getPrefHeight() / 2;
		double sy = axes.getPrefHeight()
				/ (axes.getVerticalAxis().getUpperBound() - axes.getVerticalAxis().getLowerBound());

		return -y * sy + ty;
	}

	public Expression getExpression() {
		return expression;
	}

	public Axes getAxes() {
		return axes;
	}

	@Override
	public String toString() {
		return expression.toString();
	}

	/* Events */

	private void toggleCoordinates(MouseEvent event) {
		if (event.getButton() != MouseButton.SECONDARY) {
			return;
		}

		if (isTooltipActive) {
			isTooltipActive = false;
			hideCoordinates();
		} else {
			isTooltipActive = true;
			showCoordinates(event);
		}
	}

	private void showCoordinates(MouseEvent event) {
		if (!isTooltipActive) {
			return;
		}

		double x = Math.floor(getAxes().getHorizontalAxis().getValueForDisplay(event.getX()).doubleValue() * 100) / 100;
		double y = Math.floor(getExpression().with("x", x).eval().getReal() * 100) / 100;

		tooltip.setText("x = " + x + ", f(x) = " + y);
		tooltip.show((Node) event.getSource(), event.getSceneX(), event.getSceneY());
	}

	public void hideCoordinates() {
		tooltip.setText("");
		tooltip.hide();
	}
}
