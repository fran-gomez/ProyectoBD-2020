package Views;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import java.util.Arrays;
import net.miginfocom.swing.MigLayout;

public class ParquimetroView extends JPanel {

    /* Database Connection */
    protected Connection conexion;

    /* UI Components */
    protected JList<String> ubicaciones, parquimetros, tarjetas;
    protected JButton conectarTarjeta;
    private JLabel lblUbicaciones;
    private JLabel lblParquimetros;
    private JLabel lblTarjetas;
    private JLabel lblParquimetro;
    private JLabel lblInfo;

    public ParquimetroView() {
    	setBackground(Color.DARK_GRAY);
    	setLayout(new MigLayout("", "[][grow][grow][grow]", "[47.00][35.00][26.00][140.00][grow]"));
    	
    	lblParquimetro = new JLabel("Parquimetro");
    	lblParquimetro.setForeground(Color.WHITE);
    	lblParquimetro.setFont(new Font("Source Sans Pro", Font.BOLD, 22));
    	add(lblParquimetro, "cell 0 0 3 1");
    	
    	lblInfo = new JLabel("Seleccione una ubicacion, un parquimetro, y la tarjeta que desee conectar");
    	lblInfo.setForeground(Color.WHITE);
    	lblInfo.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
    	add(lblInfo, "flowx,cell 1 1 3 1");
    	
    	lblUbicaciones = new JLabel("Ubicaciones");
    	lblUbicaciones.setForeground(Color.WHITE);
    	lblUbicaciones.setFont(new Font("Source Sans Pro", Font.BOLD, 14));
    	add(lblUbicaciones, "cell 1 2,alignx center,aligny bottom");
    	
    	lblParquimetros = new JLabel("Parquimetros");
    	lblParquimetros.setForeground(Color.WHITE);
    	lblParquimetros.setFont(new Font("Source Sans Pro", Font.BOLD, 14));
    	add(lblParquimetros, "cell 2 2,alignx center,aligny bottom");
    	
    	lblTarjetas = new JLabel("Tarjetas");
    	lblTarjetas.setForeground(Color.WHITE);
    	lblTarjetas.setFont(new Font("Source Sans Pro", Font.BOLD, 14));
    	add(lblTarjetas, "flowy,cell 3 2,alignx center,aligny bottom");
    	
    }
    
    private void startUIDatabase() {
    	try{
    		ubicaciones = new JList<>(obtenerUbicaciones());
    		ubicaciones.addListSelectionListener(new SeleccionListener());
    		add(ubicaciones, "cell 1 3,grow");
        	
        	parquimetros = new JList<>();
        	parquimetros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        	
        	add(parquimetros, "cell 2 3,grow");
        	
        	tarjetas = new JList<>(obtenerTarjetas());
        	
        	add(tarjetas, "cell 3 3,grow");
        
        	conectarTarjeta = new JButton("Conectar tarjeta");
        	conectarTarjeta.setFont(new Font("Source Sans Pro", Font.PLAIN, 12));
        	conectarTarjeta.addMouseListener(new ClickListener());
        	add(conectarTarjeta, "cell 3 4,growx");
        	
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    public void setConnection(Connection conexion) {
    	this.conexion = conexion;
    	startUIDatabase();
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

    private class SeleccionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            try {
                String[] parq = new String[64];
                String ubicacion = ubicaciones.getSelectedValue();
                if (ubicacion == null)
                    JOptionPane.showMessageDialog(new JFrame("Error"), "Por favor, seleccione una ubicacion valida", "BD-2020", JOptionPane.ERROR_MESSAGE);
                else {
                    Statement s = conexion.createStatement();
                    String sql = "SELECT id_parq FROM Parquimetros WHERE calle='" + ubicacion.split(",")[0].trim() +
                            "' AND altura=" + ubicacion.split(",")[1].trim();
                    ResultSet rs = s.executeQuery(sql);

                    int i = 0;
                    while (rs.next())
                        parq[i++] = rs.getString("id_parq");

                    parquimetros.removeSelectionInterval(0, i - 1);
                    parquimetros.setListData(Arrays.copyOf(parq, i));

                    s.close();
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
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
                String tarjeta, parquimetro;
                if (tarjetas.getSelectedIndex() < 0)
                    JOptionPane.showMessageDialog(new JFrame("Error"), "Por favor, seleccione un parquimetro valido", "BD-2020", JOptionPane.ERROR_MESSAGE);
                else {
                    tarjeta = tarjetas.getSelectedValue();
                    if (parquimetros.getSelectedIndex() < 0)
                        JOptionPane.showMessageDialog(new JFrame("Error"), "Por favor, seleccione una tarjeta valida", "BD-2020", JOptionPane.ERROR_MESSAGE);
                    else {
                        parquimetro = parquimetros.getSelectedValue();
                        String sql = "CALL conectar(" + tarjeta + "," + parquimetro + ")";
                        CallableStatement s = conexion.prepareCall(sql);
                        ResultSet rs = s.executeQuery(sql);

                        s.close();
                        rs.close();

                        System.out.println(sql);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
