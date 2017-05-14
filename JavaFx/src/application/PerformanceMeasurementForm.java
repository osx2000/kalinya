package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.kalinya.application.FindurSession;
import com.kalinya.javafx.util.RowData;
import com.kalinya.performance.Configurator;
import com.kalinya.performance.PerformanceFactory;
import com.kalinya.performance.PerformanceResult;
import com.kalinya.performance.PortfolioPerformanceResult;
import com.kalinya.performance.datasource.CSVDataSource;
import com.kalinya.performance.datasource.DataSource;
import com.kalinya.performance.datasource.DataSource1;
import com.kalinya.performance.dimensions.PerformanceDimensions;
import com.kalinya.performance.portfoliostatistics.PortfolioStatistics;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class PerformanceMeasurementForm implements javafx.fxml.Initializable {
	@FXML
	private MenuItem closeMenuItem;
	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker endDatePicker;
	@FXML
	private Tab parametersTab;
	@FXML
	private Tab resultsTab;
	@FXML
	private Button calculateButton;
	@FXML
	private Label statusBar;
	@FXML
	private ComboBox<String> dataSourceComboBox;
	@FXML
	private ComboBox<String> performanceDimensionComboBox;
	@FXML
	private TextField securityMasterFilePathField;
	@FXML
	private TextField positionsFilePathField;
	@FXML
	private TableView<RowData> resultsTableView;
	@FXML
	private Button extractToTableButton;
	@FXML
	private Button extractToCsvButton;
	@FXML
	private Button viewResultsButton;
	@FXML
	private TableColumn<RowData, String> propertyColumn1;
	@FXML
	private TableColumn<RowData, String> propertyColumn2;
	@FXML
	private TableColumn resultsColumnStartLocalMarketValue;
	@FXML
	private TableColumn resultsColumnStartBaseMarketValue;
	@FXML
	private TableColumn resultsColumnEndLocalMarketValue;
	@FXML
	private TableColumn resultsColumnEndBaseMarketValue;
	@FXML
	private TableColumn resultsColumnLocalCashflowsAmount;
	@FXML
	private TableColumn resultsColumnBaseCashflowsAmount;
	@FXML
	private TableColumn resultsColumnLocalGainLoss;
	@FXML
	private TableColumn resultsColumnBaseGainLoss;
	@FXML
	private TableColumn resultsColumnLocalRateOfReturn;
	@FXML
	private TableColumn resultsColumnBaseRateOfReturn;
	@FXML
	private TextField logFileTextField;
	@FXML
	private TextArea logFileTextArea;
	private FileWatcher fileWatcher;
	private PerformanceResult performanceResult;
	private ObservableList<String> dataSourceList = FXCollections.observableArrayList(
			DataSource1.Predefined.Csv.getName(),
			DataSource1.Predefined.FindurPmm.getName());
	private ObservableList<String> performanceDimensionList = FXCollections.observableArrayList(
			PerformanceDimensions.Predefined.ByDateByLeg.toString(),
			PerformanceDimensions.Predefined.ByDateByPortfolio.toString());
	private ObservableList<RowData> performanceResultAsList;
	protected boolean enableListViewLogging = false;

	@Override	
	public void initialize(URL location, ResourceBundle resources) {
		dataSourceComboBox.setItems(dataSourceList);
		dataSourceComboBox.setValue(DataSource1.Predefined.Csv.getName());

		performanceDimensionComboBox.setItems(performanceDimensionList);
		performanceDimensionComboBox.setValue(PerformanceDimensions.Predefined.ByDateByPortfolio.toString());

		securityMasterFilePathField.setText(Configurator.SECURITY_MASTER_FILE_PATH);
		positionsFilePathField.setText(Configurator.POSITIONS_FILE_PATH);

		setLogFileTextArea();
		initializeLogFileListener();
	}

	@FXML
	private void close() {
		fileWatcher.stopThread();
		try {
			fileWatcher.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Platform.exit();
	}
	
	private void setLogFileTextArea() {
		setLogFileTextArea(new File(getLogFilePath()));
	}

	@SuppressWarnings({ "resource", "null" })
	private void setLogFileTextArea(final File file) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				//StringBuilder log = new StringBuilder();
				List<String> log = new ArrayList<>();
				Scanner scanner = null;
				try {
					scanner = new Scanner(file).useDelimiter("\\s+");
					int i = 1;
					while (scanner.hasNext()) {
						//log.append(i + ": " + scanner.nextLine() + "\n");
						log.add(i + ": " + scanner.nextLine() + "\n");
						i++;
					}
					List<String> tail = log.subList(Math.max(log.size() - 300, 0), log.size());
					logFileTextArea.setText(tail.toString());
					autoScroll(logFileTextArea);
				} catch (FileNotFoundException ex) {
					System.err.println(ex);
				} finally {
					scanner.close();
				}
			}
		});
	}

	private String getLogFilePath() {
		String logFilePath = "C:\\Users\\Stephen\\workspace\\Data\\PerformanceMeasurementLogFile.txt";
		if(logFileTextField != null) {
			logFilePath = logFileTextField.getText();
		}
		return logFilePath;
	}

	private void initializeLogFileListener() {
		final String logFilePath = getLogFilePath();
		if(fileWatcher != null) {
			throw new IllegalStateException("The FileWatcher is already running");
		}
		fileWatcher = new FileWatcher(new File(logFilePath)) {
			@Override
			public void doOnChange() {
				setLogFileTextArea();
			}

			@Override
			public String toString() {
				return String.format("LogFileWatcher [%s]", logFilePath);
			}
		};
		fileWatcher.start();
	}

	private String getSecurityMasterFilePath() {
		return securityMasterFilePathField.getText();
	}

	private String getPositionsFilePath() {
		return positionsFilePathField.getText();
	}

	private PerformanceDimensions getPerformanceDimensions() {
		String performanceDimensionsString = performanceDimensionComboBox.getValue();
		return PerformanceDimensions.Predefined.fromName(performanceDimensionsString).getPerformanceDimensions();
	}

	private DataSource1 getDataSource() {
		String dataSourceString = dataSourceComboBox.getValue();
		return DataSource1.Predefined.fromName(dataSourceString).getDataSource();
	}

	@FXML
	private void calculateResults() {
		resultsTab.setDisable(true);
		FindurSession findurSession = new FindurSession();
		PerformanceFactory pf = findurSession.getPerformanceFactory();

		DataSource csvDataSource =  null;
		
		/*csvDataSource = new CSVDataSource.Builder()
				//.withPortfoliosFilter(getPortfolios())
				.withPositionsFilePath(Configurator.POSITIONS_FILE_PATH_MULTIPLE_PORTFOLIOS)
				.withSecurityMasterFilePath(Configurator.SECURITY_MASTER_FILE_PATH)
				.withPortfoliosFilePath(Configurator.PORTFOLIOS_FILE_PATH)
				.withBenchmarkAssociationsFilePath(Configurator.BENCHMARK_ASSOCIATIONS_FILE_PATH)
				.withResultsExtractFilePath(Configurator.PERFORMANCE_RESULTS_EXTRACT_FILE_PATH)
				.build();*/
		
		csvDataSource = new CSVDataSource.Builder()
		//.withStartDate(startDate)
		//.withEndDate(endDate)
		.withPositionsFilePath(Configurator.POSITIONS_FILE_PATH_MULTIPLE_PORTFOLIOS)
		.withSecurityMasterFilePath(Configurator.SECURITY_MASTER_FILE_PATH)
		.withPortfoliosFilePath(Configurator.PORTFOLIOS_FILE_PATH)
		.withBenchmarkAssociationsFilePath(Configurator.BENCHMARK_ASSOCIATIONS_FILE_PATH)
		.withResultsExtractFilePath(Configurator.PERFORMANCE_RESULTS_EXTRACT_FILE_PATH)
		.build();
		
		PerformanceResult performanceResults = null;
		//performanceResults = pf.calculateResults(csvDataSource, performanceDimensions);
		performanceResults = pf.calculateResults(csvDataSource.getPortfolios(), csvDataSource.getBenchmarkAssociations(), csvDataSource.getSecurityMasterData(),
				csvDataSource.getInstruments(), csvDataSource.getInstrumentLegs(), csvDataSource.getPositions(),
				csvDataSource.getCashflows(), getPerformanceDimensions());
		if (csvDataSource.requiresFindurSession()) {
			findurSession.getSession().getDebug().viewTable(performanceResults.asTable());
			if(getPerformanceDimensions().equals(PerformanceDimensions.BY_DATE_BY_PORTFOLIO)) {
				//csvDataSource.extractToUserTable("USER_perf_results_by_portfolio");
			}
			if(getPerformanceDimensions().equals(PerformanceDimensions.BY_DATE_BY_LEG)) {
				performanceResults.extractToUserTable("USER_perf_results_by_leg");
			}
		}
		performanceResults.printToCsvFile(csvDataSource.getResultsExtractFilePath());
		System.out.println(String.format("Extracted to [%s]", csvDataSource.getResultsExtractFilePath()));
		performanceResults.extractToSerializedFile(Configurator.SERIALIZED_FILE_PATH);
		//deserializePerformanceResults();
		System.out.println("Absolute results: " + performanceResults.toString());
		
		if(performanceResults instanceof PortfolioPerformanceResult) {
			PortfolioStatistics portfolioStatistics = pf.createPortfolioStatistics();
			portfolioStatistics.add(PortfolioStatistics.ACTIVE_RETURN);
			portfolioStatistics.add(PortfolioStatistics.EXCESS_RETURN);
			portfolioStatistics.add(PortfolioStatistics.TRACKING_ERROR);
			portfolioStatistics.add(PortfolioStatistics.STANDARD_DEIVATION);
			portfolioStatistics.add(PortfolioStatistics.SHARPE_RATIO);
			portfolioStatistics.calculate((PortfolioPerformanceResult) performanceResults, csvDataSource.getBenchmarkAssociations());
		}
		
		if(performanceResults.getSession() == null) {
			extractToTableButton.setDisable(true);
		}
		setPerformanceResults(performanceResults);
	}

	private PerformanceResult getPerformanceResults() {
		return performanceResult;
	}

	private void setPerformanceResults(PerformanceResult performanceResult) {
		this.performanceResult = performanceResult;
		System.out.println(performanceResult.toString());
		setStatusBarText("Calculated performance results  ");
		resultsTab.setDisable(false);
	}

	/*@SuppressWarnings("unchecked")
	@FXML
	private void setResultsTableData() {
		resultsTableView = null;

		  // 0. Initialize the columns.
		resultsColumnDate.set
		resultsColumnDate.setCellValueFactory(
				new Callback<CellDataFeatures<Person, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<Person, String> p) {
						// p.getValue() returns the Person instance for a
						// particular TableView row
						return p.getValue().lastNameProperty();
					}
				});
	}


		resultsColumnDate.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
		resultsColumnPortfolio.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Person> filteredData = new FilteredList<>(masterData, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (person.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (person.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<Person> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(personTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        personTable.setItems(sortedData);
	}*/

	@FXML
	private void extractToCsv() {
		//TODO: create variable in form to control extract path
		String path = Configurator.PERFORMANCE_RESULTS_EXTRACT_FILE_PATH;
		getPerformanceResults().printToCsvFile(path);
		setStatusBarText(String.format("Results extracted to [%s]  ",path));
	}

	private void setStatusBarText(String status) {
		System.out.println(status);
		statusBar.setText(status);
	}

	@FXML
	private void viewResults() {
		//TODO: create nested columns e.g Rate of Return | [Local | Base]
		performanceResultAsList = getPerformanceResults().asObservableList();
		propertyColumn1.setCellValueFactory(new PropertyValueFactory<RowData, String>("propertyColumn1"));
		propertyColumn2.setCellValueFactory(new PropertyValueFactory<RowData, String>("propertyColumn2"));
		resultsTableView.setItems(performanceResultAsList);
		setStatusBarText("Displayed results");
	}

	private ObservableList<RowData> getResultsData() {
		if(performanceResultAsList == null) {
			performanceResultAsList = getPerformanceResults().asObservableList();
		}
		return performanceResultAsList;
	}

	private static void autoScroll(TextArea textArea) {
		textArea.setScrollTop(Double.MAX_VALUE);
		textArea.getScrollTop();
	}
}

