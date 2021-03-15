/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import Controller.newpackage.InitMenuController;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 *
 * @author Carlos
 */
public class GUIInitMenuView implements InitMenuView {

    private InitMenuController initMenuController;

    /**
     *
     * @return
     */
    public InitMenuController getInitMenuController() {
        return initMenuController;
    }

    /**
     *
     * @param initMenuController
     */
    public void setInitMenuController(InitMenuController initMenuController) {
        this.initMenuController = initMenuController;
    }

    /**
     *
     * @param initMenuController
     */
    public GUIInitMenuView(InitMenuController initMenuController) {
        this.initMenuController = initMenuController;
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUIProfileView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUIProfileView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUIProfileView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUIProfileView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    /**
     * Enseña las opciones del menú de inicio
     *
     * @return
     */
    public boolean showLoginMenu() {
        JPanel jPanel = new JPanel(new BorderLayout(5, 5));

        JPanel jLabels = new JPanel(new GridLayout(0, 1, 2, 2));
        jLabels.add(new JLabel("Nombre de Usuario"));
        jLabels.add(new JLabel("Contraseña"));
        jPanel.add(jLabels, BorderLayout.WEST);

        JPanel jTextFields = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField name = new JTextField();
        JPasswordField pass = new JPasswordField();
        jTextFields.add(name);
        jTextFields.add(pass);
        jPanel.add(jTextFields, BorderLayout.CENTER);

        Object[] buttons = {"Salir", "Registrarse", "Iniciar Sesión"};

        int option = JOptionPane.showOptionDialog(null, jPanel, "Entrar en Tacebook", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, buttons, null);

        // Aquí se determina que pasará en caso de que se pulse cada botón
        switch (option) {
            case 2:
                initMenuController.login(name.getText(), String.valueOf(pass.getPassword()));
                return false;

            case 1:
                initMenuController.register();
                return false;

            default:
                return true;
        }
    }

    /**
     * Error de login
     */
    public void showLoginErrorMessage() {
        JOptionPane.showMessageDialog(null, "¡Error en la conexión con el almacén de datos!");
    }

    /**
     * Opciones de Registro de usuario, nombre y contraseña
     */
    public void showRegisterMenu() {

        JPanel jPanel = new JPanel(new BorderLayout(5, 5));
        JPanel jLabels = new JPanel(new GridLayout(0, 1, 2, 2));
        jLabels.add(new JLabel("Nombre de Usuario"));
        jLabels.add(new JLabel("Contraseña"));
        jLabels.add(new JLabel("Repite la contraseña"));
        jLabels.add(new JLabel("Estado"));
        jPanel.add(jLabels, BorderLayout.WEST);

        JPanel jTextFields = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField name = new JTextField();
        JPasswordField pass = new JPasswordField();
        JPasswordField pass1 = new JPasswordField();
        JTextField status = new JTextField();
        jTextFields.add(name);
        jTextFields.add(pass);
        jTextFields.add(pass1);
        jTextFields.add(status);
        jPanel.add(jTextFields, BorderLayout.CENTER);
        Object[] buttons = {"Aceptar", "Cancelar"};
        boolean exit = true;

        do {
            int option = JOptionPane.showOptionDialog(null, jPanel, "Registrarse", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, status);
            if (option == 0) {
                if (String.valueOf(pass1.getPassword()).equals(String.valueOf(pass.getPassword()))) {
                    initMenuController.createProfile(name.getText(), String.valueOf(pass.getPassword()), status.getText());
                    exit = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden", "Error en los datos", JOptionPane.WARNING_MESSAGE);
                    exit = false;
                }

            } else {
                exit = true;
            }
        } while (!exit);

    }

    /**
     * Error de "nombre ya en uso"
     *
     * @return
     */
    public String showNewNameMenu() {
        //Devuelve un String, en este caso directamente en vez de crear una variable, ya devuelvo todo.
        //TODO Falla al dar al cancel y no se como solucionarlo asi que he puesto un bucle infinito, o pones le nombre, o no sales del programa :)
        String name = null;
        while (name == null) {
            name = JOptionPane.showInputDialog(null, "El nombre introducido ya está en uso, por favor introduce otro:", JOptionPane.INFORMATION_MESSAGE);
        }
        return name;
    }

    /*
     * devuelve la opcion escogida por el usuario
     */
    private int readNumber(Scanner scanner) {
        int option = 0;
        try {
            option = scanner.nextInt();
            scanner.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("tienes que introducir un número" + e.getMessage());
            readNumber(scanner);

        }
        return option;

    }

    /**
     * Error de conexion
     */
    public void showConnectionErrorMessage() {
        JOptionPane.showMessageDialog(null, "¡Error en la conexión con el almacén de datos!");
    }

    /**
     * Error en la lectura de datos
     */
    public void showReadErrorMessage() {
        JOptionPane.showMessageDialog(null, "¡Error en la lectura de datos!");
    }

    /**
     * Error en la escritura de datos
     */
    public void showWriteErrorMessage() {
        JOptionPane.showMessageDialog(null, "¡Error en la escritura de datos!");
    }

}
