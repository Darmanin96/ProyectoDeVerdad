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

public class EmpresaController implements Initializable {

    @FXML
    private Button Añadir;

    @FXML
    private TableColumn<Empresa, String> Correo;

    @FXML
    private TableColumn<Empresa, String> Direccion;

    @FXML
    private Button Eliminar;

    @FXML
    private TableColumn<Empresa, String> Especialidad;

    @FXML
    private TableColumn<Empresa, Integer> Id_Empresa;

    @FXML
    private TableColumn<Empresa, String> Nombre;

    @FXML
    private TableColumn<Empresa, Boolean> Participa;

    @FXML
    private TableColumn<Empresa, Integer> Plazas;

    @FXML
    private TableColumn<Empresa, String> Telefono;

    @FXML
    private AnchorPane detallePanel;

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Empresa> tableEmpresa;

    private ObservableList<Empresa> empresas = FXCollections.observableArrayList();

    private Stage empresaCreateStage;

    private tutorEmpresaSelectedController tutorEmpresaSelectedController;

    private ComentariosSelectedController comentariosSelectedController;

    @FXML
    void onAñadirAction(ActionEvent event) {
        if (empresaCreateStage == null || !empresaCreateStage.isShowing()) {
            EmpresaCreateController empresaCreateController = new EmpresaCreateController();
            empresaCreateStage = new Stage();
            empresaCreateStage.setTitle("Crear Empresa");
            empresaCreateStage.initModality(Modality.APPLICATION_MODAL);
            empresaCreateStage.setScene(new Scene(empresaCreateController.getRoot()));
            empresaCreateStage.setOnHidden(e -> {
                empresaCreateStage = null;
                if (empresaCreateController.isConfirmar()) {
                    String nombre = empresaCreateController.getNombre().getText();
                    String direccion = empresaCreateController.getDireccion().getText();
                    String especialidad = empresaCreateController.getEspecialidad().getText();
                    Boolean participa = empresaCreateController.getParticipa().isSelected();
                    Integer plazasDeverdad = empresaCreateController.getPlazas().getValue();
                    String telefono = empresaCreateController.getTelefono().getText();
                    String correo = empresaCreateController.getCorreo().getText();
                    int nuevoId = insertarEmpresa(nombre, direccion, especialidad, participa, plazasDeverdad, telefono, correo);
                    if (nuevoId > 0) {
                        if (tutorEmpresaSelectedController != null) {
                            tutorEmpresaSelectedController.obtenerIdEmpresa();
                        }
                        if (comentariosSelectedController != null) {
                            comentariosSelectedController.cargarEmpresas();
                        }
                        cargarTablaEmpresas();
                    } else {
                        Alert alerta = new Alert(Alert.AlertType.ERROR);
                        alerta.setTitle("Error");
                        alerta.setHeaderText("No se pudo añadir la empresa.");
                        alerta.setContentText("Hubo un problema al insertar la empresa en la base de datos.");
                        alerta.showAndWait();
                    }
                }
            });
            empresaCreateStage.show();
        } else {
            empresaCreateStage.requestFocus();
        }
    }

    private int insertarEmpresa(String nombre, String direccion, String especialidad, Boolean participa, Integer plazasDeverdad, String telefono, String correo) {
        String sql = "INSERT INTO empresas (Nombre, Direccion, Telefono, Correo, Participa, Plazas, Especialidad) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Conectar.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nombre);
            stmt.setString(2, direccion);
            stmt.setString(3, telefono);
            stmt.setString(4, correo);
            stmt.setBoolean(5, participa);
            stmt.setInt(6, plazasDeverdad);
            stmt.setString(7, especialidad);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    @FXML
    void onEliminarAction(ActionEvent event) {
        Empresa selected = tableEmpresa.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminar Empresa");
            alert.setHeaderText("Se eliminará la empresa " + selected.getNombre());
            alert.setContentText("¿Estás seguro?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                eliminarEmpresa(selected.getId_Empresa());
                tableEmpresa.getItems().remove(selected);
            }
        }
    }

