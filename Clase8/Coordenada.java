import java.text.DecimalFormat;

public class Coordenada {
    private double x, y;

    public Coordenada(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Getters
    public double abcisa()   { return x; }
    public double ordenada() { return y; }

    // Setters para poder modificar un vértice
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.###");
        return "[" + df.format(x) + "," + df.format(y) + "]";
    }
}
