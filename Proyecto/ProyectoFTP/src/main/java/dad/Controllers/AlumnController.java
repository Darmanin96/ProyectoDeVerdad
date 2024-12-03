package dad.Controllers;

import dad.Conexion.*;
import dad.Model.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

import static dad.Conexion.Conectar.getConnection;

public class AlumnController implements Initializable {

    @FXML
    private TableColumn<Alumno, String> Apellidos;

    @FXML
    private TableColumn<Alumno, String> Contacto;

    @FXML
    private TableColumn<Alumno, String> Nombre;

    @FXML
    private TableColumn<Alumno, Boolean> Pendientes;

    @FXML
    private TableColumn<Alumno, String> Curso;

    @FXML
    private TableColumn<Alumno, Integer> id_Alumn;

    @FXML
    private TableColumn<Alumno, String> nombreTutor;

    @FXML
    private Button añadir;

    @FXML
    private Button eliminar;

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Alumno> tableAlumn;

    @FXML
    private TableColumn<Alumno, Integer> tutorGrupo;

    @FXML
    private AnchorPane detallePanel;

    private ObservableList<Alumno> alumnos = FXCollections.observableArrayList();

    private Stage alumnoCreateStage;

    private VisitaSelectedController visitaSelectedController;

    private tutorEmpresaSelectedController tutorEmpresaSelectedController;

    private tutorGrupoSelectedController tutorGrupoSelectedController;

    @FXML
    void onAñadirAction(ActionEvent event) {
        if (alumnoCreateStage == null || !alumnoCreateStage.isShowing()) {
            AlumnoCreateController alumnoCreateController = new AlumnoCreateController();
            alumnoCreateStage = new Stage();
            alumnoCreateStage.setTitle("Nuevo alumno");
            alumnoCreateStage.initModality(Modality.APPLICATION_MODAL);
            alumnoCreateStage.setScene(new Scene(alumnoCreateController.getRoot()));
            alumnoCreateStage.setOnHidden(e -> {
                alumnoCreateStage = null;
                if (alumnoCreateController.isConfirmar()) {
                    String nombre = alumnoCreateController.getNombre().getText();
                    String apellido = alumnoCreateController.getApellidos().getText();
                    Integer tutorGrupo = alumnoCreateController.getTutorGrupo().getValue();
                    Boolean pendiente = alumnoCreateController.getPendientes().isSelected();
                    String curso = alumnoCreateController.getCurso().getValue().toString();
                    String contacto = alumnoCreateController.getContactos().getText();
                    String nombreTutor = obtenerNombreTutor(tutorGrupo);
                    int nuevoId = insertarAlumno(nombre, apellido, tutorGrupo, pendiente, curso, contacto, nombreTutor);
                    if (nuevoId > 0) {
                        if (visitaSelectedController != null) {
                            visitaSelectedController.cargarAlumnos();
                        }
                        if (tutorEmpresaSelectedController != null) {
                            tutorEmpresaSelectedController.obtenerIdEmpresa();
                        }


                        cargarTablaAlumno();
                    } else {
                        Alert alerta = new Alert(Alert.AlertType.ERROR);
                        alerta.setTitle("Error");
                        alerta.setHeaderText("No se pudo añadir el alumno.");
                        alerta.setContentText("Hubo un problema al insertar el alumno en la base de datos.");
                        alerta.showAndWait();
                    }
                }
            });
            alumnoCreateStage.show();
        } else {
            alumnoCreateStage.requestFocus();
        }
    }

    private String obtenerNombreTutor(Integer id) {
        String sql = "SELECT NOMBRE FROM tutor_grupo WHERE Id_Tutor = ?";
        String nombreTutor = "";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nombreTutor = rs.getString("Nombre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nombreTutor;
    }

    private int insertarAlumno(String nombre, String apellido, Integer tutorGrupo, Boolean pendiente, String curso, String contacto, String nombreTutor) {
        String sql = "INSERT INTO alumno (Nombre, Apellidos, Tutor_Grupo, nombreTutor, Pendiente, Curso, Contacto) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setInt(3, tutorGrupo);
            stmt.setString(4, nombreTutor);
            stmt.setBoolean(5, pendiente);
            stmt.setString(6, curso);
            stmt.setString(7, contacto);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
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
        Alumno selected = tableAlumn.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminar alumno");
            alert.setHeaderText("Se eliminará el alumno " + selected.getNombre());
            alert.setContentText("¿Estás seguro?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                eliminarAlumno(selected.getId_Alumno());
                tableAlumn.getItems().remove(selected);
            }
        }
    }

