import java.util.*;

public class Inciso22 {
    public static void main(String args[]){
        Scanner input = new Scanner(System.in);
        System.out.print("Ingrese las dimensiones de las dos matrices a multiplicar.\nRecuerde que el numero de columnas de la primera matriz debe ser igual al numero de filas de la segunda matriz.\nDimensiones de la matriz 1: ");
        int filas1 = input.nextInt();
        int columnas1 = input.nextInt();
        System.out.print("Dimensiones de la matriz 2: ");
        int filas2 = input.nextInt();
        int columnas2 = input.nextInt();
        if(columnas1 != filas2){
            System.out.print("Error: El numero de columnas de la primera matriz debe ser igual al numero de filas de la segunda matriz.");
        }else{
            int[][] matriz1 = new int[filas1][columnas1];
            int[][] matriz2 = new int[filas2][columnas2];
            int[][] resultado = new int[filas1][columnas2];
            System.out.println("Ahora ingrese los elementos de la primera matriz: ");
            for(int i = 0; i < filas1; i++){
                for(int j = 0; j < columnas1; j++){
                    System.out.print("Elemento [" + (i+1) + "][" + (j+1) + "]: ");
                    matriz1[i][j] = input.nextInt();
                }
            }
            System.out.println("Ahora ingrese los elementos de la segunda matriz: ");
            for(int i = 0; i < filas2; i++){
                for(int j = 0; j < columnas2; j++){
                    System.out.print("Elemento [" + (i+1) + "][" + (j+1) + "]: ");
                    matriz2[i][j] = input.nextInt();
                }
            }
            input.close();
            for(int i = 0; i < filas1; i++){
                for(int j = 0; j < columnas2; j++){
                    for(int k = 0; k < columnas1; k++){
                        resultado[i][j] += matriz1[i][k] * matriz2[k][j];
                    }
                }
            }
            System.out.println("El resultado de la multiplicacion de las dos matrices es: ");
            for(int i = 0; i < filas1; i++){
                for(int j = 0; j < columnas2; j++){
                    System.out.print(resultado[i][j] + " ");
                }
                System.out.println();
            }
        }
    }
}
