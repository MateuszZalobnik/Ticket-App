package org.View.organizer.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.Model.Client;
import org.Model.Event;
import org.Model.User;
import org.Presenter.IPresenter;
import org.Presenter.PresenterFacade;
import org.View.client.ClientView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        getChildren().add(scrollPane);
    }

    private StackPane createEventSquare(Event event) {
        var pane = new StackPane();
        var width = 350;

        var square = new Rectangle(width, 200);
        square.setFill(Color.web("#EBE6DD"));
        square.setStroke(Color.BLACK);
        square.setStrokeWidth(0.5);

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

        var button = new Button("więcej...");
        button.setStyle("-fx-text-fill: blue; -fx-font-size: 10px; -fx-underline: true; -fx-background-color: transparent; -fx-cursor: hand;");
        button.setOnAction(e -> OpenEventDetails(event.id));

        VBox textBox = new VBox(8);
        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new javafx.geometry.Insets(10));
        textBox.getChildren().addAll(location, date, organizer);

        if (event.ticketPools.length == 0) {
            var ticketPoolsTitle = new Text("Wstęp wolny");
            ticketPoolsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            ticketPoolsTitle.setFill(Color.BLACK);
            ticketPoolsTitle.setWrappingWidth(width - 20);
            textBox.getChildren().add(ticketPoolsTitle);
        } else {
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

            textBox.getChildren().addAll(ticketPoolsTitle, ticketPools);
        }

        VBox container = new VBox();
        container.setAlignment(Pos.TOP_LEFT);
        container.setSpacing(10);
        container.setPadding(new javafx.geometry.Insets(10));
        container.getChildren().addAll(textBox, button);

        VBox.setVgrow(textBox, Priority.ALWAYS);
        VBox.setVgrow(button, Priority.NEVER);

        pane.getChildren().addAll(square, container);
        return pane;
    }


    private void OpenEventDetails(int eventId) {
        IPresenter presenter = new PresenterFacade();
        var event = presenter.GetEventDetails(eventId);

        var stage = new Stage();

        var mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: white;");

        var title = new Text(event.location);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        var date = new Text(event.sellStartDate + " - " + event.saleEndDate);
        date.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

        // Informacje o biletach
        var ticketsLabel = new Text("Bilety:");
        ticketsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        var ticketsInfo = new VBox(5);
        for (var ticketPool : event.ticketPools) {
            var ticketText = new Text("Pula nr " + ticketPool.poolNumber + ": " +
                    ticketPool.numberOfSoldTickets + "/" + ticketPool.initialNumberOfTickets);
            ticketText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            ticketsInfo.getChildren().add(ticketText);
        }

        // Liczba uczestników + pole wyszukiwania
        var participantsLabel = new Text("Uczestnicy (" + event.users.length + "):");
        participantsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        var searchField = new TextField();
        searchField.setPromptText("🔍 Wyszukaj uczestnika...");

        var usersList = new VBox(5);
        updateUsersList(usersList, event.users, "");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateUsersList(usersList, event.users, newValue);
        });

        var opinionsLabel = new Text("Opinie:");
        opinionsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Opinie
        var opinionsList = new VBox(5);
        if (event.opinions.length == 0){
            var noOpinions = new Text("Brak opinii");
            noOpinions.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            opinionsList.getChildren().add(noOpinions);
        }

        for (var opinion : event.opinions) {
            var opinionBox = new HBox(10);
            opinionBox.setStyle("-fx-background-color: #EBE6DD; -fx-padding: 10;");

            // Wyróżnienie loginu użytkownika
            var userLabel = new Text(opinion.userLogin);
            userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            userLabel.setStyle("-fx-text-fill: #3B3B3B;");

            // Treść opinii
            var opinionText = new Text(opinion.comment);
            opinionText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            opinionText.setStyle("-fx-text-fill: #5A5A5A;");

            // Ocena
            var rating = new Text(opinion.opinion + "/5");
            rating.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            rating.setStyle("-fx-text-fill: #FF9900;");

            // Kontener na login i tekst opinii
            var opinionContainer = new VBox(5, userLabel, opinionText);
            opinionContainer.setStyle("-fx-spacing: 5;");

            // Dodanie elementów do kontenera HBox
            opinionBox.getChildren().addAll(opinionContainer, rating);
            HBox.setHgrow(opinionContainer, Priority.ALWAYS);
            opinionBox.setAlignment(Pos.CENTER_LEFT);

            // Dodanie opinii do listy
            opinionsList.getChildren().add(opinionBox);
        }


        mainLayout.getChildren().addAll(
                title, date,
                ticketsLabel, ticketsInfo,
                searchField, usersList,
                participantsLabel,
                opinionsLabel, opinionsList
        );

        stage.setTitle("Ticket-app");
        stage.setScene(new Scene(mainLayout, 800, 600));
        stage.show();
    }

    private void updateUsersList(VBox usersList, User[] users, String searchText) {
        usersList.getChildren().clear();

        List<User> filteredUsers = Arrays.stream(users)
                .filter(user -> (user.login).toLowerCase().contains(searchText.toLowerCase()))
                .limit(10)
                .toList();

        for (User user : filteredUsers) {
            var userText = new Text(user.login + " (" + user.email + ")");
            userText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            usersList.getChildren().add(userText);
        }
    }

}