public class PruebaPoligono {
    public static void main(String[] args) {
        // Crear un poligono aleatorio con 5 vertices
        PoligonoIrreg poligono = new PoligonoIrreg();

        // Imprimir la creacion del poligono
        System.out.println(poligono);


        // Calcular y mostrar el area del poligono
        double area = poligono.area();
        System.out.println("Area del poligono: " + area);
    }
}
