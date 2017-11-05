package plotter.scene.plot;

import javafx.geometry.Insets;

public class PlotUtil {

	/* Initial settings */
	public static double X_MIN = -10;
	public static double X_MAX = 10;

	public static double Y_MIN = -10;
	public static double Y_MAX = 10;

	public static double X_UNITS = 1;
	public static double Y_UNITS = 1;

	public static double X_STEPS = 0.001;

	/* UI */
	public static Insets PADDING_CELL = new Insets(5);

	private PlotUtil() {
		// No instantiation
	}

}
