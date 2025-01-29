package org.View.organizer;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.Model.Organizer;
import org.View.organizer.components.CreateNewEvent;
import org.View.organizer.components.MyEventsList;
import org.View.shared.components.NavigationBar;

import java.util.List;

public class OrganizerView {

    private final BorderPane mainLayout;
    private final StackPane contentPane;
    private final Organizer organizer;
    private final Stage primaryStage;

    public OrganizerView(Organizer organizer, Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.organizer = organizer;
        mainLayout = new BorderPane();
        contentPane = new StackPane();

        var eventsList = new StackPane(new Text("This is View 1"));
        contentPane.getChildren().add(eventsList);

        var navigationList = List.of("Moje wydarzenia", "Historia wydarzeń", "Dodaj wydarzenie");
        var navigationBar = new NavigationBar(navigationList, (viewName) -> {
            switch (viewName) {
                case "Moje wydarzenia" -> switchView(new MyEventsList(organizer.id, false));
                case "Historia wydarzeń" -> switchView(new MyEventsList(organizer.id, true));
                case "Dodaj wydarzenie" -> switchView(new CreateNewEvent(organizer));
            }
        }, organizer.login, primaryStage);

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