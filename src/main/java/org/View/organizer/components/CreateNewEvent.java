package org.View.organizer.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.Model.Organizer;
import org.Presenter.CreateEventRequest;
import org.Presenter.PresenterFacade;
import org.Presenter.TicketPoolRequest;

import java.util.ArrayList;
import java.util.List;

public class CreateNewEvent extends StackPane {

    private final Organizer organizer;

    public CreateNewEvent(Organizer organizer) {
        this.organizer = organizer;

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

        // Sekcja czarnej listy (zakomentowana na razie)
        // Label blacklistLabel = new Label("Czarna lista");
        // TextField blacklistSearchField = new TextField();
        // blacklistSearchField.setPromptText("Szukaj...");
        // ListView<String> blacklistView = new ListView<>();
        // blacklistView.getItems().addAll("Login1", "Login2");

        // blacklistView.setCellFactory(lv -> new ListCell<>() {
        //     private final HBox cellContainer = new HBox(10);
        //     private final Label loginLabel = new Label();
        //     private final Button deleteButton = new Button("Usuń");

        //     {
        //         cellContainer.setAlignment(Pos.CENTER_LEFT);
        //         deleteButton.setOnAction(event -> {
        //             String item = getItem();
        //             if (item != null) {
        //                 getListView().getItems().remove(item);
        //             }
        //         });
        //         cellContainer.getChildren().addAll(loginLabel, deleteButton);
        //     }

        //     @Override
        //     protected void updateItem(String item, boolean empty) {
        //         super.updateItem(item, empty);
        //         if (empty || item == null) {
        //             setText(null);
        //             setGraphic(null);
        //         } else {
        //             loginLabel.setText(item);
        //             setGraphic(cellContainer);
        //         }
        //     }
        // });

        Button saveButton = new Button("Zapisz");
        ArrayList<TicketPoolRequest> ticketPools = new ArrayList<>();

        VBox ticketPoolsDisplay = new VBox(10);
        ticketPoolsDisplay.setAlignment(Pos.TOP_LEFT);
        // Obsługa zdarzenia kliknięcia przycisku "Dodaj pulę"
        addPoolButton.setOnAction(event -> {
            if (validatePoolData(poolNumberField, quantityField, priceField, poolStartDatePicker, poolEndDatePicker)) {
                int poolNumber = Integer.parseInt(poolNumberField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                float price = Float.parseFloat(priceField.getText());
                String poolStartDate = poolStartDatePicker.getValue() != null ? poolStartDatePicker.getValue().toString() : null;
                String poolEndDate = poolEndDatePicker.getValue() != null ? poolEndDatePicker.getValue().toString() : null;
                boolean startAfter = startAfterPrevious.isSelected();

                // Zapisz dane puli biletów
                var ticketPool = new TicketPoolRequest(quantity, price, poolStartDate, poolEndDate, startAfter, poolNumber);
                ticketPools.add(ticketPool);//dodanie do listy
                System.out.println("Dodano pulę: " + ticketPool);
                // Możesz dodać logikę zapisu do bazy danych tutaj

                // Tworzymy Label do wyświetlania szczegółów puli
                Label ticketPoolDetails = new Label("Numer: " + poolNumber + ", Ilość: " + quantity + ", Cena: " + price
                        + ", Data rozpoczęcia: " + poolStartDate + ", Data zakończenia: " + poolEndDate
                        + ", Rozpocznij po poprzedniej: " + (startAfter ? "Tak" : "Nie"));
                ticketPoolsDisplay.getChildren().add(ticketPoolDetails);
            }
        });

        // Obsługa zdarzenia kliknięcia przycisku "Zapisz"

        saveButton.setOnAction(event -> {
            if (validateFormData(nameField, locationField, startDatePicker, endDatePicker)) {
                String eventName = nameField.getText();
                String eventLocation = locationField.getText();
                String startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : null;
                String endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : null;

                var facade = new PresenterFacade();
                facade.CreateEvent(new CreateEventRequest(startDate, endDate, eventLocation, eventName, organizer.id, ticketPools));

            }
        });

        // Dodanie sekcji do głównego kontenera
        mainContainer.getChildren().addAll(eventForm, ticketPoolsLabel, ticketPoolForm, addPoolButton,ticketPoolsDisplay, saveButton);

        // Dodanie paska przewijania
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        // Dodanie głównego kontenera do StackPane
        root.getChildren().add(scrollPane);
        getChildren().add(root);
    }

    private boolean validateFormData(TextField nameField, TextField locationField, DatePicker startDatePicker, DatePicker endDatePicker) {
        if (nameField.getText().isEmpty() || locationField.getText().isEmpty() || startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert("Wszystkie pola muszą być wypełnione.");
            return false;
        }
        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showAlert("Data rozpoczęcia nie może być późniejsza niż data zakończenia.");
            return false;
        }
        return true;
    }

    private boolean validatePoolData(TextField poolNumberField, TextField quantityField, TextField priceField, DatePicker poolStartDatePicker, DatePicker poolEndDatePicker) {
        try {
            if (poolNumberField.getText().isEmpty() || quantityField.getText().isEmpty() || priceField.getText().isEmpty() || poolStartDatePicker.getValue() == null || poolEndDatePicker.getValue() == null) {
                showAlert("Wszystkie pola puli biletów muszą być wypełnione.");
                return false;
            }

            // Sprawdzenie, czy numer, ilość i cena są liczbami dodatnimi
            int poolNumber = Integer.parseInt(poolNumberField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            float price = Float.parseFloat(priceField.getText());

            if (poolNumber <= 0) {
                showAlert("Numer puli musi być liczbą dodatnią.");
                return false;
            }
            if (quantity <= 0) {
                showAlert("Ilość musi być liczbą dodatnią.");
                return false;
            }
            if (price <= 0) {
                showAlert("Cena musi być liczbą dodatnią.");
                return false;
            }

            if (poolStartDatePicker.getValue().isAfter(poolEndDatePicker.getValue())) {
                showAlert("Data rozpoczęcia puli nie może być późniejsza niż data zakończenia.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Numer, ilość i cena muszą być liczbami.");
            return false;
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}