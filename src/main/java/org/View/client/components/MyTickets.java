package org.View.client.components;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Stage;
import org.Model.Event;
import org.Model.Ticket;
import org.Presenter.IPresenter;
import org.Presenter.PresenterFacade;
import org.View.client.ClientView;

import java.util.HashMap;
import java.util.Map;

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
            if (isHistorical) {
                OpenRateScene(ticket.ticketPool.eventId);
            } else {
                showSellTicketPopup(ticket);
            }
            Refresh();
        });

        var textBox = new VBox(10, location, date, organizer, price, button);
        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new javafx.geometry.Insets(10));

        pane.getChildren().addAll(square, textBox);
        return pane;
    }

    private void showSellTicketPopup(Ticket ticket) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Odsprzedaż biletu");

        // Layout for popup
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(20));

        // Instructions
        var label = new Text("Podaj cenę odsprzedaży:");
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        // Price input
        var priceField = new TextField();
        priceField.setPromptText("Cena w zł");

        // Error message
        var errorMessage = new Text();
        errorMessage.setFill(Color.RED);
        errorMessage.setVisible(false);

        // Debugging message
        var debugMessage = new Text();
        debugMessage.setFill(Color.BLUE);
        debugMessage.setVisible(false);

        // Submit button
        var submitButton = new Button("Potwierdź");
        submitButton.setOnAction(e -> {
            String priceText = priceField.getText().trim();
            try {
                // Validate price input
                if (priceText.isEmpty()) {
                    throw new IllegalArgumentException("Cena nie może być pusta.");
                }

                System.out.println(priceText);
                float price = Float.parseFloat(priceText);
                System.out.println(price);
                if (price <= 0) {
                    throw new IllegalArgumentException("Cena musi być większa od 0.");
                }

                IPresenter presenter = new PresenterFacade();
                presenter.ResellTicket(ticket.id, price);

                popupStage.close();
                Refresh();

            } catch (NumberFormatException ex) {
                errorMessage.setText("Cena musi być liczbą.");
                errorMessage.setVisible(true);
                priceField.setStyle("-fx-border-color: red;");
            } catch (IllegalArgumentException ex) {
                errorMessage.setText(ex.getMessage());
                errorMessage.setVisible(true);
                priceField.setStyle("-fx-border-color: red;");
            } catch (Exception ex) {
                // Catching unexpected exceptions
                errorMessage.setText("Nieoczekiwany błąd: " + ex.getMessage());
                errorMessage.setVisible(true);
                ex.printStackTrace(); // For debugging in the console
            }
        });

        // Layout organization
        layout.getChildren().addAll(label, priceField, errorMessage, debugMessage, submitButton);

        // Show popup
        Scene scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.show();
    }


    private void Refresh() {
        IPresenter presenter = new PresenterFacade();
        tickets = isHistorical ? presenter.GetHistoricalTickets(userId) : presenter.GetTickets(userId);

        // Wyczyść siatkę i dodaj nowe bilety
        getChildren().clear();

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

        getChildren().add(grid);
    }

    private void OpenRateScene(int eventId) {
        Stage rateStage = new Stage();
        rateStage.setTitle("Oceń wydarzenie");

        VBox layout = new VBox(20);
        layout.setPadding(new javafx.geometry.Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Oceń wydarzenie");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane ratingsGrid = new GridPane();
        ratingsGrid.setHgap(10);
        ratingsGrid.setVgap(20);
        ratingsGrid.setAlignment(Pos.CENTER);

        String[] categories = {"Ocena ogólna", "Atmosfera", "Lokalizacja", "Obsługa", "Jakość/ceny"};
        Map<String, Integer> ratings = new HashMap<>();

        for (int i = 0; i < categories.length; i++) {
            String category = categories[i];
            Text categoryText = new Text(category);
            categoryText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

            HBox starsBox = createStarsBox(ratings, category);

            ratingsGrid.add(categoryText, 0, i);
            ratingsGrid.add(starsBox, 1, i);
        }

        TextArea commentBox = new TextArea();
        commentBox.setPromptText("Napisz opinię...");
        commentBox.setWrapText(true);
        commentBox.setPrefHeight(100);
        commentBox.setPrefWidth(400);

        Button submitButton = new Button("Prześlij opinię");
        submitButton.setStyle("-fx-background-color: #A4C3B2; -fx-text-fill: black;");
        submitButton.setOnAction(e -> {
            if (ratings.size() < categories.length || commentBox.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Dodawanie opinii nie powiodło się");
                alert.setContentText("Wypełnij wszystkie pola, aby dodać opinię.");
                alert.showAndWait();
                return;
            }
            IPresenter presenter = new PresenterFacade();

            int averageRating = ratings.values().stream().mapToInt(Integer::intValue).sum() / ratings.size();
            presenter.AddOpinion(userId, averageRating, commentBox.getText(), eventId);
            rateStage.close();
        });

        layout.getChildren().addAll(title, ratingsGrid, new Text("Komentarz:"), commentBox, submitButton);

        Scene scene = new Scene(layout, 600, 500);
        rateStage.setScene(scene);
        rateStage.initModality(Modality.APPLICATION_MODAL);
        rateStage.showAndWait();
    }

    private HBox createStarsBox(Map<String, Integer> ratings, String category) {
        HBox starsBox = new HBox(5);
        starsBox.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 5; i++) {
            int starValue = i;
            Button star = new Button("★");
            star.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            star.setStyle("-fx-background-color: transparent; -fx-text-fill: gray;");

            star.setOnAction(e -> {
                ratings.put(category, starValue);
                updateStarColors(starsBox, starValue);
            });

            starsBox.getChildren().add(star);
        }

        return starsBox;
    }

    private void updateStarColors(HBox starsBox, int rating) {
        for (int i = 0; i < starsBox.getChildren().size(); i++) {
            Button star = (Button) starsBox.getChildren().get(i);
            if (i < rating) {
                star.setStyle("-fx-background-color: transparent; -fx-text-fill: #346C4C;");
            } else {
                star.setStyle("-fx-background-color: transparent; -fx-text-fill: gray;");
            }
        }
    }

}
