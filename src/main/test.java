package main;

import java.awt.*;
import javax.swing.*;

public class test extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Obtén el objeto Graphics2D
        Graphics2D g2d = (Graphics2D) g;

        // Definir la fuente para el texto
        Font font = new Font("Arial", Font.PLAIN, 24);
        g2d.setFont(font);

        // Obtener la métrica de la fuente para calcular la altura del texto
        FontMetrics metrics = g2d.getFontMetrics(font);
        String texto = "Texto centrado verticalmente";
        int textoAncho = metrics.stringWidth(texto);
        int textoAltura = metrics.getHeight();

        // Obtén la altura del área de dibujo
        int areaDibujoAltura = getHeight();

        // Calcula la posición Y para centrar el texto verticalmente
        int posY = (areaDibujoAltura + textoAltura) / 2;

        // Dibuja el texto centrado verticalmente
        g2d.drawString(texto, 0, posY);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Centrar Texto Verticalmente");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new test());
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}