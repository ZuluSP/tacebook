package view;

import java.util.Scanner;
import modeler.Profile;
import Controller.newpackage.ProfileController;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;
import modeler.Comment;
import modeler.Message;
import modeler.Post;

/**
 * @author a19carlosvz
 *
 */
public class TextProfileView implements ProfileView {

    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'a las' HH:mm:ss");
    private int postsShowed = 10;
    private ProfileController profileController;

    public int getPostsShowed() {
        return postsShowed;
    }

    public void setPostsShowed(int postsShowed) {
        this.postsShowed = postsShowed;
    }

    public TextProfileView(ProfileController profileController) {
        this.profileController = profileController;
    }

    private void showProfileInfo(boolean ownProfile, Profile profile) {

        System.out.println("Perfil del usuario: " + profile.getName());
        System.out.println("Status: " + profile.getStatus());
        System.out.println("Tu biografía (10 últimas publicaciones)");
        if (!profile.getPosts().isEmpty()) {
            System.out.println("Posts: ");
            for (int i = 0; i < profile.getPosts().size(); i++) {
                if (ownProfile) {
                    System.out.println(i + ". " + "El " + formatter.format(profile.getPosts().get(i).getDate())
                            + " has escrito: " + " (" + (profile.getPosts().get(i).getProfileLikes().size()) + "me gusta): \n" + "    " + profile.getPosts().get(i).getText());
                } else {
                    System.out.println(i + ". " + "El " + formatter.format(profile.getPosts().get(i).getDate())
                            + profile.getName() + " escribió (" + (profile.getPosts().get(i).getProfileLikes().size()) + " me gusta): \n" + "    " + profile.getPosts().get(i).getText());
                }

                if (!profile.getPosts().get(i).getComments().isEmpty()) {
                    for (Post p : profile.getPosts()) {
                        for (Comment c : p.getComments()) {
                            System.out.println("    - " + c.getText() + " - " + profile.getName() + " - " + formatter.format(c.getDate()));
                        }
                    }
                }

            }
        }
        System.out.println("");
        if (ownProfile) {
            if (!profile.getFriendshipRequests().isEmpty()) {

                System.out.println("Soliciudes de Amistad: ");
                for (int i = 0; i < profile.getFriendshipRequests().size(); i++) {
                    System.out.println((i) + ". " + profile.getFriendshipRequests().get(i).getName());
                }
            }
            System.out.println("Lista de amigos: ");
            for (int i = 0; i < +profile.getFriends().size(); i++) {
                System.out.println((i) + ". " + profile.getFriends().get(i).getName() + " - " + profile.getFriends().get(i).getStatus());
            }
            if (!profile.getMessages().isEmpty()) {

                int notReadMessages = 0;
                int readMessages = 0;

                for (Message m : profile.getMessages()) {
                    if (!m.isRead()) {
                        notReadMessages++;
                    } else {
                        readMessages++;
                    }
                }
                System.out.println("¡Tienes " + readMessages + " mensajes leídos!: ");
                for (int i = 0; i < profile.getMessages().size(); i++) {
                    if (profile.getMessages().get(i).isRead()) {
                        System.out.println((i) + ". " + "De " + profile.getMessages().get(i).getSourceProfile().getName()
                                + formatter.format(profile.getMessages().get(i).getDate()) + " " + profile.getMessages().get(i).getText());
                    }
                }
                if (notReadMessages != 0) {
                    System.out.println("¡Tienes " + notReadMessages + " mensajes sin leer!:");
                }

                for (int i = 0; i < profile.getMessages().size(); i++) {
                    if (!profile.getMessages().get(i).isRead()) {
                        System.out.println("*" + (i) + ". " + "De " + profile.getMessages().get(i).getSourceProfile().getName()
                                + formatter.format(profile.getMessages().get(i).getDate()) + " " + profile.getMessages().get(i).getText());
                    }
                }
            }
        }
        System.out.println("");
    }

    private void changeStatus(boolean ownProfile, Scanner scanner, Profile profile) {
        scanner.nextLine();
        if (ownProfile) {
            System.out.println("Introduce un nuevo estado:");
            String status = scanner.nextLine();
            profile.setStatus(status);
            profileController.updateProfileStatus(status);
        } else {
            System.out.println("Esta opcion solo es valida en la propia biografía \n");
            showProfileMenu(profile);
        }
    }

    private int selectElement(String text, int maxNumber, Scanner scanner) {

        int answer = -1;
        while (answer < 0 || answer > maxNumber - 1) {
            System.out.println(text);
            answer = readNumber(scanner);
            if (answer < 0 || answer > maxNumber - 1) {
                System.out.println("El número debe estar entre 0 y el máximo!");
            }
        }
        return answer;
    }

