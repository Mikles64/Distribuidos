import java.util.*;

public class Inciso1{
	public static void main(String[] args){
		Scanner entrada = new Scanner(System.in);
		
		System.out.printf("Ingrese la temperatura en grados Celsius: ");
		float celsius = entrada.nextFloat();

		float fahrenheit = (celsius * 9/5) + 32;

		System.out.println(celsius + " grados Celsius equivalen a " + fahrenheit + " grados Fahrenheit");

		entrada.close();

	}
}