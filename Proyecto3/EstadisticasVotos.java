// PROYECTO 3
// Rodríguez Gutiérrez Miguel Francisco
// 7CM4

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EstadisticasVotos {
    private static final String ARCHIVO_VOTOS = "VOTOS.dat";
    private static final String[] PARTIDOS = {"PRI", "PAN", "PRD", "MORENA", "VERDE", "PT", "MC"};
    private static final Map<String, Integer> votosPartido = new ConcurrentHashMap<>();
    private static final Map<String, Integer> votosSexo = new ConcurrentHashMap<>();
    private static final Map<String, Integer> votosEstado = new ConcurrentHashMap<>();
    private static final Map<Integer, Map<String, Integer>> votosEdad = new ConcurrentHashMap<>();
    private static int totalVotos = 0;
    private static volatile boolean ejecutando = true;
    
    // Variables para el manejo de la interfaz
    private static int opcionSeleccionada = 0;
    private static String mensajeConsulta = "";
    private static boolean esperandoEntrada = false;
    private static StringBuilder entradaUsuario = new StringBuilder();
    
    public static void main(String[] args) {
        // Inicializar contadores
        for (String partido : PARTIDOS) {
            votosPartido.put(partido, 0);
        }
        votosSexo.put("H", 0);
        votosSexo.put("M", 0);
        
        // Hilo para leer votos
        Thread lector = new Thread(() -> leerVotos());
        lector.start();
        
        // Hilo para la interfaz
        try {
            iniciarInterfaz();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ejecutando = false;
        try {
            lector.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void leerVotos() {
        long ultimaPosicion = 0;
        RandomAccessFile archivo = null;
        
        try {
            // Crear archivo si no existe
            File file = new File(ARCHIVO_VOTOS);
            if (!file.exists()) {
                file.createNewFile();
            }
            
            archivo = new RandomAccessFile(ARCHIVO_VOTOS, "r");
            
            while (ejecutando) {
                long longitudActual = archivo.length();
                
                // Si el archivo se hizo mas pequeño, reiniciar
                if (longitudActual < ultimaPosicion) {
                    System.out.println("Archivo truncado, reiniciando lectura desde el inicio");
                    ultimaPosicion = 0;
                    archivo.seek(ultimaPosicion);
                }
                
                // Si hay nuevo contenido para leer
                if (longitudActual > ultimaPosicion) {
                    archivo.seek(ultimaPosicion);
                    String linea;
                    int votosNuevos = 0;
                    
                    while ((linea = archivo.readLine()) != null) {
                        procesarVoto(linea);
                        votosNuevos++;
                    }

                    ultimaPosicion = archivo.getFilePointer();
                }
                
                Thread.sleep(100);
        }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (archivo != null) {
                try {
                    archivo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void procesarVoto(String linea) {
        String[] partes = linea.split(",");
        if (partes.length != 2) return;
        
        String curp = partes[0];
        String partido = partes[1];
        
        // Actualizar contadores
        votosPartido.merge(partido, 1, Integer::sum);
        totalVotos++;
        
        // Extraer informacion del CURP
        char sexo = curp.charAt(10);
        String estado = curp.substring(11, 13);
        votosSexo.merge(String.valueOf(sexo), 1, Integer::sum);
        votosEstado.merge(estado, 1, Integer::sum);
        
        String añoStr = curp.substring(4, 6);
        int añoNacimiento = Integer.parseInt(añoStr);
        int edad = calcularEdad(añoNacimiento);
        
        votosEdad.computeIfAbsent(edad, k -> new ConcurrentHashMap<>())
                .merge(String.valueOf(sexo), 1, Integer::sum);
    }
    
    private static int calcularEdad(int añoCurp) {
        Calendar ahora = Calendar.getInstance();
        int añoActual = ahora.get(Calendar.YEAR) % 100;
        int añoCompleto = (añoCurp <= añoActual) ? 
            2000 + añoCurp : 1900 + añoCurp;
        return ahora.get(Calendar.YEAR) - añoCompleto;
    }
    
    private static void iniciarInterfaz() throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        TextGraphics tg = screen.newTextGraphics();
        
        screen.startScreen();
        
        while (ejecutando) {
            screen.clear();
            dibujarEstadisticasConBarras(tg);
            dibujarMenuInteractivo(tg);
            dibujarResultadosConsulta(tg);
            screen.refresh();
            procesarEntrada(terminal, screen);
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        screen.stopScreen();
    }
    
    private static void dibujarEstadisticasConBarras(TextGraphics tg) {
        int y = 0;
        tg.putString(0, y++, "=== ESTADÍSTICAS DE VOTACIÓN EN TIEMPO REAL ===");
        tg.putString(0, y++, "Número total de votos: " + String.format("%,d", totalVotos));
        y++;
        
        // Calcular el maximo para normalizar las barras
        int maxVotos = votosPartido.values().stream().max(Integer::compare).orElse(1);
        int anchoBarra = 50; // Ancho maximo de la barra
        
        tg.putString(0, y++, "Distribución por partido:");
        for (String partido : PARTIDOS) {
            int votos = votosPartido.getOrDefault(partido, 0);
            double porcentaje = totalVotos > 0 ? (votos * 100.0 / totalVotos) : 0;
            
            // Crear barra de progreso
            int longitudBarra = totalVotos > 0 ? (int)((votos * anchoBarra) / (double)maxVotos) : 0;
            String barra = generarBarraProgreso(longitudBarra, anchoBarra);
            
            String linea = String.format("%-8s %6d [%s] %5.1f%%", 
                partido, votos, barra, porcentaje);
            tg.putString(2, y++, linea);
        }
        y++;
    }
    
    private static String generarBarraProgreso(int longitud, int anchoTotal) {
        StringBuilder barra = new StringBuilder();
        for (int i = 0; i < anchoTotal; i++) {
            if (i < longitud) {
                barra.append('|');
            } else {
                barra.append(' ');
            }
        }
        return barra.toString();
    }
    
    private static void dibujarMenuInteractivo(TextGraphics tg) {
        int y = 15;
        tg.putString(0, y++, "=== CONSULTAS ===");
        tg.putString(0, y++, "1. Votos totales por sexo");
        tg.putString(0, y++, "2. Votos totales por estado");
        tg.putString(0, y++, "3. Votos por edad y sexo");
        
        if (esperandoEntrada) {
            tg.putString(0, y++, "Ingresa la edad y presiona Enter: " + entradaUsuario.toString());
        } else {
            tg.putString(0, y++, "Ingresa una opción (1-3) y presiona Enter: ");
        }
    }
    
    private static void dibujarResultadosConsulta(TextGraphics tg) {
        if (opcionSeleccionada == 0) return;
        
        int y = 22;
        tg.putString(0, y++, "=== RESULTADO DE CONSULTA ===");
        
        switch (opcionSeleccionada) {
            case 1:
                tg.putString(0, y++, "Votos por sexo:");
                tg.putString(2, y++, "Hombres: " + votosSexo.getOrDefault("H", 0));
                tg.putString(2, y++, "Mujeres: " + votosSexo.getOrDefault("M", 0));
                break;
                
            case 2:
                tg.putString(0, y++, "Votos por estado (top 10):");
                List<Map.Entry<String, Integer>> topEstados = votosEstado.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(10)
                    .collect(java.util.stream.Collectors.toList());

                for (Map.Entry<String, Integer> entry : topEstados) {
                    tg.putString(2, y++, entry.getKey() + ": " + entry.getValue());
                }
                break;
                
            case 3:
                if (!mensajeConsulta.isEmpty()) {
                    String[] lineas = mensajeConsulta.split("\n");
                    for (String linea : lineas) {
                        tg.putString(0, y++, linea);
                    }
                }
                break;
        }
    }
    
    private static void procesarEntrada(Terminal terminal, Screen screen) throws IOException {
        KeyStroke keyStroke = terminal.pollInput();
        if (keyStroke == null) return;
        
        if (esperandoEntrada) {
            // Modo de entrada para la edad
            if (keyStroke.getKeyType() == KeyType.Character) {
                char c = keyStroke.getCharacter();
                if (Character.isDigit(c) && entradaUsuario.length() < 3) {
                    entradaUsuario.append(c);
                }
            } else if (keyStroke.getKeyType() == KeyType.Backspace) {
                if (entradaUsuario.length() > 0) {
                    entradaUsuario.setLength(entradaUsuario.length() - 1);
                }
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                if (entradaUsuario.length() > 0) {
                    try {
                        int edad = Integer.parseInt(entradaUsuario.toString());
                        Map<String, Integer> votos = votosEdad.getOrDefault(edad, new HashMap<>());
                        int votosH = votos.getOrDefault("H", 0);
                        int votosM = votos.getOrDefault("M", 0);
                        
                        mensajeConsulta = String.format(
                            "Votos para edad %d:\nHombres: %d\nMujeres: %d\nTotal: %d",
                            edad, votosH, votosM, votosH + votosM
                        );
                        opcionSeleccionada = 3;
                    } catch (NumberFormatException e) {
                        mensajeConsulta = "Edad inválida";
                    }
                }
                esperandoEntrada = false;
                entradaUsuario.setLength(0);
            }
        } else {
            // Modo de seleccion de opción principal
            if (keyStroke.getKeyType() == KeyType.Character) {
                char c = keyStroke.getCharacter();
                if (c == '1') {
                    opcionSeleccionada = 1;
                } else if (c == '2') {
                    opcionSeleccionada = 2;
                } else if (c == '3') {
                    esperandoEntrada = true;
                    opcionSeleccionada = 3;
                    mensajeConsulta = "";
                }
            }
        }
    }
}