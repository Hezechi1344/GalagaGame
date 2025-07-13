package galaga;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GalagaGame extends JFrame {
    public static final int ANCHO = 800, ALTO = 600;
    public static final int TAM_JUGADOR = 40, TAM_ENEMIGO = 40;
    public static final int VEL_ENEMIGO = 1;

    private Jugador jugador;
    private ArrayList<Enemigo> enemigos = new ArrayList<>();
    private boolean[] teclas = new boolean[256];
    private boolean juegoTerminado = false;
    private Random random = new Random();

    public GalagaGame() {
        setTitle("Galaga Java");
        setSize(ANCHO, ALTO);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(new PanelJuego());

        jugador = new Jugador(ANCHO / 2, ALTO - 100, TAM_JUGADOR, TAM_JUGADOR);
        crearEnemigos(10);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                teclas[e.getKeyCode()] = true;
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !juegoTerminado) {
                    jugador.disparar();
                }
                if (e.getKeyCode() == KeyEvent.VK_R && juegoTerminado) {
                    reiniciar();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                teclas[e.getKeyCode()] = false;
            }
        });

        Timer timer = new Timer(30, e -> {
            if (!juegoTerminado) actualizar();
            repaint();
        });
        timer.start();
    }

    private void crearEnemigos(int cantidad) {
        enemigos.clear();
        for (int i = 0; i < cantidad; i++) {
            enemigos.add(new Enemigo(random.nextInt(ANCHO - TAM_ENEMIGO)));
        }
    }

    private void actualizar() {
        if (teclas[KeyEvent.VK_LEFT]) jugador.moverIzquierda(0);
        if (teclas[KeyEvent.VK_RIGHT]) jugador.moverDerecha(ANCHO);

        jugador.actualizarDisparos();

        for (Enemigo e : enemigos) {
            e.mover(VEL_ENEMIGO);
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
                return;
            }
        }
    }

    private void verificarFin() {
        for (Enemigo e : enemigos) {
            if (e.getY() > ALTO - TAM_ENEMIGO) {
                juegoTerminado = true;
                break;
            }
        }
    }

    private void reiniciar() {
        jugador.reiniciar(ANCHO / 2, ALTO - 100);
        enemigos.clear();
        juegoTerminado = false;
        crearEnemigos(10);
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
            g.drawString("Puntuación: " + jugador.getPuntos(), 20, 30);

            if (juegoTerminado) {
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                g.drawString("GAME OVER", ANCHO / 2 - 150, ALTO / 2);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.drawString("Presiona R para reiniciar", ANCHO / 2 - 100, ALTO / 2 + 40);
            }
        }
    }


}

