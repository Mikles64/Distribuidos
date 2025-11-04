/**
 * PROYECTO 4
 * Rodríguez Gutiérrez Miguel Francisco
 * 7CM4
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuscaPalindromos {
    
    public Map<Integer, List<String>> buscarPalindromos(String texto, String idTexto) {
        Map<Integer, List<String>> resultados = new HashMap<>();
        
        for (int n = 1; n <= 5; n++) {
            resultados.put(n, new ArrayList<>());
        }
        
        // Limpiar texto
        String textoLimpio = limpiarTexto(texto);
        String[] palabras = textoLimpio.split("\\s+");
        
        // Buscar palíndromos de 1 palabra
        for (int i = 0; i < palabras.length; i++) {
            if (esPalindromo(palabras[i]) && palabras[i].length() > 1) {
                resultados.get(1).add(palabras[i] + "-" + idTexto);
            }
        }
        
        // Buscar palíndromos de 2 a 5 palabras
        for (int n = 2; n <= 5; n++) {
            for (int i = 0; i <= palabras.length - n; i++) {
                String frase = construirFrase(palabras, i, n);
                if (esPalindromo(frase)) {
                    String fraseOriginal = String.join(" ", Arrays.copyOfRange(palabras, i, i + n));
                    resultados.get(n).add(fraseOriginal + "-" + idTexto);
                }
            }
        }
        
        return resultados;
    }
    
    private String limpiarTexto(String texto) {
        return texto.toLowerCase()
                   .replaceAll("[^a-z\\s]", "")
                   .replaceAll("\\s+", " ")
                   .trim();
    }
    
    private boolean esPalindromo(String texto) {
        if (texto.isEmpty()) return false;
        
        String limpio = texto.replaceAll("[^a-z]", "");
        if (limpio.length() < 2) return false;
        
        return new StringBuilder(limpio).reverse().toString().equals(limpio);
    }
    
    private String construirFrase(String[] palabras, int inicio, int longitud) {
        StringBuilder sb = new StringBuilder();
        for (int i = inicio; i < inicio + longitud; i++) {
            sb.append(palabras[i]);
        }
        return sb.toString();
    }
}