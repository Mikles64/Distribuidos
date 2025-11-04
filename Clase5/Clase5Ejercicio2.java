import java.util.*;
import java.lang.*;

public class Clase5Ejercicio2{
	public static void main(String args[]){
		int palabras = Integer.parseInt(args[0]);//Numero de palabras
		String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";//Letras en mayusculas
		int randIndex;//Indice al azar
		StringBuilder palabra = new StringBuilder();//Palabra de 3 letras
		StringBuilder cadena = new StringBuilder();//Cadena final
		int numIPN = 0;//Conteo de veces que aparece IPN
		for(int i = 0; i < palabras; i++){
			for(int j = 0; j < 3; j++){
				randIndex = (int) (Math.random() * 26);
				palabra.append(charset.charAt(randIndex));

			}
			cadena.append(palabra);
			//Contamos las veces que sale la palabra IPN
			if(palabra.toString().equals("IPN")){
				numIPN++;
			}
			palabra.delete(0, 3);
			cadena.append(" ");
		}
		//Buscamos donde aparece la cadena IPN
		int posicion = cadena.indexOf("IPN");
		while (posicion != -1){
			System.out.println("IPN encontrado en el indice " + posicion);
			posicion = cadena.indexOf("IPN", posicion + 1);
		}

		System.out.printf("La palabra IPN aparece %d veces\n", numIPN);

	}
}