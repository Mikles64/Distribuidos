import java.util.*;

public class Inciso5{
	public static void main(String args[]){
		System.out.print("Ingrese los anios que ha trabajado en la empresa: ");
		Scanner input = new Scanner(System.in);
		int anios = input.nextInt();
		int diasVac;
		if(anios >= 1 && anios <= 5)
			diasVac = 5;
		else if(anios >= 6 && anios <= 10)
			diasVac = 10;
		else if(anios >= 11 && anios < 20)
			diasVac = anios;
		else if (anios >= 20 && anios <= 32)
			diasVac = 20 + 2 * (anios - 20);
		else if (anios > 32)
			diasVac = 45;
		else
			diasVac = 0;
		input.close();
		if(diasVac != 0)
			System.out.printf("Le corresponden %d dias de vacaciones \n", diasVac);
		else
			System.out.println("No se le han asignado dias de vacaciones");
	}
}