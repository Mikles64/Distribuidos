public class PruebaRectangulo {
    public static void main (String[] args) {
        Coordenada supIzq = new Coordenada(2, 3);
        Coordenada infDer = new Coordenada(5, 1);
        Rectangulo rect1 = new Rectangulo(2,3,5,1);
        double ancho, alto;

        Rectangulo rect2 = new Rectangulo(supIzq, infDer);
        
        System.out.println("Calculando el área del rectángulo 1 dadas sus coordenadas en un plano cartesiano:");
        System.out.println(rect1);
        alto = rect1.superiorIzquierda().ordenada() - rect1.inferiorDerecha().ordenada();
        ancho = rect1.inferiorDerecha().abcisa() - rect1.superiorIzquierda().abcisa();
        System.out.println("El área del rectángulo 1 es = " + ancho*alto);

        System.out.println("Calculando el área de un rectángulo 2 dadas las coordenadas de dos esquinas:");
        System.out.println(rect2);
        alto = rect2.superiorIzquierda().ordenada() - rect2.inferiorDerecha().ordenada();
        ancho = rect2.inferiorDerecha().abcisa() - rect2.superiorIzquierda().abcisa();
        System.out.println("El área del rectángulo 2 es = " + ancho*alto);
    }
}