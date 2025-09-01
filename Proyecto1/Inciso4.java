import java.util.*;

public class Inciso4{
	public static void main(String args[]){
		System.out.print("Ingrese su sexo (H/M) y edad en años: ");
		Scanner input = new Scanner(System.in);
		String sexo = input.next();
		int edad = input.nextInt();
		input.close();
		String edificio;
		switch(sexo){
			case "H":
				if(edad == 18)
					edificio = "Edificio A";
				else if(edad >= 19 && edad <= 22)
					edificio = "Edificio C";
				else if(edad > 22)
					edificio = "Edificio E1";
				else
					edificio = "x";
				break; 
			case "M":
				if(edad == 18)
					edificio = "Edificio B";
				else if(edad >= 19 && edad <= 22)
					edificio = "Edificio D";
				else if(edad > 22)
					edificio = "Edificio E2";
				else
					edificio = "x";
				break;
			default:
				edificio = "x";
				break;
		}
		if(edificio != "x")
			System.out.printf("Se le ha asignado el %s \n", edificio);
		else
			System.out.println("Sexo o edad inválidos");
	}
}