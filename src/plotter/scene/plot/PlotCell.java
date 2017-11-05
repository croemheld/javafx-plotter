package plotter.scene.plot;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class PlotCell extends ListCell<Plot> {

	/* Private fields */
	private VBox vbox = new VBox();
	private HBox hbox = new HBox();
	private Pane pane = new Pane();
	private Label functionLabel = new Label("");
	private Label axesLabel = new Label("");
	private Font functionFont = new Font(14);
	private Font axesFont = new Font(11);
	private Button remove = new Button("x");

	public PlotCell() {
		super();

		vbox.getChildren().addAll(functionLabel, axesLabel);
		hbox.getChildren().addAll(vbox, pane, remove);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.setFillHeight(true);
		HBox.setHgrow(pane, Priority.ALWAYS);
		remove.setOnAction(event -> getListView().getItems().remove(getItem()));

		setOnMouseEntered(event -> showRemoveButton());
		setOnMouseExited(event -> hideRemoveButton());
	}

	@Override
	protected void updateItem(Plot plot, boolean empty) {
		super.updateItem(plot, empty);
		setText(null);
		setGraphic(null);

		if (plot != null && !empty) {
			functionLabel.setText(plot.getExpression().toString());
			functionLabel.setFont(functionFont);
			functionLabel.setPadding(PlotUtil.PADDING_CELL);
			axesLabel.setText(plot.getAxes().toString());
			axesLabel.setPadding(PlotUtil.PADDING_CELL);
			axesLabel.setFont(axesFont);
			remove.getStyleClass().add("template-button-remove-hidden");

			setGraphic(hbox);
		}
	}

	private void showRemoveButton() {
		remove.getStyleClass().removeAll("template-button-remove-hidden");
		remove.getStyleClass().add("template-button-remove-show");
	}

	private void hideRemoveButton() {
		remove.getStyleClass().removeAll("template-button-remove-show");
		remove.getStyleClass().add("template-button-remove-hidden");
	}

}
