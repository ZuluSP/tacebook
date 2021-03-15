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
import java.util.Date;
import modeler.Post;
import modeler.Profile;

/**
 *
 * @author a19carlosvz
 */
public class PostDB {

    /**
     * Guarda los posts
     *
     * @param post
     * @throws PersistenceException
     */
    public static void save(Post post) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO Post (text,date,profile,author) VALUES (?,?,?,?)");
            pst.setString(1, post.getText());
            pst.setTimestamp(2, new Timestamp(post.getDate().getTime()));
            pst.setString(3, post.getProfile().getName());
            pst.setString(4, post.getAuthor().getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    /**
     * Guarda los likes de los posts
     *
     * @param post
     * @param profile
     * @throws PersistenceException
     */
  
    public static void saveLike(Post post, Profile profile) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO PROFILELIKESPOST VALUES (?,?)");
            pst.setInt(1, post.getId());
            pst.setString(2, profile.getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

}
