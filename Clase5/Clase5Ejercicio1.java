import java.util.*;

public class Clase5Ejercicio1{   
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        char[] cadenota = new char[n*4];
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 3; j++) {
                cadenota[i*4 + j] = charset.charAt(random.nextInt(26));
            }
            cadenota[i*4 + 3] = ' ';
        }

        String cadenotaStr = new String(cadenota);
        int numIPN = 0;
        int indexPalabra = cadenotaStr.indexOf("IPN");
        System.out.println(cadenotaStr);
        while (indexPalabra != -1) {
            numIPN++;
            System.out.println("IPN en la posicion " + indexPalabra);
            indexPalabra = cadenotaStr.indexOf("IPN", indexPalabra + 1);
        }

        System.out.printf("La palabra IPN aparece %d veces\n", numIPN);
    }
}
