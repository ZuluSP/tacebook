package persistence;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import modeler.Comment;
import modeler.Message;
import modeler.Post;
import modeler.Profile;

/**
 *
 * @author a19carlosvz
 */
public class ProfileDB {

    /**
     * Encuentra un perfil por el nombre
     *
     * @param name Nombre del perfil
     * @return Perfil que buscamos por nombre
     * @throws PersistenceException Excepción
     */
    public static Profile findByName(String name, int numberOfPosts) throws PersistenceException {
        try {
            Connection c = TacebookDB.getConnection();
            PreparedStatement select = c.prepareStatement("select * from Profile where name = ?");
            select.setString(1, name);
            ResultSet rst = select.executeQuery();
            Profile p = null;
            if (rst.next()) {
                p = new Profile(rst.getString("name"), rst.getString("password"), rst.getString("status"));
                loadProfileData(p, numberOfPosts);
            }
            rst.close();
            select.close();
            return p;
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_READ, ex.getMessage());
        }
    }

    /**
     * Encuentra un perfil por nombre y contraseña
     *
     * @param name
     * @param password
     * @param numberOfPosts
     * @return Perfil que buscamos
     * @throws PersistenceException
     */
 public static Profile findByNameAndPassword(String name, String password, int numberOfPosts) throws PersistenceException {
        try {
            Connection c = TacebookDB.getConnection();
            PreparedStatement select = c.prepareStatement("select * from Profile where name = ? and password = ?");
            select.setString(1, name);

            try {
                select.setString(2, getPasswordHash(password));
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(ProfileDB.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            ResultSet rst = select.executeQuery();
            Profile p = null;
            if (rst.next()) {
                p = new Profile(rst.getString("name"), rst.getString("password"), rst.getString("status"));
                loadProfileData(p, numberOfPosts);
            }
            rst.close();
            select.close();
            return p;
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_READ, ex.getMessage());
        }
    }

   /**
     * Guarda un perfil en la lista de perfiles
     *
     * @param profile
     * @throws PersistenceException
     */
    public static void save(Profile profile) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO Profile (name,password,status) VALUES (?,?,?)");
            pst.setString(1, profile.getName());
            try {
                pst.setString(2, getPasswordHash(profile.getPassword()));
            } catch (NoSuchAlgorithmException ex) {
                java.util.logging.Logger.getLogger(ProfileDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
           
            pst.setString(3, profile.getStatus());
            pst.execute();
            pst.close();

        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    /**
     * Actualiza el estado del perfil
     *
     * @param profile
     * @throws PersistenceException
     */
    public static void update(Profile profile) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("Update Profile set status = ? where name = ? ");
            pst.setString(1, profile.getStatus());
            pst.setString(2,profile.getName());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, e.getMessage());
        }
    }

    /**
     * Guarda la solicitud de amistad
     *
     * @param destProfile
     * @param sourceProfile
     * @throws PersistenceException
     */
    public static void saveFriendshipRequest(Profile destProfile, Profile sourceProfile) throws PersistenceException {

        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO FriendRequest VALUES (?,?)");
            pst.setString(1, sourceProfile.getName());
            pst.setString(2, destProfile.getName());
            pst.execute();
            pst.close();

//            destProfile.getFriendshipRequests().add(sourceProfile);
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }

    }

    /**
     * Elimina la solicitud de amistad
     *
     * @param destProfile
     * @param sourceProfile
     * @throws PersistenceException
     */
    public static void removeFrienshipRequest(Profile destProfile, Profile sourceProfile) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("DELETE FROM FriendRequest where sourceProfile = ? and destinationProfile = ?");
            pst.setString(1, sourceProfile.getName());
            pst.setString(2, destProfile.getName());
            pst.execute();
            pst.close();

        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }

    }

    /**
     * Guarda la amistad
     *
     * @param profile1
     * @param profile2
     * @throws PersistenceException
     */
    public static void saveFriendship(Profile profile1, Profile profile2) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO Friend VALUES (?,?)");
            pst.setString(1, profile1.getName());
            pst.setString(2, profile2.getName());
            pst.execute();
            pst.close();

        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }

    }

    private static void loadProfileData(Profile p, int numberOfPosts) throws SQLException, PersistenceException {
        // Cargar posts
        Connection c = TacebookDB.getConnection();
        PreparedStatement pst = c.prepareStatement("select * from Post where profile = ? order by date desc limit ?");
        pst.setString(1, p.getName());
        pst.setInt(2, numberOfPosts);
        ResultSet rst = pst.executeQuery();
        while (rst.next()) {
            Post post = new Post(rst.getInt("id"), new Date(rst.getTimestamp("date").getTime()), rst.getString("text"),
                    p, new Profile(rst.getString("author"), "", ""));
            p.getPosts().add(post);
            loadPostData(post,p);
        }
        rst.close();
        pst.close();

        // Cargar amistades del perfil
        pst = c.prepareStatement("SELECT * FROM FRIEND AS F LEFT JOIN PROFILE AS PROF1 ON F.PROFILE1=PROF1.NAME "
                + "LEFT JOIN PROFILE AS PROF2 ON F.PROFILE2=PROF2.NAME WHERE PROFILE1=? OR PROFILE2=?");
        
        pst.setString(1, p.getName());
        pst.setString(2, p.getName());
        rst = pst.executeQuery();
        while (rst.next()) {
            Profile friend; 
            if (rst.getString("profile1").equals(p.getName())) {
                friend = new Profile(rst.getString("profile2"), "", rst.getString(8));
            } else {
                friend = new Profile(rst.getString("profile1"), "", rst.getString("status"));
            }
            p.getFriends().add(friend);
        }
        rst.close();
        pst.close();

        // Cargar solicitudes amistad
        pst = c.prepareStatement("select sourceProfile from FriendRequest where destinationProfile = ?");
        pst.setString(1, p.getName());
        rst = pst.executeQuery();
        while (rst.next()) {
            p.getFriendshipRequests().add(new Profile(rst.getString("sourceProfile"), "", "status"));
        }
        rst.close();
        pst.close();

        // Cargar mensajes
        pst = c.prepareStatement("select * from Message where destination = ? order by date desc");
        pst.setString(1, p.getName());
        rst = pst.executeQuery();
        while (rst.next()) {
            p.getMessages().add(new Message(rst.getInt("id"), rst.getString("text"),
                    new Date(rst.getTimestamp("date").getTime()), rst.getBoolean("isRead"),
                    new Profile(rst.getString("destination"), "", ""),
                    new Profile(rst.getString("source"), "", "")));
        }
        rst.close();
        pst.close();
    }

    private static void loadPostData(Post post, Profile profile) throws SQLException, PersistenceException {
        // Cargar los comentarios
        Connection c = TacebookDB.getConnection();
        PreparedStatement select = c.prepareStatement("select * from Comment where idPost = ? order by date desc");
        select.setInt(1, post.getId());
        ResultSet rst = select.executeQuery();
        while (rst.next()) {
            Comment comment = new Comment(0, new Date(rst.getTimestamp("date").getTime()), rst.getString("text"), post, new Profile(rst.getString("author"), "", ""));
            post.getComments().add(comment);
        }
        rst.close();
        select.close();
        // Cargar los likes      
        select = c.prepareStatement("select * from ProfileLikesPost where idPost = ?");
        select.setInt(1, post.getId());
        rst = select.executeQuery();
        while (rst.next()) {
           post.getProfileLikes().add(new Profile(rst.getString("profile"), "", ""));
        }
        rst.close();
        select.close();
        
    }
    private static String getPasswordHash(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(password.getBytes());
        return new String(messageDigest.digest());
    }
}
