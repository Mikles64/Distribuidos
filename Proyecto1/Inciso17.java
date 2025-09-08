import java.util.*;

public class Inciso17{
    public static void main(String args[]){
        int[] arreglo = new int[10];
        Scanner input = new Scanner(System.in);

        System.out.print("Ingrese 10 numeros enteros para mostrar en orden inverso: ");
        for(int i = 0; i < 10; i++){
            arreglo[i] = input.nextInt();
        }

        System.out.print("Los numeros al reves son: ");
        for(int j = 9; j >= 0; j--){
            System.out.printf("%d ", arreglo[j]);
        }
        System.out.println();
    }
}