    private void eliminarAlumno(int alumnoId) {
        String sql = "DELETE FROM alumno WHERE Id_Alumno = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, alumnoId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Alumno eliminado correctamente.");
            } else {
                System.out.println("No se encontró el alumno.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id_Alumn.setCellValueFactory(new PropertyValueFactory<>("id_Alumno"));
        Nombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        Apellidos.setCellValueFactory(new PropertyValueFactory<>("Apellido"));
        tutorGrupo.setCellValueFactory(new PropertyValueFactory<>("Tutor_grupo"));
        nombreTutor.setCellValueFactory(new PropertyValueFactory<>("nombreTutor"));
        Pendientes.setCellValueFactory(new PropertyValueFactory<>("pendientes"));
        Curso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        Contacto.setCellValueFactory(new PropertyValueFactory<>("Contacto"));
        Nombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        Apellidos.setCellValueFactory(cellData -> cellData.getValue().apellidoProperty());
        tutorGrupo.setCellValueFactory(cellData -> cellData.getValue().tutor_grupoProperty().asObject());
        nombreTutor.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        Pendientes.setCellValueFactory(cellData -> cellData.getValue().pendientesProperty());
        Curso.setCellValueFactory(cellData -> cellData.getValue().cursoProperty());
        Contacto.setCellValueFactory(cellData -> cellData.getValue().contactoProperty());
        cargarTablaAlumno();
        tableAlumn.setItems(alumnos);
        tableAlumn.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            detallePanel.setVisible(newValue != null);
            if (newValue != null) {
                cargar(newValue);
            }
        });
    }

    public void cargar(Alumno alumnoSeleccionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AlumnSelectedView.fxml"));
            Pane pane = loader.load();
            AlumnSelectedController alumnSelectedController = loader.getController();
            alumnSelectedController.obtenerAlumno(alumnoSeleccionado);
            detallePanel.getChildren().clear();
            detallePanel.getChildren().add(pane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void cargarTablaAlumno() {
        alumnos.clear();
        String sql = "SELECT * FROM alumno";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alumnos.add(new Alumno(
                        rs.getInt("Id_Alumno"),
                        rs.getString("Nombre"),
                        rs.getString("Apellidos"),
                        rs.getInt("Tutor_Grupo"),
                        rs.getBoolean("Pendiente"),
                        rs.getString("Curso"),
                        rs.getString("Contacto"),
                        rs.getString("nombreTutor")
                ));
            }
            tableAlumn.setItems(alumnos);
            tableAlumn.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setVisitaSelectedController(VisitaSelectedController visitaSelectedController) {
        this.visitaSelectedController = visitaSelectedController;
    }

    public TableColumn<Alumno, String> getApellidos() {
        return Apellidos;
    }

    public void setApellidos(TableColumn<Alumno, String> apellidos) {
        Apellidos = apellidos;
    }

    public TableColumn<Alumno, String> getContacto() {
        return Contacto;
    }

    public void setContacto(TableColumn<Alumno, String> contacto) {
        Contacto = contacto;
    }

    public TableColumn<Alumno, String> getNombre() {
        return Nombre;
    }

    public void setNombre(TableColumn<Alumno, String> nombre) {
        Nombre = nombre;
    }

    public TableColumn<Alumno, Boolean> getPendientes() {
        return Pendientes;
    }

    public void setPendientes(TableColumn<Alumno, Boolean> pendientes) {
        Pendientes = pendientes;
    }

    public TableColumn<Alumno, String> getCurso() {
        return Curso;
    }

    public void setCurso(TableColumn<Alumno, String> curso) {
        Curso = curso;
    }

    public TableColumn<Alumno, Integer> getId_Alumn() {
        return id_Alumn;
    }

    public void setId_Alumn(TableColumn<Alumno, Integer> id_Alumn) {
        this.id_Alumn = id_Alumn;
    }

    public TableColumn<Alumno, String> getNombreTutor() {
        return nombreTutor;
    }

    public void setNombreTutor(TableColumn<Alumno, String> nombreTutor) {
        this.nombreTutor = nombreTutor;
    }

    public Button getAñadir() {
        return añadir;
    }

    public void setAñadir(Button añadir) {
        this.añadir = añadir;
    }

    public Button getEliminar() {
        return eliminar;
    }

    public void setEliminar(Button eliminar) {
        this.eliminar = eliminar;
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public TableView<Alumno> getTableAlumn() {
        return tableAlumn;
    }

    public void setTableAlumn(TableView<Alumno> tableAlumn) {
        this.tableAlumn = tableAlumn;
    }

    public TableColumn<Alumno, Integer> getTutorGrupo() {
        return tutorGrupo;
    }

    public void setTutorGrupo(TableColumn<Alumno, Integer> tutorGrupo) {
        this.tutorGrupo = tutorGrupo;
    }

    public AnchorPane getDetallePanel() {
        return detallePanel;
    }

    public void setDetallePanel(AnchorPane detallePanel) {
        this.detallePanel = detallePanel;
    }

    public ObservableList<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(ObservableList<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    public Stage getAlumnoCreateStage() {
        return alumnoCreateStage;
    }

    public void setAlumnoCreateStage(Stage alumnoCreateStage) {
        this.alumnoCreateStage = alumnoCreateStage;
    }

    public VisitaSelectedController getVisitaSelectedController() {
        return visitaSelectedController;
    }

    public dad.Controllers.tutorEmpresaSelectedController getTutorEmpresaSelectedController() {
        return tutorEmpresaSelectedController;
    }

    public void setTutorEmpresaSelectedController(dad.Controllers.tutorEmpresaSelectedController tutorEmpresaSelectedController) {
        this.tutorEmpresaSelectedController = tutorEmpresaSelectedController;
    }

    public dad.Controllers.tutorGrupoSelectedController getTutorGrupoSelectedController() {
        return tutorGrupoSelectedController;
    }

    public void setTutorGrupoSelectedController(dad.Controllers.tutorGrupoSelectedController tutorGrupoSelectedController) {
        this.tutorGrupoSelectedController = tutorGrupoSelectedController;
    }
}
