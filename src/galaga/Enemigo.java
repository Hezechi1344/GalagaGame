package galaga;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

public class Enemigo {
    private int x, y;
    private int tamaño;
    private Color color;
    private final String simbolo = "x";

    public Enemigo(int pantallaAncho) {
        this.tamaño = (int)(Math.random() * 30 + 30);
        this.x = (int)(Math.random() * (pantallaAncho - tamaño));
        this.y = (int)(Math.random() * -100) - 50;
        this.color = new Color(
            (int)(Math.random() * 155 + 100),
            (int)(Math.random() * 155 + 100),
            (int)(Math.random() * 155 + 100)
        );
    }


    public void mover(int velocidad) {
        y += velocidad;
    }

    public void dibujar(Graphics g) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, tamaño));
        g.drawString(simbolo, x, y);
    }


    public Rectangle getRect() {
        return new Rectangle(x, y, tamaño, tamaño);
    }

 
    public int getY() {
        return y;
    }
}
