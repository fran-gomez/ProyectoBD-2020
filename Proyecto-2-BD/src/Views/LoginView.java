package Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;


public class LoginView extends JPanel {
    private static final long serialVersionUID = 1L;

    protected JFrame ventana;

    private JButton login;
    private JLabel labelBienvenide;
    private JLabel labelUsername;
    private JLabel labelPassword;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JTextField username;
    private JPasswordField password;

    public LoginView(JFrame ventana) {
        super();

        this.ventana = ventana;
        labelBienvenide = new JLabel();

        jPanel1 = new JPanel();
        jPanel2 = new JPanel();
        jPanel3 = new JPanel();

        labelUsername = new JLabel();
        labelPassword = new JLabel();
        username = new JTextField();
        password = new JPasswordField();

        login = new JButton();

        setPreferredSize(new java.awt.Dimension(400,400));

        labelBienvenide.setFont(new Font("Calibri",1,18));
        labelBienvenide.setHorizontalAlignment(SwingConstants.CENTER);
        labelBienvenide.setText("Parquimetros");
        labelBienvenide.setPreferredSize(new Dimension(400,50));
        add(labelBienvenide);

        labelUsername.setText("Nombre de usuario");
        labelPassword.setText("Contraseña");
        labelUsername.setHorizontalAlignment(SwingConstants.CENTER);
        labelPassword.setHorizontalAlignment(SwingConstants.CENTER);

        jPanel1.setPreferredSize(new Dimension(200,140));

        username.setHorizontalAlignment(JTextField.CENTER);
        username.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        username.setPreferredSize(new Dimension(200,30));
        jPanel1.add(labelUsername);
        jPanel1.add(username);

        password.setHorizontalAlignment(JTextField.CENTER);
        password.setPreferredSize(new Dimension(200,30));
        jPanel1.add(labelPassword);
        jPanel1.add(password);


        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 200, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );

        add(jPanel3);

        login.setText("Iniciar sesion");
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(e);
            }
        });
        add(login);
    }

    private void login(ActionEvent evt){
        String srv = "localhost:3306";
        String bd = "parquimetros";
        String uname = username.getText();
        String psswd = "admin"; //
        // String psswd = String.valueOf(password.getPassword().toString());
        // TODO obtener el password del jpassword field que con la sentencia de arriba no se pudo.
        // hay que codificarla con md5 para ingresar
        Connection conexion;

        try{
            Class.forName("org.mariadb.jdbc.Driver");
            if(uname.equals("admin")) {
                conexion = DriverManager.getConnection("jdbc:mysql://" + srv + "/" + bd + "?serverTimezone=America/Argentina/Buenos_Aires", uname, psswd);
                if(conexion != null){
                    cleanUI();
                    if(uname.equals("admin")){
                        new AdminView(ventana, conexion);
                    }
                }
            } //else es un chabon de los parquimetros
        } catch (SQLInvalidAuthorizationSpecException e) {
            JOptionPane.showMessageDialog(null, "Error en la contraseña de " + uname, "BD-2020", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e){
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void cleanUI() {
        username.setVisible(false);
        password.setVisible(false);
        login.setVisible(false);
        labelPassword.setVisible(false);
        labelUsername.setVisible(false);
    }

}
