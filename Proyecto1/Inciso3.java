import java.util.*;

public class Inciso3 {
    public static void main(String[] args) {
        System.out.println("Calcular el radio de la circunferencia inscrita en un triángulo");
        Scanner input = new Scanner(System.in);
        System.out.print("Ingrese la longitud del lado A: ");
        double a = input.nextDouble();
        System.out.print("Ingrese la longitud del lado B: ");
        double b = input.nextDouble();
        System.out.print("Ingrese la longitud del lado C: ");
        double c = input.nextDouble();
        input.close();
        double semiperímetro = (a + b + c) / 2;
        double area = Math.sqrt(semiperímetro * (semiperímetro - a) * (semiperímetro - b) * (semiperímetro - c)); // Fórmula de Herón
        double radio = area / semiperímetro;
        System.out.printf("El radio de la circunferencia inscrita es: %.4f\n", radio);
    }
}
