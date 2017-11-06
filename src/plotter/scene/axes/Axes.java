package plotter.scene.axes;

import javafx.beans.binding.Bindings;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;

public class Axes extends Pane {

	/* Private fields */
	private NumberAxis xAxis;
	private NumberAxis yAxis;

	public Axes() {
		setPane();
		setHorizontalAxis(AxesUtil.AXES_X_MIN, AxesUtil.AXES_X_MAX, AxesUtil.AXES_X_UNIT);
		setVerticalAxis(AxesUtil.AXES_Y_MIN, AxesUtil.AXES_Y_MAX, AxesUtil.AXES_Y_UNIT);
		getChildren().setAll(xAxis, yAxis);
	}

	public Axes(double xFrom, double xTo, double yFrom, double yTo, double xUnit, double yUnit) {
		setPane();
		setHorizontalAxis(xFrom, xTo, xUnit);
		setVerticalAxis(yFrom, yTo, yUnit);
		getChildren().setAll(xAxis, yAxis);
	}

	private void setPane() {
		setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
		setPrefSize(AxesUtil.WINDOW_WIDTH, AxesUtil.WINDOW_HEIGHT);
		setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
	}

	private void setHorizontalAxis(double from, double to, double unit) {
		xAxis = new NumberAxis(from, to, unit);
		xAxis.setSide(Side.BOTTOM);
		xAxis.setLabel("x");
		xAxis.setMinorTickVisible(false);
		xAxis.setPrefWidth(AxesUtil.WINDOW_WIDTH);
		xAxis.setLayoutY(AxesUtil.WINDOW_HEIGHT);
	}

	private void setVerticalAxis(double from, double to, double unit) {
		yAxis = new NumberAxis(from, to, unit);
		yAxis.setSide(Side.LEFT);
		yAxis.setLabel("f(x)");
		yAxis.setMinorTickVisible(false);
		yAxis.setPrefHeight(AxesUtil.WINDOW_HEIGHT);
		yAxis.layoutXProperty().bind(Bindings.subtract(1, yAxis.widthProperty()));
	}

	public Axes setAxes(double xFrom, double xTo, double xUnit, double yFrom, double yTo, double yUnit) {
		setPane();
		setHorizontalAxis(xFrom, xTo, xUnit);
		setVerticalAxis(yFrom, yTo, yUnit);
		getChildren().setAll(xAxis, yAxis);

		return this;
	}

	public NumberAxis getHorizontalAxis() {
		return xAxis;
	}

	public NumberAxis getVerticalAxis() {
		return yAxis;
	}

	@Override
	public String toString() {
		return "x: " + xAxis.getLowerBound() + ":" + xAxis.getUpperBound() + " - y: " + yAxis.getLowerBound() + ":"
				+ yAxis.getUpperBound();
	}
}
