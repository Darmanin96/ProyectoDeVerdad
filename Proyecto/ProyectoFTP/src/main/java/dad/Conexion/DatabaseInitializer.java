package dad.Conexion;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String SCRIPT_SQL = """
            
                CREATE TABLE `alumno` (
                  `Id_Alumno` int(11) NOT NULL,
                  `Nombre` varchar(25) NOT NULL,
                  `Apellidos` varchar(35) NOT NULL,
                  `Tutor_Grupo` int(11) DEFAULT NULL,
                  `Pendiente` tinyint(1) DEFAULT 0,
                  `Programa` varchar(50) DEFAULT NULL,
                  `Contacto` varchar(50) DEFAULT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

                CREATE TABLE `alumnos_empresas_rel` (
                  `Id_Asignacion` int(11) NOT NULL,
                  `Id_Alumno` int(11) NOT NULL,
                  `Nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
                   `Apellidos` varchar(90) COLLATE utf8mb4_spanish_ci NOT NULL,
                  `Id_Empresa` int(11) NOT NULL,
                  `Id_Tutor_Empresa` int(11) DEFAULT NULL,
                  `Id_Tutor_Docente` int(11) DEFAULT NULL,
                  `Fecha_Inicio` date NOT NULL,
                   `Fecha_Fin` date NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

                CREATE TABLE `comentarios_empresa` (
                  `Id_Comentario` int(11) NOT NULL,
                  `Comentario` text NOT NULL,
                  `Fecha_Comentario` timestamp NOT NULL DEFAULT current_timestamp(),
                  `Id_Empresa` int(11) NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

                CREATE TABLE `empresas` (
                  `Id_Empresa` int(11) NOT NULL,
                  `Nombre` varchar(100) NOT NULL,
                  `Direccion` text DEFAULT NULL,
                  `Telefono` varchar(15) DEFAULT NULL,
                  `Correo` varchar(100) DEFAULT NULL,
                  `Participa` tinyint(1) DEFAULT 0,
                  `Plazas` int(11) DEFAULT NULL,
                  `Especialidad` varchar(50) DEFAULT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

                CREATE TABLE `tutor_empresa` (
                  `Id_Tutor` int(11) NOT NULL,
                  `Nombre` varchar(50) NOT NULL,
                  `Contacto` varchar(50) DEFAULT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

                CREATE TABLE `tutor_grupo` (
                  `Id_Tutor` int(11) NOT NULL,
                  `Nombre` varchar(35) NOT NULL,
                  `Grupo` varchar(32) NOT NULL,
                  `Id_Empresa` int(10) NOT NULL,
                   `nombreEmpresa` varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

                CREATE TABLE `visitas` (
                  `Id_Visita` int(11) NOT NULL,
                  `Fecha_Visita` date NOT NULL,
                  `Observaciones` text COLLATE utf8mb4_spanish_ci DEFAULT NULL,
                  `Id_Alumno` int(11) NOT NULL,
                  `nombreAlumno` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
                  `apellidosAlumno` varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL,
                  `Id_Tutor` int(11) NOT NULL,
                  `nombreTutorGrupo` varchar(100) COLLATE utf8mb4_spanish_ci NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

                ALTER TABLE `alumno`
                  ADD PRIMARY KEY (`Id_Alumno`),
                  ADD KEY `Fk_Tutor_Grupo` (`Tutor_Grupo`);

                ALTER TABLE `alumnos_empresas_rel`
                  ADD PRIMARY KEY (`Id_Asignacion`),
                  ADD KEY `Id_Alumno` (`Id_Alumno`),
                  ADD KEY `Id_Empresa` (`Id_Empresa`),
                  ADD KEY `Id_Tutor_Empresa` (`Id_Tutor_Empresa`),
                  ADD KEY `Id_Tutor_Docente` (`Id_Tutor_Docente`);
  
                ALTER TABLE `comentarios_empresa`
                  ADD PRIMARY KEY (`Id_Comentario`),
                  ADD KEY `Id_Empresa` (`Id_Empresa`);

                ALTER TABLE `empresas`
                  ADD PRIMARY KEY (`Id_Empresa`);

                ALTER TABLE `tutor_empresa`
                  ADD PRIMARY KEY (`Id_Tutor`);

                ALTER TABLE `tutor_grupo`
                  ADD PRIMARY KEY (`Id_Tutor`);
 
                ALTER TABLE `visitas`
                  ADD PRIMARY KEY (`Id_Visita`),
                  ADD KEY `Id_Alumno` (`Id_Alumno`),
                  ADD KEY `Id_Tutor` (`Id_Tutor`);

                ALTER TABLE `alumno`
                  MODIFY `Id_Alumno` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

                ALTER TABLE `alumnos_empresas_rel`
                  MODIFY `Id_Asignacion` int(11) NOT NULL AUTO_INCREMENT;

                ALTER TABLE `comentarios_empresa`
                  MODIFY `Id_Comentario` int(11) NOT NULL AUTO_INCREMENT;

                ALTER TABLE `empresas`
                  MODIFY `Id_Empresa` int(11) NOT NULL AUTO_INCREMENT;

                ALTER TABLE `tutor_empresa`
                  MODIFY `Id_Tutor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

                ALTER TABLE `tutor_grupo`
                  MODIFY `Id_Tutor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

                ALTER TABLE `visitas`
                  MODIFY `Id_Visita` int(11) NOT NULL AUTO_INCREMENT;

                ALTER TABLE `alumno`
                  ADD CONSTRAINT `Fk_Tutor_Grupo` FOREIGN KEY (`Tutor_Grupo`) REFERENCES `tutor_grupo` (`Id_Tutor`);

                ALTER TABLE `alumnos_empresas_rel`
                  ADD CONSTRAINT `alumnos_empresas_rel_ibfk_1` FOREIGN KEY (`Id_Alumno`) REFERENCES `alumno` (`Id_Alumno`) ON DELETE CASCADE,
                  ADD CONSTRAINT `alumnos_empresas_rel_ibfk_2` FOREIGN KEY (`Id_Empresa`) REFERENCES `empresas` (`Id_Empresa`) ON DELETE CASCADE,
                  ADD CONSTRAINT `alumnos_empresas_rel_ibfk_3` FOREIGN KEY (`Id_Tutor_Empresa`) REFERENCES `tutor_empresa` (`Id_Tutor`) ON DELETE SET NULL,
                  ADD CONSTRAINT `alumnos_empresas_rel_ibfk_4` FOREIGN KEY (`Id_Tutor_Docente`) REFERENCES `tutor_grupo` (`Id_Tutor`) ON DELETE SET NULL;

                ALTER TABLE `comentarios_empresa`
                  ADD CONSTRAINT `comentarios_empresa_ibfk_1` FOREIGN KEY (`Id_Empresa`) REFERENCES `empresas` (`Id_Empresa`) ON DELETE CASCADE;

                ALTER TABLE `visitas`
                  ADD CONSTRAINT `visitas_ibfk_1` FOREIGN KEY (`Id_Alumno`) REFERENCES `alumno` (`Id_Alumno`) ON DELETE CASCADE,
                  ADD CONSTRAINT `visitas_ibfk_2` FOREIGN KEY (`Id_Tutor`) REFERENCES `tutor_grupo` (`Id_Tutor`) ON DELETE CASCADE;
    """;

    public static void initializeDatabase() throws SQLException {
        try (Connection connection = Conectar.getConnection();
             Statement statement = connection.createStatement()) {

            for (String query : SCRIPT_SQL.split(";")) {
                if (!query.trim().isEmpty()) {
                    statement.execute(query.trim() + ";");
                }
            }

            System.out.println("Base de datos inicializada con éxito.");

        } catch (SQLException e) {
            throw new SQLException("Error ejecutando el script SQL", e);
        }
    }
}