package org.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.Presenter.PresenterFacade;
import javafx.scene.paint.Color;

import java.awt.*;

public class UISignIn extends BaseUILogin {
    public static GridPane getView(Stage stage) {
        UISignIn instance = new UISignIn();
        GridPane gridPane = instance.createBaseLayout("Zarejestruj się");

        // Center align components
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // E-mail field
        TextField emailField = new TextField();
        emailField.setPromptText("E-mail");
        styleTextField(emailField);
        gridPane.add(emailField, 0, 1);

        // Login field
        TextField loginField = new TextField();
        loginField.setPromptText("Login");
        styleTextField(loginField);
        gridPane.add(loginField, 0, 2);

        // Password field
        TextField passwordField = new PasswordField();
        passwordField.setPromptText("Hasło");
        styleTextField(passwordField);
        gridPane.add(passwordField, 0, 3);

        // Role field as a dropdown list
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Klient", "Organizator"); // Add predefined roles
        roleComboBox.setPromptText("Rola"); // Placeholder text
        roleComboBox.setStyle("-fx-background-color: #D3D3D3; -fx-border-color: lightgray; -fx-font-size: 14px;");
        roleComboBox.setPrefWidth(300);
        gridPane.add(roleComboBox, 0, 4);

        // Register button
        Button signInButton = new Button("Zarejestruj się");
        styleButton(signInButton);
        gridPane.add(signInButton, 0, 5);

        Label warningLabel = new Label();
        warningLabel.setTextFill(Color.RED); // Set warning text color
        warningLabel.setVisible(false); // Initially, make it invisible

        // button action
        signInButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String login = loginField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleComboBox.getValue();


            // Check if all necessary fields are provided
            if (!email.isEmpty() && !login.isEmpty() && !password.isEmpty() && role != null) {
                int role_int = -1;

                if (role.equals("Klient")) {
                    role_int = 0;
                } else if (role.equals("Organizator")) {
                    role_int = 1;
                }

                PresenterFacade facade = new PresenterFacade();
                facade.CreateAccount(login, email, password, role_int);

                GridPane loginView = UILogin.getView(stage);
                Scene scene = new Scene(loginView, 400, 300);
                stage.setScene(scene);

                warningLabel.setVisible(false);
                System.out.println(login + " " + email + " " + password + " " + role_int);
            } else {
                warningLabel.setText("Proszę uzupełnić wszystkie wymagane pola!"); // cos nie działa
                warningLabel.setVisible(true);
            }
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