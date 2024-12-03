package dad.Controllers;

import dad.Conexion.*;
import dad.Model.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.*;
import java.util.*;

import static dad.Conexion.Conectar.getConnection;

public class VisitaController implements Initializable {

    @FXML
    private Button Añadir;

    @FXML
    private Button Eliminar;

    @FXML
    private TableColumn<Visita, LocalDate> Fecha;

    @FXML
    private TableColumn<Visita, Integer> IdAlumno;

    @FXML
    private TableColumn<Visita, Integer> IdTutor;

    @FXML
    private TableColumn<Visita, Integer> IdVisitas;

    @FXML
    private TableColumn<Visita, String> Observaciones;

    @FXML
    private TableColumn<Visita, String> apellidosAlumno;

    @FXML
    private TableColumn<Visita, String> nombreAlumno;

    @FXML
    private TableColumn<Visita, String> nombreTutor;

    @FXML
    private AnchorPane detallePanel;

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Visita> tableVisitas;

    private ObservableList<Visita> visitasLista = FXCollections.observableArrayList();

    private AlumnSelectedController alumnSelectedController;

    @FXML
    void onAñadirAction(ActionEvent event) {
        VisitaCreateController visitaCreateController = new VisitaCreateController();
        Stage stage = new Stage();
        stage.setTitle("Nueva visita");
        stage.setScene(new Scene(visitaCreateController.getRoot()));
        stage.showAndWait();

        if (visitaCreateController.isConfirmar()) {
            Integer idAlumnoVerdad = visitaCreateController.getIdAlumno().getValue();
            Integer idTutorVerdad = visitaCreateController.getIdTutor().getValue();
            String observaciones = visitaCreateController.getObservaciones().getText();
            LocalDate fecha = visitaCreateController.getDate().getValue();
            String nombreAlumno = obtenerNombreAlumno(idAlumnoVerdad);
            String apellidosAlumno = obtenerApellidosAlumno(idAlumnoVerdad);
            String nombreTutor = obtenerNombreTutor(idTutorVerdad);
            int nuevoId = insertarVisita(fecha, observaciones, idAlumnoVerdad, idTutorVerdad, nombreTutor, apellidosAlumno, nombreAlumno);
            if (nuevoId > 0) {
                cargarTablaVisita(); // Recargar tabla
                tableVisitas.refresh();
            } else {
                mostrarError("Error al añadir", "No se pudo añadir la visita.");
            }
        }
    }

