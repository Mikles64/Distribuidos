import java.util.*;

public class Inciso10{
    public static void main(String[] args) {
        System.out.print("Ingrese un numero entero para sumar desde 1 hasta ese numero: ");
        Scanner input = new Scanner(System.in);
        int num = input.nextInt();
        input.close();
        int suma = 0;
        for(int i = 1; i <= num; i++){
            suma = suma + i;
        }
        System.out.println("La suma de 1 hasta " + num + " es " + suma);
    }
}