import java.util.*;

public class Inciso19 {
    public static void main(String args[]){
        int[] nums = new int[10];
        Scanner input = new Scanner(System.in);
        System.out.print("Ingrese 10 numeros enteros: ");
        for(int i = 0; i < 10; i++){
            nums[i] = input.nextInt();
        }
        input.close();
        boolean descendente = true;
        for(int i = 0; i < 9; i++){
            if(nums[i] < nums[i+1]){
                descendente = false;
                break;
            }
        }
        if(descendente)
            System.out.println("Los numeros estan ordenados de forma descendente");
        else
            System.out.println("Los numeros no estan ordenados de forma descendente");
    }
}