    private int insertarVisita(LocalDate fecha, String observaciones, Integer idAlumno, Integer idTutor, String nombre, String apellidos, String nombreTutor) {
        String sql = "INSERT INTO visitas (Fecha_Visita, Observaciones, Id_Alumno, nombreAlumno, apellidosAlumno, Id_Tutor, nombreTutorGrupo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fecha.toString());
            stmt.setString(2, observaciones);
            stmt.setInt(3, idAlumno);
            stmt.setString(4, nombre);
            stmt.setString(5, apellidos);
            stmt.setInt(6, idTutor);
            stmt.setString(7, nombreTutor);

            int i = stmt.executeUpdate();
            if (i > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @FXML
    void onEliminarAction(ActionEvent event) {
        Visita selected = tableVisitas.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminar Visita");
            alert.setHeaderText("Se eliminará la visita con " + selected.getFecha());
            alert.setContentText("¿Estás seguro?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                eliminarVisita(selected.getIdVisita());
                cargarTablaVisita();
                tableVisitas.refresh(); // Refrescar la tabla
            }
        }
    }

    private void eliminarVisita(int visitaId) {
        String sql = "DELETE FROM visitas WHERE Id_Visita = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, visitaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String obtenerNombreAlumno(Integer idAlumno) {
        String sql = "SELECT Nombre FROM alumno WHERE Id_Alumno = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAlumno);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Nombre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String obtenerApellidosAlumno(Integer idAlumno) {
        String sql = "SELECT Apellidos FROM alumno WHERE Id_Alumno = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAlumno);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Apellidos");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String obtenerNombreTutor(Integer idTutor) {
        String sql = "SELECT Nombre FROM tutor_grupo WHERE Id_Tutor = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTutor);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Nombre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        IdVisitas.setCellValueFactory(new PropertyValueFactory<>("idVisita"));
        IdTutor.setCellValueFactory(cellData -> cellData.getValue().idTutorProperty().asObject());
        IdAlumno.setCellValueFactory(cellData -> cellData.getValue().idAlumnoProperty().asObject());
        Fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        Observaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
        nombreAlumno.setCellValueFactory(new PropertyValueFactory<>("nombreAlumno"));
        nombreTutor.setCellValueFactory(new PropertyValueFactory<>("nombreTutorGrupo"));
        apellidosAlumno.setCellValueFactory(new PropertyValueFactory<>("apellidosAlumno"));

        cargarTablaVisita();
        tableVisitas.setItems(visitasLista);
        tableVisitas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            detallePanel.setVisible(newValue != null);
            if (newValue != null) {
                cargar(newValue);
            }
        });
    }

    public void cargar(Visita visitaSeleccionada) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VisitaSelected.fxml"));
            Pane pane = loader.load();
            VisitaSelectedController visitaSelectedController = loader.getController();
            visitaSelectedController.obtenerVisita(visitaSeleccionada);
            detallePanel.getChildren().clear();
            detallePanel.getChildren().add(pane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void cargarTablaVisita() {
        visitasLista.clear();
        String sql = "SELECT * FROM visitas";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                visitasLista.add(new Visita(
                        rs.getInt("Id_Visita"),
                        rs.getDate("Fecha_Visita").toLocalDate(),
                        rs.getString("Observaciones"),
                        rs.getInt("Id_Alumno"),
                        rs.getInt("Id_Tutor"),
                        rs.getString("nombreAlumno"),
                        rs.getString("nombreTutorGrupo"),
                        rs.getString("apellidosAlumno")
                ));
            }
            tableVisitas.setItems(visitasLista);
            tableVisitas.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Button getAñadir() {
        return Añadir;
    }

    public void setAñadir(Button añadir) {
        Añadir = añadir;
    }

    public Button getEliminar() {
        return Eliminar;
    }

    public void setEliminar(Button eliminar) {
        Eliminar = eliminar;
    }

    public TableColumn<Visita, LocalDate> getFecha() {
        return Fecha;
    }

    public void setFecha(TableColumn<Visita, LocalDate> fecha) {
        Fecha = fecha;
    }

    public TableColumn<Visita, Integer> getIdAlumno() {
        return IdAlumno;
    }

    public void setIdAlumno(TableColumn<Visita, Integer> idAlumno) {
        IdAlumno = idAlumno;
    }

    public TableColumn<Visita, Integer> getIdTutor() {
        return IdTutor;
    }

    public void setIdTutor(TableColumn<Visita, Integer> idTutor) {
        IdTutor = idTutor;
    }

    public TableColumn<Visita, Integer> getIdVisitas() {
        return IdVisitas;
    }

    public void setIdVisitas(TableColumn<Visita, Integer> idVisitas) {
        IdVisitas = idVisitas;
    }

    public TableColumn<Visita, String> getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(TableColumn<Visita, String> observaciones) {
        Observaciones = observaciones;
    }

    public TableColumn<Visita, String> getApellidosAlumno() {
        return apellidosAlumno;
    }

    public void setApellidosAlumno(TableColumn<Visita, String> apellidosAlumno) {
        this.apellidosAlumno = apellidosAlumno;
    }

    public TableColumn<Visita, String> getNombreAlumno() {
        return nombreAlumno;
    }

    public void setNombreAlumno(TableColumn<Visita, String> nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public TableColumn<Visita, String> getNombreTutor() {
        return nombreTutor;
    }

    public void setNombreTutor(TableColumn<Visita, String> nombreTutor) {
        this.nombreTutor = nombreTutor;
    }

    public AnchorPane getDetallePanel() {
        return detallePanel;
    }

    public void setDetallePanel(AnchorPane detallePanel) {
        this.detallePanel = detallePanel;
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public TableView<Visita> getTableVisitas() {
        return tableVisitas;
    }

    public void setTableVisitas(TableView<Visita> tableVisitas) {
        this.tableVisitas = tableVisitas;
    }

    public ObservableList<Visita> getVisitasLista() {
        return visitasLista;
    }

    public void setVisitasLista(ObservableList<Visita> visitasLista) {
        this.visitasLista = visitasLista;
    }

    public AlumnSelectedController getAlumnSelectedController() {
        return alumnSelectedController;
    }

    public void setAlumnSelectedController(AlumnSelectedController alumnSelectedController) {
        this.alumnSelectedController = alumnSelectedController;
    }
}
