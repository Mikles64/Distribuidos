import java.util.*;

public class Inciso15 {
    public static void main(String args[]){
        Scanner input = new Scanner(System.in);
        int numPensado = (int)(Math.random() * 100 + 1);
        int numEntrada;

        System.out.print("Estoy pensando en un numero entre el 1 y 100. Adivinalo (0 para rendirse): ");
        while (true) {
            numEntrada = input.nextInt();

            if (numEntrada == 0) {
                System.out.println("Te rendiste. El numero pensado era: " + numPensado);
                break;
            } else if (numEntrada < 1 || numEntrada > 100) {
                System.out.print("El numero debe estar entre 1 y 100. Intenta de nuevo: ");
            } else if (numEntrada < numPensado) {
                System.out.print("El numero es mayor. Intenta de nuevo: ");
            } else if (numEntrada > numPensado) {
                System.out.print("El numero es menor. Intenta de nuevo: ");
            } else {
                System.out.println("Felicidades! Adivinaste el numero.");
                break;
            }
        }
        input.close();
    }
}