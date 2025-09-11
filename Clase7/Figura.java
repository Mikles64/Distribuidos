public abstract class Figura implements Desplazable {
    protected Coordenada centro;
    protected Coordenada[] vertices;

    public Figura(Coordenada centro, int numVertices) {
        this.centro = centro;
        this.vertices = new Coordenada[numVertices];
    }

    public Coordenada getCentro() {
        return centro;
    }

    public Coordenada[] getVertices() {
        return vertices;
    }

    public abstract double area();

    @Override
    public void desplazar(double dx, double dy) {
        // desplaza centro
        centro = new Coordenada(centro.abcisa() + dx, centro.ordenada() + dy);
        // desplaza todos los vértices
        for (int i = 0; i < vertices.length; i++) {
            Coordenada v = vertices[i];
            vertices[i] = new Coordenada(v.abcisa() + dx, v.ordenada() + dy);
        }
    }
}
