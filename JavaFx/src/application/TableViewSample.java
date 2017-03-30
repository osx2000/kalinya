package application;

import com.kalinya.javafx.util.RowData;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

//public class TableViewSample implements javafx.fxml.Initializable {
	public class TableViewSample extends Application {
	/*@Override	
	public void initialize(URL location, ResourceBundle resources) {
	}*/

	//private TableView<Person> table = new TableView<>();
	private TableView<RowData> table = new TableView<>();
	private TableView<Person> personTable = new TableView<>();
	
	private ObservableList<Person> personData;
	private ObservableList<RowData> data;


	public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage stage) {
    	//personExample(stage);
		tableRowExample(stage);
	}

    private void tableRowExample(Stage stage) {
		data = getTableRowData();
		Scene scene = new Scene(new Group());
		stage.setTitle("Table View Sample");
		stage.setWidth(450);
		stage.setHeight(500);

		final Label label = new Label("Address Book");
		label.setFont(new Font("Arial", 20));

		table.setEditable(true);

		TableColumn<RowData, String> firstNameCol = new TableColumn<>("Date");
		firstNameCol.setMinWidth(100);
		firstNameCol.setCellValueFactory(
				new PropertyValueFactory<RowData, String>("propertyColumn1"));

		TableColumn<RowData, String> lastNameCol = new TableColumn<>("Portfolio");
		lastNameCol.setMinWidth(100);
		lastNameCol.setCellValueFactory(
				new PropertyValueFactory<RowData, String>("propertyColumn2"));

		table.setItems(data);
		table.getColumns().addAll(firstNameCol, lastNameCol);

		final VBox vbox = new VBox();

		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.getChildren().addAll(label, table);

		((Group) scene.getRoot()).getChildren().addAll(vbox);

		stage.setScene(scene);
		stage.show();
	}
    
	private void personExample(Stage stage) {
		personData = getPersonData();
		Scene scene = new Scene(new Group());
		stage.setTitle("Table View Sample");
		stage.setWidth(450);
		stage.setHeight(500);

		final Label label = new Label("Address Book");
		label.setFont(new Font("Arial", 20));

		personTable.setEditable(true);

		TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
		firstNameCol.setMinWidth(100);
		firstNameCol.setCellValueFactory(
				new PropertyValueFactory<Person, String>("firstName"));

		TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
		lastNameCol.setMinWidth(100);
		lastNameCol.setCellValueFactory(
				new PropertyValueFactory<Person, String>("lastName"));

		personTable.setItems(personData);
		personTable.getColumns().addAll(firstNameCol, lastNameCol);

		final VBox vbox = new VBox();

		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.getChildren().addAll(label, table);

		((Group) scene.getRoot()).getChildren().addAll(vbox);

		stage.setScene(scene);
		stage.show();
	}

	private ObservableList<Person> getPersonData() {
		ObservableList<Person> team = FXCollections.observableArrayList(
				new Person("Jacob", "Smith", "jacob.smith@example.com"),
				new Person("Isabella", "Johnson", "isabella.johnson@example.com"),
				new Person("Ethan", "Williams", "ethan.williams@example.com"),
				new Person("Emma", "Jones", "emma.jones@example.com"),
				new Person("Michael", "Brown", "michael.brown@example.com")
				);
		return team;
	}
	
	private ObservableList<RowData> getTableRowData() {
		ObservableList<RowData> team = FXCollections.observableArrayList(
				new RowData("2-Jan-2017", "Assets"),
				new RowData("3-Jan-2017", "Liabilities")
				);
		return team;
	}
}
