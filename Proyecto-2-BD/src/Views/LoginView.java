package Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.MessageDigest;
import java.sql.*;

import static javax.swing.UIManager.getString;


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
        labelPassword.setText("Contrase�a");
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
        password.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent keyEvent) {}
            public void keyReleased(KeyEvent keyEvent) {}

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == '\n')
                    login();
            }
        });
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
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        login.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent keyEvent) {}
            public void keyReleased(KeyEvent keyEvent) {}

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == '\n')
                    login();
            }
        });
        add(login);
    }

    private void login(){
        String srv = "localhost:3306";
        String bd = "parquimetros";
        String uname = username.getText();
        String psswd = password.getText();
        String url = "jdbc:mysql://" + srv + "/" + bd + "?serverTimezone=America/Argentina/Buenos_Aires";
        Connection conexion;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            if (uname.equals("admin")) {
                conexion = DriverManager.getConnection(url, uname, psswd);
                cleanUI();
                if(conexion != null)
                    new AdminView(ventana, conexion);
            } else if (esNumero(uname)) {
                conexion = DriverManager.getConnection(url, "inspector", "inspector");
                cleanUI();
                if (loginInspector(uname, conexion, psswd))
                    new InspectorView(ventana, conexion, uname);
                else
                    throw new SQLException();
            }

        } catch (SQLException e){
            JOptionPane.showMessageDialog(new JFrame("Error"), "Error en la contrase�a de " + uname, "BD-2020", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean loginInspector(String uname, Connection conexion, String password) {
        try {
            Statement s = conexion.createStatement();
            String sql = "SELECT legajo, password FROM inspectores WHERE legajo=" + uname;
            ResultSet rs = s.executeQuery(sql);
            String md5Passwd = "";

            while (rs.next())
                md5Passwd = rs.getString("password");

            s.close();
            rs.close();

            return md5Passwd.equals(MD5.getMD5(password));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }
/*
    private String MD5(String str, Connection c) {
        String password = "";

        try {
            Statement s = c.createStatement();
            String sql = "password=md5("+str+")";
            ResultSet rs = s.executeQuery(sql);


            while (rs.next())
                password = rs.getString("password");

            s.close();
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            return password;
        }
    }
*/
    private boolean esNumero(String uname) {
        try {
            Integer.parseInt(uname);
            return true;
        } catch(NumberFormatException e){
            return false;
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
