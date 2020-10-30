package Views;

import quick.dbtable.DBTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.nimbus.State;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InspectorView extends JPanel {

    protected Connection conexion;
    protected JFrame ventana;
    protected String legajo;

    protected JTextField patentesUbicacion;
    protected JList<String> ubicaciones, parquimetros;
    protected DBTable multasGeneradas;
    protected JButton generarMultas;

    public InspectorView(JFrame ventana, Connection conexion, String legajo) {
        try {
            this.ventana = ventana;
            this.conexion = conexion;
            this.legajo = legajo;

            patentesUbicacion = new JTextField();

            ubicaciones = new JList<>(obtenerUbicaciones(conexion));
            ubicaciones.addListSelectionListener(new SeleccionListener(this));
            ubicaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            parquimetros = new JList<>();
            parquimetros.addListSelectionListener(new SeleccionParquimetroListener());
            parquimetros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            multasGeneradas = new DBTable();
            multasGeneradas.setConnection(conexion);
            multasGeneradas.setEditable(false);

            generarMultas = new JButton("Generar multas");
            generarMultas.addKeyListener(new MultasEnterListener());
            generarMultas.addMouseListener(new MultasClickListener());

            this.setLayout(new GridBagLayout());

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;

            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 4;
            c.gridheight = 2;
            this.add(patentesUbicacion, c);

            c.gridx = 0;
            c.gridy = 2;
            c.gridheight = 2;
            c.gridwidth = 1;
            this.add(ubicaciones, c);

            c.gridx = 1;
            c.gridy = 2;
            c.gridheight = 2;
            c.gridheight = 1;
            this.add(parquimetros, c);

            c.gridx = 2;
            c.gridy = 2;
            c.gridheight = 1;
            c.gridwidth = 2;
            c.weighty = 1.0;
            this.add(multasGeneradas, c);

            c.gridx = 3;
            c.gridy = 3;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.weightx = 0;
            c.weighty = 0;
            this.add(generarMultas, c);

            ventana.getContentPane().add(this);

            JOptionPane.showMessageDialog(new JFrame("Error"), "Por favor, ingrese las patentes de los autos " +
                    "estacionados en su ubicacion separadas por una ,", "BD-2020", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private String[] obtenerUbicaciones(Connection conexion) throws SQLException {

        int i = 0;
        String[] ubicaciones;
        Statement s = conexion.createStatement();
        String sql = "SELECT calle, altura FROM ubicaciones";
        ResultSet rs = s.executeQuery(sql);

        ubicaciones = new String[20];
        while (rs.next())
            ubicaciones[i++] = (rs.getString("calle") + ", " + rs.getString("altura"));

        s.close();
        rs.close();

        return Arrays.copyOf(ubicaciones, i);
    }

    private void verificarMultas() throws SQLException {

        if (patentesUbicacion.getText().equals("")) {
            JOptionPane.showMessageDialog(new JFrame("Error"), "Por favor, ingrese las patentes de los autos " +
                    "estacionados en su ubicacion", "BD-2020", JOptionPane.ERROR_MESSAGE);
        } else {
            Set<String> patentesParquimetro = new HashSet<>();
            Set<String> patentesInspector = new HashSet<>();

            String id_asociado_con;

            Statement s = conexion.createStatement();
            String id_parq = parquimetros.getSelectedValue();
            String sql = "SELECT patente FROM " +
                    "(automoviles NATURAL JOIN tarjetas NATURAL JOIN estacionamientos NATURAL JOIN parquimetros) " +
                    "WHERE id_parq=" + id_parq + " AND id_tarjeta=estacionamientos.id_tarjeta";
            ResultSet rs = s.executeQuery(sql);

            // Si consideramos a las patentes cuya tarjeta esta asociada al parquimetro como un conjunto
            // y a las patentes que el inspector ingresa a mano, para saber que patentes no estan relacionadas
            // con un parquimetr basta con calcular la diferencia entre ambos conjuntos
            while (rs.next())
                patentesParquimetro.add(rs.getString("patente"));
            patentesInspector.addAll(Arrays.asList(patentesUbicacion.getText().split(",")));
            patentesInspector.removeAll(patentesParquimetro);

            String calle = ubicaciones.getSelectedValue().split(",")[0];
            String altura = ubicaciones.getSelectedValue().split(",")[1];
            id_asociado_con = obtenerIdAsocidadoCon(legajo, calle, altura);
            // Para cada patente en el conjunto diferencia, se labra una multa
            for (String patente: patentesInspector)
                labrarMulta(patente, id_asociado_con);

            actualizarDTable(multasGeneradas);

            s.close();
            rs.close();
        }
        // SELECT patente FROM (automoviles NATURAL JOIN tarjetas NATURAL JOIN estacionamientos NATURAL JOIN parquimetros) WHERE id_parq= AND id_tarjeta=estacionamientos.id_tarjeta
    }

    private void actualizarDTable(DBTable multasGeneradas) throws SQLException {
        Statement s = conexion.createStatement();
        String sql = "SELECT * FROM multa WHERE fecha >= CURDATE() AND hora <= CURTIME()";
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

    // TODO: Si la patente no existe en la BD, se rompe todo. ¿Deberia pasar eso? Yo creo que no...
    private void labrarMulta(String patente, String id_asociado_con) throws SQLException {
        Statement s = conexion.createStatement();
        String sql = "INSERT INTO multa(fecha,hora,patente,id_asociado_con) " +
                        "VALUES (CURDATE(),CURTIME(),'" + patente + "'," +
                            id_asociado_con + ")";
        s.executeUpdate(sql);

        s.close();
    }

    private String obtenerIdAsocidadoCon(String legajo, String calle, String altura) throws SQLException {
        Statement s = conexion.createStatement();
        String sql = "SELECT id_asociado_con FROM " +
                        "(asociado_con NATURAL JOIN inspectores NATURAL JOIN parquimetros) " +
                        "WHERE legajo=" + legajo + " AND calle='" + calle + "' AND altura=" + altura;
        ResultSet rs = s.executeQuery(sql);
        String id_asociado_con = "";

        while (rs.next())
            id_asociado_con = rs.getString("id_asociado_con");

        if (id_asociado_con.equals(""))
            System.exit(32);
        s.close();
        rs.close();

        return id_asociado_con;
    }

    private class MultasClickListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            try {
                verificarMultas();
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
                    verificarMultas();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    private class SeleccionListener implements ListSelectionListener {
        protected JPanel panel;

        public SeleccionListener(InspectorView inspectorView) {
            panel = inspectorView;
        }

        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            try {
                int i = 0;
                String tabla = ubicaciones.getSelectedValue();
                String[] camposTabla;
                Statement s = conexion.createStatement();
                ResultSet rs = s.executeQuery("SELECT id_parq FROM (ubicaciones NATURAL JOIN parquimetros) " +
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

    private class SeleccionParquimetroListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            try {
                Statement s = conexion.createStatement();
                String id_parq = parquimetros.getSelectedValue();
                String sql = "INSERT INTO accede(legajo,id_parq,fecha,hora) " +
                                "VALUES (" + legajo + "," + id_parq + "CURDATE(),CURTIME())";
                s.executeUpdate(sql);
                s.close();

                generarMultas.setEnabled(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
