package galaga;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

public class Enemigo {
    private int x, y;
    private int tamaño;
    private final Color color = Color.RED;  
    private int direccion;
    private int pantallaAncho;

    public Enemigo(int pantallaAncho, boolean ladoIzquierdo) {
        this.pantallaAncho = pantallaAncho;
        this.tamaño = 40;  
        this.y = 5;
        this.x = ladoIzquierdo ? 0 : pantallaAncho - tamaño;
        this.direccion = ladoIzquierdo ? 1 : -1;
    }

    public void mover(int velocidad) {
        x += direccion * velocidad;

        if (x <= 0) {
            direccion = 1;
            y += 40;
            x = 0;
        } else if (x + tamaño >= pantallaAncho) {
            direccion = -1;
            y += tamaño / 2;
            x = pantallaAncho - tamaño;
        }
    }

    public void dibujar(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, tamaño, tamaño);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, tamaño, tamaño);
    }

    public int getY() {
        return y;
    }
}

