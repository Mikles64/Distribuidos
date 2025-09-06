import java.util.*;

public class Inciso9{
	public static void main(String args[]){
		System.out.print("Ingrese un numero del 0 al 9999: ");
		Scanner input = new Scanner(System.in);
		int num = input.nextInt();
		String numS = String.valueOf(num);
		input.close();
		if(num < 0 || num > 9999)
			System.out.println("Numero fuera del rango.");
		else if(numS.length() == 1 || ((numS.length() == 2) && numS.charAt(0) == numS.charAt(1)) || ((numS.length() == 3) && numS.charAt(0) == numS.charAt(2)) || ((numS.length() == 4) && numS.charAt(0) == numS.charAt(3) && numS.charAt(1) == numS.charAt(2)))
			System.out.println("Es numero capicua.");
		else
			System.out.println("No es numero capicua.");
	}
}