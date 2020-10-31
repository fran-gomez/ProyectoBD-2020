package Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;

import static javax.swing.UIManager.getString;

public class LoginView extends JPanel {
    private static final long serialVersionUID = 1L;

    protected JFrame ventana;
    private JPanel jPanel1;
    private JPanel jPanel2;
    
    private JButton login;
    private JLabel labelBienvenide;
    private JLabel labelUsername;
    private JLabel labelPassword;
    private JTextField username;
    private JPasswordField password;

    public LoginView(JFrame ventana) {
        super();

        this.ventana = ventana;
        labelBienvenide = new JLabel();

        jPanel1 = new JPanel();
        jPanel2 = new JPanel();

        labelUsername = new JLabel();
        labelPassword = new JLabel();
        username = new JTextField();
        password = new JPasswordField();

        login = new JButton();

        // Reposicionando las componentes graficas
        setPreferredSize(new java.awt.Dimension(400,400));

        labelBienvenide.setFont(new Font("Calibri",1,18));
        labelBienvenide.setHorizontalAlignment(SwingConstants.CENTER);
        labelBienvenide.setText("Parquimetros");
        labelBienvenide.setPreferredSize(new Dimension(400,50));
        add(labelBienvenide);

        labelUsername.setText("Usuario o Legajo");
        labelPassword.setText("Password");
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


        GroupLayout jPanel3Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel3Layout);
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

        add(jPanel2);

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
        // Tratamos varias tecnicas para obtener el texto almacenado en el JPasswordField pero solo pudimos solucionarlo con 
        // getText aunque este en desuso. Sabemos de las implicaciones en fallas de seguridad pero utilizamos MD5 para esta salvedad.
        String psswd = password.getText();
        String url = "jdbc:mysql://" + srv + "/" + bd + "?serverTimezone=America/Argentina/Buenos_Aires";
        Connection conexion;

        try{
        	// Usamos ambos servicios de bases de datos, por lo que dejamos las clases adjuntas.
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Class.forName("org.mariadb.jdbc.Driver");

            if (uname.equals("admin")) {
                conexion = DriverManager.getConnection(url, uname, psswd);
                if(conexion != null){
                    new AdminView(ventana, conexion);
                    cleanView();
                }
            } else if (esNumero(uname)) {
                conexion = DriverManager.getConnection(url, "inspector", "inspector");
                if (loginInspector(uname, conexion, psswd)){
                    new InspectorView(ventana, conexion, uname);
                    cleanView();
                }
                else
                    throw new SQLException();
            } else {
                JOptionPane.showMessageDialog(new JFrame("Error"), "No se encontraron usuarios o inspectores");
                resetFields();
            }

        } catch (SQLException e){
            JOptionPane.showMessageDialog(new JFrame("Error"), "Password incorrecto de " + uname, "BD-2020", JOptionPane.ERROR_MESSAGE);
            resetFields();
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

    private boolean esNumero(String uname) {
        try {
            Integer.parseInt(uname);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private void cleanView(){
        username.setVisible(false);
        password.setVisible(false);
        login.setVisible(false);
        labelPassword.setVisible(false);
        labelUsername.setVisible(false);
    }
    private void resetFields() {
        username.setText("");
        password.setText("");
    }

}
