package plotter.scene.template;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TemplateUtil {

	/* Constants */

	public static Insets PADDING_INPUT = new Insets(20, 10, 20, 0);
	public static Insets PADDING_GRAPH = new Insets(20);
	public static Insets PADDING_GRAPH_LIST = new Insets(20);

	public static double SPACING_INPUT = 10;

	private TemplateUtil() {
		// No instantiation
	}

	/* Template UI */

	public static Label createLabel(String name, String styleClass) {
		Label label = new Label(name);
		label.getStyleClass().add(styleClass);

		return label;
	}

	public static TextField createField(String styleClass) {
		TextField field = new TextField();
		field.getStyleClass().add(styleClass);

		return field;
	}

	public static TextField createField(int width, String styleClass) {
		TextField field = createField(styleClass);
		field.setMinWidth(width);
		field.setMaxWidth(width);
		field.setPadding(PADDING_INPUT);

		return field;
	}

	public static TextField createField(String text, int width, String styleClass) {
		TextField field = createField(width, styleClass);
		field.setText(text);
		field.setPadding(PADDING_INPUT);

		return field;
	}

}
