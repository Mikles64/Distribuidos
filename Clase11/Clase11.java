import java.util.*;
import java.io.*;

public class Clase11{ 
    public static void main(String[] args) throws Exception
    {
        BufferedReader texto = new BufferedReader(new FileReader("El_viejo_y_el_mar.txt"));
 
        // HashMap para contar caracteres
        HashMap<Character, Integer> contador = new HashMap<>();
        String st;
        while ((st = texto.readLine()) != null) {
            // Contar caracteres de cada linea
            for (char caracter : st.toCharArray()) {
                contador.put(caracter, contador.getOrDefault(caracter, 0) + 1);
            }
        }
        texto.close();
 
        /*System.out.println("Numero de caracteres distintos: " + contador.size());
        System.out.println("Mapa de caracteres y ocurrencias: " + contador);*/

        ArrayList<HashMap.Entry<Character, Integer>> lista = new ArrayList<>(contador.entrySet());
            // Comparator para ordenar de mayor a menor
            Comparator<HashMap.Entry<Character, Integer>> comparador = new Comparator<HashMap.Entry<Character, Integer>>() {
                @Override
                public int compare(HashMap.Entry<Character, Integer> a, HashMap.Entry<Character, Integer> b) {
                    return b.getValue().compareTo(a.getValue());
                }
            };
            lista.sort(comparador);
 
            System.out.println("Caracteres ordenados:");
            for (HashMap.Entry<Character, Integer> entry : lista) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
    }
}