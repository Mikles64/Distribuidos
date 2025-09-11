public class Rectangulo extends Figura {
    private double base;
    private double altura;

    public Rectangulo(Coordenada centro, double base, double altura) {
        super(centro, 4);
        this.base = base;
        this.altura = altura;
        construirVertices();
    }

    private void construirVertices() {
        double cx = centro.abcisa(), cy = centro.ordenada();
        double hx = base / 2.0, hy = altura / 2.0;

        // Orden: sup-izq, sup-der, inf-der, inf-izq
        vertices[0] = new Coordenada(cx - hx, cy + hy);
        vertices[1] = new Coordenada(cx + hx, cy + hy);
        vertices[2] = new Coordenada(cx + hx, cy - hy);
        vertices[3] = new Coordenada(cx - hx, cy - hy);
    }

    @Override
    public double area() {
        return base * altura;
    }

    public double getBase() { return base; }
    public double getAltura() { return altura; }
}
