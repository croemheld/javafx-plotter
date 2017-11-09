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

		double lowerBound = axes.getHorizontalLowerBound();
		double upperBound = axes.getHorizontalUpperBound();

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
			ComplexNumber w = expression.eval(x);
			double y = isReal ? w.getReal() : w.getImaginary();
			
			realOnly = realOnly && w.isReal();

			if (x == lowerBound) {
				path.getElements().add(new MoveTo(mapHorizontal(x), mapVertical(y)));
			} else {
				path.getElements().add(new LineTo(mapHorizontal(x), mapVertical(y)));
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
		double ppwu = axes.getPrefWidth() / axes.getHorizontalBound();
		double origin = -axes.getHorizontalLowerBound() * ppwu;
		
		return x * ppwu + origin;
	}
	
	private double mapVertical(double y) {
		double pphu = axes.getPrefHeight() / axes.getVerticalBound();
		double origin = axes.getVerticalUpperBound() * pphu;
		
		return -y * pphu + origin;
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
		tooltip.show((Node) event.getSource(), event.getSceneX(), mapVertical(expression.eval(x).getReal()) + 150);
	}

	public void hideCoordinates() {
		tooltip.setText("");
		tooltip.hide();
	}
}
