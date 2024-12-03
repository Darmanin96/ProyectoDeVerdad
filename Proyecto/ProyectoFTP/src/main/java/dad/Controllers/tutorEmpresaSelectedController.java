package dad.Controllers;

import dad.Conexion.*;
import dad.Model.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.net.*;
import java.sql.*;
import java.util.*;

public class tutorEmpresaSelectedController implements Initializable {

    @FXML
    private TextField Contacto;

    @FXML
    private TextField Nombre;

    @FXML
    private Button Actualizar;

    @FXML
    private Button Cancelar;

    @FXML
    private Button Limpiar;

    @FXML
    private BorderPane root;

    @FXML
    private ChoiceBox<Integer> idEmpresa;

    private tutorEmpresa tutorEmpresa;

    @FXML
    void onActualizarAction(ActionEvent event) {
        if (!validarCampos()) {
            mostrarAlerta("Campos inválidos", "Por favor corrige los errores antes de continuar.");
            return;
        }

        String nombre = Nombre.getText();
        String contacto = Contacto.getText();
        Integer ID_Empresa = idEmpresa.getValue();  // Obtener el valor del ChoiceBox

        String sql = "UPDATE tutor_empresa SET Nombre = ?, Contacto = ?, Id_Empresa = ? WHERE Id_Tutor = ?";
        try (Connection con = Conectar.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, contacto);
            stmt.setInt(3, ID_Empresa);  // Aquí se usa el valor del ChoiceBox
            stmt.setInt(4, tutorEmpresa.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Base de Datos Actualizada");
                alert.setHeaderText("La base de datos ha sido actualizada");
                alert.showAndWait();
            } else {
                mostrarAlerta("Error al actualizar", "No se encontró un tutor con ese ID.");
            }

        } catch (Exception e) {
            mostrarAlerta("Error de conexión", "No se pudo conectar a la base de datos.");
        }
    }


    private void actualizarNombreEmpresa(Integer idNombreEmpresa){
        String sql = "SELECT Nombre FROM empresas WHERE Id_Empresa=?";
        try (Connection conn = Conectar.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,idNombreEmpresa);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                String nombreEmpresa = rs.getString("Nombre");
                tutorEmpresa.setNombreEmpresa(nombreEmpresa);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText("Introduzca correctamente los campos requeridos");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private boolean validarCampos() {
        boolean nombreValido = !Nombre.getText().isBlank() && Nombre.getText().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$");
        boolean ContactoValido = (
                !Contacto.getText().isBlank() &&
                        (Contacto.getText().matches("^\\+?[0-9]{1,4}?[\\s.-]?\\(?[0-9]{1,4}\\)?[\\s.-]?[0-9]{1,4}[\\s.-]?[0-9]{1,9}$") ||
                                Contacto.getText().matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
        );
        return nombreValido && ContactoValido;
    }

    @FXML
    void onCancelarAction(ActionEvent event) {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onLimpiarAction(ActionEvent event) {
        limpiar();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validarCampo(Nombre, "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", "Introducir el Nombre correctamente");
        validarCampo(Contacto, "^\\+?[0-9]{1,4}?[\\s.-]?\\(?[0-9]{1,4}\\)?[\\s.-]?[0-9]{1,4}[\\s.-]?[0-9]{1,9}$|^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
                "Introduzca un teléfono o correo electrónico válido.");
        obtenerIdEmpresa();
        idEmpresa.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                actualizarNombreEmpresa(newValue);
            }
        });

    }

    private void validarCampo(TextField textField, String regex, String errorMessage) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(regex)) {
                textField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                Tooltip tooltip = new Tooltip(errorMessage);
                Tooltip.install(textField, tooltip);
            } else {
                textField.setStyle(null);
                Tooltip.uninstall(textField, null);
            }
        });
    }

    public void obtenerIdEmpresa() {
        String sql = "SELECT Id_Empresa FROM empresas";
        try (Connection con = Conectar.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            idEmpresa.getItems().clear();
            while (rs.next()) {
                idEmpresa.getItems().add(rs.getInt("Id_Empresa"));
            }

            if (tutorEmpresa != null) {
                idEmpresa.setValue(tutorEmpresa.getId_Empresa());
            }
        } catch (Exception e) {
            System.err.println("Error al obtener los IDs de las empresas: " + e.getMessage());
        }
    }

    private void limpiar() {
        Nombre.setText("");
        Contacto.setText("");
    }

    public void obtenertutorEmpresa(tutorEmpresa tutor) {
        if (tutor != null) {
            this.tutorEmpresa = tutor;
            Nombre.textProperty().bindBidirectional(tutor.nombreProperty());
            Contacto.textProperty().bindBidirectional(tutor.contactoProperty());
            idEmpresa.valueProperty().bindBidirectional(tutor.id_EmpresaProperty().asObject());
        }
    }
}
