/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;

public class LoginScreen extends javax.swing.JPanel {

    private JButton login;
    private JLabel labelBienvenido;
    private JLabel labelUsername;
    private JLabel labelPassword;
    private JPanel jPanel1;
    private JPanel jPanel3;
    private JTextField username;
    private JPasswordField password;
    private JPanel jPanel2;
    protected JFrame ventana;

    public LoginScreen(JFrame ventana) {
        super();

        this.ventana = ventana;
        labelBienvenido = new JLabel();
        jPanel3 = new JPanel();
        jPanel1 = new JPanel();
        labelPassword = new JLabel();
        labelUsername = new JLabel();
        username = new JTextField();
        password = new JPasswordField();
        login = new JButton();

        setPreferredSize(new java.awt.Dimension(400, 400));

        labelBienvenido.setFont(new java.awt.Font("Tahoma", 1, 18));
        labelBienvenido.setHorizontalAlignment(SwingConstants.CENTER);
        labelBienvenido.setText("Reserva pasajes de avion");
        labelBienvenido.setPreferredSize(new java.awt.Dimension(400, 50));
        add(labelBienvenido);

        labelUsername.setText("Nombre de Usuario");
        labelPassword.setText("Contraseña");
        labelPassword.setHorizontalAlignment(SwingConstants.CENTER);
        labelUsername.setHorizontalAlignment(SwingConstants.CENTER);

        labelUsername.setPreferredSize(new java.awt.Dimension(200, 15));
        labelUsername.setPreferredSize(new java.awt.Dimension(200, 15));

        jPanel3.setPreferredSize(new java.awt.Dimension(400, 200));

        jPanel1.setPreferredSize(new java.awt.Dimension(200, 140));

        username.setHorizontalAlignment(JTextField.CENTER);
        username.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        username.setPreferredSize(new java.awt.Dimension(200, 30));
        jPanel1.add(labelUsername);
        jPanel1.add(username);


        password.setHorizontalAlignment(JTextField.CENTER);
        password.setPreferredSize(new java.awt.Dimension(200, 30));
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
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iniciarSesion(evt);
            }
        });
        add(login);
    }

    private void iniciarSesion(java.awt.event.ActionEvent evt) {
        String srv = "localhost:3306";
        String bd = "parquimetros";
        String uname = username.getText();
        String psswd = password.getText();
        Connection conexion;

        try {
            if (uname.equals("admin")) {
                conexion = DriverManager.getConnection("jdbc:mysql://" + srv + "/" + bd + "?serverTimezone=America/Argentina/Buenos_Aires", uname, psswd);

                if (conexion != null){
                    limpiarUI();
                    if (uname.equals("admin")){
                        new AdminUI(ventana, conexion);
                    }
                }

            } else if(esNumero(uname)){ // ha de ser un legajo de un empleado
                conexion = DriverManager.getConnection("jdbc:mysql://" + srv + "/" + bd + "?serverTimezone=America/Argentina/Buenos_Aires", "empleado", "empleado");

                if (conexion != null) {
                    new EmpleadoUI(ventana, conexion, Integer.parseInt(uname), psswd);
                }

            }

        } catch (SQLInvalidAuthorizationSpecException e) {
            JOptionPane.showMessageDialog(null,
                    "Error en la contraseña de " + uname,
                    "BD-2019",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void limpiarUI() {
        username.setVisible(false);
        password.setVisible(false);
        login.setVisible(false);
        labelPassword.setVisible(false);
        labelUsername.setVisible(false);
    }

    private boolean esNumero(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}