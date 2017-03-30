package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class KeyPadForm implements javafx.fxml.Initializable {

	@FXML
	private VBox keypad;
	@FXML
	private PasswordField passwordDisplay;
	@FXML
	private TextField textField;
	@FXML
	private GridPane gridPane;
	@FXML
	private Button delete;
	@FXML
	private Button printHello;

	public KeyPadForm() {
		System.out.println("Constructor called");
	}
	
	@Override	
	public void initialize(URL location, ResourceBundle resources) {		
		//printHello.setOnAction(printHello());	
	}

	@FXML
	private void delete() {
		if(textField.getText().equalsIgnoreCase("Deleted")) {
			passwordDisplay.setText("");
			textField.setText("");
		} else {
			passwordDisplay.setText("Deleted");
			textField.setText("Deleted");
		}
	}
	
	@FXML
	private void keyTyped() {
		if(textField.getText().equalsIgnoreCase("Hello")) {
			passwordDisplay.setText("Goodbye");
			textField.setText("Goodbye");
		} else {
			passwordDisplay.setText("Hello");
			textField.setText("Hello");
		}
	}
}

