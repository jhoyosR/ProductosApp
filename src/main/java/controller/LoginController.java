package controller;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import view.LoginView;
import view.ProductosView;

import java.sql.*;

public class LoginController {
    private LoginView loginView;

    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        initializeListeners();
    }

    private void initializeListeners() {
        loginView.getLoginButton().setOnAction(e -> handleLogin());
        loginView.getRegisterButton().setOnAction(e -> openRegisterForm()); // Agregar listener al botón de registro
    }

    private void handleLogin() {
        String username = loginView.getUsernameField().getText();
        String id = loginView.getPasswordField().getText();

        if (authenticate(username, id)) {
            showAlert("Éxito", "Inicio de sesión exitoso", Alert.AlertType.INFORMATION);
            openProductosWindow();
        } else {
            showAlert("Error", "Usuario o ID incorrectos", Alert.AlertType.ERROR);
        }
    }

    private boolean authenticate(String username, String id) {
        boolean isAuthenticated = false;
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://bc2hky8dpornvthdni1y-mysql.services.clever-cloud.com:3306/bc2hky8dpornvthdni1y",
                "upgfp6ned3m77ha4", "TdAsLKdnXx0XEHNwKFCB");
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM clientes WHERE nombre = ? AND id_cliente = ?")) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    isAuthenticated = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error de conexión a la base de datos", Alert.AlertType.ERROR);
        }
        return isAuthenticated;
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void openProductosWindow() {
        ProductosView productosView = new ProductosView();
        ProductosController productosController = new ProductosController(productosView);
        Stage productosStage = new Stage();
        productosStage.setTitle("Consulta de Productos");
        productosStage.setScene(new Scene(productosView, 700, 400));
        productosStage.show();
        ((Stage) loginView.getScene().getWindow()).close();
    }

    private void openRegisterForm() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Registrar Cliente");

        GridPane registerPane = new GridPane();
        registerPane.setPadding(new javafx.geometry.Insets(20));
        registerPane.setVgap(10);
        registerPane.setHgap(10);

        TextField nameField = new TextField();
        TextField idField = new TextField();
        Button registerButton = new Button("Registrar");

        registerPane.add(new Label("Nombre: "), 0, 0);
        registerPane.add(nameField, 1, 0);
        registerPane.add(new Label("ID: "), 0, 1);
        registerPane.add(idField, 1, 1);
        registerPane.add(registerButton, 1, 2);

        registerButton.setOnAction(e -> {
            String name = nameField.getText();
            String id = idField.getText();
            if (isClientRegistered(name, id)) {
                showAlert("Error", "El cliente ya está registrado", Alert.AlertType.ERROR);
            } else {
                registerClient(name, id);
                showAlert("Éxito", "Cliente registrado exitosamente", Alert.AlertType.INFORMATION);
                registerStage.close();
            }
        });

        Scene scene = new Scene(registerPane, 300, 200);
        registerStage.setScene(scene);
        registerStage.show();
    }

    private boolean isClientRegistered(String name, String id) {
        boolean isRegistered = false;
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://bc2hky8dpornvthdni1y-mysql.services.clever-cloud.com:3306/bc2hky8dpornvthdni1y",
                "upgfp6ned3m77ha4", "TdAsLKdnXx0XEHNwKFCB");
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM clientes WHERE nombre = ? AND id_cliente = ?")) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    isRegistered = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error de conexión a la base de datos", Alert.AlertType.ERROR);
        }
        return isRegistered;
    }

    private void registerClient(String name, String id) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://bc2hky8dpornvthdni1y-mysql.services.clever-cloud.com:3306/bc2hky8dpornvthdni1y",
                "upgfp6ned3m77ha4", "TdAsLKdnXx0XEHNwKFCB");
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO clientes (nombre, id_cliente) VALUES (?, ?)")) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error al registrar el cliente", Alert.AlertType.ERROR);
        }
    }
}
