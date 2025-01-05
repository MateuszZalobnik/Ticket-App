package org.View.organizer;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.View.organizer.components.MyEventsList;
import org.View.shared.components.NavigationBar;

import java.util.List;

public class OrganizerView {

    private final BorderPane mainLayout;
    private final StackPane contentPane;

    public OrganizerView() {
        mainLayout = new BorderPane();
        contentPane = new StackPane();

        var eventsList = new StackPane(new Text("This is View 1"));
        contentPane.getChildren().add(eventsList);

        var navigationList = List.of("Moje wydarzenia", "Historia wydarzeń", "Dodaj wydarzenie");
        var navigationBar = new NavigationBar(navigationList, (viewName) -> {
            switch (viewName) {
                case "Moje wydarzenia" -> switchView(new MyEventsList("This is View 1"));
                case "Historia wydarzeń" -> switchView(new StackPane(new Text("This is View 2")));
                case "Dodaj wydarzenie" -> switchView(new StackPane(new Text("This is View 3")));
            }
        }, "Jan Kowalski");

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