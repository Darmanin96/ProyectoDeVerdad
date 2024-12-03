package dad.Controllers;

import dad.Conexion.*;
import dad.Model.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.net.*;
import java.sql.*;
import java.util.*;

public class AlumnSelectedController implements Initializable {

    @FXML
    private TextField Apellidos;

    @FXML
    private TextField Contactos;

    @FXML
    private ChoiceBox<dad.Model.Curso> Curso;

    @FXML
    private TextField Nombre;

    @FXML
    private CheckBox Pendientes;

    @FXML
    private BorderPane root;

    @FXML
    private ChoiceBox<Integer> tutorGrupo;

    private Alumno alumnoSeleccionado;

    @FXML
    private AnchorPane detallePanel;

    private VisitaSelectedController visitaSelectedController;


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

    private boolean validarCampos() {
        boolean nombreValido = !Nombre.getText().isBlank() && Nombre.getText().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$");
        boolean apellidosValido = !Apellidos.getText().isBlank() && Apellidos.getText().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$");

        return nombreValido && apellidosValido;
    }


    @FXML
    void onActionActualizar(ActionEvent event) {
        if (!validarCampos()) {
            mostrarAlerta("Campos inválidos", "Por favor corrige los errores antes de continuar.");
            return;
        }
        actualizar();
    }

    public void cargarIdTutorCurso(){
        String sql = "SELECT Id_Tutor FROM tutor_grupo";
        try (Connection connection = Conectar.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            ObservableList<Integer> tutorgrupolist = FXCollections.observableArrayList();
            while (rs.next()) {
                tutorgrupolist.add(rs.getInt("Id_Tutor"));
            }
            tutorGrupo.setItems(tutorgrupolist);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void actualizar() {
        String nombre = Nombre.getText();
        String apellidos = Apellidos.getText();
        Integer tutorGrp = tutorGrupo.getValue();
        boolean pendientes = Pendientes.isSelected();
        String contacto = Contactos.getText();
        String nombreTutorGrupo = tutorGrp.toString();
        String curso = Curso.getValue() != null ? Curso.getValue().name() : null;
        String sql = "UPDATE alumno " +
                "SET Nombre = ?, Apellidos = ?, Tutor_grupo = ?, nombreTutor=?, Pendiente = ?, Contacto = ?, Curso = ? " +
                "WHERE Id_Alumno = ?";
        try (Connection con = Conectar.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellidos);
            stmt.setInt(3, tutorGrp);
            stmt.setString(4,nombreTutorGrupo);
            stmt.setBoolean(5, pendientes);
            stmt.setString(7,curso);
            stmt.setString(6, contacto);
            stmt.setInt(8, alumnoSeleccionado.getId_Alumno());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("El alumno ha sido actualizado con éxito.");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Base de Datos Actualizada");
                alert.setHeaderText("La base de datos ha sido actualizada");
                alert.showAndWait();
                alert.setHeaderText("Base de datos actualizada");
            } else {
                System.err.println("No se encontró un alumno con ese ID.");
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar el alumno: " + e.getMessage());
        }
    }


    @FXML
    void onActionCancelar(ActionEvent event) {
        cerrar();
    }


    @FXML
    void onActionLimpiar(ActionEvent event) {
        limpiar();
    }

    private void cerrar() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    private void limpiar() {
        Nombre.setText("");
        Apellidos.setText("");
        Contactos.setText("");
        Curso.getSelectionModel().clearSelection();
        tutorGrupo.getSelectionModel().clearSelection();
        Pendientes.setSelected(false);

    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText("Introduzca correctamente los campos requeridos");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Inicialización de la clase
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validaciones de campo
        validarCampo(Nombre, "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", "Introduzca el Nombre correctamente.");
        validarCampo(Apellidos, "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", "Introduzca los apellidos correctamente.");
        Curso.setItems(FXCollections.observableArrayList(dad.Model.Curso.values())); // Cursos disponibles
        cargarIdTutorCurso(); // Cargar lista de tutores

        // Listener para detectar cambios en el ChoiceBox de curso
        Curso.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (alumnoSeleccionado != null && newValue != null) {
                alumnoSeleccionado.setCurso(newValue.name());
            }
        });
        Nombre.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Nombre cambiado: " + newValue);
        });

        Pendientes.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Pendientes cambiado: " + newValue);
        });


        tutorGrupo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String nombreTutor = obtenerNombreTutor(newValue);
                if (alumnoSeleccionado != null) {
                    alumnoSeleccionado.setNombreTutor(nombreTutor);
                }
            }
        });

    }




    public void obtenerAlumno(Alumno alumno) {
        this.alumnoSeleccionado = alumno;
        Nombre.textProperty().bindBidirectional(alumno.nombreProperty());
        Apellidos.textProperty().bindBidirectional(alumno.apellidoProperty());
        Pendientes.selectedProperty().bindBidirectional(alumno.pendientesProperty());
        Contactos.textProperty().bindBidirectional(alumno.contactoProperty());
        tutorGrupo.valueProperty().bindBidirectional(alumno.tutor_grupoProperty().asObject());
        Curso.getSelectionModel().select(dad.Model.Curso.valueOf(alumno.getCurso()));
    }


    private String obtenerNombreTutor(Integer tutorGrupoId) {
        String sql = "SELECT Nombre FROM tutor_grupo WHERE Id_Tutor = ?";
        String nombreTutor = "";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tutorGrupoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nombreTutor = rs.getString("Nombre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nombreTutor;
    }



    private ObservableList<Integer> obtenerIdTutors() {
        ObservableList<Integer> tutorGrupoList = FXCollections.observableArrayList();
        String sql = "SELECT Id_Tutor FROM tutor_grupo";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tutorGrupoList.add(rs.getInt("Id_Tutor"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tutorGrupoList;
    }


    public AnchorPane getDetallePanel() {
        return detallePanel;
    }

    public void setDetallePanel(AnchorPane detallePanel) {
        this.detallePanel = detallePanel;
    }

    public TextField getApellidos() {
        return Apellidos;
    }

    public void setApellidos(TextField apellidos) {
        Apellidos = apellidos;
    }

    public TextField getContactos() {
        return Contactos;
    }

    public void setContactos(TextField contactos) {
        Contactos = contactos;
    }

    public ChoiceBox<dad.Model.Curso> getCurso() {
        return Curso;
    }

    public void setCurso(ChoiceBox<dad.Model.Curso> curso) {
        Curso = curso;
    }

    public TextField getNombre() {
        return Nombre;
    }

    public void setNombre(TextField nombre) {
        Nombre = nombre;
    }

    public CheckBox getPendientes() {
        return Pendientes;
    }

    public void setPendientes(CheckBox pendientes) {
        Pendientes = pendientes;
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public Alumno getAlumnoSeleccionado() {
        return alumnoSeleccionado;
    }

    public ChoiceBox<Integer> getTutorGrupo() {
        return tutorGrupo;
    }
}