import java.util.*;

public class Inciso16{
	public static void main(String args[]){
		int suma = 0;
		double promedio;
		int numEntrada;
		int conteo = 0;
		System.out.println("Ingrese multiples numeros para promediarlos (0 para terminar de ingresar):");
		Scanner input = new Scanner(System.in);
		numEntrada = input.nextInt();
		while(numEntrada != 0){
			suma += numEntrada;
			conteo++;
			numEntrada = input.nextInt();
		}
		input.close();
		promedio = (double) suma / conteo;
		System.out.printf("El promedio de los numeros ingresados es %.2f \n", promedio);
	}
}