/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import Controller.newpackage.ProfileController;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import modeler.Comment;
import modeler.Message;
import modeler.Profile;

/**
 *
 * @author Carlos
 */
public class GUIProfileView extends javax.swing.JDialog implements ProfileView {

    /**
     *
     * @author Carlos
     */
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'a las' HH:mm:ss");
    private int postsShowed = 10;
    private ProfileController profileController;

    /**
     * Creates new form GUIProfileView
     *
     * @param parent
     * @param modal
     * @param profileController
     */
    public GUIProfileView(java.awt.Frame parent, boolean modal, ProfileController profileController) {
        super(parent, modal);
        initComponents();
        this.profileController = profileController;
    }

    /**
     *
     * @return
     */
    public ProfileController getProfileController() {
        return profileController;
    }

    /**
     *
     * @param profileController
     */
    public void setProfileController(ProfileController profileController) {
        this.profileController = profileController;
    }

    /**
     *
     * @return
     */
    public int getPostsShowed() {
        return postsShowed;
    }

    /**
     *
     * @param postsShowed
     */
    public void setPostsShowed(int postsShowed) {
        this.postsShowed = postsShowed;
    }

    /*
    * Muestra la informacion del perfil
     */
    private void showProfileInfo(boolean ownProfile, Profile profile) {

//        System.out.println("Perfil propio: " + ownProfile);
        // USUARIO Y ESTADO DEL PERFIL
        jLabelNameText.setText(profile.getName());
        jLabelStatusText.setText(profile.getStatus());

        // VISIBILIDAD BOTONES EN PERFIL DE AMIGOS, PANEL INFERIOR
        jButtonCambiarEstado.setVisible(ownProfile);
        jButtonEnviarMensaje2.setVisible(!ownProfile);
        jButtonVolverAMiBiografía.setVisible(!ownProfile);

        //TABLA DE POSTS
        jLabelUltimasPublicaciones.setText(postsShowed + " últimas publicaciones");
        DefaultTableModel dtm = (DefaultTableModel) jTablePublicaciones.getModel();
        dtm.setRowCount(0);
        for (int i = 0; i < profile.getPosts().size() && i < postsShowed; i++) {
            dtm.addRow(new String[]{(String.valueOf(this.formatter.format(profile.getPosts().get(i).getDate()))),
                (profile.getPosts().get(i).getAuthor().getName().equals(profileController.
                getSessionProfile().getName())) ? "Tú escribiste" : profile.getPosts().get(i).getAuthor().getName(),
                profile.getPosts().get(i).getText(),
                String.valueOf(profile.getPosts().get(i).getProfileLikes().size())});
        }

        // SOLICITUDES DE AMISTAD
        jScrollPaneSolicitudesAmistad.setVisible(ownProfile);
        jButtonAceptarSolicitudAmistad.setVisible(ownProfile);
        jButtonRechazarSolicitudAmistad.setVisible(ownProfile);
        jButtonVerBiografia.setVisible(ownProfile);
        jButtonEnviarMensajePrivado.setVisible(ownProfile);
        jLabelSolicitudesAmistad.setVisible(ownProfile);
        jButtonNuevaSolicitudAmistad.setVisible(ownProfile);

        // LISTA DE AMIGOS
        DefaultTableModel dtmf = (DefaultTableModel) jTableListaAmigos.getModel();
        dtmf.setRowCount(0);
        for (int i = 0; i < profile.getFriends().size(); i++) {
            dtmf.addRow(new Object[]{
                profile.getFriends().get(i).getName(),
                profile.getFriends().get(i).getStatus()
            });
        }

        if (ownProfile) {
            DefaultListModel lista = new DefaultListModel();
            for (int i = 0; i < profile.getFriendshipRequests().size(); i++) {
                lista.addElement(profile.getFriendshipRequests().get(i).getName()
                        + " quiere establecer amistad contigo");
            }
            jListSolicitudAmistad.setModel(lista);

            //COMENTARIOS
            ((DefaultTableModel) jTableComents.getModel()).setRowCount(0);

            //MENSAJES 
            DefaultTableModel dtmsg = (DefaultTableModel) jTableMensajesPrivados.getModel();
            dtmsg.setRowCount(0);
            if (!profile.getMessages().isEmpty()) {
                for (int i = 0; i < profile.getMessages().size(); i++) {
                    dtmsg.addRow(new Object[]{
                        profileController.getSessionProfile().getMessages().get(i).isRead(),
                        (String.valueOf(this.formatter.format(profile.getMessages().get(i).getDate()))),
                        (profile.getMessages().get(i).getSourceProfile().getName()),
                        profile.getMessages().get(i).getText(),});

                }
            }

        } else {
            // VISIBILIDAD EN CASO DE ESTAR EN EL PERFIL DE UN AMIGO

        }
    }

