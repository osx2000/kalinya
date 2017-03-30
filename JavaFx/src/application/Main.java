package application;

import java.io.IOException;
import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		String resourceLocation = "";
		resourceLocation = "keypad.fxml";
		resourceLocation = "PerformanceMeasurement.fxml";
		resourceLocation = "PerformanceMeasurement.fxml";
		
		showStageUsingResource(stage, resourceLocation);
	}
	
	@Override
	public void stop() {
		Map<Thread, StackTraceElement[]> threadDetails = Thread.getAllStackTraces();
		for(Thread thread: threadDetails.keySet()) {
			if(thread instanceof FileWatcher) {
				((FileWatcher) thread).stopThread();
				try {
					thread.join();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	private void showStageUsingResource(Stage stage, String resourceLocation) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource(resourceLocation));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Scene scene = new Scene(root);
		System.out.println(root.getProperties().toString());
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}
}
