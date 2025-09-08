
public class Inciso11 {
    public static void main(String[] args) {
        int millares, centenas, decenas, unidades, suma;
        for(int i = 1; i <= 5000; i++){
            //Primero tomamos cada cifra de i
            unidades = i % 10;
            decenas = (i / 10) % 10;
            centenas = (i / 100) % 10;
            millares = (i / 1000) % 10;
            //Hacemos la suma de los cubos
            suma = (int)(Math.pow(unidades, 3) + Math.pow(decenas, 3) + Math.pow(centenas, 3) + Math.pow(millares, 3));
            //Luego usamos if para que ver si cumple con la regla
            if(suma == i)
                System.out.printf("%d^3 + %d^3 + %d^3 + %d^3 = %d\n", millares, centenas, decenas, unidades, i);
        }
    }
}
