package plotter.main;

import javafx.application.Application;
import javafx.stage.Stage;
import plotter.scene.template.Template;

public class JavaFXPlotter extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) {
		Template.getInstance(stage);
	}

}
