
public class PruebaPoligono {
    public static void main(String[] args) {
        // Crear un poligono aleatorio con 5 vertices
        PoligonoIrreg poligono = new PoligonoIrreg();

        //Creamos 7 vertices
        Coordenada vert1 = new Coordenada(-100 + Math.random() * 200, -100 + Math.random() * 200);
        poligono.anadeVertice(vert1);
        Coordenada vert2 = new Coordenada(-100 + Math.random() * 200, -100 + Math.random() * 200);
        poligono.anadeVertice(vert2);
        Coordenada vert3 = new Coordenada(-100 + Math.random() * 200, -100 + Math.random() * 200);
        poligono.anadeVertice(vert3);
        Coordenada vert4 = new Coordenada(-100 + Math.random() * 200, -100 + Math.random() * 200);
        poligono.anadeVertice(vert4);
        Coordenada vert5 = new Coordenada(-100 + Math.random() * 200, -100 + Math.random() * 200);
        poligono.anadeVertice(vert5);
        Coordenada vert6 = new Coordenada(-100 + Math.random() * 200, -100 + Math.random() * 200);
        poligono.anadeVertice(vert6);
        Coordenada vert7 = new Coordenada(-100 + Math.random() * 200, -100 + Math.random() * 200);
        poligono.anadeVertice(vert7);
        System.out.println();

        System.out.println("Poligono sin ordenar:");
        System.out.println(poligono);

        //Ordenamos los vertices del poligono
        poligono.ordenaVertices();

        System.out.println("Poligono ordenado:");
        System.out.println(poligono);

    }
}
