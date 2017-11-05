package plotter.scene.template;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import plotter.expressions.Expression;
import plotter.scene.axes.Axes;
import plotter.scene.plot.Plot;
import plotter.scene.plot.PlotCell;
import plotter.scene.plot.PlotUtil;

public class Template extends Pane {

	/* Singleton */
	private static Template template;

	/* Private fields */
	private Stage stage;
	private BorderPane borderPane;

	/* Text fields */
	private TextField functionField;
	private TextField xFromField;
	private TextField xToField;
	private TextField yFromField;
	private TextField yToField;

	/* Sections */
	private HBox input;
	private StackPane graph;
	private ListView<Plot> graphList;

	/* Tooltip */

	public static Template getInstance(Stage stage) {
		if (template == null) {
			template = new Template(stage);
		}

		return template;
	}

	private Template(Stage stage) {
		this.stage = stage;
		createPane();
	}

	private void createPane() {
		borderPane = new BorderPane();

		createInputNode();
		createGraphNode(null);
		createGraphList();

		Scene scene = new Scene(borderPane);
		scene.getStylesheets().add(getClass().getResource("src/css/style.css").toExternalForm());

		stage.setMaximized(true);
		stage.setScene(scene);
		stage.show();
	}

	private void createInputNode() {
		input = new HBox();
		input.setPadding(TemplateUtil.PADDING_INPUT);
		input.setSpacing(TemplateUtil.SPACING_INPUT);
		input.setId("template-input");

		Label functionLabel = TemplateUtil.createLabel("f(x) = ", "template-input-label");
		Label xFromLabel = TemplateUtil.createLabel("x from: ", "template-input-label");
		Label xToLabel = TemplateUtil.createLabel("x to: ", "template-input-label");
		Label yFromLabel = TemplateUtil.createLabel("y from: ", "template-input-label");
		Label yToLabel = TemplateUtil.createLabel("y to: ", "template-input-label");

		functionField = TemplateUtil.createField(250, "template-input-field");
		xFromField = TemplateUtil.createField(Double.toString(PlotUtil.X_MIN), 75, "template-coordinate-field");
		xToField = TemplateUtil.createField(Double.toString(PlotUtil.X_MAX), 75, "template-coordinate-field");
		yFromField = TemplateUtil.createField(Double.toString(PlotUtil.Y_MIN), 75, "template-coordinate-field");
		yToField = TemplateUtil.createField(Double.toString(PlotUtil.Y_MAX), 75, "template-coordinate-field");

		StackPane buttonPane = new StackPane();
		Button evalButton = new Button("evaluate");
		evalButton.setOnAction(event -> evaluate());
		buttonPane.getChildren().add(evalButton);
		buttonPane.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(buttonPane, Priority.ALWAYS);

		input.getChildren().addAll(functionLabel, functionField);
		input.getChildren().addAll(xFromLabel, xFromField);
		input.getChildren().addAll(xToLabel, xToField);
		input.getChildren().addAll(yFromLabel, yFromField);
		input.getChildren().addAll(yToLabel, yToField);
		input.getChildren().add(buttonPane);
		borderPane.setTop(input);
	}

	private void createGraphNode(Expression expression) {
		if (expression == null) {
			createGraphPane(new Axes());
		} else {
			Plot plot = new Plot(expression);
			createGraphPane(plot);
			graphList.getItems().add(plot);
		}
	}

	private void createGraphList() {
		graphList = new ListView<Plot>();
		graphList.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> redraw(n));
		graphList.setCellFactory(plot -> new PlotCell());
		borderPane.setLeft(graphList);
	}

	private void createGraphPane(Pane pane) {
		graph = new StackPane(pane);
		graph.setPadding(TemplateUtil.PADDING_GRAPH);
		borderPane.setCenter(graph);
	}

	/* Events */

	private void evaluate() {
		String function = functionField.getText();
		if (!function.isEmpty()) {
			Expression expression = new Expression(function);
			createGraphNode(expression);
		}
	}

	private void redraw(Plot plot) {
		if (plot != null) {
			functionField.setText(plot.getExpression().toString());
			createGraphPane(plot);
		}
	}

}