import java.io.*;

public class Inciso23 {
    public static void main(String args[]){
        try(
            BufferedReader lector = new BufferedReader(new FileReader("entrada.txt"));
            BufferedWriter escritor = new BufferedWriter(new FileWriter("salida.txt"));
        ){
            String linea;
            int numeroLinea = 1;
            while ((linea = lector.readLine()) != null){
                if (numeroLinea % 2 != 0) { // Línea impar
                    escritor.write(linea);
                    escritor.newLine();
                }
                numeroLinea++;
            }
            System.out.println("Líneas impares copiadas a salida.txt");
        }catch (IOException e){
            System.out.println("Ocurrió un error: " + e.getMessage());
        }
    }
}
