package Views;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import java.util.Arrays;

public class ParquimetroView extends JPanel {

    protected JFrame ventana;
    protected Connection conexion;

    protected JList<String> ubicaciones, parquimetros, tarjetas;
    protected JButton conectarTarjeta;

    public ParquimetroView(JFrame ventana, Connection conexion) {
        try {
            this.conexion = conexion;
            this.ventana = ventana;

            this.ubicaciones = new JList<>(obtenerUbicaciones());
            this.ubicaciones.addListSelectionListener(new SeleccionListener());

            this.parquimetros = new JList<>();

            this.tarjetas = new JList<>(obtenerTarjetas());

            this.conectarTarjeta = new JButton("Conectar tarjeta");
            this.conectarTarjeta.addMouseListener(new ClickListener());

            this.setLayout(new BorderLayout());
            this.add(new JScrollPane(ubicaciones), BorderLayout.WEST);
            this.add(new JScrollPane(parquimetros), BorderLayout.CENTER);
            this.add(new JScrollPane(tarjetas), BorderLayout.EAST);
            this.add(conectarTarjeta, BorderLayout.SOUTH);
            ventana.getContentPane().add(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String[] obtenerTarjetas() throws SQLException {

        int i = 0;
        String[] ubicaciones;
        Statement s = conexion.createStatement();
        String sql = "SELECT id_tarjeta FROM tarjetas";
        ResultSet rs = s.executeQuery(sql);

        ubicaciones = new String[20];
        while (rs.next())
            ubicaciones[i++] = rs.getString("id_tarjeta");

        s.close();
        rs.close();

        return Arrays.copyOf(ubicaciones, i);
    }

    private String[] obtenerUbicaciones() throws SQLException {

        int i = 0;
        String[] ubicaciones;
        Statement s = conexion.createStatement();
        String sql = "SELECT calle, altura FROM Ubicaciones";
        ResultSet rs = s.executeQuery(sql);

        ubicaciones = new String[64];
        while (rs.next())
            ubicaciones[i++] = (rs.getString("calle") + ", " + rs.getString("altura"));

        s.close();
        rs.close();

        return Arrays.copyOf(ubicaciones, i);
    }

    private class SeleccionListener implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            try {
                int i = 0;
                String[] parq = new String[64];
                String ubicacion = ubicaciones.getSelectedValue();
                Statement s = conexion.createStatement();
                String sql = "SELECT id_parq FROM Parquimetros WHERE calle='" + ubicacion.split(",")[0].trim() +
                                "' AND altura=" + ubicacion.split(",")[1].trim();
                ResultSet rs = s.executeQuery(sql);

                while (rs.next())
                    parq[i++] = rs.getString("id_parq");

                parquimetros.removeSelectionInterval(0, i - 1);
                parquimetros.setListData(Arrays.copyOf(parq, i));

                s.close();
                rs.close();
            } catch (SQLException e) {

            }
        }
    }

    private class ClickListener implements MouseListener {
        public void mousePressed(MouseEvent mouseEvent) {}
        public void mouseReleased(MouseEvent mouseEvent) {}
        public void mouseEntered(MouseEvent mouseEvent) {}
        public void mouseExited(MouseEvent mouseEvent) {}

        public void mouseClicked(MouseEvent mouseEvent) {
            try {
                String tarjeta = tarjetas.getSelectedValue();
                String parquimetro = parquimetros.getSelectedValue();

                if (parquimetro == null || parquimetro.equals("")) {
                    JOptionPane.showMessageDialog(new JFrame("Error"), "Por favor, seleccione un parquimetro valido", "BD-2020", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (tarjeta == null || tarjeta.equals("")) {
                    JOptionPane.showMessageDialog(new JFrame("Error"), "Por favor, seleccione una tarjeta valida", "BD-2020", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "CALL conectar(" + tarjeta + "," + parquimetro + ")";
                CallableStatement s = conexion.prepareCall(sql);
                s.executeUpdate(sql);

                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
