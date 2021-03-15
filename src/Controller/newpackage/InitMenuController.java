package Controller.newpackage;

import java.util.logging.Level;
import java.util.logging.Logger;
import modeler.Profile;
import persistence.PersistenceException;
import persistence.ProfileDB;
import persistence.TacebookDB;
import view.GUIInitMenuView;
import view.InitMenuView;
import view.TextInitMenuView;

/**
 *
 * @author a19carlosvz
 */
public class InitMenuController {

    private static boolean textMode = false;
    private InitMenuView initMenuView;

    /**
     * Se ejecuta el programa desde aquí
     * @param args
     */
    public static void main(String[] args) {    
    // Si recibimos "text" como parametro, entonces establecemos "textMode" a true
     if ((args.length > 0) && (args[0].equalsIgnoreCase("text"))){
         textMode = true;
     }
     InitMenuController object = new InitMenuController(textMode);
        object.init();
        TacebookDB.close();

    }

    /**
     * Constructor
     * @param textMode
     */
    public InitMenuController(boolean textMode) {
        if (textMode) {
          initMenuView = new TextInitMenuView(this);
        } else {
           initMenuView = new GUIInitMenuView(this);
        }
    }
    /*
    * Inicia el programa
    */
    private void init() {
        while (!initMenuView.showLoginMenu());
    }

    /**
     * Permite al usuario hacer login
     * 
     * @param username Usuario
     * @param password Contraseña
     */
    public void login(String username, String password) {
        Profile p = null;
        try {
            p = ProfileDB.findByNameAndPassword(username, password, 10);
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }

        if (p == null) {
            this.initMenuView.showLoginErrorMessage();
        } else {
            ProfileController loginProfile = new ProfileController(textMode);
            loginProfile.openSession(p);
        }

    }

    /**
     * Muestra el menu de registro
     */
    public void register() {
        this.initMenuView.showRegisterMenu();
    }

    /**
     * Creas el perfil del usuario
     * 
     * @param name
     * @param password
     * @param status
     */
    public void createProfile(String name, String password, String status) {

        try {
            while (ProfileDB.findByName(name, 0) != null) {
                name = this.initMenuView.showNewNameMenu();
            }
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
        Profile profile = new Profile(name, password, status);
        try {
            ProfileDB.save(profile);
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
        ProfileController newProfile = new ProfileController(textMode);
        newProfile.openSession(profile);
    }
    /*
     * Excepcion de persistencia
    */
    private void proccessPersistenceException(PersistenceException ex) {
        if (ex.getCode() == ex.CANNOT_READ) {
            initMenuView.showReadErrorMessage();
        } else if (ex.getCode() == ex.CANNOT_WRITE) {
            initMenuView.showWriteErrorMessage();
        } else if (ex.getCode() == ex.CONECTION_ERROR) {
            initMenuView.showConnectionErrorMessage();
        }

    }

}
