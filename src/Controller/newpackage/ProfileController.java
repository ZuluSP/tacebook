package Controller.newpackage;

import java.util.Date;
import modeler.*;
import persistence.*;
import view.*;

/**
 * @author a19carlosvz
 */
public class ProfileController {

    private boolean textMode;

    private ProfileView profileView;

    private Profile sessionProfile;

    private Profile shownProfile;

    /**
     * Constructor
     *
     * @param textMode
     */
    public ProfileController(boolean textMode) {
        if (textMode) {
            profileView = new TextProfileView(this);
        } else {
            profileView = new GUIProfileView(null, true, this);
        }
    }

    /**
     *
     * @return
     */
    public ProfileView getProfileView() {
        return profileView;
    }

    /**
     *
     * @param profileView
     */
    public void setProfileView(ProfileView profileView) {
        this.profileView = profileView;
    }

    /**
     *
     * @return
     */
    public Profile getShownProfile() {
        return shownProfile;
    }

    /**
     *
     * @param shownProfile
     */
    public void setShownProfile(Profile shownProfile) {
        this.shownProfile = shownProfile;
        reloadProfile();
    }

    /**
     *
     * @return
     */
    public Profile getSessionProfile() {
        return sessionProfile;
    }

    /**
     * Perfil que tiene iniciada la sesion
     *
     * @param sessionProfile
     */
    public void setSessionProfile(Profile sessionProfile) {
        this.sessionProfile = sessionProfile;
    }

    /**
     * Devuelve los posts mostrados
     *
     * @return
     */
    public int getPostsShowed() {
        int posts = this.profileView.getPostsShowed();
        return posts;
    }