    /*
    * Permite cambiar el estado del perfil 
     */
    private void changeStatus() {
        String nuevoEstado = JOptionPane.showInputDialog(this, "Introduce tu "
                + "nuevo estado", "Cambio de estado", JOptionPane.INFORMATION_MESSAGE);
        profileController.updateProfileStatus(nuevoEstado);
    }

    /*
     * Escribe un nuevo post
     */
    private void writeNewPost(Profile profile) {
        try {
            String post = JOptionPane.showInputDialog(null, "Introduce el texto de la publicación");
            //TODO
            if ((post != null)) {
                profileController.newPost(post, profile);
            }

        } catch (NullPointerException e) {

        }
    }

    /*
     * Comenta un nuevo post
     */
    private void commentPost(Profile profile) {
        try {
            if (profile.getPosts().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay publicaciones disponibles");
            } else if (jTablePublicaciones.getSelectedRow() != -1) {
                String comment = JOptionPane.showInputDialog(null, "Introduce tu comentario");
                if (comment.equals(JOptionPane.CANCEL_OPTION)) {
                    dispose();
                }
                this.profileController.newComment(profile.getPosts().get(jTablePublicaciones.getSelectedRow()), comment);
            } else {
                JOptionPane.showMessageDialog(null, "No hay seleccionado ningun post");
            }
        } catch (NullPointerException e) {
        }

    }

    /*
     *  Muestra los comentarios hechos en el JTableComments
     */
    private void showComments() {
        try {
            if (jTablePublicaciones.getSelectedRow() != -1) {
                DefaultTableModel dtmf = (DefaultTableModel) jTableComents.getModel();
                dtmf.setRowCount(0);
                for (Comment c : profileController.getShownProfile().getPosts().
                        get(jTablePublicaciones.getSelectedRow()).getComments()) {
                    dtmf.addRow(new Object[]{
                        c.getText(),
                        c.getSourceProfile().getName(),
                        this.formatter.format(c.getDate())
                    });
                }

            }
        } catch (IndexOutOfBoundsException e) {
        }

    }

