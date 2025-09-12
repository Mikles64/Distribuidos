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

    public double magnitud(double x, double y){
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.###");
        return "[" + df.format(x) + "," + df.format(y) + "]\tMagnitud: " + df.format(magnitud(x, y));
    }
}
