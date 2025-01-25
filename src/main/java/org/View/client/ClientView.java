package org.View.client;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.Model.Client;
import org.Model.User;
import org.View.organizer.components.CreateNewEvent;
import org.View.organizer.components.MyEventsList;
import org.View.shared.components.NavigationBar;

import java.util.List;

public class ClientView {
    private final BorderPane mainLayout;
    private final StackPane contentPane;
    private final Client client;
    private final Stage primaryStage;

    public ClientView(Client client, Stage primaryStage) {
        this.client = client;
        this.primaryStage = primaryStage;
        mainLayout = new BorderPane();
        contentPane = new StackPane();

        var eventsList = new StackPane(new Text("This is View 1"));
        contentPane.getChildren().add(eventsList);

        var navigationList = List.of("Moje wydarzenia");
        var navigationBar = new NavigationBar(navigationList, (viewName) -> {
            switch (viewName) {
                case "Moje wydarzenia" -> switchView(new StackPane(new Text("This is client 1")));
            }
        }, client.login, primaryStage);

        mainLayout.setTop(navigationBar.getNavigationBar());
        mainLayout.setCenter(contentPane);

        navigationBar.navigateTo("Moje wydarzenia");
    }

    public BorderPane getView() {
        return mainLayout;
    }

    private void switchView(StackPane view) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(view);
    }
}
