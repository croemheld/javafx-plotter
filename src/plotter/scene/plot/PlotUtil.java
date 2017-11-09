package plotter.scene.plot;

import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import plotter.scene.axes.Axes;

public class PlotUtil {

	/* Initial settings */
	public static double X_MIN = -10;
	public static double X_MAX = 10;

	public static double Y_MIN = -10;
	public static double Y_MAX = 10;

	public static double X_UNITS = 1;
	public static double Y_UNITS = 1;

	public static double X_STEPS = 0.01;

	/* UI */
	public static Insets PADDING_CELL = new Insets(5);

	private PlotUtil() {
		// No instantiation
	}
	
	public static void drawHorizontalLines(Path gridPath, Path originPath, Axes axes) {
		double xLowerBound = axes.getHorizontalLowerBound();
		double xUpperBound = axes.getHorizontalUpperBound();
		double yLowerBound = axes.getVerticalLowerBound();
		double yUpperBound = axes.getVerticalUpperBound();
		double yUnit = axes.getVerticalAxis().getTickUnit();
		
		for(double y = yLowerBound + yUnit; y <= yUpperBound; y += yUnit) {
			if(y == 0) {
				addLine(originPath, axes, xLowerBound, y, xUpperBound, y);
				continue;
			}
			
			addLine(gridPath, axes, xLowerBound, y, xUpperBound, y);
		}
	}
	
	public static void drawVerticalLines(Path gridPath, Path originPath, Axes axes) {
		double xLowerBound = axes.getHorizontalLowerBound();
		double xUpperBound = axes.getHorizontalUpperBound();
		double yLowerBound = axes.getVerticalLowerBound();
		double yUpperBound = axes.getVerticalUpperBound();
		double xUnit = axes.getHorizontalAxis().getTickUnit();
		
		for(double x = xLowerBound + xUnit; x <= xUpperBound; x += xUnit) {
			if(x == 0) {
				addLine(originPath, axes, x, yLowerBound, x, yUpperBound);
				continue;
			}
			
			addLine(gridPath, axes, x, yLowerBound, x, yUpperBound);
		}
	}
	
	private static void addLine(Path path, Axes axes, double xFrom, double yFrom, double xTo, double yTo) {
		path.getElements().add(new MoveTo(mapHorizontal(axes, xFrom), mapVertical(axes, yFrom)));
		path.getElements().add(new LineTo(mapHorizontal(axes, xTo), mapVertical(axes, yTo)));
	}
	
	public static double pixelsPerWidthUnit(Axes axes) {
		return axes.getPrefWidth() / axes.getHorizontalBound();
	}
	
	public static double pixelsPerHeightUnit(Axes axes) {
		return axes.getPrefHeight() / axes.getVerticalBound();
	}

	public static void setStroke(Axes axes, Path path, Color color, int width) {
		Rectangle rectangle = new Rectangle(0, 0, axes.getPrefWidth(), axes.getPrefHeight());
		path.setStroke(color.deriveColor(0, 1, 1, 1));
		path.setStrokeWidth(width);
		path.setClip(rectangle);
	}
	
	public static double mapHorizontal(Axes axes, double x) {
		double ppwu = pixelsPerWidthUnit(axes);
		double origin = -axes.getHorizontalLowerBound() * ppwu;
		
		return x * ppwu + origin;
	}
	
	public static double mapVertical(Axes axes, double y) {
		double pphu = pixelsPerHeightUnit(axes);
		double origin = axes.getVerticalUpperBound() * pphu;
		
		return -y * pphu + origin;
	}

}
