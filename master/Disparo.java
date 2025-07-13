package galaga;


public class Disparo {
    private final int x;
    private int y;

    public Disparo(int xInicial, int yInicial) {
        this.x = xInicial;
        this.y = yInicial;
    }

    public void mover() {
        int velocidad = 10;
        y -= velocidad;
    }

    public boolean fueraDePantalla() {
        return y < 0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
