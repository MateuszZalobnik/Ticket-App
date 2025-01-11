package org.View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.Model.Facade;

public class App extends Application {
	private IDisplay[] view;
	private Scene scene;

	@Override
	public void start(Stage primaryStage) {
		// Start with the Login view
		GridPane loginView = UILogin.getView(primaryStage);
		Scene scene = new Scene(loginView, 400, 300);
		primaryStage.setTitle("Login and Registration");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
