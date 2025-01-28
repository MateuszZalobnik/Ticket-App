package org.View.organizer.components;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.Model.Event;
import org.Presenter.IPresenter;
import org.Presenter.PresenterFacade;

public class MyEventsList extends StackPane {
    public MyEventsList(Integer id, boolean isHistorical) {
        IPresenter presenter = new PresenterFacade();
        var events = isHistorical ? presenter.GetHistoricalEventsById(id) : presenter.GetEventsById(id);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new javafx.geometry.Insets(10));

        int row = 0;
        int column = 0;

        for (int i = 0; i < events.length; i++) {
            StackPane eventPane = createEventSquare(events[i]);

            GridPane.setFillWidth(eventPane, true);
            eventPane.setMaxWidth(Double.MAX_VALUE);

            grid.add(eventPane, column, row);

            column++;
            if (column == 3) { // Resetowanie kolumny po 3 elementach
                column = 0;
                row++;
            }
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        getChildren().add(grid);
    }

    private StackPane createEventSquare(Event event) {
        var pane = new StackPane();
        var width = 350;

        // Tworzymy prostokąt z paddingiem i kolorystyką
        var square = new Rectangle(width, 150);
        square.setFill(Color.web("#EBE6DD"));
        square.setStroke(Color.BLACK);
        square.setStrokeWidth(0.5);

        // Tworzymy teksty i ustawiamy pogrubienie oraz wyrównanie
        var location = new Text(event.location);
        location.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        location.setFill(Color.BLACK);
        location.setWrappingWidth(width - 20);

        var date = new Text(event.sellStartDate + " - " + event.saleEndDate);
        date.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        date.setFill(Color.BLACK);
        date.setWrappingWidth(width - 20);

        var organizer = new Text("Organizator: " + event.organizer);
        organizer.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        organizer.setFill(Color.BLACK);
        organizer.setWrappingWidth(width - 20);

        if (event.ticketPools.length == 0) {
            var ticketPoolsTitle = new Text("Wstęp wolny");
            ticketPoolsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            ticketPoolsTitle.setFill(Color.BLACK);
            ticketPoolsTitle.setWrappingWidth(width - 20);

            var textBox = new VBox(10, location, date, organizer, ticketPoolsTitle);
            textBox.setAlignment(Pos.TOP_CENTER);
            textBox.setPadding(new javafx.geometry.Insets(10));

            ticketPoolsTitle.setTranslateY(10);

            pane.getChildren().addAll(square, textBox);
            return pane;
        }

        var ticketPoolsTitle = new Text("Bilety:");
        ticketPoolsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        ticketPoolsTitle.setFill(Color.BLACK);
        ticketPoolsTitle.setWrappingWidth(width - 20);

        var ticketPools = new Text();
        ticketPools.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        String ticketPoolsText = "";
        for (int i = 0; i < event.ticketPools.length; i++) {
            var ticketPool = event.ticketPools[i];
            ticketPoolsText += "Pula nr " + ticketPool.poolNumber + ": pozostało:" + ticketPool.initialNumberOfTickets + " - " + ticketPool.price + " PLN\n";
        }
        ticketPools.setText(ticketPoolsText);
        ticketPools.setFill(Color.BLACK);
        ticketPools.setWrappingWidth(width - 20);

        var textBox = new VBox(10, location, date, organizer, ticketPoolsTitle, ticketPools);
        textBox.setAlignment(Pos.TOP_CENTER);
        textBox.setPadding(new javafx.geometry.Insets(10));

        ticketPoolsTitle.setTranslateY(10);
        ticketPools.setTranslateY(10);

        pane.getChildren().addAll(square, textBox);
        return pane;
    }
}
