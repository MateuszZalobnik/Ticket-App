package org.View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class App extends Application {
	private Scene scene;

	@Override
	public void start(Stage primaryStage) {
		GridPane loginView = UILogin.getView(primaryStage);
		Scene scene = new Scene(loginView, 400, 300);
		primaryStage.setTitle("Ticket-app");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
