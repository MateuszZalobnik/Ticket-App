package org.View.client.components;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.Model.Event;
import org.Model.Ticket;
import org.Presenter.IPresenter;
import org.Presenter.PresenterFacade;
import org.View.client.ClientView;

public class MyResells extends StackPane {
    private Ticket[] tickets;
    private int userId;
    public MyResells(int userId) {
        this.userId = userId;
        // Fetch tickets for resale from the presenter
        IPresenter presenter = new PresenterFacade();
        tickets = presenter.GetTicketsForResell(userId); // Assumes the presenter has this method implemented
        System.out.println(tickets.length);
        // Create a GridPane layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new javafx.geometry.Insets(10));

        int row = 0;
        int column = 0;

        // Populate the grid with ticket information
        for (int i = 0; i < tickets.length; i++) {
            StackPane ticketPane = createTicketSquare(tickets[i]);

            GridPane.setFillWidth(ticketPane, true);
            ticketPane.setMaxWidth(Double.MAX_VALUE);

            grid.add(ticketPane, column, row);

            column++;
            if (column == 3) { // Reset column after 3 elements
                column = 0;
                row++;
            }
        }

        // Add the grid to a scrollable pane
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        getChildren().add(scrollPane); // Add the scroll pane to the main StackPane
    }

    /**
     * Creates a visual representation of a ticket.
     */
    private StackPane createTicketSquare(Ticket ticket) {
        var pane = new StackPane();
        var width = 350;

        // Rectangle as the background
        var square = new Rectangle(width, 150);
        square.setFill(Color.web("#EBE6DD"));
        square.setStroke(Color.BLACK);
        square.setStrokeWidth(0.5);

        // Ticket details
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

        // Add elements to the pane
        var vbox = new VBox(location, date, organizer, price);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(5);

        pane.getChildren().addAll(square, vbox);
        return pane;
    }
}

