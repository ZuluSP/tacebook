/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import modeler.Message;

/**
 *
 * @author a19carlosvz
 */
public class MessageDB {

    /**
     * Guarda los mensajes
     *
     * @param message
     * @throws PersistenceException
     */
    public static void save(Message message) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO Message (text,date,isRead,source,destination) VALUES (?,?,?,?,?)");
            pst.setString(1, message.getText());
            pst.setTimestamp(2, new Timestamp(message.getDate().getTime()));
            pst.setBoolean(3, message.isRead());
            pst.setString(4, message.getSourceProfile().getName());
            pst.setString(5, message.getDestProfile().getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }
 
    /**
     * Actualiza los mensajes
     *
     * @param message
     * @throws PersistenceException
     */
    public static void update(Message message) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("Update Message set isRead = ? where id = ?");
            pst.setBoolean(1, message.isRead());
            pst.setInt(2, message.getId());
            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, e.getMessage());
        }
        
    }

    /**
     * Elimina los mensajes marcados
     *
     * @param message
     * @throws PersistenceException
     */
    public static void remove(Message message) throws PersistenceException {
         Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("DELETE FROM Message where id = ?");
            pst.setInt(1, message.getId());
            pst.execute();
            pst.close();

        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

}
