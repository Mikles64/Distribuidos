import java.util.*;

public class Inciso20 {
    public static void main(String args[]){
        int[] arreglo = new int[10];
        Scanner input = new Scanner(System.in);

        System.out.print("Ingrese 10 numeros enteros: ");
        for(int i = 0; i < 10; i++){
            arreglo[i] = input.nextInt();
        }

        int ultimo = arreglo[9];
        for(int j = 8; j >= 0; j--){
            arreglo[j + 1] = arreglo[j];
        }
        arreglo[0] = ultimo;

        System.out.print("El arreglo desplazado es: ");
        for(int k = 0; k < 10; k++){
            System.out.printf("%d ", arreglo[k]);
        }
        System.out.println();
    }
}
