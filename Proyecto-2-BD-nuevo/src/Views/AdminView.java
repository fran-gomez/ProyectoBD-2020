package Views;
import quick.dbtable.DBTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;
import java.util.Arrays;
import net.miginfocom.swing.MigLayout;

public class AdminView extends JPanel {
	private static final long serialVersionUID = 1L;

	protected Connection conexion;
	protected JFrame ventana;

	protected DBTable modelo;
	protected JList<String> tablas, campos;
	private JTextArea sentencia;

	public AdminView() {
		createUIComponents();
	}

	private void createUIComponents() {
		setBackground(Color.DARK_GRAY);
		setLayout(new MigLayout("", "[142.00,grow][225.00,grow]", "[56.00][56.00][104.00,grow][50.00,grow]"));

		JLabel lblInfo = new JLabel("Administrador");
		lblInfo.setForeground(Color.WHITE);
		lblInfo.setFont(new Font("Source Sans Pro Black", Font.PLAIN, 18));
		add(lblInfo, "cell 0 0,alignx left");

		JLabel lblInfo2 = new JLabel("Ingrese su consulta SQL y su consulta se mostrara debajo. ");
		lblInfo2.setForeground(Color.WHITE);
		lblInfo2.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
		add(lblInfo2, "cell 1 0,aligny bottom");

		JLabel lblSentencia = new JLabel("Sentencia SQL");
		lblSentencia.setForeground(Color.WHITE);
		lblSentencia.setFont(new Font("Source Sans Pro", Font.PLAIN, 14));
		add(lblSentencia, "cell 0 1,alignx left");

		JLabel lblResultado = new JLabel("Resultado");
		lblResultado.setForeground(Color.WHITE);
		lblResultado.setFont(new Font("Source Sans Pro", Font.PLAIN, 14));
		add(lblResultado, "cell 0 2,alignx left");

	}

	private String[] getTable() throws SQLException{
		String tablas[] = new String[12];
		DatabaseMetaData db = conexion.getMetaData();
		ResultSet rs;

		rs = db.getTables(null, null, null, new String[] {"TABLE"});
		int k = 0;
		while (rs.next() && k < 12) {
			tablas[k++] = rs.getString("TABLE_NAME");
		}
		rs.close();

		return Arrays.copyOf(tablas, k);
	}

	public void setConnection(Connection conexion) {
		this.conexion = conexion;
		setUIDatabase();
	}

	private void setUIDatabase() {
		try {
			if(conexion != null) {
				sentencia = new JTextArea();
				add(sentencia, "cell 1 1,grow");
				sentencia.addKeyListener(new EnterListener());

				tablas = new JList<>(getTable());
				tablas.addListSelectionListener(new SeleccionListener());
				add(tablas, "cell 0 3,grow");

				campos = new JList<>();
				add(campos, "cell 1 3,grow");

				modelo = new DBTable();
				modelo.setConnection(conexion);
				modelo.setEditable(false);
				add(modelo, "cell 1 2,alignx center");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	private void executeSentence(String sent) {
		try {

			modelo.setSelectSql(sent.trim());
			modelo.createColumnModelFromQuery();
			// para que muestre correctamente los valores de tipo TIME (hora)
			for (int i = 0; i < modelo.getColumnCount(); i++) {
				if	 (modelo.getColumn(i).getType()==Types.TIME) {
					modelo.getColumn(i).setType(Types.CHAR);
				}
				// cambiar el formato en que se muestran los valores de tipo DATE
				if	 (modelo.getColumn(i).getType()==Types.DATE) {
					modelo.getColumn(i).setDateFormat("dd/MM/YYYY");
				}
			}

			modelo.refresh();
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
					ex.getMessage() + "\n",
					"Error al ejecutar la consulta.",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private class EnterListener implements KeyListener {

		public void keyTyped(KeyEvent keyEvent) {}

		public void keyPressed(KeyEvent keyEvent) {
			try {
				String consulta;
				Statement s;

				if (keyEvent.getKeyChar() == '\n') {
					consulta = sentencia.getText().split(" ")[0];
					if (consulta.equals("SELECT") || consulta.equals("select"))
						executeSentence(sentencia.getText());
					else {
						s = conexion.createStatement();
						s.execute(sentencia.getText().trim());

						s.close();
					}
				}
			} catch(SQLException throwables){
				JOptionPane.showMessageDialog(new JFrame("Informacion"), "Fallo al ejecutar la consulta",
						"BD-2020", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		public void keyReleased(KeyEvent keyEvent) {}
	}

	private class SeleccionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent listSelectionEvent) {
			try {
				int i = 0;
				String tabla = tablas.getSelectedValue();
				String[] camposTabla;
				Statement sentencia = conexion.createStatement();
				ResultSet rs = sentencia.executeQuery("describe " + tabla);

				camposTabla = new String[21];
				while (rs.next()) {
					camposTabla[i] = rs.getString("Field");
					i++;
				}
				campos.removeSelectionInterval(0, i - 1);
				campos.setListData(Arrays.copyOf(camposTabla, i));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
