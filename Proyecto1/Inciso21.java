
import java.util.Scanner;

public class Inciso21{
    public static void main(String args[]){
        int[][] matriz = new int[5][5];
        Scanner input = new Scanner(System.in);
        System.out.println("Ingrese los elementos de la matriz 5x5 (izquierda a derecha y arriba hacia abajo):");
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                matriz[i][j] = input.nextInt();
            }
        }
        input.close();
        int suma = 0;
        for(int j = 0; j < 5; j++){
            for(int k = 0; k < 5; k++){
                suma += matriz[j][k];
            }
        }
        System.out.println("La suma total de los elementos de la matriz es " + suma);
    }
}
