package Views;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;

public class Login {
	/* Constants */
	final static String PARQUIMETROS = "Parquimetros";
	final static String UNIDAD_PERSONAL = "Unidad Personal";
	final static String ADMIN = "Admin";
	final static String LOGIN = "Login";

	/* General UI Components */
	private JPanel mainPanel; 
	private JPanel viewLogin;
	private AdminView viewAdmin;
	
	private ParquimetroView viewParquimetro;
	private UnidadPersonalView viewUnidadPersonal;

	/* Login UI Components */
	private JLabel lblUsuario;
	private JTextField txtUsuario;
	private JLabel lblPassword;
	private JPasswordField txtPassword;
	private JLabel lblBienvenido;
	private JLabel lblInfo1;
	private JLabel lblComision;
	private JButton btnLogin;
	private JLabel lblInfo2;

	/* DB Connection */
	Connection conexion;

	public void createUIComponents(Container pane) {
		/* Create the panel and the "cards" */
		mainPanel = new JPanel(new CardLayout());
		
		viewLogin = new JPanel();
		viewAdmin = new AdminView();
		
		viewUnidadPersonal = new UnidadPersonalView();
		viewParquimetro = new ParquimetroView();

		/* Set de login layout */
		viewLogin.setBackground(Color.DARK_GRAY);
		viewLogin.setLayout(
				new MigLayout("", "[114.00px][112.00px][132.00][102.00]", "[64.00px][51.00][39.00][36.00][30.00][25.00][48.00][76.00][179.00]"));

		mainPanel.add(viewLogin, LOGIN);  	
		lblBienvenido = new JLabel("Bienvenido");
		lblBienvenido.setForeground(Color.WHITE);
		lblBienvenido.setFont(new Font("Roboto", Font.BOLD, 22));
		viewLogin.add(lblBienvenido, "cell 1 1 2 1");

		lblInfo1 = new JLabel("Ingrese con su usuario y contrase\u00F1a.");
		lblInfo1.setForeground(Color.WHITE);
		lblInfo1.setFont(new Font("Roboto", Font.PLAIN, 12));
		viewLogin.add(lblInfo1, "cell 1 3 3 1");

		//Setting up the login card
		lblUsuario = new JLabel("Usuario");
		lblUsuario.setForeground(Color.WHITE);
		lblUsuario.setFont(new Font("Roboto", Font.BOLD, 14));
		viewLogin.add(lblUsuario, "cell 1 5,alignx left");

		txtUsuario = new JTextField();
		txtUsuario.setColumns(16);
		txtUsuario.setFont(new Font("Roboto", Font.PLAIN, 12));
		viewLogin.add(txtUsuario, "cell 2 5,alignx left");

		lblPassword = new JLabel("Password");
		lblPassword.setForeground(Color.WHITE);
		lblPassword.setFont(new Font("Roboto", Font.BOLD, 14));
		viewLogin.add(lblPassword, "cell 1 6,alignx left");

		btnLogin = new JButton("Ingresar");
		btnLogin.setFont(new Font("Roboto", Font.PLAIN, 12));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		viewLogin.add(btnLogin, "cell 2 7,alignx center,aligny bottom");

		txtPassword = new JPasswordField();
		txtPassword.setColumns(16);
		txtPassword.setFont(new Font("Roboto", Font.BOLD, 12));
		txtPassword.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent keyEvent) {}
			public void keyReleased(KeyEvent keyEvent) {}

			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.getKeyChar() == '\n')
					login();
			}
		});
		viewLogin.add(txtPassword, "cell 2 6,alignx left");


		lblComision = new JLabel("Comision Iglesias-Volonterio");
		lblComision.setForeground(Color.WHITE);
		lblComision.setFont(new Font("Roboto", Font.PLAIN, 12));
		viewLogin.add(lblComision, "cell 0 8 2 1,aligny bottom");

		lblInfo2 = new JLabel("La aplicacion lo redirigira de acuerdo a su rol");
		lblInfo2.setForeground(Color.WHITE);
		lblInfo2.setFont(new Font("Roboto", Font.PLAIN, 12));
		viewLogin.add(lblInfo2, "cell 2 8 2 1,alignx right,aligny bottom");

		mainPanel.add(viewAdmin, ADMIN);
		mainPanel.add(viewUnidadPersonal, UNIDAD_PERSONAL);
		mainPanel.add(viewParquimetro, PARQUIMETROS);

		pane.add(mainPanel, BorderLayout.CENTER);
	}

	public void switchCard(String card) {
		CardLayout cl = (CardLayout)(mainPanel.getLayout());
		cl.show(mainPanel, card);
	}
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("BD - 2020");
		frame.setPreferredSize(new Dimension(500,500));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		Login log = new Login();
		log.createUIComponents(frame.getContentPane());

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private void login(){
		String srv = "localhost:3306";
		String bd = "parquimetros";
		String uname = txtUsuario.getText();
		String psswd = txtPassword.getText();
		String url = "jdbc:mysql://" + srv + "/" + bd + "?serverTimezone=America/Argentina/Buenos_Aires";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			//Class.forName("org.mariadb.jdbc.Driver");

			/* Si el usuario ingresado es Admin, cambia su correspondiente vista */
			if (uname.equals("admin")) {
				conexion = DriverManager.getConnection(url, uname, psswd);
				if(conexion != null){
					viewAdmin.setConnection(conexion);
					switchCard(ADMIN);
				}
			}else if (esNumero(uname)) {
                conexion = DriverManager.getConnection(url, "inspector", "inspector");
                if (loginUnidadPersonal(uname, conexion, psswd)){
                    viewUnidadPersonal.setConnection(conexion);
                    viewUnidadPersonal.setLegajo(uname);
                    switchCard(UNIDAD_PERSONAL);
                }
                else
                    throw new SQLException();
			}else if (uname.equals("parquimetro")) {
                conexion = DriverManager.getConnection(url, uname, psswd);
                if (conexion != null) {
                	viewParquimetro.setConnection(conexion);
                	switchCard(PARQUIMETROS);	
                }
            }else {
                JOptionPane.showMessageDialog(new JFrame("Error"), "No se encontraron usuarios o inspectores");
                resetFields();
            }

		}catch (SQLException e){
			JOptionPane.showMessageDialog(new JFrame("Error"), "Password incorrecto de " + uname, "BD-2020", JOptionPane.ERROR_MESSAGE);
			resetFields();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private boolean loginUnidadPersonal(String uname, Connection conexion, String password) {
		try {
			Statement s = conexion.createStatement();
			String sql = "SELECT legajo, password FROM Inspectores WHERE legajo=" + uname;
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

	private void resetFields() {
		txtUsuario.setText("");
		txtPassword.setText("");
	}
	

	public static void main(String[] args) {
		/* Use an appropriate Look and Feel */
		try {
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
