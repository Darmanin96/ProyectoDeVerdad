package dad.Conexion;

import com.zaxxer.hikari.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Conectar {
    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl("jdbc:mysql://localhost:3306/mysql"); // Usamos 'mysql' como conexión inicial
            config.setUsername("root");
            config.setPassword("");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            dataSource = new HikariDataSource(config);

            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {

                ResultSet resultSet = statement.executeQuery("SHOW DATABASES LIKE 'gestion_FTP'");
                if (!resultSet.next()) {
                    // Si la base de datos no existe, crearla
                    statement.executeUpdate("CREATE DATABASE gestion_FTP");
                    System.out.println("Base de datos 'gestion' creada.");
                } else {
                    System.out.println("La base de datos 'gestion' ya existe.");
                }
            }

            // Reconfigurar HikariCP para usar la base de datos 'gestion'
            config.setJdbcUrl("jdbc:mysql://localhost:3306/gestion_FTP");
            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            System.err.println("Error configurando la conexión: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}