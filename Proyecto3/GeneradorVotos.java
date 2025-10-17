// PROYECTO 3
// Rodríguez Gutiérrez Miguel Francisco
// 7CM4

import java.io.*;

public class GeneradorVotos {
    private static final String[] PARTIDOS = {"PRI", "PAN", "PRD", "MORENA", "VERDE", "PT", "MC"};
    private static final String ARCHIVO_VOTOS = "VOTOS.dat";
    
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.out.println("Uso: java GeneradorVotos <votos_por_segundo>");
            return;
        }
        
        int votosPorSegundo = Integer.parseInt(args[0]);
        int intervalo = 1000 / votosPorSegundo;
        
        System.out.println("Generador de votos iniciado...");
        System.out.println("Votos por segundo: " + votosPorSegundo);
        System.out.println("Archivo: " + ARCHIVO_VOTOS);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_VOTOS, true))) {
            long contador = 0;
            while (true) {
                String curp = generarCURP();
                String partido = PARTIDOS[(int)(Math.random() * PARTIDOS.length)];
                
                writer.println(curp + "," + partido);
                writer.flush();
                
                contador++;
                if (contador % 100 == 0) {
                    System.out.println("Votos generados: " + contador);
                }
                
                Thread.sleep(intervalo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String generarCURP() {
        String Letra = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Numero = "0123456789";
        String Sexo = "HM";
        String[] Entidad = {"AS", "BC", "BS", "CC", "CS", "CH", "CL", "CM", "DF", "DG", "GT", "GR", "HG", "JC", "MC", "MN", "MS", "NT", "NL", "OC", "PL", "QT", "QR", "SP", "SL", "SR", "TC", "TL", "TS", "VZ", "YN", "ZS"};
        
        StringBuilder sb = new StringBuilder(18);
        
        // Primeras 4 letras
        for (int i = 0; i < 4; i++) {
            int indice = (int)(Letra.length() * Math.random());
            sb.append(Letra.charAt(indice));
        }
        
        // Año, mes y dia
        int año = 65 + (int)(Math.random() * 40);
        sb.append(String.format("%02d", año));
        sb.append(String.format("%02d", (int)(Math.random() * 12) + 1));
        sb.append(String.format("%02d", (int)(Math.random() * 28) + 1));

        // Sexo
        int indiceSexo = (int)(Sexo.length() * Math.random());
        sb.append(Sexo.charAt(indiceSexo));
        
        // Entidad federativa
        sb.append(Entidad[(int)(Math.random() * Entidad.length)]);
        
        // Consonantes internas
        for (int i = 0; i < 3; i++) {
            int indice = (int)(Letra.length() * Math.random());
            sb.append(Letra.charAt(indice));
        }
        
        // Dígitos verificadores
        sb.append(String.format("%02d", (int)(Math.random() * 100)));
        
        return sb.toString();
    }
}