package Views;

import quick.dbtable.DBTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
    protected JPanel panelOeste;
    protected JPanel panelCentral;
    protected JPanel panelEste;


    protected String legajo;
    protected JLabel tituloUnidad;

    protected JTextField patentesUbicacion;
    protected JList<String> ubicaciones, parquimetros;
    protected DBTable multasGeneradas;
    protected JButton generarMultas;

    public InspectorView(JFrame ventana, Connection conexion, String legajo) {
        try {
            this.ventana = ventana;
            this.conexion = conexion;
            this.legajo = legajo;

            this.setLayout(new BorderLayout());
            ((BorderLayout) this.getLayout()).setHgap(3);
            ((BorderLayout) this.getLayout()).setVgap(3);

            panelOeste = new JPanel();
            panelCentral = new JPanel();
            panelEste = new JPanel();

            GroupLayout oeste = new GroupLayout(panelOeste);
            panelOeste.setLayout(oeste);
            GroupLayout este = new GroupLayout(panelEste);
            panelEste.setLayout(este);

            // Norte
            tituloUnidad = new JLabel("Bienvenide, " + legajo);

            // Oeste
            panelOeste.setLayout(new BoxLayout(panelOeste, BoxLayout.Y_AXIS));
            //panelOeste.setPreferredSize(new Dimension(200,400));

            ubicaciones = new JList<>(obtenerUbicaciones(conexion));
            ubicaciones.addListSelectionListener(new SeleccionListener(this));
            ubicaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            panelOeste.add(new JScrollPane(ubicaciones));

            // Centro

            panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
            //panelCentral.setPreferredSize(new Dimension(200,400));

            parquimetros = new JList<>();
            parquimetros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            panelCentral.add(new JScrollPane(parquimetros));

            // Este
            panelEste.setLayout(new BoxLayout(panelEste, BoxLayout.Y_AXIS));
            //panelEste.setPreferredSize(new Dimension(200,400));

            patentesUbicacion = new JTextField();

            generarMultas = new JButton("Generar multas");
            generarMultas.addKeyListener(new MultasEnterListener());
            generarMultas.addMouseListener(new MultasClickListener());

            panelEste.add(patentesUbicacion);
            panelEste.add(generarMultas);

            // Sur
            multasGeneradas = new DBTable();
            multasGeneradas.setConnection(conexion);
            multasGeneradas.setEditable(false);

            this.add(tituloUnidad,BorderLayout.NORTH);
            this.add(panelOeste, BorderLayout.WEST);
            this.add(panelCentral, BorderLayout.CENTER);
            this.add(panelEste, BorderLayout.EAST);

            multasGeneradas.setPreferredSize(new Dimension(400,200));
            this.add(multasGeneradas, BorderLayout.SOUTH);

            ventana.getContentPane().add(this);

            JOptionPane.showMessageDialog(new JFrame("Informacion"), "Por favor, ingrese las patentes de los autos " +
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

    private void verificarPatentes() throws SQLException {

        if (patentesUbicacion.getText().equals("")) {
            JOptionPane.showMessageDialog(new JFrame("Error"), "Por favor, ingrese las patentes de los autos " +
                    "estacionados en su ubicacion", "BD-2020", JOptionPane.ERROR_MESSAGE);
        } else {
            Set<String> patentesValidas = new HashSet<>();
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

            // Se crea un conjunto de patentes validas (Patentes cuyos automoviles estan en la bd)
            // Luego, se crea el conjunto de patentes que el inspector ingreso a mano, y se eliminan
            // todas las que tengan una tarjeta asociada al parquimetro.
            // Finalmente, se intersectan los conjuntos de patentes validas con las patentes que no
            // pagaron en el parquimetro, para evitar el error de las patentes inexistentes en la bd
            patentesValidas.addAll(obtenerPatentesValidas());
            while (rs.next())
                patentesParquimetro.add(rs.getString("patente"));

            patentesInspector.addAll(Arrays.asList(patentesUbicacion.getText().split(",")));
            patentesInspector.removeAll(patentesParquimetro);

            patentesInspector.retainAll(patentesValidas);

            String calle = ubicaciones.getSelectedValue().split(",")[0];
            String altura = ubicaciones.getSelectedValue().split(",")[1];
            id_asociado_con = obtenerIdAsocidadoCon(legajo, calle, altura);

            if (id_asociado_con.equals(""))
                JOptionPane.showMessageDialog(new JFrame("Error"), "Error. El inspector " + legajo +
                            " no esta autorizado a labrar multas en " + ubicaciones.getSelectedValue(), "BD-2020", JOptionPane.ERROR_MESSAGE);
            else {
                // Para cada patente en el conjunto diferencia, se labra una multa
                for (String patente : patentesInspector)
                    labrarMulta(patente, id_asociado_con);

                actualizarDTable(multasGeneradas);
            }
            s.close();
            rs.close();
        }
        // SELECT patente FROM (automoviles NATURAL JOIN tarjetas NATURAL JOIN estacionamientos NATURAL JOIN parquimetros) WHERE id_parq= AND id_tarjeta=estacionamientos.id_tarjeta
    }

    private Set<String> obtenerPatentesValidas() throws SQLException {

        Set<String> patentes;
        Statement s = conexion.createStatement();
        String sql = "SELECT patente FROM automoviles";
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
                        "(multa NATURAL JOIN asociado_con) WHERE fecha >= CURDATE() AND hora <= CURTIME() AND legajo=" +
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

        s.close();
        rs.close();

        return id_asociado_con;
    }

    private void registrarAcceso() {
        try {
            Statement s = conexion.createStatement();
            String id_parq = parquimetros.getSelectedValue();
            String sql = "INSERT INTO accede(legajo,id_parq,fecha,hora) " +
                    "VALUES (" + legajo + "," + id_parq + ",CURDATE(),CURTIME())";

            System.out.println(sql);
            s.executeUpdate(sql);
            s.close();

            generarMultas.setEnabled(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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

}
