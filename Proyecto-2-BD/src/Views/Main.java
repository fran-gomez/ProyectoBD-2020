package Views;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame ventana = new JFrame("BD-2020");
        LoginView ls = new LoginView(ventana);

        ventana.setBounds(0, 0, 800, 600);
        ventana.getContentPane().add(ls);
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ventana.setResizable(false);
        ventana.setVisible(true);
    }

}