    /*
    * Añade un like a la publicación
     */
    private void addLike(Profile profile) {
        try {
            if (profile.getPosts().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay publicaciones disponibles");
            } else if (jTablePublicaciones.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "No has seleccionado ninguna publicacion BRO");
            }
            profileController.newLike(profile.getPosts().get(jTablePublicaciones.getSelectedRow()));
        } catch (IndexOutOfBoundsException e) {
        }

    }

    /*
     * Muestra la biografía de un perfil (el de un amigo seleccionado o para
     * volver a la biografía del usuario de la sesión en caso de no estar en el suyo)
     */
    private void showBiography(boolean ownprofile) {
        // Si el perfil de la sesión es igual al mostrado, el usuario está en su perfil
        // Mostramos la pestaña de biografía
        jTabbedPane.setSelectedIndex(0);
        // Activar/desactivar pestaña de mensajes privados
        jTabbedPane.setEnabledAt(2, ownprofile);
        profileController.reloadProfile();
    }

    /*
     * Muestra el contenido del mensaje seleccionado
     */
    private void showMessage() {
        Message m = profileController.getShownProfile().getMessages().get(jTableMensajesPrivados.getSelectedRow());
        profileController.markMessageAsRead(m);
        JPanel texto = new JPanel(new GridLayout(3, 1));

        texto.add(new JLabel("De: " + m.getSourceProfile().getName() + "\n"));
        texto.add(new JLabel("Fecha: " + m.getDate() + "\n"));
        texto.add(new JLabel("texto: \n" + m.getText() + "\n"));
        Object[] buttons = {"Responder", "Eliminar", "Volver"};

        int option = JOptionPane.showOptionDialog(this, texto, "Mensaje Privado", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, DEFAULT_MODALITY_TYPE);
        switch (option) {
            case 0:
                sendPrivateMessage(m.getSourceProfile());
                break;
            case 1:
                profileController.deleteMessage(m);
                break;
        }
    }

    /*
     * Envia una solicitud de amistad
     */
    private void sendFriendshipRequest() {
        try {
            String friendToRequest = JOptionPane.showInputDialog("Introduce el nombre "
                    + "del perfil que quieres añadir como amigo:");

            if (friendToRequest.equals(profileController.getSessionProfile().getName())) {
                JOptionPane.showMessageDialog(this, "LOCO, QUE NO PUEDES AGREGARTE A TI MISMO! O.o");
            } else {
                profileController.newFriendshipRequest(friendToRequest);
            }
        } catch (NullPointerException ex) {
            ex.getMessage();
        }
    }


    /*
    * Envía un mensaje privado
     */
    private void sendPrivateMessage(Profile destProfile) {
        //TODO HAY QUE COMPROBAR QUE NO PUEDAS ENVIARTE MENSAJES A TI MISMO DESDE LA BIOGRAFÍA DE OTRO
        // if(profileController.getShownProfile().getFriends().get(jTableListaAmigos.getSelectedRow()).equals(profileController.getSessionProfile())){
        // JOptionPane.showMessageDialog(null, "No puedes enviarte un mensaje a ti mismo");
        //}
        String msg = JOptionPane.showInputDialog(this, "Introduce el texto "
                + "del mensaje", "Mensaje privado", JOptionPane.INFORMATION_MESSAGE);
        profileController.newMessage(destProfile, msg);
    }

    /*
     * Borra un mensaje privado
     */
    private void deletePrivateMessage() {
        if ((profileController.getShownProfile().getMessages().isEmpty())) {
            JOptionPane.showMessageDialog(null, "No hay mensajes diponibles");
        } else if (jTableMensajesPrivados.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "No hay ningun mensaje seleccionado");
        } else {
            profileController.deleteMessage(profileController.getShownProfile().getMessages().get(jTableMensajesPrivados.getSelectedRow()));
        }
    }

    /*
    * Muestra posts antiguos
     */
    private void showOldPosts() {
        try {
            postsShowed = Integer.parseInt(JOptionPane.showInputDialog("Escoge "
                    + "el numero de publicaciones que quieres ver: "));
            profileController.reloadProfile();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tienes que poner un número, BROH");
        }

    }

    /**
     * Error de perfil no encontrado
     */
    public void showProfileNotFoundMessage() {
        JOptionPane.showMessageDialog(this, "Perfil no encontrado.");
    }

    /**
     * Error de like a tu propio mensaje
     */
    public void showCannotLikeOwnPostMessage() {
        JOptionPane.showMessageDialog(this, "No puedes dar like a tu propio mensaje CRUCKONCIO.");
    }

    /**
     * Error de ya le has dado me gusta a este post
     */
    public void showAlreadyLikedPostMessage() {
        JOptionPane.showMessageDialog(this, "A este post ya le has dado me gusta.");
    }

    /**
     * Error "usuario" ya es tu amigo
     *
     * @param profileName
     */
    public void showIsAlreadyFriendMessage(String profileName) {
        JOptionPane.showMessageDialog(this, profileName + " ya es tu amigo.");

    }

    /**
     * Ya tiene una solicitud de amistad tuya
     *
     * @param profileName
     */
    public void showExistsFriendshipRequestMessage(String profileName) {
        JOptionPane.showMessageDialog(this, "ya tiene una solicitud de amistad contigo. " + profileName);
    }

    /**
     * Ya existe la misma solicitud de amistad
     *
     * @param profileName
     */
    public void showDuplicateFriendshipRequestMessage(String profileName) {
        JOptionPane.showMessageDialog(null, "Ya existe una solicitud de amistad para " + profileName);
    }

    /**
     * Muestra el menu del perfil y las opciones que el usuario desea hacer
     *
     * @param profile
     */
    public void showProfileMenu(Profile profile) {
        // UN RECUERDO DE ESTA MIERDA
//        System.out.println("Sesión:" + profileController.getSessionProfile().getName() + ".");
//        System.out.println("Vista:" + profileController.getShownProfile().getName() + ".");
        showProfileInfo(profile.getName().equals(profileController.getSessionProfile().getName()), profile);
        this.setVisible(true);
    }

    /*
    * Devuelve la opcion introducida por el usuario
     */
    private int readNumber(Scanner scanner) {
        int option = 0;
        try {
            option = scanner.nextInt();
            scanner.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("Tienes que introducir un número" + e.getMessage());
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelSuperior = new javax.swing.JPanel();
        jLabelName = new javax.swing.JLabel();
        jLabelNameText = new javax.swing.JLabel();
        jLabelLogoTacebook = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jLabelStatusText = new javax.swing.JLabel();
        jTabbedPane = new javax.swing.JTabbedPane();
        jTabbedBiografia = new javax.swing.JPanel();
        jSplitBibliografia = new javax.swing.JSplitPane();
        jPanelPublicaciones = new javax.swing.JPanel();
        jPanelButtonsPublicaciones = new javax.swing.JPanel();
        jButtonNuevaPubli = new javax.swing.JButton();
        jButtonComentar = new javax.swing.JButton();
        jButtonMeGusta = new javax.swing.JButton();
        jButtonVerAntPublics = new javax.swing.JButton();
        jScrollPublicaciones = new javax.swing.JScrollPane();
        jTablePublicaciones = new javax.swing.JTable();
        jLabelUltimasPublicaciones = new javax.swing.JLabel();
        jPanelComentarios = new javax.swing.JPanel();
        jLabelComentarios = new javax.swing.JLabel();
        jScrollComentarios = new javax.swing.JScrollPane();
        jTableComents = new javax.swing.JTable();
        jTabbedAmigos = new javax.swing.JPanel();
        jSplitAmigos = new javax.swing.JSplitPane();
        jPanelArriba = new javax.swing.JPanel();
        jScrollPaneAmigos = new javax.swing.JScrollPane();
        jTableListaAmigos = new javax.swing.JTable();
        jPanelOpcionesAmigos = new javax.swing.JPanel();
        jButtonVerBiografia = new javax.swing.JButton();
        jButtonEnviarMensajePrivado = new javax.swing.JButton();
        jPanelAbajo = new javax.swing.JPanel();
        jScrollPaneSolicitudesAmistad = new javax.swing.JScrollPane();
        jListSolicitudAmistad = new javax.swing.JList<>();
        jLabelSolicitudesAmistad = new javax.swing.JLabel();
        jPanelOpcionesSolicitud = new javax.swing.JPanel();
        jButtonAceptarSolicitudAmistad = new javax.swing.JButton();
        jButtonRechazarSolicitudAmistad = new javax.swing.JButton();
        jButtonNuevaSolicitudAmistad = new javax.swing.JButton();
        jLabelListaAmigos = new javax.swing.JLabel();
        jTabbedMensajesPrivados = new javax.swing.JPanel();
        jScrollPaneMensajesPrivados = new javax.swing.JScrollPane();
        jTableMensajesPrivados = new javax.swing.JTable();
        jLabelMensajesPrivados = new javax.swing.JLabel();
        jPanelOpcionesMensajesPrivados = new javax.swing.JPanel();
        jButtonLeerMensaje = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanelInferior = new javax.swing.JPanel();
        jButtonVolverAMiBiografía = new javax.swing.JButton();
        jButtonEnviarMensaje2 = new javax.swing.JButton();
        jButtonCambiarEstado = new javax.swing.JButton();
        jButtonCerrarSesion = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tacebook");
        setPreferredSize(new java.awt.Dimension(1120, 800));
        setResizable(false);

        jLabelName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Usuario-de-género-neutro-48.png"))); // NOI18N
        jLabelName.setText("Perfil de usuario:");
        jPanelSuperior.add(jLabelName);

        jLabelNameText.setMaximumSize(new java.awt.Dimension(280, 44));
        jLabelNameText.setMinimumSize(new java.awt.Dimension(280, 44));
        jLabelNameText.setPreferredSize(new java.awt.Dimension(280, 44));
        jPanelSuperior.add(jLabelNameText);

        jLabelLogoTacebook.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLogoTacebook.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/TACEBOOK.png.png"))); // NOI18N
        jPanelSuperior.add(jLabelLogoTacebook);

        jLabelStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Status-48.png"))); // NOI18N
        jLabelStatus.setText("Estado actual:");
        jPanelSuperior.add(jLabelStatus);

        jLabelStatusText.setMaximumSize(new java.awt.Dimension(280, 44));
        jLabelStatusText.setMinimumSize(new java.awt.Dimension(280, 44));
        jLabelStatusText.setPreferredSize(new java.awt.Dimension(280, 44));
        jPanelSuperior.add(jLabelStatusText);

        jSplitBibliografia.setDividerLocation(300);
        jSplitBibliografia.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanelPublicaciones.setLayout(new java.awt.BorderLayout());

        jPanelButtonsPublicaciones.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelButtonsPublicaciones.setLayout(new java.awt.GridLayout(1, 0));

        jButtonNuevaPubli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Nueva-publicacion-48.png"))); // NOI18N
        jButtonNuevaPubli.setText("Nueva Publicación");
        jButtonNuevaPubli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNuevaPubliActionPerformed(evt);
            }
        });
        jPanelButtonsPublicaciones.add(jButtonNuevaPubli);

        jButtonComentar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/comentar-30.png"))); // NOI18N
        jButtonComentar.setText("Comentar");
        jButtonComentar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonComentarActionPerformed(evt);
            }
        });
        jPanelButtonsPublicaciones.add(jButtonComentar);

        jButtonMeGusta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/me-gusta-30.png"))); // NOI18N
        jButtonMeGusta.setText("Me gusta");
        jButtonMeGusta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMeGustaActionPerformed(evt);
            }
        });
        jPanelButtonsPublicaciones.add(jButtonMeGusta);

        jButtonVerAntPublics.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/publicaciones anteriores-30.png"))); // NOI18N
        jButtonVerAntPublics.setText("Ver anteriores publicaciones");
        jButtonVerAntPublics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVerAntPublicsActionPerformed(evt);
            }
        });
        jPanelButtonsPublicaciones.add(jButtonVerAntPublics);

        jPanelPublicaciones.add(jPanelButtonsPublicaciones, java.awt.BorderLayout.SOUTH);

        jTablePublicaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "Autor", "Texto", "Me gustas"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTablePublicaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTablePublicacionesMouseClicked(evt);
            }
        });
        jTablePublicaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTablePublicacionesKeyReleased(evt);
            }
        });
        jScrollPublicaciones.setViewportView(jTablePublicaciones);

        jPanelPublicaciones.add(jScrollPublicaciones, java.awt.BorderLayout.CENTER);

        jLabelUltimasPublicaciones.setText("10 ultimas publicaciones:");
        jPanelPublicaciones.add(jLabelUltimasPublicaciones, java.awt.BorderLayout.PAGE_START);

        jSplitBibliografia.setTopComponent(jPanelPublicaciones);

        jPanelComentarios.setLayout(new java.awt.BorderLayout());

        jLabelComentarios.setText("Comentarios: ");
        jPanelComentarios.add(jLabelComentarios, java.awt.BorderLayout.PAGE_START);

        jTableComents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Texto", "De", "Fecha"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollComentarios.setViewportView(jTableComents);

        jPanelComentarios.add(jScrollComentarios, java.awt.BorderLayout.CENTER);

        jSplitBibliografia.setBottomComponent(jPanelComentarios);

        javax.swing.GroupLayout jTabbedBiografiaLayout = new javax.swing.GroupLayout(jTabbedBiografia);
        jTabbedBiografia.setLayout(jTabbedBiografiaLayout);
        jTabbedBiografiaLayout.setHorizontalGroup(
            jTabbedBiografiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitBibliografia, javax.swing.GroupLayout.DEFAULT_SIZE, 1117, Short.MAX_VALUE)
        );
        jTabbedBiografiaLayout.setVerticalGroup(
            jTabbedBiografiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTabbedBiografiaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitBibliografia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Biografía", new javax.swing.ImageIcon(getClass().getResource("/Imagenes/biografía-30.png")), jTabbedBiografia); // NOI18N

        jSplitAmigos.setDividerLocation(300);
        jSplitAmigos.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanelArriba.setLayout(new java.awt.BorderLayout());

        jTableListaAmigos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneAmigos.setViewportView(jTableListaAmigos);

        jPanelArriba.add(jScrollPaneAmigos, java.awt.BorderLayout.CENTER);

        jButtonVerBiografia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/biografía-30.png"))); // NOI18N
        jButtonVerBiografia.setText("Ver Biografía");
        jButtonVerBiografia.setMaximumSize(new java.awt.Dimension(196, 46));
        jButtonVerBiografia.setMinimumSize(new java.awt.Dimension(196, 46));
        jButtonVerBiografia.setPreferredSize(new java.awt.Dimension(196, 46));
        jButtonVerBiografia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVerBiografiaActionPerformed(evt);
            }
        });
        jPanelOpcionesAmigos.add(jButtonVerBiografia);

        jButtonEnviarMensajePrivado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/enviar Mensaje-30.png"))); // NOI18N
        jButtonEnviarMensajePrivado.setText("Enviar mensaje privado");
        jButtonEnviarMensajePrivado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEnviarMensajePrivadoActionPerformed(evt);
            }
        });
        jPanelOpcionesAmigos.add(jButtonEnviarMensajePrivado);

        jPanelArriba.add(jPanelOpcionesAmigos, java.awt.BorderLayout.PAGE_END);

        jSplitAmigos.setTopComponent(jPanelArriba);

        jPanelAbajo.setLayout(new java.awt.BorderLayout());

        jScrollPaneSolicitudesAmistad.setViewportView(jListSolicitudAmistad);

        jPanelAbajo.add(jScrollPaneSolicitudesAmistad, java.awt.BorderLayout.CENTER);

        jLabelSolicitudesAmistad.setText("Tienes solicitudes de amistad de los siguientes perfiles:");
        jPanelAbajo.add(jLabelSolicitudesAmistad, java.awt.BorderLayout.PAGE_START);

        jSplitAmigos.setRightComponent(jPanelAbajo);

        jButtonAceptarSolicitudAmistad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/aceptar-30.png"))); // NOI18N
        jButtonAceptarSolicitudAmistad.setText("Aceptar solicitud");
        jButtonAceptarSolicitudAmistad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAceptarSolicitudAmistadActionPerformed(evt);
            }
        });
        jPanelOpcionesSolicitud.add(jButtonAceptarSolicitudAmistad);

        jButtonRechazarSolicitudAmistad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Rechazar solicitud-30.png"))); // NOI18N
        jButtonRechazarSolicitudAmistad.setText("Rechazar solicitud");
        jButtonRechazarSolicitudAmistad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRechazarSolicitudAmistadActionPerformed(evt);
            }
        });
        jPanelOpcionesSolicitud.add(jButtonRechazarSolicitudAmistad);

        jButtonNuevaSolicitudAmistad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/solicitud de amistad-30.png"))); // NOI18N
        jButtonNuevaSolicitudAmistad.setText("Nueva solicitud de amistad");
        jButtonNuevaSolicitudAmistad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNuevaSolicitudAmistadActionPerformed(evt);
            }
        });
        jPanelOpcionesSolicitud.add(jButtonNuevaSolicitudAmistad);

        jLabelListaAmigos.setText("Lista de amig@s:");

        javax.swing.GroupLayout jTabbedAmigosLayout = new javax.swing.GroupLayout(jTabbedAmigos);
        jTabbedAmigos.setLayout(jTabbedAmigosLayout);
        jTabbedAmigosLayout.setHorizontalGroup(
            jTabbedAmigosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitAmigos)
            .addComponent(jPanelOpcionesSolicitud, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jTabbedAmigosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelListaAmigos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jTabbedAmigosLayout.setVerticalGroup(
            jTabbedAmigosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTabbedAmigosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelListaAmigos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitAmigos, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelOpcionesSolicitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane.addTab("Amigos", new javax.swing.ImageIcon(getClass().getResource("/Imagenes/amigos-30.png")), jTabbedAmigos); // NOI18N

        jTableMensajesPrivados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Visto", "Fecha", "De", "Texto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableMensajesPrivados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMensajesPrivadosMouseClicked(evt);
            }
        });
        jScrollPaneMensajesPrivados.setViewportView(jTableMensajesPrivados);

        jLabelMensajesPrivados.setText("Mensajes privados:");

        jButtonLeerMensaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Leer mensaje-30.png"))); // NOI18N
        jButtonLeerMensaje.setText("Leer Mensaje");
        jButtonLeerMensaje.setMaximumSize(new java.awt.Dimension(196, 46));
        jButtonLeerMensaje.setMinimumSize(new java.awt.Dimension(196, 46));
        jButtonLeerMensaje.setPreferredSize(new java.awt.Dimension(196, 46));
        jButtonLeerMensaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLeerMensajeActionPerformed(evt);
            }
        });
        jPanelOpcionesMensajesPrivados.add(jButtonLeerMensaje);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/eliminar-30.png"))); // NOI18N
        jButton3.setText("Eliminar Mensaje");
        jButton3.setMaximumSize(new java.awt.Dimension(196, 46));
        jButton3.setMinimumSize(new java.awt.Dimension(196, 46));
        jButton3.setPreferredSize(new java.awt.Dimension(196, 46));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanelOpcionesMensajesPrivados.add(jButton3);

        javax.swing.GroupLayout jTabbedMensajesPrivadosLayout = new javax.swing.GroupLayout(jTabbedMensajesPrivados);
        jTabbedMensajesPrivados.setLayout(jTabbedMensajesPrivadosLayout);
        jTabbedMensajesPrivadosLayout.setHorizontalGroup(
            jTabbedMensajesPrivadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTabbedMensajesPrivadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTabbedMensajesPrivadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneMensajesPrivados, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1105, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jTabbedMensajesPrivadosLayout.createSequentialGroup()
                        .addComponent(jLabelMensajesPrivados, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanelOpcionesMensajesPrivados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jTabbedMensajesPrivadosLayout.setVerticalGroup(
            jTabbedMensajesPrivadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTabbedMensajesPrivadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelMensajesPrivados, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jScrollPaneMensajesPrivados, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelOpcionesMensajesPrivados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Mensajes Privados", new javax.swing.ImageIcon(getClass().getResource("/Imagenes/mensaje privado-30.png")), jTabbedMensajesPrivados); // NOI18N

        jPanelInferior.setBorder(new javax.swing.border.MatteBorder(null));
        jPanelInferior.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout();
        flowLayout1.setAlignOnBaseline(true);
        jPanelInferior.setLayout(flowLayout1);

        jButtonVolverAMiBiografía.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/volver-30.png"))); // NOI18N
        jButtonVolverAMiBiografía.setText("Volver a mi biografía");
        jButtonVolverAMiBiografía.setMaximumSize(new java.awt.Dimension(196, 46));
        jButtonVolverAMiBiografía.setMinimumSize(new java.awt.Dimension(196, 46));
        jButtonVolverAMiBiografía.setPreferredSize(new java.awt.Dimension(196, 46));
        jButtonVolverAMiBiografía.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVolverAMiBiografíaActionPerformed(evt);
            }
        });
        jPanelInferior.add(jButtonVolverAMiBiografía);

        jButtonEnviarMensaje2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/enviar Mensaje-30.png"))); // NOI18N
        jButtonEnviarMensaje2.setText("Enviar Mensaje Privado");
        jButtonEnviarMensaje2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEnviarMensaje2ActionPerformed(evt);
            }
        });
        jPanelInferior.add(jButtonEnviarMensaje2);

        jButtonCambiarEstado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Status-30.png"))); // NOI18N
        jButtonCambiarEstado.setText("Cambiar estado");
        jButtonCambiarEstado.setMaximumSize(new java.awt.Dimension(196, 46));
        jButtonCambiarEstado.setMinimumSize(new java.awt.Dimension(196, 46));
        jButtonCambiarEstado.setPreferredSize(new java.awt.Dimension(196, 46));
        jButtonCambiarEstado.setVerifyInputWhenFocusTarget(false);
        jButtonCambiarEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCambiarEstadoActionPerformed(evt);
            }
        });
        jPanelInferior.add(jButtonCambiarEstado);

        jButtonCerrarSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cerrar sesion-30.png"))); // NOI18N
        jButtonCerrarSesion.setText("Cerrar sesión");
        jButtonCerrarSesion.setBorder(null);
        jButtonCerrarSesion.setBorderPainted(false);
        jButtonCerrarSesion.setMaximumSize(new java.awt.Dimension(196, 46));
        jButtonCerrarSesion.setMinimumSize(new java.awt.Dimension(196, 46));
        jButtonCerrarSesion.setPreferredSize(new java.awt.Dimension(196, 46));
        jButtonCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCerrarSesionActionPerformed(evt);
            }
        });
        jPanelInferior.add(jButtonCerrarSesion);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane)
                .addGap(1, 1, 1))
            .addComponent(jPanelSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelInferior, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 646, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelInferior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonVerAntPublicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVerAntPublicsActionPerformed
        showOldPosts();
    }//GEN-LAST:event_jButtonVerAntPublicsActionPerformed

    private void jButtonMeGustaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMeGustaActionPerformed

        addLike(profileController.getShownProfile());
    }//GEN-LAST:event_jButtonMeGustaActionPerformed

    private void jButtonComentarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonComentarActionPerformed
        commentPost(profileController.getShownProfile());
    }//GEN-LAST:event_jButtonComentarActionPerformed

    private void jButtonNuevaPubliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNuevaPubliActionPerformed
        writeNewPost(profileController.getShownProfile());
    }//GEN-LAST:event_jButtonNuevaPubliActionPerformed

    private void jButtonCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCerrarSesionActionPerformed
        int option = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas cerrar sesión?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            dispose();
        }
    }//GEN-LAST:event_jButtonCerrarSesionActionPerformed

    private void jButtonCambiarEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCambiarEstadoActionPerformed
        changeStatus();
    }//GEN-LAST:event_jButtonCambiarEstadoActionPerformed

    private void jButtonVerBiografiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVerBiografiaActionPerformed

        try {
            if (jTableListaAmigos.getSelectedRow() != -1) {
                profileController.setShownProfile(profileController.getSessionProfile().getFriends().get(jTableListaAmigos.getSelectedRow()));
                showBiography(false);
            } else {
                // Mensaje en caso de no seleccionar una amistad
                JOptionPane.showMessageDialog(this, "Selecciona una amistad para ver su biografía");
            }
        } catch (NullPointerException e) {
            System.out.println("Error al ver la bio: " + e.getMessage());
        }

    }//GEN-LAST:event_jButtonVerBiografiaActionPerformed

    private void jButtonNuevaSolicitudAmistadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNuevaSolicitudAmistadActionPerformed
        sendFriendshipRequest();
    }//GEN-LAST:event_jButtonNuevaSolicitudAmistadActionPerformed

    private void jButtonAceptarSolicitudAmistadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAceptarSolicitudAmistadActionPerformed
        profileController.acceptFriendshipRequest(profileController.
                getSessionProfile().getFriendshipRequests().get(jListSolicitudAmistad.getSelectedIndex()));
    }//GEN-LAST:event_jButtonAceptarSolicitudAmistadActionPerformed

    private void jButtonRechazarSolicitudAmistadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRechazarSolicitudAmistadActionPerformed

        profileController.rejectFriendshipRequest(profileController.
                getSessionProfile().getFriendshipRequests().get(jListSolicitudAmistad.getSelectedIndex()));
    }//GEN-LAST:event_jButtonRechazarSolicitudAmistadActionPerformed

    private void jTablePublicacionesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTablePublicacionesMouseClicked
        showComments();
    }//GEN-LAST:event_jTablePublicacionesMouseClicked

    private void jTablePublicacionesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTablePublicacionesKeyReleased
        showComments();
    }//GEN-LAST:event_jTablePublicacionesKeyReleased

    private void jButtonVolverAMiBiografíaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVolverAMiBiografíaActionPerformed
        profileController.setShownProfile(profileController.getSessionProfile());
        showBiography(true);
    }//GEN-LAST:event_jButtonVolverAMiBiografíaActionPerformed

    private void jButtonEnviarMensaje2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnviarMensaje2ActionPerformed
        sendPrivateMessage(profileController.getShownProfile());
    }//GEN-LAST:event_jButtonEnviarMensaje2ActionPerformed

    private void jButtonEnviarMensajePrivadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnviarMensajePrivadoActionPerformed
        if (jTableListaAmigos.getSelectedRow() != -1) {
            sendPrivateMessage(profileController.getShownProfile().
                    getFriends().get(jTableListaAmigos.getSelectedRow()));
            //TODO HAY QUE COMPROBAR QUE NO PUEDAS ENVIARTE MENSAJES A TI MISMO DESDE LA BIOGRAFÍA DE OTRO
            // if(profileController.getShownProfile().getFriends().get(jTableListaAmigos.getSelectedRow()).equals(profileController.getSessionProfile())){
            // JOptionPane.showMessageDialog(null, "No puedes enviarte un mensaje a ti mismo");
            //}
        } else if (profileController.getShownProfile().getFriends().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bro, no tienes amigos...");
        } else {
            JOptionPane.showMessageDialog(this, "Debes seleccionar una amistad");
        }

    }//GEN-LAST:event_jButtonEnviarMensajePrivadoActionPerformed

    private void jTableMensajesPrivadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMensajesPrivadosMouseClicked
        if (evt.getClickCount() > 1) {
            showMessage();
        }
    }//GEN-LAST:event_jTableMensajesPrivadosMouseClicked

    private void jButtonLeerMensajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeerMensajeActionPerformed
        if (jTableMensajesPrivados.getSelectedRow() != -1) {
            showMessage();
        } else {
            JOptionPane.showMessageDialog(null, "Ustéh nesesita selecssionar un mensaje");
        }
    }//GEN-LAST:event_jButtonLeerMensajeActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        deletePrivateMessage();
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButtonAceptarSolicitudAmistad;
    private javax.swing.JButton jButtonCambiarEstado;
    private javax.swing.JButton jButtonCerrarSesion;
    private javax.swing.JButton jButtonComentar;
    private javax.swing.JButton jButtonEnviarMensaje2;
    private javax.swing.JButton jButtonEnviarMensajePrivado;
    private javax.swing.JButton jButtonLeerMensaje;
    private javax.swing.JButton jButtonMeGusta;
    private javax.swing.JButton jButtonNuevaPubli;
    private javax.swing.JButton jButtonNuevaSolicitudAmistad;
    private javax.swing.JButton jButtonRechazarSolicitudAmistad;
    private javax.swing.JButton jButtonVerAntPublics;
    private javax.swing.JButton jButtonVerBiografia;
    private javax.swing.JButton jButtonVolverAMiBiografía;
    private javax.swing.JLabel jLabelComentarios;
    private javax.swing.JLabel jLabelListaAmigos;
    private javax.swing.JLabel jLabelLogoTacebook;
    private javax.swing.JLabel jLabelMensajesPrivados;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelNameText;
    private javax.swing.JLabel jLabelSolicitudesAmistad;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelStatusText;
    private javax.swing.JLabel jLabelUltimasPublicaciones;
    private javax.swing.JList<String> jListSolicitudAmistad;
    private javax.swing.JPanel jPanelAbajo;
    private javax.swing.JPanel jPanelArriba;
    private javax.swing.JPanel jPanelButtonsPublicaciones;
    private javax.swing.JPanel jPanelComentarios;
    private javax.swing.JPanel jPanelInferior;
    private javax.swing.JPanel jPanelOpcionesAmigos;
    private javax.swing.JPanel jPanelOpcionesMensajesPrivados;
    private javax.swing.JPanel jPanelOpcionesSolicitud;
    private javax.swing.JPanel jPanelPublicaciones;
    private javax.swing.JPanel jPanelSuperior;
    private javax.swing.JScrollPane jScrollComentarios;
    private javax.swing.JScrollPane jScrollPaneAmigos;
    private javax.swing.JScrollPane jScrollPaneMensajesPrivados;
    private javax.swing.JScrollPane jScrollPaneSolicitudesAmistad;
    private javax.swing.JScrollPane jScrollPublicaciones;
    private javax.swing.JSplitPane jSplitAmigos;
    private javax.swing.JSplitPane jSplitBibliografia;
    private javax.swing.JPanel jTabbedAmigos;
    private javax.swing.JPanel jTabbedBiografia;
    private javax.swing.JPanel jTabbedMensajesPrivados;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable jTableComents;
    private javax.swing.JTable jTableListaAmigos;
    private javax.swing.JTable jTableMensajesPrivados;
    private javax.swing.JTable jTablePublicaciones;
    // End of variables declaration//GEN-END:variables
}
