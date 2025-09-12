import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PoligonoIrreg {
    private final List<Coordenada> vertices = new ArrayList<>();

    public PoligonoIrreg(){
        
    };

    public PoligonoIrreg(List<Coordenada> puntos) {
        // Aseguramos un orden “circular” estable: por ángulo alrededor del centroide
        List<Coordenada> copia = new ArrayList<>(puntos);
        ordenarPorAngulo(copia);
        vertices.addAll(copia);
    }

    // Fábrica: polígono aleatorio dentro de un rectángulo [minX,maxX]x[minY,maxY]
    public static PoligonoIrreg aleatorio(int n, double minX, double maxX, double minY, double maxY) {
        if (n < 3) throw new IllegalArgumentException("n debe ser >= 3");
        List<Coordenada> pts = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double x = minX + Math.random() * (maxX - minX);
            double y = minY + Math.random() * (maxY - minY);
            pts.add(new Coordenada(x, y));
        }
        return new PoligonoIrreg(pts);
    }

    // Área por la fórmula del Shoelace
    public double area() {
        double suma = 0.0;
        int m = vertices.size();
        for (int i = 0; i < m; i++) {
            Coordenada a = vertices.get(i);
            Coordenada b = vertices.get((i + 1) % m);
            suma += a.abcisa() * b.ordenada() - b.abcisa() * a.ordenada();
        }
        return Math.abs(suma) / 2.0;
    }

    public int numVertices() {
        return vertices.size();
    }

    public Coordenada getVertice(int i) {
        rango(i);
        return vertices.get(i);
    }

    // Modificar el punto de un vértice (sobrescribe coordenadas)
    public void setVertice(int i, double nuevoX, double nuevoY) {
        rango(i);
        vertices.get(i).setX(nuevoX);
        vertices.get(i).setY(nuevoY);
        // Reordenamos para mantener el polígono “bien formado”
        ordenarPorAngulo(vertices);
    }

    private void rango(int i) {
        if (i < 0 || i >= vertices.size()) {
            throw new IndexOutOfBoundsException("Índice de vértice inválido: " + i);
        }
    }

    private static void ordenarPorAngulo(List<Coordenada> pts) {
        // Calcular el centroide (promedio de las coordenadas)
        double cx = 0, cy = 0;
        for (Coordenada p : pts) {
            cx += p.abcisa();  // Acumulando coordenada x
            cy += p.ordenada(); // Acumulando coordenada y
        }
        cx /= pts.size(); // Promedio de las coordenadas x
        cy /= pts.size(); // Promedio de las coordenadas y

        // Crear un objeto 'Centroide' con los valores calculados
        Centroide centroide = new Centroide(cx, cy);

        // Ordenar los puntos por el ángulo respecto al centroide
        Collections.sort(pts, new Comparator<Coordenada>() {
            @Override
            public int compare(Coordenada p1, Coordenada p2) {
                // Usamos los valores 'final' del objeto centroide
                double a1 = Math.atan2(p1.ordenada() - centroide.cy, p1.abcisa() - centroide.cx); // Ángulo de p1
                double a2 = Math.atan2(p2.ordenada() - centroide.cy, p2.abcisa() - centroide.cx); // Ángulo de p2
                return Double.compare(a1, a2); // Comparar los ángulos
            }
        });
    }

    public void anadeVertice(Coordenada vertice){
        Coordenada vex = new Coordenada(vertice.abcisa(), vertice.ordenada());
        this.vertices.add(vex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Poligono con " + vertices.size() + " vertices:\n");
        for (int i = 0; i < vertices.size(); i++) {
            sb.append(i).append(": ").append(vertices.get(i)).append("\n");
        }
        return sb.toString();
    }

    // Clase interna para almacenar el centroide
    static class Centroide {
        final double cx, cy;

        Centroide(double cx, double cy) {
            this.cx = cx;
            this.cy = cy;
        }
    }
}
