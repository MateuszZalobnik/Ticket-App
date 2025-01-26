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
import org.Model.Event;
import org.Model.Ticket;
import org.Presenter.IPresenter;
import org.Presenter.PresenterFacade;

public class MyTickets extends StackPane {
    private Ticket[] tickets;
    private final int userId;
    private final boolean isHistorical;
    public MyTickets(Integer id, boolean isHistorical) {
        this.isHistorical = isHistorical;
        this.userId = id;
        IPresenter presenter = new PresenterFacade();
        tickets = isHistorical ? presenter.GetHistoricalTickets(id) : presenter.GetTickets(id);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new javafx.geometry.Insets(10));

        int row = 0;
        int column = 0;

        for (int i = 0; i < tickets.length; i++) {
            StackPane eventPane = createTicketSquare(tickets[i]);

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

    private StackPane createTicketSquare(Ticket ticket) {
        var pane = new StackPane();
        var width = 350;

        // Tworzymy prostokąt z paddingiem i kolorystyką
        var square = new Rectangle(width, 150);
        square.setFill(Color.web("#EBE6DD"));
        square.setStroke(Color.BLACK);
        square.setStrokeWidth(0.5);

        // Tworzymy teksty i ustawiamy pogrubienie oraz wyrównanie
        var location = new Text(ticket.location);
        location.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        location.setFill(Color.BLACK);
        location.setWrappingWidth(width - 20);

        var date = new Text(ticket.sellStartDate + " - " + ticket.saleEndDate);
        date.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        date.setFill(Color.BLACK);
        date.setWrappingWidth(width - 20);

        var organizer = new Text("Organizator: " + ticket.organizer);
        organizer.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        organizer.setFill(Color.BLACK);
        organizer.setWrappingWidth(width - 20);

        var price = new Text("Cena: " + ticket.price + " zł");
        price.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        price.setFill(Color.BLACK);
        price.setWrappingWidth(width - 20);

        var button = new Button(isHistorical ? "Oceń" : "Odsprzedaj");
        button.setStyle("-fx-text-fill: blue; -fx-font-size: 10px; -fx-underline: true; -fx-background-color: transparent; -fx-cursor: hand;");
        button.setOnAction(e -> {
            IPresenter presenter = new PresenterFacade();
            // TODO
            Refresh();
        });

        var textBox = new VBox(10, location, date, organizer, price, button);
        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new javafx.geometry.Insets(10));

        pane.getChildren().addAll(square, textBox);
        return pane;
    }

    private void Refresh() {
        IPresenter presenter = new PresenterFacade();
        tickets = isHistorical ? presenter.GetHistoricalTickets(userId) : presenter.GetTickets(userId);
        getChildren().clear();
        getChildren().add(new MyTickets(userId, isHistorical));
    }
}
