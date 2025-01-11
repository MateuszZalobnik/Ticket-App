package org.View;


import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.Model.Facade;
import org.Presenter.PresenterFacade;

import java.awt.*;

public class UILogin extends BaseUILogin {
    public static GridPane getView(Stage stage) {
        UILogin instance = new UILogin();
        GridPane gridPane = instance.createBaseLayout("Zaloguj się");

        // Center align components
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Login field
        TextField loginField = new TextField();
        loginField.setPromptText("Login");
        styleTextField(loginField);
        gridPane.add(loginField, 0, 1);

        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Hasło");
        styleTextField(passwordField);
        gridPane.add(passwordField, 0, 2);

        // Login button
        Button loginButton = new Button("Zaloguj się");
        styleButton(loginButton);
        gridPane.add(loginButton, 0, 3);

        // Register link
        Label registerLink = new Label("Jeśli nie masz konta, zarejestruj się.");
        registerLink.setTextFill(Color.BLUE);
        registerLink.setStyle("-fx-underline: true;");
        gridPane.add(registerLink, 0, 4);

        // Event for switching to the Sign-In view
        registerLink.setOnMouseClicked(event -> {
            GridPane signInView = UISignIn.getView(stage);
            Scene scene = new Scene(signInView, 400, 300);
            stage.setScene(scene);
        });

        // Event for sending info about log in to system
        loginButton.setOnMouseClicked(event -> {
            String login = loginField.getText().trim();
            String password = passwordField.getText().trim();

            PresenterFacade presenterFacade = new PresenterFacade();
            presenterFacade.LogIn(login, password);
            Facade facade = new Facade();
            facade.getConnection();
        });

        return gridPane;
    }

    // Utility methods for styling
    private static void styleTextField(TextField textField) {
        textField.setStyle("-fx-background-color: #D3D3D3; -fx-border-color: lightgray; -fx-font-size: 14px;");
        textField.setPrefWidth(300);
    }

    private static void styleButton(Button button) {
        button.setStyle("-fx-background-color: #8FBC8F; -fx-text-fill: black; -fx-font-size: 14px;");
        button.setPrefWidth(150);
    }
}