    private void eliminarEmpresa(int empresaId) {
        String sql = "DELETE FROM empresas WHERE Id_Empresa = ?";
        try (Connection connection = Conectar.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, empresaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Id_Empresa.setCellValueFactory(new PropertyValueFactory<>("id_Empresa"));
        Nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        Direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        Participa.setCellValueFactory(new PropertyValueFactory<>("participa"));
        Plazas.setCellValueFactory(new PropertyValueFactory<>("plazas"));
        Telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        Especialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        Correo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        cargarTablaEmpresas();
        tableEmpresa.setItems(empresas);

        tableEmpresa.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            detallePanel.setVisible(newValue != null);
            if (newValue != null) {
                cargar(newValue);
            }
        });
    }

    public void cargar(Empresa empresaSeleccionada) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EmpresaSelectedView.fxml"));
            Pane pane = loader.load();
            EmpresaSelectedController empresaSelectedController = loader.getController();
            empresaSelectedController.obtenerEmpresa(empresaSeleccionada);
            detallePanel.getChildren().clear();
            detallePanel.getChildren().add(pane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void cargarTablaEmpresas() {
        empresas.clear();
        String sql = "SELECT * FROM empresas";
        try (Connection connection = Conectar.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                empresas.add(new Empresa(
                        rs.getInt("Id_Empresa"),
                        rs.getString("Nombre"),
                        rs.getString("Direccion"),
                        rs.getString("Telefono"),
                        rs.getString("Correo"),
                        rs.getBoolean("Participa"),
                        rs.getInt("Plazas"),
                        rs.getString("Especialidad")
                ));
            }
            tableEmpresa.setItems(empresas);
            tableEmpresa.refresh();
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

    public TableColumn<Empresa, String> getCorreo() {
        return Correo;
    }

    public void setCorreo(TableColumn<Empresa, String> correo) {
        Correo = correo;
    }

    public TableColumn<Empresa, String> getDireccion() {
        return Direccion;
    }

    public void setDireccion(TableColumn<Empresa, String> direccion) {
        Direccion = direccion;
    }

    public Button getEliminar() {
        return Eliminar;
    }

    public void setEliminar(Button eliminar) {
        Eliminar = eliminar;
    }

    public TableColumn<Empresa, String> getEspecialidad() {
        return Especialidad;
    }

    public void setEspecialidad(TableColumn<Empresa, String> especialidad) {
        Especialidad = especialidad;
    }

    public TableColumn<Empresa, Integer> getId_Empresa() {
        return Id_Empresa;
    }

    public void setId_Empresa(TableColumn<Empresa, Integer> id_Empresa) {
        Id_Empresa = id_Empresa;
    }

    public TableColumn<Empresa, String> getNombre() {
        return Nombre;
    }

    public void setNombre(TableColumn<Empresa, String> nombre) {
        Nombre = nombre;
    }

    public TableColumn<Empresa, Boolean> getParticipa() {
        return Participa;
    }

    public void setParticipa(TableColumn<Empresa, Boolean> participa) {
        Participa = participa;
    }

    public TableColumn<Empresa, Integer> getPlazas() {
        return Plazas;
    }

    public void setPlazas(TableColumn<Empresa, Integer> plazas) {
        Plazas = plazas;
    }

    public TableColumn<Empresa, String> getTelefono() {
        return Telefono;
    }

    public void setTelefono(TableColumn<Empresa, String> telefono) {
        Telefono = telefono;
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

    public TableView<Empresa> getTableEmpresa() {
        return tableEmpresa;
    }

    public void setTableEmpresa(TableView<Empresa> tableEmpresa) {
        this.tableEmpresa = tableEmpresa;
    }

    public ObservableList<Empresa> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(ObservableList<Empresa> empresas) {
        this.empresas = empresas;
    }

    public Stage getEmpresaCreateStage() {
        return empresaCreateStage;
    }

    public void setEmpresaCreateStage(Stage empresaCreateStage) {
        this.empresaCreateStage = empresaCreateStage;
    }

    public dad.Controllers.tutorEmpresaSelectedController getTutorEmpresaSelectedController() {
        return tutorEmpresaSelectedController;
    }

    public void setTutorEmpresaSelectedController(dad.Controllers.tutorEmpresaSelectedController tutorEmpresaSelectedController) {
        this.tutorEmpresaSelectedController = tutorEmpresaSelectedController;
    }

    public ComentariosSelectedController getComentariosSelectedController() {
        return comentariosSelectedController;
    }

    public void setComentariosSelectedController(ComentariosSelectedController comentariosSelectedController) {
        this.comentariosSelectedController = comentariosSelectedController;
    }
}