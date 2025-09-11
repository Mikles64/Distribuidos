public class TrianguloEq extends Figura {
    private double lado;

    public TrianguloEq(Coordenada centro, double lado) {
        super(centro, 3);
        this.lado = lado;
        construirVertices();
    }

    private void construirVertices() {
        double R = lado / Math.sqrt(3.0); // radio circunscrito
        double cx = centro.abcisa(), cy = centro.ordenada();
        double[] angs = { Math.toRadians(90), Math.toRadians(210), Math.toRadians(330) };

        for (int i = 0; i < 3; i++) {
            double x = cx + R * Math.cos(angs[i]);
            double y = cy + R * Math.sin(angs[i]);
            vertices[i] = new Coordenada(x, y);
        }
    }

    @Override
    public double area() {
        return (Math.sqrt(3.0) / 4.0) * lado * lado;
    }

    public double getLado() { return lado; }
}
