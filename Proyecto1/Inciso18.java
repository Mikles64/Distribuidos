import java.util.*;

public class Inciso18{
	public static void main(String args[]){
		String[] ciudades = new String[10];
		Scanner input = new Scanner(System.in);
		System.out.print("Ingrese el nombre de 10 ciudades: ");
		for(int i = 0; i < 10; i++){
			ciudades[i] = input.next();
		}
		input.close();
		String ciudadMasLarga = ciudades[0];
		for(int i = 1; i < 10; i++){
			if(ciudades[i].length() > ciudadMasLarga.length()){
				ciudadMasLarga = ciudades[i];
			}
		}
		System.out.println("La ciudad con el nombre mas largo es: " + ciudadMasLarga);
	}
}