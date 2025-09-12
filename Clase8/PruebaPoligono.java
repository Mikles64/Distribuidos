import java.util.*;

public class PruebaPoligono {
    public static void main(String[] args) {
        // Crear un poligono aleatorio con 5 vertices
        PoligonoIrreg poligono = new PoligonoIrreg();

        // Imprimir la creacion del poligono
        System.out.println(poligono);

        //Creamos 3 vertices
        Coordenada vert1 = new Coordenada(-100 + Math.random() * 200, -100 + Math.random() * 200);
        poligono.anadeVertice(vert1);
        System.out.println("Se agrego el vertice " + vert1);
        Coordenada vert2 = new Coordenada(-100 + Math.random() * 200, -100 + Math.random() * 200);
        poligono.anadeVertice(vert2);
        System.out.println("Se agrego el vertice " + vert2);
        Coordenada vert3 = new Coordenada(-100 + Math.random() * 200, -100 + Math.random() * 200);
        poligono.anadeVertice(vert3);
        System.out.println("Se agrego el vertice " + vert3);
        System.out.println();

        System.out.println(poligono);

    }
}
