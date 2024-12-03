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
import java.time.LocalDate;
import java.util.*;

public class VisitaSelectedController implements Initializable {

    @FXML
    private Button Actualizar;

    @FXML
    private Button Cancelar;

    @FXML
    private DatePicker Date;

    @FXML
    private Button Limpiar;

    @FXML
    private TextArea Observaciones;

    @FXML
    private BorderPane root;

    private Visita visitaSeleccionada;

    @FXML
    private ChoiceBox<Integer> idAlumno;

    @FXML
    private ChoiceBox<Integer> idTutor;

    private VisitaController visitaController;

    @FXML
    void onActualizarAction(ActionEvent event) {
        // Verificar que los campos estén correctamente validados
        if (!validarCampos()) {
            mostrarAlerta("Campos inválidos", "Por favor corrige los errores antes de continuar.");
        } else {
            // Si la validación pasa, actualizar los datos en la base de datos
            Integer idAlumnoSeleccionado = idAlumno.getValue();
            Integer idTutorSeleccionado = idTutor.getValue();
            String fecha = Date.getValue().toString();
            String observaciones = Observaciones.getText();
            String sql = "UPDATE visitas SET Fecha_Visita =?, Observaciones=?, Id_Alumno=?, Id_Tutor=? WHERE Id_Visita = ?";

            try (Connection con = Conectar.getConnection();
                 PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, fecha);
                stmt.setString(2, observaciones);
                stmt.setInt(3, idAlumnoSeleccionado);
                stmt.setInt(4, idTutorSeleccionado);
                stmt.setInt(5, visitaSeleccionada.getIdVisita());
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    // Si la actualización es exitosa, actualizar el objeto Visita
                    visitaSeleccionada.setIdAlumno(idAlumnoSeleccionado);
                    visitaSeleccionada.setIdTutor(idTutorSeleccionado);
                    visitaSeleccionada.setObservaciones(observaciones);
                    visitaSeleccionada.setFecha(Date.getValue());

                    mostrarAlerta("Actualización exitosa", "La visita ha sido actualizada correctamente.");
                } else {
                    mostrarAlerta("Error al actualizar", "No se pudo actualizar la visita.");
                }
            } catch (Exception e) {
                System.err.println("Error al actualizar la visita: " + e.getMessage());
                mostrarAlerta("Error", "Ocurrió un error al intentar actualizar la visita.");
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);  // Opcionalmente puedes agregar un header
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private boolean validarCampos() {
        // Validar que las observaciones no estén vacías y tengan solo letras
        boolean observacionesValido = !Observaciones.getText().isBlank() && Observaciones.getText().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$");
        return observacionesValido;
    }

    public void cargarAlumnos() {
        // Cargar los alumnos desde la base de datos
        String sql = "SELECT Id_Alumno, Nombre, Apellidos FROM alumno";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<Integer> idAlumnos = FXCollections.observableArrayList();
            while (rs.next()) {
                Integer id = rs.getInt("Id_Alumno");
                idAlumnos.add(id);
            }

            idAlumno.setItems(idAlumnos);
            if (!idAlumnos.isEmpty()) {
                idAlumno.setValue(idAlumnos.get(0));  // Seleccionar el primer alumno por defecto
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cargarTutores() {
        // Cargar los tutores desde la base de datos
        String sql = "SELECT Id_Tutor, Nombre FROM tutor_grupo";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<Integer> idTutores = FXCollections.observableArrayList();
            while (rs.next()) {
                Integer id = rs.getInt("Id_Tutor");
                idTutores.add(id);
            }

            idTutor.setItems(idTutores);
            if (!idTutores.isEmpty()) {
                idTutor.setValue(idTutores.get(0));  // Seleccionar el primer tutor por defecto
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void obtenerVisita(Visita visita) {
        if (visita != null) {
            this.visitaSeleccionada = visita;
            idAlumno.valueProperty().bindBidirectional(visita.idAlumnoProperty().asObject());
            idTutor.valueProperty().bindBidirectional(visita.idTutorProperty().asObject());
            Observaciones.textProperty().bindBidirectional(visita.observacionesProperty());
            Date.valueProperty().bindBidirectional(visita.fechaProperty());
        }
    }

    private void actualizarNombreAlumno(Integer idAlumno) {
        // Cargar el nombre del alumno según su ID
        String sql = "SELECT Nombre, Apellidos FROM alumno WHERE Id_Alumno = ?";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAlumno);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nombreAlumno = rs.getString("Nombre");
                String apellidosAlumno = rs.getString("Apellidos");
                visitaSeleccionada.setNombreAlumno(nombreAlumno + " " + apellidosAlumno);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void actualizarNombreTutor(Integer idTutor) {
        // Cargar el nombre del tutor según su ID
        String sql = "SELECT Nombre FROM tutor_grupo WHERE Id_Tutor = ?";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTutor);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nombreTutor = rs.getString("Nombre");
                visitaSeleccionada.setNombreTutorGrupo(nombreTutor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onCancelarAction(ActionEvent event) {
        cerrar();
    }

    private void cerrar() {
        // Cerrar la ventana de detalle de la visita
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onLimpiarAction(ActionEvent event) {
        Date.setValue(null);
        Observaciones.setText("");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTutores();
        cargarAlumnos();
        // Listener para actualizar los nombres al cambiar la selección de los ChoiceBox
        idAlumno.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                actualizarNombreAlumno(newValue);
            }
        });

        idTutor.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                actualizarNombreTutor(newValue);
            }
        });
    }

    public Button getActualizar() {
        return Actualizar;
    }

    public void setActualizar(Button actualizar) {
        Actualizar = actualizar;
    }

    public Button getCancelar() {
        return Cancelar;
    }

    public void setCancelar(Button cancelar) {
        Cancelar = cancelar;
    }

    public DatePicker getDate() {
        return Date;
    }

    public void setDate(DatePicker date) {
        Date = date;
    }

    public Button getLimpiar() {
        return Limpiar;
    }

    public void setLimpiar(Button limpiar) {
        Limpiar = limpiar;
    }

    public TextArea getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(TextArea observaciones) {
        Observaciones = observaciones;
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public Visita getVisitaSeleccionada() {
        return visitaSeleccionada;
    }

    public void setVisitaSeleccionada(Visita visitaSeleccionada) {
        this.visitaSeleccionada = visitaSeleccionada;
    }

    public ChoiceBox<Integer> getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(ChoiceBox<Integer> idAlumno) {
        this.idAlumno = idAlumno;
    }

    public ChoiceBox<Integer> getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(ChoiceBox<Integer> idTutor) {
        this.idTutor = idTutor;
    }

    public VisitaController getVisitaController() {
        return visitaController;
    }

    public void setVisitaController(VisitaController visitaController) {
        this.visitaController = visitaController;
    }
}
