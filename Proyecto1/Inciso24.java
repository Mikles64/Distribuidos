import java.io.*;

public class Inciso24{
    public static void main(String[] args){
        try(
            BufferedReader lector = new BufferedReader(new FileReader("entrada.txt"));
        ){
            int[] conteoLetras = new int[26]; // Para 'a' a 'z'
            int caracter;
            while((caracter = lector.read()) != -1){
                char letra = Character.toLowerCase((char)caracter);
                if(letra >= 'a' && letra <= 'z'){
                    conteoLetras[letra - 'a']++;
                }
            }
            System.out.println("Conteo de letras en el archivo:");
            for(int i = 0; i < conteoLetras.length; i++){
                char letra = (char)('a' + i);
                System.out.println(letra + ": " + conteoLetras[i]);
            }
        }catch(IOException e){
            System.out.println("Ocurrió un error: " + e.getMessage());
        }
    }
}
