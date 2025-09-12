import java.util.*;

public class PoligonoIrreg {
    private final List<Coordenada> vertices = new ArrayList<>();

    public PoligonoIrreg(){};

    public PoligonoIrreg(List<Coordenada> puntos) {
        List<Coordenada> copia = new ArrayList<>(puntos);
        vertices.addAll(copia);
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

    public void anadeVertice(Coordenada vertice){
        Coordenada vex = new Coordenada(vertice.abcisa(), vertice.ordenada());
        vertices.add(vex);
    }

    public void ordenaVertices(){
        Comparator comp = new ordenaCoordenadas();
        Collections.sort(vertices, comp);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Poligono con " + vertices.size() + " vertices\n");
        for (int i = 0; i < vertices.size(); i++) {
            sb.append(i + 1).append(": ").append(vertices.get(i)).append("\n");
        }
        return sb.toString();
    }
}

class ordenaCoordenadas implements Comparator{
    @Override
    public int compare(Object o1, Object o2){
        Coordenada c1 = (Coordenada) o1;
        Coordenada c2 = (Coordenada) o2;
        if(c1.magnitud(c1.abcisa(), c1.ordenada()) < c2.magnitud(c2.abcisa(), c2.ordenada())){
            return -1;
        } else if(c1.magnitud(c1.abcisa(), c1.ordenada()) > c2.magnitud(c2.abcisa(), c2.ordenada())){
            return 1;
        } else {
            return 0;
        }
    }
}