    /**
     * Recarga el perfil
     */
    public void reloadProfile() {

        try {
            this.shownProfile = ProfileDB.findByName(shownProfile.getName(), profileView.getPostsShowed());

            // For para comprobar por consola si los mensajes están leídos o no correctamente
            for (int i = 0; i < shownProfile.getMessages().size(); i++) {
                System.out.println(shownProfile.getMessages().get(i).isRead());
            }
            System.out.println("-");

        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
        profileView.showProfileMenu(shownProfile);
    }

    /**
     * Inicia sesion
     *
     * @param profile
     */
    public void openSession(Profile profile) {
        this.sessionProfile = profile;
        this.shownProfile = profile;
        this.profileView.showProfileMenu(shownProfile);
    }

    /**
     * Actualiza el status del perfil del usuario
     *
     * @param newStatus
     */
    public void updateProfileStatus(String newStatus) {
        sessionProfile.setStatus(newStatus);
        try {
            ProfileDB.update(sessionProfile);
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
        reloadProfile();
    }

    /**
     * Publica un nuevo post y luego recarga el perfil
     *
     * @param text
     * @param destProfile
     */
    public void newPost(String text, Profile destProfile) {
        Post newPost = new Post(0, new Date(), text, destProfile, sessionProfile);
        try {
            PostDB.save(newPost);
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
        reloadProfile();

    }

    /**
     * Publica un nuevo comentario y luego recarga el perfil
     *
     * @param post
     * @param commentText
     */
    public void newComment(Post post, String commentText) {

        Comment newComment = new Comment(0, new Date(), commentText, post, sessionProfile);
        try {
            CommentDB.save(newComment);
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
        reloadProfile();
    }

    /**
     * Nuevo like, luego recarga el perfil
     *
     * @param post
     */
    public void newLike(Post post) {
        boolean likeAvaliable = true;
        if (post.getAuthor().getName().equals(sessionProfile.getName())) {
            profileView.showCannotLikeOwnPostMessage();
            likeAvaliable = false;
        }
        for (int i = 0; i < post.getProfileLikes().size(); i++) {
            if (post.getProfileLikes().get(i).getName().equals(sessionProfile.getName())) {
                profileView.showAlreadyLikedPostMessage();
                likeAvaliable = false;
            }
        }
        if (likeAvaliable) {
            try {
                PostDB.saveLike(post, sessionProfile);

            } catch (PersistenceException ex) {
                proccessPersistenceException(ex);
            }
            reloadProfile();
        }

    }

    /**
     * Nueva solicitud de amistad, luego recarga el perfil
     *
     * @param profileName
     */
    public void newFriendshipRequest(String profileName) {
        try {
            Profile profile1 = ProfileDB.findByName(profileName, 0);
            // Comprobamos que exista un perfil co nome do parámetro, se non existe chamamos a showProfileNotFoundMessage
            if (profile1 == null) {
                this.profileView.showProfileNotFoundMessage();
            } else {
                // Comprobamos se xa son amigos, se xa son amigos chamamos a showIsAlreadyFriendMessage
                for (Profile friend : profile1.getFriends()) {
                    if (friend.getName().equals(this.sessionProfile.getName())) {
                        this.profileView.showIsAlreadyFriendMessage(profileName);
                        reloadProfile();
                        return;
                    }
                }
                // Comprobamos que non enviaramos xa unha peticion a ese perfil, se xa a enviamos chamamos a showDuplicateFrienshipRequestMessage
                for (Profile friendRequest : profile1.getFriendshipRequests()) {
                    if (friendRequest.getName().equals(this.sessionProfile.getName())) {
                        this.profileView.showDuplicateFriendshipRequestMessage(profileName);
                        reloadProfile();
                        return;
                    }
                }
                // Comprobamos que ese perfil non nos haxa enviado unha peticion a nos, se xa no la enviou chamamos a showExistsFrienshipRequestMessage
                for (Profile friendRequest : this.sessionProfile.getFriendshipRequests()) {
                    if (friendRequest.getName().equals(profileName)) {
                        this.profileView.showExistsFriendshipRequestMessage(profileName);
                        reloadProfile();
                        return;
                    }
                }
                // Se non ocorre nada do anterior gardamos a peticion de amizade
                ProfileDB.saveFriendshipRequest(profile1, this.sessionProfile);
            }
            // Invocamos a reloadProfile
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }

    }

    /**
     * Aceptar una solicitud de amistad y luego recargar el perfil
     *
     * @param sourceProfile
     */
    public void acceptFriendshipRequest(Profile sourceProfile) {
        try {
            ProfileDB.saveFriendship(sessionProfile, sourceProfile);
            ProfileDB.removeFrienshipRequest(sessionProfile, sourceProfile);
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }

    }

    /**
     * Rechaza una solicitud de amistad y luego recarga el perfil
     *
     * @param sourceProfile
     */
    public void rejectFriendshipRequest(Profile sourceProfile) {
        try {
            ProfileDB.removeFrienshipRequest(sessionProfile, sourceProfile);
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }

    }

    /**
     * Nuevo mensaje, y luego recarga el perfil
     *
     * @param destProfile
     * @param text
     */
    public void newMessage(Profile destProfile, String text) {
        try {
            Message message = new Message(0, text, new Date(), false, destProfile, sessionProfile);
            MessageDB.save(message);
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
    }

    /**
     * Borra el mensaje seleccionado, luego recarga el perfil
     *
     * @param message
     */
    public void deleteMessage(Message message) {
        try {
            MessageDB.remove(message);
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
        reloadProfile();
    }

    /**
     * Marca el mensaje como leído
     *
     * @param message
     */
    public void markMessageAsRead(Message message) {
        try {
            message.setRead(true);
            MessageDB.update(message);
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
    }

    /**
     * Contesta un mensaje
     *
     * @param message
     * @param text
     */
    public void replyMessage(Message message, String text) {
        try {
            message.setRead(true);
            MessageDB.update(message);
            newMessage(message.getSourceProfile(), text);
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
    }

    /*
     * Excepcion de persistencia
     */
    private void proccessPersistenceException(PersistenceException ex) {
        if (ex.getCode() == ex.CANNOT_READ) {
            profileView.showReadErrorMessage();
        } else if (ex.getCode() == ex.CANNOT_WRITE) {
            profileView.showWriteErrorMessage();
        } else if (ex.getCode() == ex.CONECTION_ERROR) {
            profileView.showConnectionErrorMessage();
        }

    }

}
