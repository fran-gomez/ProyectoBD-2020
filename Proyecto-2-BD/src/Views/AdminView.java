package Views;
import quick.dbtable.DBTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;

public class AdminView extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected Connection conexion;
    protected JFrame ventana;

    protected DBTable modelo;
    protected JTextField sentencias;
    protected JList<String> tablas, campos;

    public AdminView(JFrame ventana, Connection conexion) {
        try {
            this.conexion = conexion;
            this.ventana = ventana;

            sentencias = new JTextField();

            tablas = new JList<>(obtenerTabla());
            campos = new JList<>();

            modelo = new DBTable();
            modelo.setConnection(conexion);
            modelo.setEditable(false);

            sentencias.addKeyListener(new EnterListener());
            tablas.addListSelectionListener(new SeleccionListener(this));

            this.setLayout(new BorderLayout());
            this.add(sentencias, BorderLayout.NORTH);
            this.add(modelo, BorderLayout.CENTER);
            this.add(new JScrollPane(tablas), BorderLayout.WEST);
            this.add(new JScrollPane(campos), BorderLayout.SOUTH);

            ventana.getContentPane().add(this);

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private String[] obtenerTabla() throws SQLException{
        String tablas[] = new String[15];
        DatabaseMetaData db = conexion.getMetaData();
        ResultSet rs;

        rs = db.getTables(null, null, "%", null);
        int k = 0;
        while (rs.next()) {
            tablas[k++] = rs.getString(3);
        }
        rs.close();

        return tablas;
    }


    private void ejecutarSentencia(String sent) {
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
            if (keyEvent.getKeyChar() == '\n')
                ejecutarSentencia(sentencias.getText());
        }

        public void keyReleased(KeyEvent keyEvent) {}
    }

    private class SeleccionListener implements ListSelectionListener {
        protected JPanel panel;

        public SeleccionListener(AdminView adminView) {
            panel = adminView;
        }

        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            try {
                int i = 1;
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
                campos.setListData(camposTabla);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}