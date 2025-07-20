package galaga;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GalagaGame extends JFrame {

    public static final int ANCHO = 800, ALTO = 600;
    public static final int TAM_JUGADOR = 40, TAM_ENEMIGO = 40;

    private enum EstadoJuego { MENU, JUGANDO, GAME_OVER, TABLA }
    private EstadoJuego estado = EstadoJuego.MENU;

    private int opcionSeleccionada = 0;
    private final String[] opcionesMenu = {"Iniciar Juego", "Tabla de Posiciones", "Salir"};

    private Jugador jugador;
    private ArrayList<Enemigo> enemigos = new ArrayList<>();
    private boolean[] teclas = new boolean[256];
    private boolean juegoTerminado = false;

    private int tiempoEntreEnemigos = 50;
    private int contadorSpawn = 0;
    private int totalEnemigosCreados = 0;
    private int maxEnemigos = 10;
    private int velocidadEnemigo = 10;
    private int nivel = 1;

    private String nombreJugador = "Jugador";
    private ArrayList<String> tablaPuntajes = new ArrayList<>();

    public GalagaGame() {
        setTitle("Galaga Java");
        setSize(ANCHO, ALTO);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(new PanelJuego());

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                teclas[e.getKeyCode()] = true;
                
                if (estado == EstadoJuego.TABLA) {
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        estado = EstadoJuego.MENU;
                    }
                }    
                if (estado == EstadoJuego.MENU) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        opcionSeleccionada = (opcionSeleccionada - 1 + opcionesMenu.length) % opcionesMenu.length;
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        opcionSeleccionada = (opcionSeleccionada + 1) % opcionesMenu.length;
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        ejecutarOpcionMenu();
                    }
                }

                if (estado == EstadoJuego.JUGANDO && !juegoTerminado) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        jugador.disparar();
                    }
                }

                if (estado == EstadoJuego.JUGANDO && juegoTerminado) {
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        estado = EstadoJuego.MENU;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                teclas[e.getKeyCode()] = false;
            }
        });

        Timer timer = new Timer(30, e -> {
            if (estado == EstadoJuego.JUGANDO && !juegoTerminado) {
                actualizar();
            }
            repaint();
        });
        timer.start();
    }

    private void ejecutarOpcionMenu() {
        switch (opcionSeleccionada) {
            case 0:  // Iniciar Juego
                nombreJugador = JOptionPane.showInputDialog(this, "Ingresa tu nombre:");
                if (nombreJugador == null || nombreJugador.isEmpty()) nombreJugador = "Jugador";
                iniciarJuego();
                break;
            case 1:  // Tabla de Posiciones
                estado = EstadoJuego.TABLA;
                break;
            case 2:  // Salir
                System.exit(0);
                break;
        }
    }

    private void iniciarJuego() {
        estado = EstadoJuego.JUGANDO;
        nivel = 1;
        velocidadEnemigo = 8;
        maxEnemigos = 10;
        jugador = new Jugador(ANCHO / 2, ALTO - 100, TAM_JUGADOR, TAM_JUGADOR);
        enemigos.clear();
        totalEnemigosCreados = 0;
        contadorSpawn = 0;
        juegoTerminado = false;
    }

    private void siguienteNivel() {
        nivel++;
        velocidadEnemigo += 2;
        maxEnemigos += 5;
        totalEnemigosCreados = 0;
        enemigos.clear();
    }

    private void actualizar() {
        if (teclas[KeyEvent.VK_LEFT]) jugador.moverIzquierda(0);
        if (teclas[KeyEvent.VK_RIGHT]) jugador.moverDerecha(ANCHO);
        if (teclas[KeyEvent.VK_UP]) jugador.moverArriba(0);
        if (teclas[KeyEvent.VK_DOWN]) jugador.moverAbajo(ALTO);

        jugador.actualizarDisparos();

        contadorSpawn++;
        if (contadorSpawn >= tiempoEntreEnemigos && totalEnemigosCreados < maxEnemigos) {
            contadorSpawn = 0;
            boolean ladoIzquierdo = (totalEnemigosCreados % 2 == 0);
            enemigos.add(new Enemigo(ANCHO, ladoIzquierdo));
            totalEnemigosCreados++;
        }

        for (Enemigo e : enemigos) {
            e.mover(velocidadEnemigo);
        }

        detectarColisiones();
        verificarFin();
    }

    private void detectarColisiones() {
        Rectangle jugadorRect = jugador.getRectangulo();
        ArrayList<Disparo> disparos = new ArrayList<>(jugador.getDisparos());
        for (int i = disparos.size() - 1; i >= 0; i--) {
            Rectangle rectBala = new Rectangle(disparos.get(i).getX(), disparos.get(i).getY(), 5, 10);
            for (int j = enemigos.size() - 1; j >= 0; j--) {
                if (rectBala.intersects(enemigos.get(j).getRect())) {
                    jugador.sumarPuntos(100);
                    enemigos.remove(j);
                    jugador.getDisparos().remove(i);
                    break;
                }
            }
        }

        for (Enemigo e : enemigos) {
            if (jugadorRect.intersects(e.getRect())) {
                juegoTerminado = true;
                break;
            }
        }
    }

    private void verificarFin() {
        if (enemigos.isEmpty() && totalEnemigosCreados >= maxEnemigos) {
            siguienteNivel();
        }

        for (Enemigo e : enemigos) {
            if (e.getY() > ALTO - TAM_ENEMIGO) {
                juegoTerminado = true;
                break;
            }
        }

        if (juegoTerminado) {
            tablaPuntajes.add(nombreJugador + ": " + jugador.getPuntos() + " puntos (Nivel " + nivel + ")");
        }
    }

    private class PanelJuego extends JPanel {
        public PanelJuego() {
            setDoubleBuffered(true);
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(ANCHO, ALTO));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            switch (estado) {
                case MENU:
                    dibujarMenu(g);
                    break;
                case JUGANDO:
                    dibujarJuego(g);
                    break;
                case TABLA:
                    dibujarTabla(g);
                    break;
            }
        }

        private void dibujarMenu(Graphics g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("GALAGA JAVA", ANCHO / 2 - 140, 150);

            g.setFont(new Font("Arial", Font.PLAIN, 24));
            for (int i = 0; i < opcionesMenu.length; i++) {
                if (i == opcionSeleccionada) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString(opcionesMenu[i], ANCHO / 2 - 80, 250 + i * 50);
            }

            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.setColor(Color.GRAY);
            g.drawString("Usa ↑ ↓ para elegir y ENTER para seleccionar.", ANCHO / 2 - 150, 450);
        }

        private void dibujarJuego(Graphics g) {
            jugador.actualizarDisparos();
            g.setColor(Color.GREEN);
            g.fillRect(jugador.getX(), jugador.getY(), TAM_JUGADOR, TAM_JUGADOR);

            for (Disparo d : jugador.getDisparos()) {
                g.setColor(Color.YELLOW);
                g.fillRect(d.getX(), d.getY(), 5, 10);
            }

            for (Enemigo e : enemigos) e.dibujar(g);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Puntos: " + jugador.getPuntos(), 20, 30);
            g.drawString("Nivel: " + nivel, 20, 60);

            if (juegoTerminado) {
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                g.drawString("GAME OVER", ANCHO / 2 - 150, ALTO / 2);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.drawString("Presiona R para volver al menú", ANCHO / 2 - 110, ALTO / 2 + 40);
            }
        }

        private void dibujarTabla(Graphics g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Tabla de Posiciones", ANCHO / 2 - 150, 100);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            int yPos = 150;
            if (tablaPuntajes.isEmpty()) {
                g.drawString("Sin registros aún.", ANCHO / 2 - 80, yPos);
            } else {
                for (String entrada : tablaPuntajes) {
                    g.drawString(entrada, ANCHO / 2 - 150, yPos);
                    yPos += 30;
                }
            }

            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.setColor(Color.GRAY);
            g.drawString("Presiona BACKSPACE para volver al menú.", ANCHO / 2 - 120, ALTO - 50);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PantallaInicio juego = new PantallaInicio();
            juego.setVisible(true);
        });
    }
}
