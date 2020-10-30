import javax.swing.*;

public class main {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame ventana = new JFrame("BD-2020");
                LoginScreen ls = new LoginScreen(ventana);

                ventana.getContentPane().add(ls);
                ventana.getContentPane().setLayout(new java.awt.FlowLayout());
                ventana.setBounds(0, 0, 400, 400);

                ventana.setMaximumSize(new java.awt.Dimension(400, 400));
                ventana.setMinimumSize(new java.awt.Dimension(400, 400));
                ventana.setPreferredSize(new java.awt.Dimension(400, 400));
                ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                ventana.setVisible(true);
                ventana.setResizable(false);
            }
        });

    }
}
