package Views;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import quick.dbtable.DBTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.miginfocom.swing.MigLayout;
import java.awt.Color;
import java.awt.Font;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class UnidadPersonalView extends JPanel {
	private static final long serialVersionUID = 1L;

	/* Database Connection */ 
	private Connection conexion;

	private String legajo;

	/* UI Components */
	protected JTextField patentesUbicacion;
	protected JList<String> ubicaciones, parquimetros;
	protected DBTable multasGeneradas;
	protected JButton generarMultas;
	private JLabel lblParquimetros;
	private JLabel lblUbicaciones;
	private JLabel lblInfo;
	private JLabel lblMultasGeneradas;
	private JLabel lblUnidadPersonal;
	private JLabel lblLegajo;
	private JLabel lblPrimeroSeleccioneUna;

	public UnidadPersonalView(){
		setBackground(Color.DARK_GRAY);
		setLayout(new MigLayout("", "[84.00,grow][111.00,grow][168.00][138.00,grow][]", "[-4.00][60.00][][37.00][29.00][27.00][72.00,grow]"));

		lblLegajo = new JLabel(legajo);
		lblLegajo.setForeground(Color.WHITE);
		lblLegajo.setFont(new Font("Source Sans Pro", Font.BOLD, 14));
		add(lblLegajo, "cell 1 0");
		
				lblUnidadPersonal = new JLabel("Unidad Personal");
				lblUnidadPersonal.setForeground(Color.WHITE);
				lblUnidadPersonal.setFont(new Font("Source Sans Pro", Font.BOLD, 22));
				add(lblUnidadPersonal, "cell 0 1 2 1");

		lblInfo = new JLabel("Ingreso de patenes");
		lblInfo.setForeground(new Color(255, 255, 255));
		lblInfo.setFont(new Font("Source Sans Pro", Font.PLAIN, 14));
		add(lblInfo, "cell 0 2");

		patentesUbicacion = new JTextField();
		patentesUbicacion.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		add(patentesUbicacion, "cell 0 3 3 1,growx");
		patentesUbicacion.setColumns(10);

		generarMultas = new JButton("Generar multas");
		generarMultas.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		generarMultas.addKeyListener(new MultasEnterListener());
		generarMultas.addMouseListener(new MultasClickListener());

		add(generarMultas, "cell 3 3,alignx center");
		
		lblPrimeroSeleccioneUna = new JLabel("Primero, seleccione una ubicacion y un parquimetro");
		lblPrimeroSeleccioneUna.setForeground(Color.WHITE);
		lblPrimeroSeleccioneUna.setFont(new Font("Source Sans Pro", Font.PLAIN, 14));
		add(lblPrimeroSeleccioneUna, "cell 0 4 3 1");

		lblUbicaciones = new JLabel("Ubicaciones");
		lblUbicaciones.setForeground(Color.WHITE);
		lblUbicaciones.setFont(new Font("Source Sans Pro", Font.PLAIN, 14));
		add(lblUbicaciones, "cell 0 5,alignx center");

		lblParquimetros = new JLabel("Parquimetros");
		lblParquimetros.setForeground(new Color(255, 255, 255));
		lblParquimetros.setFont(new Font("Source Sans Pro", Font.PLAIN, 14));
		add(lblParquimetros, "cell 1 5,alignx center");

		lblMultasGeneradas = new JLabel("Multas");
		lblMultasGeneradas.setForeground(Color.WHITE);
		lblMultasGeneradas.setFont(new Font("Source Sans Pro", Font.PLAIN, 14));
		add(lblMultasGeneradas, "cell 2 5 2 1,alignx center");

	}

	public void setConnection(Connection conexion) {
		this.conexion = conexion;
		startUIDatabase();
	}

	private void startUIDatabase() {
		try {

			ubicaciones = new JList<String>(obtenerUbicaciones(conexion));
			ubicaciones.addListSelectionListener(new SeleccionListener(this));
			ubicaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			add(ubicaciones, "cell 0 6,grow");

			parquimetros = new JList<String>();
			parquimetros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			add(parquimetros, "cell 1 6,grow");

			multasGeneradas = new DBTable();
			multasGeneradas.setConnection(conexion);
			multasGeneradas.setEditable(false);
			add(multasGeneradas, "cell 2 6 2 1");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setLegajo(String legajo) {
		this.legajo = legajo;
	}

	private String[] obtenerUbicaciones(Connection conexion) throws SQLException {

		int i = 0;
		String[] ubicaciones;
		Statement s = conexion.createStatement();
		String sql = "SELECT calle, altura FROM Ubicaciones";
		ResultSet rs = s.executeQuery(sql);

		ubicaciones = new String[20];
		while (rs.next())
			ubicaciones[i++] = (rs.getString("calle") + ", " + rs.getString("altura"));

		s.close();
		rs.close();

		return Arrays.copyOf(ubicaciones, i);
	}


	private class MultasClickListener implements MouseListener {

		public void mouseClicked(MouseEvent mouseEvent) {
			try {
				if (parquimetros.getSelectedIndex() >= 0) {
					registrarAcceso();
					verificarPatentes();
				} else
					JOptionPane.showMessageDialog(new JFrame("Error"), "Primero debe seleccionar un parquimetro",
							"BD-2020", JOptionPane.ERROR_MESSAGE);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		}

		public void mousePressed(MouseEvent mouseEvent) {}
		public void mouseReleased(MouseEvent mouseEvent) {}
		public void mouseEntered(MouseEvent mouseEvent) {}
		public void mouseExited(MouseEvent mouseEvent) {}
	}

	private class MultasEnterListener implements KeyListener {

		public void keyTyped(KeyEvent keyEvent) {}
		public void keyReleased(KeyEvent keyEvent) {}

		public void keyPressed(KeyEvent keyEvent) {
			if (keyEvent.getKeyChar() == '\n') {
				try {
					if (parquimetros.getSelectedIndex() >= 0) {
						registrarAcceso();
						verificarPatentes();
					} else
						JOptionPane.showMessageDialog(new JFrame("Error"), "Primero debe seleccionar un parquimetro",
								"BD-2020", JOptionPane.ERROR_MESSAGE);
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}
			}
		}
	}

	private class SeleccionListener implements ListSelectionListener {
		protected JPanel panel;

		public SeleccionListener(UnidadPersonalView unidadPersonalView) {
			panel = unidadPersonalView;
		}

		public void valueChanged(ListSelectionEvent listSelectionEvent) {
			try {
				int i = 0;
				String tabla = ubicaciones.getSelectedValue();
				String[] camposTabla;
				Statement s = conexion.createStatement();
				ResultSet rs = s.executeQuery("SELECT id_parq FROM (Ubicaciones NATURAL JOIN Parquimetros) " +
						"WHERE calle='" + tabla.split(",")[0] + "' and altura='" + tabla.split(",")[1] + "'");

				camposTabla = new String[21];
				while (rs.next()) {
					camposTabla[i] = rs.getString("id_parq");
					i++;
				}

				parquimetros.removeSelectionInterval(0, i - 1);
				parquimetros.setListData(Arrays.copyOf(camposTabla, i));

				s.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void registrarAcceso() {
		try {
			Statement s = conexion.createStatement();
			String id_parq = parquimetros.getSelectedValue();
			String sql = "INSERT INTO Accede(legajo,id_parq,fecha,hora) " +
					"VALUES (" + legajo + "," + id_parq + ",CURDATE(),CURTIME())";

			s.executeUpdate(sql);
			s.close();

			generarMultas.setEnabled(true);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	private void verificarPatentes() throws SQLException {

		if (patentesUbicacion.getText().equals("")) {
			JOptionPane.showMessageDialog(new JFrame("Error"), "Por favor, ingrese las patentes de los autos " +
					"estacionados en su ubicacion", "BD-2020", JOptionPane.ERROR_MESSAGE);
		} else {
			Set<String> estacionamientosAbiertos = new HashSet<>();
			Set<String> patentesIngresadas = new HashSet<>();

			String id_asociado_con;

			Statement s = conexion.createStatement();
			String calle = ubicaciones.getSelectedValue().split(",")[0].trim();
			String altura = ubicaciones.getSelectedValue().split(",")[1].trim();
			String sql = "SELECT patente FROM " +
					"(tarjetas NATURAL JOIN Estacionamientos NATURAL JOIN Parquimetros) " +
					"WHERE calle='" + calle + "' AND altura=" + altura +
					" AND fecha_sal IS NULL AND hora_sal IS NULL";
			ResultSet rs = s.executeQuery(sql);

			// Si consideramos a las patentes cuya tarjeta esta asociada al parquimetro como un conjunto
			// y a las patentes que el inspector ingresa a mano, para saber que patentes no estan relacionadas
			// con un parquimetr basta con calcular la diferencia entre ambos conjuntos

			// Se crea un conjunto de patentes validas (Patentes cuyos automoviles estan en la bd)
			// Luego, se crea el conjunto de patentes que el inspector ingreso a mano, y se eliminan
			// todas las que tengan una tarjeta asociada al parquimetro.
			// Finalmente, se intersectan los conjuntos de patentes validas con las patentes que no
			// pagaron en el parquimetro, para evitar el error de las patentes inexistentes en la bd
			while (rs.next())
				estacionamientosAbiertos.add(rs.getString("patente"));

			for (String patente: patentesUbicacion.getText().split(","))
				patentesIngresadas.add(patente.trim());
			patentesIngresadas.removeAll(estacionamientosAbiertos);

			patentesIngresadas.retainAll(obtenerPatentesValidas());
			// Se verifica que el inspector este asociado a la ubicacion seleccionada
			id_asociado_con = obtenerIdAsocidadoCon(legajo, calle, altura);

			if (id_asociado_con.equals(""))
				JOptionPane.showMessageDialog(new JFrame("Error"), "Error. El inspector " + legajo +
						" no esta autorizado a labrar multas en " + ubicaciones.getSelectedValue(), "BD-2020", JOptionPane.ERROR_MESSAGE);
			else {
				// Para cada patente en el conjunto diferencia, se labra una multa
				for (String patente : patentesIngresadas)
					labrarMulta(patente, id_asociado_con);

				actualizarDTable(multasGeneradas);
			}
			s.close();
			rs.close();
		}
	}

	private Set<String> obtenerPatentesValidas() throws SQLException {
		Set<String> patentes;
		Statement s = conexion.createStatement();
		String sql = "SELECT patente FROM Automoviles";
		ResultSet rs = s.executeQuery(sql);

		patentes = new HashSet<>();
		while (rs.next())
			patentes.add(rs.getString("patente"));

		s.close();
		rs.close();

		return patentes;
	}

	private void actualizarDTable(DBTable multasGeneradas) throws SQLException {
		Statement s = conexion.createStatement();
		String sql = "SELECT numero, fecha, hora, calle, altura, patente, legajo FROM " +
				"(Multa NATURAL JOIN Asociado_con) WHERE fecha = CURDATE() AND hora = CURTIME() AND legajo=" +
				legajo;
		ResultSet rs = s.executeQuery(sql);

		multasGeneradas.setSelectSql(sql);
		multasGeneradas.createColumnModelFromQuery();
		// para que muestre correctamente los valores de tipo TIME (hora)
		for (int i = 0; i < multasGeneradas.getColumnCount(); i++) {
			if	 (multasGeneradas.getColumn(i).getType()==Types.TIME) {
				multasGeneradas.getColumn(i).setType(Types.CHAR);
			}
			// cambiar el formato en que se muestran los valores de tipo DATE
			if	 (multasGeneradas.getColumn(i).getType()==Types.DATE) {
				multasGeneradas.getColumn(i).setDateFormat("dd/MM/YYYY");
			}
		}
		multasGeneradas.refresh();

		s.close();
		rs.close();
	}

	private void labrarMulta(String patente, String id_asociado_con) throws SQLException {
		Statement s = conexion.createStatement();
		String sql = "INSERT INTO Multa(fecha,hora,patente,id_asociado_con) " +
				"VALUES (CURDATE(),CURTIME(),'" + patente + "'," +
				id_asociado_con + ")";
		s.executeUpdate(sql);

		s.close();
	}

	private String obtenerIdAsocidadoCon(String legajo, String calle, String altura) throws SQLException {
		Statement s = conexion.createStatement();
		String sql = "SELECT id_asociado_con FROM " +
				"(Asociado_con NATURAL JOIN Inspectores NATURAL JOIN Parquimetros) " +
				"WHERE legajo=" + legajo + " AND calle='" + calle + "' AND altura=" + altura +
				" AND dia='" + obtenerDia() + "' AND turno='" + obtenerTurno() + "'";
		ResultSet rs = s.executeQuery(sql);
		String id_asociado_con = "";

		while (rs.next())
			id_asociado_con = rs.getString("id_asociado_con");

		s.close();
		rs.close();

		return id_asociado_con;
	}

	private String obtenerTurno() throws SQLException {
		String turno = "";

		Statement s = conexion.createStatement();
		String sql = "SELECT CURTIME() > \"08:00:00\" AND CURTIME() < \"13:59:59\"";
		ResultSet rs = s.executeQuery(sql);

		while (rs.next())
			if (rs.getBoolean(1))
				turno = "M";

		if (turno.equals("")) {
			rs = s.executeQuery("SELECT CURTIME() > \"14:00:00\" AND CURTIME() < \"20:00:00\"");
			while (rs.next())
				if (rs.getBoolean(1))
					turno = "T";
		}

		return turno;
	}

	private String obtenerDia() throws SQLException {

		Statement s = conexion.createStatement();
		String sql = "SELECT DAYOFWEEK(CURDATE())";
		ResultSet rs = s.executeQuery(sql);
		String[] dias = {"Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa"};
		String dia = "";

		while (rs.next())
			dia = dias[rs.getInt(1)-1];

		s.close();
		rs.close();

		return dia;
	}

}
