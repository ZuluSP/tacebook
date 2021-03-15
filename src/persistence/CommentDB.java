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
import modeler.Comment;

/**
 *
 * @author a19carlosvz
 */
public class CommentDB {

    /**
     * Guarda los comentarios que se han hecho
     *
     * @param comment
     * @throws PersistenceException
     */
    public static void save(Comment comment) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO Comment (text,date,author,idPost) VALUES (?,?,?,?)");
            pst.setString(1, comment.getText());
            pst.setTimestamp(2, new Timestamp(comment.getDate().getTime()));
            pst.setString(3, comment.getSourceProfile().getName());
            pst.setInt(4, comment.getPost().getId());
            pst.execute();
            pst.close();

        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

}
