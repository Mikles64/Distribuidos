import java.util.*;

public class Inciso2{
	public static void main(String args[]){
		Scanner input = new Scanner(System.in);

		System.out.println("Multiplicación de dos fraciones: a/b * c/d");
		System.out.printf("Ingrese el valor de 'a': ");
		int A = input.nextInt();
		System.out.printf("Ingrese el valor de 'b': ");
		int B = input.nextInt();
		System.out.printf("Ingrese el valor de 'c': ");
		int C = input.nextInt();
		System.out.printf("Ingrese el valor de 'd': ");
		int D = input.nextInt();

		float resultado = (A * C) / (B * D);
		int num = A * C;
		int den = B * D;

		System.out.println("El resultado en decimal es: " + resultado);
		System.out.println("El resultado en fracción es: " + num + "/" + den);

		input.close();
	}
}