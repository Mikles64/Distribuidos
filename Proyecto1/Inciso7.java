import java.util.*;

public class Inciso7{
	public static void main(String args[]){
		System.out.print("Ingrese la cantidad de kW consumidos: ");
		Scanner input = new Scanner(System.in);
		int kw = input.nextInt();
		System.out.print("Ingrese su tipo de contrato (Hogar o Negocio): ");
		String cont = input.next();
		input.close();
		double cobro;
		switch(cont){
			case "Hogar":
				if(kw >= 0 && kw <= 250)
					cobro = (double) kw * 0.65;
				else if(kw >= 251 && kw <= 500)
					cobro = (double) kw * 0.85;
				else if(kw >= 501 && kw <= 1200)
					cobro = (double) kw * 1.5;
				else if(kw >= 1201 && kw <= 2100)
					cobro = (double) kw * 2.5;
				else if(kw >= 2101)
					cobro = (double) kw * 3;
				else
					cobro = -1;
				break;
			case "Negocio":
				cobro = (double) kw * 5;
				break;
			default:
				cobro = -1;
				break;
		}
		if(cobro >= 0)
			System.out.printf("La cantidad a pagar es de $%.2f MXN\n", cobro);
		else
			System.out.println("Cantidad de kW o tipo de contrato invalidos");
	}
}