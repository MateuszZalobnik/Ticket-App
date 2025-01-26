package org.View.client.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.Model.Client;
import org.Model.Event;
import org.Model.TicketPool;
import org.Presenter.IPresenter;
import org.Presenter.PresenterFacade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventList extends StackPane {
    private final int clientId;
    private Event[] events;

    public EventList(int clientId) {
        this.clientId = clientId;
        IPresenter presenter = new PresenterFacade();
        events = presenter.GetEventsById(null);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new javafx.geometry.Insets(10));

        int row = 0;
        int column = 0;

        for (int i = 0; i < events.length; i++) {
            StackPane eventPane = createEventSquare(events[i]);
            if (eventPane == null) {
                continue;
            }

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
            var price = new Text("Cena: " + "Wydarzenie bez biletów");
            price.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            price.setFill(Color.BLACK);
            price.setWrappingWidth(width - 20);
            var textBox = new VBox(10, location, date, organizer, price);
            textBox.setAlignment(Pos.TOP_LEFT);
            textBox.setPadding(new javafx.geometry.Insets(10));
            pane.getChildren().addAll(square, textBox);
            return pane;
        }

        var currentPrice = getCurrentTicketPool(event.ticketPools);
        if (currentPrice == null){
            return null;
        }
        var price = new Text("Cena: " + currentPrice.price + " zł");
        price.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        price.setFill(Color.BLACK);
        price.setWrappingWidth(width - 20);

        var buyButton = new Button("Kup");
        buyButton.setStyle("-fx-text-fill: blue; -fx-font-size: 10px; -fx-underline: true; -fx-background-color: transparent; -fx-cursor: hand;");
        buyButton.setOnAction(e -> {
            IPresenter presenter = new PresenterFacade();
            presenter.BuyTicket(event.ticketPools[0].id, clientId);
            Refresh();
        });

        var textBox = new VBox(10, location, date, organizer, price, buyButton);
        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new javafx.geometry.Insets(10));

        pane.getChildren().addAll(square, textBox);
        return pane;
    }

    private void Refresh() {
        IPresenter presenter = new PresenterFacade();
        events = presenter.GetEventsById(null);
        getChildren().clear();
        getChildren().add(new EventList(clientId));
    }

    public TicketPool getCurrentTicketPool(TicketPool[] ticketPools) {
        Date currentDate = new Date();

        for (TicketPool pool : ticketPools) {
            try {
                if (pool.numberOfSoldTickets < pool.initialNumberOfTickets) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date sellStart = dateFormat.parse(pool.sellStartDate);
                    Date sellEnd = dateFormat.parse(pool.sellEndDate);

                    if (currentDate.after(sellStart) && currentDate.before(sellEnd)) {
                        return pool;
                    }
                }

                if (pool.numberOfSoldTickets >= pool.initialNumberOfTickets && pool.shouldStartWhenPreviousPoolEnd) {
                    return pool;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