    private void writeNewPost(Scanner scanner, Profile profile) {

        System.out.println("Escribe tu publicación");
        scanner.nextLine();
        String post = scanner.nextLine();
        this.profileController.newPost(post, profile);

    }

    private void commentPost(Scanner scanner, Profile profile) {

        if (profile.getPosts().isEmpty()) {
            System.out.println("No hay publicaciones disponibles");
            showProfileMenu(profile);
        } else {
            for (int i = 0; i < profile.getPosts().size(); i++) {
                System.out.print(i + ". ");
                System.out.print(profile.getPosts().get(i).getText());
                System.out.println("");
            }

            int selectedPost = -1;
            if (postsShowed < profile.getPosts().size()) {
                selectedPost = selectElement("Escoge la publicacion que quieras comentar", postsShowed, scanner);
            } else {
                selectedPost = selectElement("Escoge la publicacion que quieras comentar", profile.getPosts().size(), scanner);
            }
            System.out.println("Escribe tu comentario:");
            scanner.nextLine();
            String newComment = scanner.nextLine();
            this.profileController.newComment(profile.getPosts().get(selectedPost), newComment);
        }
    }

    private void addLike(Scanner scanner, Profile profile) {
        if (profile.getPosts().isEmpty()) {
            System.out.println("No hay publicaciones disponibles");
            showProfileMenu(profile);
        }
        int selectedPost = 0;
        if (postsShowed < profile.getPosts().size()) {
            selectedPost = selectElement("Escoge la publicacion que quieras comentar", postsShowed, scanner);
        } else {
            selectedPost = selectElement("Escoge la publicacion que quieras comentar", profile.getPosts().size(), scanner);
        }

        this.profileController.newLike(profile.getPosts().get(selectedPost));

    }

    private void showBiography(boolean ownProfile, Scanner scanner, Profile profile) {
        if (ownProfile) {
            if (!profile.getFriends().isEmpty()) {
                int answer = selectElement("Selecciona una amistad para ver su Biogragía", profile.getFriends().size(), scanner);
                showProfileMenu(profile.getFriends().get(answer));
            } else {
                System.out.println("¡No tienes amigos disponibles!");
            }

        } else {
            showProfileMenu(profileController.getSessionProfile());
        }

    }

    private void sendFriendshipRequest(boolean ownProfile, Scanner scanner, Profile profile) {
        System.out.println("Introduce el nombre del perfil que quieres añadir como amigo:");
        scanner.nextLine();
        String answer = scanner.nextLine();
        profileController.newFriendshipRequest(answer);
    }

    private void proccessFriendshipRequest(boolean ownProfile, Scanner scanner, Profile profile, boolean accept) {

        if (profile.getFriendshipRequests().isEmpty()) {
            System.out.println("No hay solicitudes de amistad Disponibles");
            showProfileMenu(profile);

        } else {
            if (accept) {
                int answer = selectElement("Escribe el numero de la solicitud de amistad que quieres aceptar", profile.getFriendshipRequests().size(), scanner);
                profileController.acceptFriendshipRequest(profile.getFriendshipRequests().get(answer));

            } else {

                int answer = selectElement("Escribe el numero de la solicitud que quieres rechazar", profile.getFriendshipRequests().size(), scanner);

                profileController.rejectFriendshipRequest(profile.getFriendshipRequests().get(answer));
            }
        }
    }

    private void sendPrivateMessage(boolean ownProfile, Scanner scanner, Profile profile) {

        if (profile.getFriends().isEmpty()) {
            System.out.println("No hay amigos!");
            showProfileMenu(profile);
        } else {
            int answer = selectElement("Selecciona a un amigo", profile.getFriends().size(), scanner);
            System.out.println("Escribe el mensaje:");
            scanner.nextLine();
            String text = scanner.nextLine();
            profileController.newMessage(profile.getFriends().get(answer), text);
        }

    }

    private void readPrivateMessage(boolean ownProfile, Scanner scanner, Profile profile) {
        if (profile.getMessages().isEmpty()) {
            System.out.println("No hay mensajes!!");
            showProfileMenu(profile);
        } else {
            int answer = selectElement("Selecciona el mensaje que deseas leer ", profile.getMessages().size(), scanner);
            System.out.println(profile.getMessages().get(answer).getText());
            profileController.markMessageAsRead(profile.getMessages().get(answer));

            System.out.println("");

            int option = selectElement("Escoge una opción \n 1. Responder al mensaje \n 2. Borrar el mensaje \n 3. Volver a la biografía", 3, scanner);
            switch (option) {
                case 1:
                    sendPrivateMessage(ownProfile, scanner, profile);
                    break;
                case 2:
                    deletePrivateMessage(ownProfile, scanner, profile);
                    break;
                case 3:
                    showBiography(ownProfile, scanner, profile);
                    break;
                default:
                    System.out.println("Opción erronea");
                    showProfileInfo(ownProfile, profile);
            }
        }

    }

