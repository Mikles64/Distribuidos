import java.util.*;

public class Inciso14{
    public static void main(String args[]){
        int x, y;
        int suma = 0;
        System.out.print("Ingrese el valor de inicio y el valor de salto: ");
        Scanner input = new Scanner(System.in);
        x = input.nextInt();
        y = input.nextInt();
        input.close();
        suma += x;
        while(suma <= 200){
            System.out.printf("%d ", suma);
            suma += y;
        }
        System.out.println();
    }
    
}
