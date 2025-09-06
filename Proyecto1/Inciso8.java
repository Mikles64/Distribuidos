import java.util.*;

public class Inciso8{
	public static void main(String args[]){
		System.out.print("Ingrese 3 numeros para ordenar: ");
		Scanner input = new Scanner(System.in);
		int x = input.nextInt();
		int y = input.nextInt();
		int z = input.nextInt();
		input.close();
		int a;
		int b;
		int c;	
		if(x <= y && y <= z){			
			a = x;
			b = y;
			c = z;
		} else if(x <= z && z <= y){
			a = x;
			b = z;
			c = y;
		} else if(y <= x && x <= z){
			a = y;
			b = x;
			c = z;
		} else if(y <= z && z <= x){
			a = y;
			b = z;
			c = x;
		} else if(z <= y && y <= x){
			a = z;
			b = y;
			c = x;
		} else{
			a = z;
			b = x;
			c = y;
		}
		System.out.printf("Los numeros ordenados son: %d %d %d \n", a, b, c);
	}
}