    private void deletePrivateMessage(boolean ownProfile, Scanner scanner, Profile profile) {
        if (profile.getMessages().isEmpty()) {
            System.out.println("No hay mensajes diponibles");
            showProfileMenu(profile);
        } else {

            int answer = selectElement("Selecciona el mensaje que desea borrar: ", profile.getMessages().size(), scanner);
            profileController.deleteMessage(profile.getMessages().get(answer));
        }

    }

    private void showOldPosts(Scanner scanner, Profile profile) {
        System.out.println("Posts: " + profile.getPosts().size());
        System.out.println("Escoge el numero de publicaciones que quieres ver: ");
        postsShowed = readNumber(scanner);

    }

    public void showProfileNotFoundMessage() {
        System.out.println("Perfil no encontrado.");
    }

    public void showCannotLikeOwnPostMessage() {
        System.out.println("No puedes dar like a tu propio mensaje.");

    }

    public void showAlreadyLikedPostMessage() {
        System.out.println("A este post ya le has dado me gusta.");

    }

    public void showIsAlreadyFriendMessage(String profileName) {
        System.out.println(profileName + " ya es tu amigo.");

    }

    public void showExistsFriendshipRequestMessage(String profileName) {
        System.out.println(profileName + " ya tiene una solicitud de amistad contigo.");
    }

    public void showDuplicateFriendshipRequestMessage(String profileName) {
        System.out.println("Ya existe una solicitud de amistad para " + profileName);
    }

    public void showProfileMenu(Profile profile) {
        showProfileInfo(true, profile);
        boolean ownprofile = profile.getName().equals(profileController.getSessionProfile().getName());
        System.out.println("1. Escribir una nueva publicacion");
        System.out.println("2. Comentar unha publicación");
        System.out.println("3. Hacer me gusta sobre una publicación");
        if (!ownprofile) {
            System.out.println("4. Volver a mi biografía");
        } else {
            System.out.println("4. Ver la biografía de un amigo");
            System.out.println("5. Enviar una solicitud de amistad");
            System.out.println("6. Aceptar una solicitud de amistad");
            System.out.println("7. Rechazar una solicitud de amistad");
        }
        System.out.println("8. Enviar un mensaje privado a un amigo");
        if (ownprofile) {
            System.out.println("9. Leer un mensaje privado");
            System.out.println("10. Eliminar un mensaje privado");
        }
        System.out.println("11. Ver publicaciones anteriores");
        if (ownprofile) {
            System.out.println("12. Cambiar el estado");
        }
        System.out.println("13. Cerrar sesión");
        Scanner scanner = new Scanner(System.in);
        int option = readNumber(scanner);

        switch (option) {
            case 1:
                writeNewPost(scanner, profile);
                break;
            case 2:
                commentPost(scanner, profile);
                break;
            case 3:
                addLike(scanner, profile);
                break;
            case 4:
                showBiography(ownprofile, scanner, profile);
                break;
            case 5:
                sendFriendshipRequest(ownprofile, scanner, profile);
                break;
            case 6:
                proccessFriendshipRequest(ownprofile, scanner, profile, true);
                break;
            case 7:
                proccessFriendshipRequest(ownprofile, scanner, profile, false);
                break;
            case 8:
                sendPrivateMessage(ownprofile, scanner, profile);
                break;
            case 9:
                readPrivateMessage(ownprofile, scanner, profile);
                break;
            case 10:
                deletePrivateMessage(ownprofile, scanner, profile);
                break;
            case 11:
                showOldPosts(scanner, profile);
                break;
            case 12:
                changeStatus(true, scanner, profile);
                break;
            case 13:
                return;
            default:
                System.out.println("Opción erronea");
                showProfileMenu(profile);

        }

    }

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

    public void showConnectionErrorMessage() {
        System.out.println("¡Error en la conexión con el almacén de datos!");
    }

    public void showReadErrorMessage() {
        System.out.println("¡Error en la lectura de datos!");
    }

    public void showWriteErrorMessage() {
        System.out.println("¡Error en la escritura de datos!");
    }

}
