package org.View.organizer.components;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.Model.Event;
import org.Presenter.IPresenter;
import org.Presenter.PresenterFacade;

import static java.sql.Types.NULL;

public class MyEventsList extends StackPane {
    public MyEventsList(String text) {
        IPresenter presenter = new PresenterFacade();
        //        var events = presenter.GetEventsById(NULL);
        Event[] events = new Event[3];

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);

        int row = 0;
        int column = 0;

        for (int i = 0; i < events.length; i++) {
            StackPane eventPane = createEventSquare(events[i]);

            grid.add(eventPane, column, row);

            column++;
            if (column == 3) {
                row++;
            }
        }

        getChildren().add(grid);
    }

    private StackPane createEventSquare(Event event) {
        StackPane pane = new StackPane();

        Rectangle square = new Rectangle(100, 100, Color.LIGHTGRAY);
        square.setArcWidth(15); // Zaokrąglone rogi
        square.setArcHeight(15);

        // Tekst w środku kwadratu
        Text eventText = new Text("event.location");
        eventText.setFill(Color.BLACK);

        // Dodajemy kwadrat i tekst do StackPane
        pane.getChildren().addAll(square, eventText);
        return pane;
    }
}
