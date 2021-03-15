package persistence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author a19carlosvz
 */
public class TacebookDB {

    // Referencia á conexión coa BD
    private static Connection connection = null;

    /**
     * Cierra el tacebook
     */
    public static void close() {
        try {
            Connection c = getConnection();
            c.close();
        } catch (SQLException | PersistenceException ex) {
            Logger.getLogger(TacebookDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Obtén unha única conexión coa base de datos, abríndoa se é necesario
     *
     * @return Conexión coa base de datos aberta
     * @throws PersistenceException Se se produce un erro ao conectar coa BD
     */
    public static Connection getConnection() throws PersistenceException {
        // Obtenemos una conexión con la base de datos
        try {
            if (connection == null) {

                //TacebookDB.class.getClassLoader().getResourceAsStream
                InputStream input = new FileInputStream("src/config/db.properties");

                if (input == null) {
                    System.out.println("No se puede leer el fichero de propiedades");
                } else {

                    // Cargamos propiedades del fichero
                    Properties prop = new Properties();

                    try {
                        prop.load(input);
                        // Cerramos el flujo
                        input.close();

                        String url = prop.getProperty("url");
                        String user = prop.getProperty("user");
                        String password = prop.getProperty("password");

                        connection = DriverManager.getConnection(url, user, password);

                    } catch (IOException ex) {
                        Logger.getLogger(TacebookDB.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                // Versión sin archivo de propiedades
//                String url = "jdbc:sqlite:src\\persistence\\tacebook.db";
//                String user = "";
//                String pass = "";
//                connection = DriverManager.getConnection(url, user, pass);

            }
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.CONECTION_ERROR, e.getMessage());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TacebookDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }
}
