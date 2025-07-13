package galaga;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PantallaInicio extends JFrame {

    public PantallaInicio() {
        setTitle("Galaga - Menú de Inicio");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("GALAGA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 60));
        titulo.setForeground(Color.CYAN);

        JButton botonInicio = new JButton("Iniciar Juego");
        botonInicio.setFont(new Font("Arial", Font.BOLD, 30));
        botonInicio.setFocusPainted(false);

        botonInicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cierra la ventana del menú
                SwingUtilities.invokeLater(() -> {
                    GalagaGame juego = new GalagaGame();
                    juego.setVisible(true);
                });
            }
        });

        panel.add(titulo, BorderLayout.CENTER);
        panel.add(botonInicio, BorderLayout.SOUTH);
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PantallaInicio menu = new PantallaInicio();
            menu.setVisible(true);
        });
    }
}