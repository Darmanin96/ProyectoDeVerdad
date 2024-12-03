package dad.Controllers;

import dad.Model.Comentarios;
import dad.Conexion.Conectar;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ComentariosSelectedController implements Initializable {

    @FXML
    private Button Añadir;

    @FXML
    private Button Cancelar;

    @FXML
    private TextArea Comentario;

    @FXML
    private DatePicker Fecha;

    @FXML
    private ChoiceBox<Integer> IdEmpresa;

    @FXML
    private Button Limpiar;

    @FXML
    private BorderPane root;

    private Comentarios comentariosSelected;

    private boolean confirmar = false;

    @FXML
    void onAñadirAction(ActionEvent event) {
        // Recuperar los datos del formulario
        LocalDate fecha = Fecha.getValue();
        String comentarioText = Comentario.getText();
        Integer idEmpresa = IdEmpresa.getValue();

        // Validar los campos
        if (!validarCampos()) {
            mostrarAlerta("Campos inválidos", "Por favor, rellena todos los campos correctamente.");
            return;
        }

        String sql;
        if (comentariosSelected == null) {
            sql = "INSERT INTO comentarios_empresa (Comentario, Fecha_Comentario, Id_Empresa) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE comentarios_empresa SET Comentario = ?, Fecha_Comentario = ?, Id_Empresa = ? WHERE Id_Comentario = ?";
        }

        try (Connection con = Conectar.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, comentarioText);
            stmt.setDate(2, Date.valueOf(fecha));
            stmt.setInt(3, idEmpresa);

            if (comentariosSelected != null) {
                stmt.setInt(4, comentariosSelected.getIdComentario()); // Solo si es actualización
            }

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                mostrarConfirmacion("Operación exitosa", "La base de datos ha sido actualizada correctamente.");
                confirmar = true;
            } else {
                mostrarAlerta("Error al agregar/actualizar", "Hubo un error al intentar agregar o actualizar el comentario.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de conexión", "No se pudo conectar a la base de datos.");
        }
    }

    // Método para mostrar una alerta
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para mostrar una confirmación
    private void mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
    void onLImpiarAction(ActionEvent event) {
        limpiar();
    }

    private void limpiar() {
        Comentario.setText("");
        Fecha.setValue(null);
        IdEmpresa.setValue(null);
    }

    // Método para validar los campos
    private boolean validarCampos() {
        boolean comentarioValido = !Comentario.getText().isBlank();
        boolean fechaValida = Fecha.getValue() != null;
        boolean idEmpresaValido = IdEmpresa.getValue() != null;

        return comentarioValido && fechaValida && idEmpresaValido;
    }

    public void obtenerComentario(Comentarios comentario) {
        if (comentario != null) {
            this.comentariosSelected = comentario;
            Comentario.textProperty().bindBidirectional(comentario.comentarioProperty());
            Fecha.valueProperty().bindBidirectional(comentario.fechaComentarioProperty());
            IdEmpresa.valueProperty().bindBidirectional(comentario.idEmpresaProperty().asObject());

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validarCampo(Comentario, "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", "Introduzca un comentario válido.");
        Fecha.setStyle("-fx-border-color: none;");
        cargarEmpresas();
        IdEmpresa.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                actualizarNombreEmpresa(newValue);
            }
        });
    }

    // Método para validar cada campo con un regex
    private void validarCampo(TextArea textField, String regex, String errorMessage) {
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

    private void actualizarNombreEmpresa(Integer idEmpresa) {
        String sql = "SELECT Nombre FROM empresas WHERE Id_Empresa=?";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmpresa);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nombreEmpresa = rs.getString("Nombre");
                comentariosSelected.setNombreEmpresa(nombreEmpresa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cargarEmpresas() {
        // Obtener las empresas desde la base de datos
        String sql = "SELECT Id_Empresa, Nombre FROM empresas";
        try (Connection con = Conectar.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                IdEmpresa.getItems().add(rs.getInt("Id_Empresa"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de carga", "Hubo un error al cargar las empresas.");
        }
    }

    public boolean isConfirmar() {
        return confirmar;
    }

    public void setConfirmar(boolean confirmar) {
        this.confirmar = confirmar;
    }

    public Button getAñadir() {
        return Añadir;
    }

    public void setAñadir(Button añadir) {
        Añadir = añadir;
    }

    public Button getCancelar() {
        return Cancelar;
    }

    public void setCancelar(Button cancelar) {
        Cancelar = cancelar;
    }

    public TextArea getComentario() {
        return Comentario;
    }

    public void setComentario(TextArea comentario) {
        Comentario = comentario;
    }

    public DatePicker getFecha() {
        return Fecha;
    }

    public void setFecha(DatePicker fecha) {
        Fecha = fecha;
    }

    public ChoiceBox<Integer> getIdEmpresa() {
        return IdEmpresa;
    }

    public void setIdEmpresa(ChoiceBox<Integer> idEmpresa) {
        IdEmpresa = idEmpresa;
    }

    public Button getLimpiar() {
        return Limpiar;
    }

    public void setLimpiar(Button limpiar) {
        Limpiar = limpiar;
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public Comentarios getComentariosSelected() {
        return comentariosSelected;
    }

    public void setComentariosSelected(Comentarios comentariosSelected) {
        this.comentariosSelected = comentariosSelected;
    }
}
