package org.View.shared.components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;

public class NavigationBar {

    private final HBox navigationBar;
    private Button activeButton;
    private final Stage primaryStage;

    public NavigationBar(List<String> pages, Consumer<String> onNavigate, String username, Stage primaryStage) {
        this.primaryStage = primaryStage;
        navigationBar = new HBox(10);
        navigationBar.setStyle("-fx-background-color: #EBE6DD; -fx-padding: 10;");
        navigationBar.setPrefHeight(50);

        for (String page : pages) {
            var button = createNavButton(page, onNavigate);
            navigationBar.getChildren().add(button);
        }

        var spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // right side: username and logout button
        var usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14; -fx-font-weight: bold;");

        var logoutButton = new Button("Wyloguj się");
        logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-cursor: hand;");
        logoutButton.setOnAction(e -> {
            primaryStage.close();
        });

        // Add all components to the navigation bar
        navigationBar.getChildren().addAll(spacer, usernameLabel, logoutButton);
    }

    public HBox getNavigationBar() {
        return navigationBar;
    }

    private Button createNavButton(String viewName, Consumer<String> onNavigate) {
        var button = new Button(viewName);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-cursor: hand;");
        button.setOnAction(e -> {
            onNavigate.accept(viewName);
            setActiveButton(button);
        });
        return button;
    }

    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-cursor: hand;");
        }
        activeButton = button;
        activeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-underline: true; -fx-cursor: hand;");
    }

    public void navigateTo(String viewName) {
        for (var node : navigationBar.getChildren()) {
            if (node instanceof Button) {
                var button = (Button) node;
                if (button.getText().equals(viewName)) {
                    button.fire();
                    return;
                }
            }
        }
    }
}

