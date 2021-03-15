package view;

import java.util.Scanner;
import Controller.newpackage.InitMenuController;
import java.util.NoSuchElementException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author a19carlosvz
 */
public class TextInitMenuView implements InitMenuView {

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
    public TextInitMenuView(InitMenuController initMenuController) {
        this.initMenuController = initMenuController;
    }

    /**
     * Muestra el menú del Login
     *
     * @return
     */
    public boolean showLoginMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Seleccione que desea hacer: ");
        System.out.println("1. Iniciar Sesión");
        System.out.println("2. Crear una cuenta");
        System.out.println("3. Salir");
        int option = readNumber(scanner);
        scanner.nextLine();
        switch (option) {
            case 1:
                System.out.println("Por favor, meta su nombre de usuario:");
                String user = scanner.nextLine();
                System.out.println("Por favor, meta su contraseña:");
                String password = scanner.nextLine();
                initMenuController.login(user, password);
                return false;

            case 2:
                initMenuController.register();

                return false;

            case 3:
                return true;

            default:
                System.out.println("Opción no válida!");
                return false;
        }
    }

    /**
     * Usuario o contraseña incorrectos
     */
    public void showLoginErrorMessage() {
        System.out.println("Usuario o Contraseña incorrectos");
    }

    /**
     * Muestra el menú de Registro
     */
    public void showRegisterMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("¡Bienvenido! Por favor introduzca los datos pedidos para registrarte: ");
        System.out.println("Introduzca el nombre de usuario: ");
        String user = scanner.nextLine();
        boolean test = false;
        String password = "";
        while (!test) {
            System.out.println("Introduzca la contraseña:");
            if (System.console() == null) {
                password = scanner.nextLine();
            } else {
                //Hay dos maneras para que funcione el ocultar la pass, una es usando uno de los constructores de la clase String
                password = new String(System.console().readPassword());
            }
            System.out.println("Introduzca de nuevo la contraseña");
            String password2;
            if (System.console() == null) {
                password2 = scanner.nextLine();
            } else {
                //Otra manera de que funcione ocultar la pass es pasando el ValueOf de la clase String
                password2 = String.valueOf(System.console().readPassword());
            }
            if (password.equals(password2)) {
                test = true;
            } else {
                System.out.println("Las contraseñas no coinciden");
            }
        }
        System.out.println("Introduzca un estado");

        String status = scanner.nextLine();

        initMenuController.createProfile(user, password, status);

    }

    /**
     * Muestra el nombre del menú
     *
     * @return
     */
    public String showNewNameMenu() {
        System.out.println("El nombre introducido ya está en uso, por favor introduce otro:");

        Scanner scanner = new Scanner(System.in);
        String user = scanner.nextLine();

        return user;
    }

    /*
     * Lee y devuelve la opcion escogida por el usuario
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
     * Error al conectarse
     */
    public void showConnectionErrorMessage() {
        System.out.println("¡Error en la conexión con el almacén de datos!");
    }

    /**
     * Error en la lectura de datos
     */
    public void showReadErrorMessage() {
        System.out.println("¡Error en la lectura de datos!");
    }

    /**
     * Error en la ESCRITURA de datos
     */
    public void showWriteErrorMessage() {
        System.out.println("¡Error en la escritura de datos!");
    }

}
