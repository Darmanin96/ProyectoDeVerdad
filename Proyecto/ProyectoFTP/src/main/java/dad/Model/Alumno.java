package dad.Model;

import javafx.beans.property.*;

public class Alumno {
    private IntegerProperty id_Alumno = new SimpleIntegerProperty();
    private StringProperty nombre = new SimpleStringProperty();
    private StringProperty apellido = new SimpleStringProperty();
    private IntegerProperty tutor_grupo = new SimpleIntegerProperty();
    private BooleanProperty pendientes = new SimpleBooleanProperty();
    private StringProperty curso = new SimpleStringProperty();
    private StringProperty contacto = new SimpleStringProperty();
    private StringProperty nombreTutor = new SimpleStringProperty();

    public Alumno(int id, String nombre, String apellido, int tutor_grupo, boolean pendientes, String curso, String contacto,String tutor) {
        this.id_Alumno.set(id);
        this.nombre.set(nombre);
        this.apellido.set(apellido);
        this.tutor_grupo.set(tutor_grupo);
        this.pendientes.set(pendientes);
        this.curso.set(curso);
        this.contacto.set(contacto);
        this.nombreTutor.set(tutor);
    }

    public Alumno() { }

    public int getId_Alumno() {
        return id_Alumno.get();
    }

    public IntegerProperty id_AlumnoProperty() {
        return id_Alumno;
    }

    public void setId_Alumno(int id_Alumno) {
        this.id_Alumno.set(id_Alumno);
    }


    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getApellido() {
        return apellido.get();
    }

    public StringProperty apellidoProperty() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido.set(apellido);
    }

    public int getTutor_grupo() {
        return tutor_grupo.get();
    }

    public IntegerProperty tutor_grupoProperty() {
        return tutor_grupo;
    }


    public void setTutor_grupo(int tutor_grupo) {
        this.tutor_grupo.set(tutor_grupo);
    }

    public boolean isPendientes() {
        return pendientes.get();
    }

    public BooleanProperty pendientesProperty() {
        return pendientes;
    }

    public void setPendientes(boolean pendientes) {
        this.pendientes.set(pendientes);
    }

    public String getCurso() {
        return curso.get();
    }

    public StringProperty cursoProperty() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso.set(curso);
    }

    public String getContacto() {
        return contacto.get();
    }

    public StringProperty contactoProperty() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto.set(contacto);
    }

    public String getNombreTutor() {
        return nombreTutor.get();
    }

    public StringProperty nombreTutorProperty() {
        return nombreTutor;
    }

    public void setNombreTutor(String nombreTutor) {
        this.nombreTutor.set(nombreTutor);
    }
}
