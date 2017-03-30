package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FirstFx extends Application {
	
	Label lbText;
	Button btnClick;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		lbText = new Label("Some text");
		btnClick = new Button("Toggle it");
		btnClick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if(lbText.getText().equalsIgnoreCase("Some Text")) {
					lbText.setText("New text");
				} else {
					lbText.setText("Some Text");
				}
			}
		});
		VBox root = new VBox();
		root.getChildren().addAll(lbText, btnClick);
		Scene scene = new Scene(root, 500, 500);
		stage.setScene(scene);
		stage.show();
	}
}
