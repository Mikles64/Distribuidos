public class PruebaFiguras {
    // coordenadas a 2 decimales
    public static String formatear(Coordenada c) {
        return "[" + String.format("%.2f", c.abcisa()) + ", " + String.format("%.2f", c.ordenada()) + "]";
    }

    public static void main(String[] args) {
        // Crear figuras
        TrianguloEq t = new TrianguloEq(new Coordenada(0, 0), 6);
        Rectangulo r = new Rectangulo(new Coordenada(2, 1), 4, 3);

        // Estado inicial
        System.out.println("=== INICIO ===");
        System.out.printf("Area Triangulo: %.2f\n", t.area());
        System.out.print("Vertices Triangulo: ");
        for (Coordenada v : t.getVertices()) {
            System.out.print(formatear(v) + " ");
        }
        System.out.println();

        System.out.printf("Area Rectangulo: %.2f\n", r.area());
        System.out.print("Vertices Rectangulo: ");
        for (Coordenada v : r.getVertices()) {
            System.out.print(formatear(v) + " ");
        }
        System.out.println();

        // Desplazar
        double dx = 3, dy = -2; //3 a la der y -2 abajo
        t.desplazar(dx, dy);
        r.desplazar(dx, dy);

        // Estado final
        System.out.println("\n=== FINAL (dx=" + dx + ", dy=" + dy + ") ===");
        System.out.printf("Area Triangulo: %.2f\n", t.area());
        System.out.print("Vertices Triangulo: ");
        for (Coordenada v : t.getVertices()) {
            System.out.print(formatear(v) + " ");
        }
        System.out.println();

        System.out.printf("Area Rectangulo: %.2f\n", r.area());
        System.out.print("Vertices Rectangulo: ");
        for (Coordenada v : r.getVertices()) {
            System.out.print(formatear(v) + " ");
        }
        System.out.println();
    }
}
