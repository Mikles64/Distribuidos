import java.util.*;

public class Inciso6{
	public static void main(String args[]){
		System.out.print("Ingrese los ejes de su vehiculo para el cobro: ");
		Scanner input = new Scanner(System.in);
		int ejes = input.nextInt();
		input.close();
		float cuota;
		if(ejes > 0 && ejes <= 3)
			cuota = 20 * ejes;
		else if(ejes >= 4 && ejes <= 6)
			cuota = 250;
		else if(ejes > 6)
			cuota = 250 + 50 * (ejes - 6);
		else
			cuota = 0;

		if(cuota != 0)
			System.out.printf("Su cuota: $%.2f \n", cuota);
		else
			System.out.println("Numero de ejes inválido");
	}
}