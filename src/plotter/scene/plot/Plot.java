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
import plotter.expressions.ComplexNumber;
import plotter.expressions.Expression;
import plotter.scene.axes.Axes;

public class Plot extends Pane {

	private Path gridPath;
	private Path originPath;
	private Axes axes;
	private Path realPath;
	private Path imaginaryPath;
	private Expression expression;
	private Tooltip tooltip;
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

		gridPath = new Path();
		originPath = new Path();
		realPath = new Path();
		imaginaryPath = new Path();
		tooltip = new Tooltip();

		double xLowerBound = axes.getHorizontalLowerBound();
		double xUpperBound = axes.getHorizontalUpperBound();
		
		plotGrid(axes);

		boolean realOnly = plotLine(realPath, xLowerBound, xUpperBound, true);

		if (!realOnly) {
			plotLine(imaginaryPath, xLowerBound, xUpperBound, false);
		}

		setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
		setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
		setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

		getChildren().setAll(gridPath, originPath, axes, realPath, imaginaryPath);
	}

	public void redraw() {
		getChildren().setAll(gridPath, originPath, axes, realPath, imaginaryPath);
	}
	
	private void plotGrid(Axes axes) {
		PlotUtil.setStroke(axes, gridPath, Color.LIGHTGRAY, 1);
		PlotUtil.setStroke(axes, originPath, Color.DARKGRAY, 2);
		PlotUtil.drawHorizontalLines(gridPath, originPath, axes);
		PlotUtil.drawVerticalLines(gridPath, originPath, axes);
	}

	private boolean plotLine(Path path, double lowerBound, double upperBound, boolean isReal) {
		boolean realOnly = true;
		PlotUtil.setStroke(axes, path, (isReal ? Color.ORANGE : Color.DEEPSKYBLUE), 1);

		for (double x = lowerBound; x < upperBound; x += PlotUtil.X_STEPS) {
			ComplexNumber w = expression.eval(x);
			double y = isReal ? w.getReal() : w.getImaginary();
			
			realOnly = realOnly && w.isReal();

			if (x == lowerBound) {
				path.getElements().add(new MoveTo(PlotUtil.mapHorizontal(axes, x), PlotUtil.mapVertical(axes, y)));
			} else {
				path.getElements().add(new LineTo(PlotUtil.mapHorizontal(axes, x), PlotUtil.mapVertical(axes, y)));
			}
		}

		return realOnly;
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
		if (event.getButton() != MouseButton.PRIMARY) {
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

		double x = Math.floor(axes.getHorizontalAxis().getValueForDisplay(event.getX()).doubleValue() * 100) / 100;
		double y = Math.floor(expression.eval(x).getReal() * 100) / 100;

		tooltip.setText("x = " + x + ", f(x) = " + y);
		
		// TODO: instead of 150px calculate the height difference from window top border to beginning of axes
		tooltip.show((Node) event.getSource(), event.getSceneX(), PlotUtil.mapVertical(axes, expression.eval(x).getReal()) + 150);
	}

	public void hideCoordinates() {
		tooltip.setText("");
		tooltip.hide();
	}
}
