package org.View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.View.organizer.OrganizerView;

public class App extends Application {
	private IDisplay[] view;

	@Override
	public void start(Stage stage) {
		OrganizerView organizerView = new OrganizerView();

		Scene scene = new Scene(organizerView.getView(), 800, 600);
		stage.setScene(scene);
		stage.setTitle("Organizator");
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}