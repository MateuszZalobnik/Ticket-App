package org.View.organizer.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CreateNewEvent extends StackPane {

    public CreateNewEvent(String text) {
        // Główny kontener
        StackPane root = new StackPane();
        root.setPadding(new Insets(20));

        // Kontener główny (VBox dla organizacji sekcji)
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);

        // Sekcja formularza wydarzenia
        GridPane eventForm = new GridPane();
        eventForm.setHgap(10);
        eventForm.setVgap(10);

        // Pola tekstowe i etykiety
        Label nameLabel = new Label("Nazwa:");
        TextField nameField = new TextField();

        Label locationLabel = new Label("Miejsce:");
        TextField locationField = new TextField();

        Label startDateLabel = new Label("Data rozpoczęcia:");
        DatePicker startDatePicker = new DatePicker();

        Label endDateLabel = new Label("Data zakończenia:");
        DatePicker endDatePicker = new DatePicker();

        // Dodanie pól do siatki
        eventForm.add(nameLabel, 0, 0);
        eventForm.add(nameField, 0, 1);
        eventForm.add(locationLabel, 0, 2);
        eventForm.add(locationField, 0, 3);
        eventForm.add(startDateLabel, 1, 2);
        eventForm.add(startDatePicker, 1, 3);
        eventForm.add(endDateLabel, 2, 2);
        eventForm.add(endDatePicker, 2, 3);

        // Sekcja puli biletów
        Label ticketPoolsLabel = new Label("Pule biletów:");
        GridPane ticketPoolForm = new GridPane();
        ticketPoolForm.setHgap(10);
        ticketPoolForm.setVgap(10);

        Label poolNumberLabel = new Label("Numer:");
        TextField poolNumberField = new TextField();

        Label quantityLabel = new Label("Ilość:");
        TextField quantityField = new TextField();

        Label priceLabel = new Label("Cena:");
        TextField priceField = new TextField();

        Label poolStartDateLabel = new Label("Data rozpoczęcia:");
        DatePicker poolStartDatePicker = new DatePicker();

        Label poolEndDateLabel = new Label("Data zakończenia:");
        DatePicker poolEndDatePicker = new DatePicker();

        CheckBox startAfterPrevious = new CheckBox("Rozpocznij po wyczerpaniu poprzedniej puli");

        ticketPoolForm.add(poolNumberLabel, 0, 0);
        ticketPoolForm.add(poolNumberField, 0, 1);
        ticketPoolForm.add(quantityLabel, 1, 0);
        ticketPoolForm.add(quantityField, 1, 1);
        ticketPoolForm.add(priceLabel, 2, 0);
        ticketPoolForm.add(priceField, 2, 1);
        ticketPoolForm.add(startAfterPrevious, 0, 2, 2, 1);
        ticketPoolForm.add(poolStartDateLabel, 0, 3);
        ticketPoolForm.add(poolStartDatePicker, 0, 4);

        ticketPoolForm.add(poolEndDateLabel, 1, 3);
        ticketPoolForm.add(poolEndDatePicker, 1, 4);


        Button addPoolButton = new Button("+ Dodaj pulę");

        // Sekcja czarnej listy
        Label blacklistLabel = new Label("Czarna lista");
        TextField blacklistSearchField = new TextField();
        blacklistSearchField.setPromptText("Szukaj...");
        ListView<String> blacklistView = new ListView<>();
        blacklistView.getItems().addAll("Login1", "Login2");

        // Dodanie CellFactory dla przycisków Usuń
        blacklistView.setCellFactory(lv -> new ListCell<>() {
            private final HBox cellContainer = new HBox(10);
            private final Label loginLabel = new Label();
            private final Button deleteButton = new Button("Usuń");

            {
                cellContainer.setAlignment(Pos.CENTER_LEFT);
                deleteButton.setOnAction(event -> {
                    String item = getItem();
                    if (item != null) {
                        getListView().getItems().remove(item); // Usuń element z listy
                    }
                });
                cellContainer.getChildren().addAll(loginLabel, deleteButton);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    loginLabel.setText(item); // Ustaw nazwę loginu
                    setGraphic(cellContainer); // Ustaw niestandardowy układ
                }
            }
        });


        Button saveButton = new Button("Zapisz");

        // Dodanie sekcji do głównego kontenera
        mainContainer.getChildren().addAll(eventForm, ticketPoolsLabel, ticketPoolForm, addPoolButton, blacklistLabel, blacklistSearchField, blacklistView, saveButton);

        // Dodanie głównego kontenera do StackPane
        root.getChildren().add(mainContainer);
        getChildren().add(root);
    }
}