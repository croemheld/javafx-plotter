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

		realPath = new Path();
		imaginaryPath = new Path();

		double lowerBound = axes.getHorizontalAxis().getLowerBound();
		double upperBound = axes.getHorizontalAxis().getUpperBound();

		boolean realOnly = plotLine(realPath, lowerBound, upperBound, true);

		if (!realOnly) {
			plotLine(imaginaryPath, lowerBound, upperBound, false);
		}

		setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
		setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
		setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

		getChildren().setAll(axes, realPath, imaginaryPath);
	}

	public void redraw() {
		getChildren().setAll(axes, realPath, imaginaryPath);
	}

	private boolean plotLine(Path path, double lowerBound, double upperBound, boolean isReal) {
		boolean realOnly = true;
		setStroke(path, (isReal ? Color.ORANGE : Color.DEEPSKYBLUE));

		for (double x = lowerBound; x < upperBound; x += PlotUtil.X_STEPS) {
			ComplexNumber y = expression.eval(x);
			realOnly = realOnly && y.isReal();

			if (x == lowerBound) {
				path.getElements()
						.add(new MoveTo(mapHorizontal(x), mapVertical((isReal ? y.getReal() : y.getImaginary()))));
			} else {
				path.getElements()
						.add(new LineTo(mapHorizontal(x), mapVertical((isReal ? y.getReal() : y.getImaginary()))));
			}
		}

		return realOnly;
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
		double y = Math.floor(getExpression().eval(x).getReal() * 100) / 100;

		tooltip.setText("x = " + x + ", f(x) = " + y);
		tooltip.show((Node) event.getSource(), event.getSceneX(), event.getSceneY());
	}

	public void hideCoordinates() {
		tooltip.setText("");
		tooltip.hide();
	}
}
