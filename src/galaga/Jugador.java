package galaga;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Jugador {
   
    private int x;
    private int y;
    private final int ancho;
    private final int alto;
    private final int velocidad;
    private int vida;
    private int puntos;
    private boolean estaVivo;

    // --- Lista de disparos (balas) del jugador ---
    private List<Disparo> disparos;

    // --- Constructor ---
    public Jugador(int xInicial, int yInicial, int ancho, int alto) {
        this.x = xInicial;
        this.y = yInicial;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidad = 8; // Velocidad estándar del jugador (nave)
        this.vida = 3;       // 3 vidas por defecto
        this.puntos = 0;
        this.estaVivo = true;
        this.disparos = new ArrayList<>();
    }

    // --- Movimiento lateral ---
    public void moverIzquierda(int limiteIzquierdo) {
        if (x - velocidad >= limiteIzquierdo) {
            x -= velocidad;
        }
    }

    public void moverDerecha(int limiteDerecho) {
        if (x + ancho + velocidad <= limiteDerecho) {
            x += velocidad;
        }
    }

    // --- Disparo ---
    public void disparar() {
        if (estaVivo) {
            Disparo nuevoDisparo = new Disparo(x + ancho / 2, y);
            disparos.add(nuevoDisparo);
        }
    }

    // --- Actualización de disparos (mover y eliminar los que salgan de pantalla) ---
    public void actualizarDisparos() {
        List<Disparo> activos = new ArrayList<>();
        for (Disparo d : disparos) {
            d.mover();
            if (!d.fueraDePantalla()) {
                activos.add(d);
            }
        }
        disparos = activos;
    }

    // --- Recibir daño ---
    public void recibirDaño() {
        if (estaVivo) {
            vida--;
            if (vida <= 0) estaVivo = false;
        }
    }

    // --- Aumentar puntos ---
    public void sumarPuntos(int cantidad) {
        if (estaVivo) {
            puntos += cantidad;
        }
    }

    // --- Reiniciar jugador (opcional) ---
    public void reiniciar(int xInicial, int yInicial) {
        this.x = xInicial;
        this.y = yInicial;
        this.vida = 3;
        this.puntos = 0;
        this.estaVivo = true;
        this.disparos.clear();
    }

    // --- Getters útiles ---
    public boolean estaVivo() { return estaVivo; }
    public int getVida() { return vida; }
    public int getPuntos() { return puntos; }
    public int getX() { return x; }
    public int getY() { return y; }
    public List<Disparo> getDisparos() { return disparos; }
    public Rectangle getRectangulo() {
        return new Rectangle(x, y, ancho, alto);
    }
}

