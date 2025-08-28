import java.util.*;

public class Inciso2{
	public static void main(String args[]){
		Scanner inA = new Scanner(System.in);
		Scanner inB = new Scanner(System.in);
		Scanner inC = new Scanner(System.in);
		Scanner inD = new Scanner(System.in);

		System.out.println("Multiplicación de dos fraciones: a/b * c/d");
		System.out.printf("Ingrese el valor de 'a': ");
		int A = inA.nextInt();
		System.out.printf("Ingrese el valor de 'b': ");
		int B = inB.nextInt();
		System.out.printf("Ingrese el valor de 'c': ");
		int C = inC.nextInt();
		System.out.printf("Ingrese el valor de 'd': ");
		int D = inD.nextInt();

		float resultado = (A * C) / (B * D);
		int num = A * C;
		int den = B * D;

		System.out.println("El resultado en decimal es: " + resultado);
		System.out.println("El resultado en fracción es: " + num + "/" + den);

		inA.close();
		inB.close();
		inC.close();
		inD.close();
	}
}