// Proyecto 4
// Rodríguez Gutiérrez Miguel Francisco
// 7CM4

import com.google.cloud.storage.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import com.google.gson.Gson;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class Servidor {
    private Storage storage;
    private String bucketName = "bucket-textos";
    private static final int PORT = 8080;
    private Gson gson = new Gson();
    private OperatingSystemMXBean osBean;
    
    // Mapeo de archivos a titulos reales
    private Map<String, String> fileTitles = new HashMap<>();
    
    // Clase interna para resultados de palindromos
    public static class PalindromeResult {
        private String phrase;
        private int fileId;
        private String fileName;
        
        public PalindromeResult(String phrase, int fileId, String fileName) {
            this.phrase = phrase;
            this.fileId = fileId;
            this.fileName = fileName;
        }
        
        public String getPhrase() { return phrase; }
        public int getFileId() { return fileId; }
        public String getFileName() { return fileName; }
        
        @Override
        public String toString() {
            return phrase + "-" + fileId;
        }
    }
    
    // Clase interna para el buscador de palindromos
    public static class PalindromeFinder {
        
        public static String normalizeText(String text) {
            if (text == null) return "";
            // Eliminar puntuación, números y convertir a minúsculas, mantener solo letras y espacios
            String normalized = text.replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
            // Reemplazar múltiples espacios por uno solo y saltos de linea por espacios
            normalized = normalized.replaceAll("\\s+", " ").trim();
            return normalized;
        }
        
        public static boolean isPalindrome(String text) {
            if (text == null || text.isEmpty()) return false;
            
            // Eliminar todos los espacios y caracteres no alfabeticos para la comparación
            String cleanText = text.replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (cleanText.isEmpty()) return false;
            
            int left = 0;
            int right = cleanText.length() - 1;
            
            while (left < right) {
                if (cleanText.charAt(left) != cleanText.charAt(right)) {
                    return false;
                }
                left++;
                right--;
            }
            return true;
        }
        
        public static Map<Integer, List<PalindromeResult>> findPalindromesInText(String text, String fileName) {
            Map<Integer, List<PalindromeResult>> results = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                results.put(i, new ArrayList<>());
            }
            
            if (text == null || text.isEmpty()) return results;
            
            String normalizedText = normalizeText(text);
            String[] words = normalizedText.split("\\s+");
            
            // Buscar palindromos de 1 a 5 palabras consecutivas
            for (int wordCount = 1; wordCount <= 5; wordCount++) {
                for (int i = 0; i <= words.length - wordCount; i++) {
                    StringBuilder phraseBuilder = new StringBuilder();
                    for (int j = 0; j < wordCount; j++) {
                        if (j > 0) phraseBuilder.append(" ");
                        phraseBuilder.append(words[i + j]);
                    }
                    String phrase = phraseBuilder.toString();
                    
                    if (isPalindrome(phrase)) {
                        int fileId = extractFileId(fileName);
                        results.get(wordCount).add(new PalindromeResult(phrase, fileId, fileName));
                    }
                }
            }
            
            return results;
        }
        
        private static int extractFileId(String fileName) {
            try {
                // Extraer numero del formato
                if (fileName.startsWith("pg") && fileName.endsWith(".txt")) {
                    String number = fileName.substring(2, fileName.length() - 4);
                    return Integer.parseInt(number) % 10; // Para tener IDs pequeños (1-6)
                }
            } catch (Exception e) {
                return Math.abs(fileName.hashCode()) % 10 + 1;
            }
            return 1;
        }
    }

    public Servidor() {
        initializeFileTitles();
        try {
            this.osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        } catch (Exception e) {
            System.err.println("No se pudo obtener OperatingSystemMXBean");
        }
    }

    public static void main(String[] args) throws IOException {
        Servidor server = new Servidor();
        server.start();
    }
    
    private void start() throws IOException {
        initializeGCS();
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/process", new ProcessHandler());
        server.createContext("/status", new StatusHandler());
        server.createContext("/files", new FilesHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        
        System.out.println("Servidor de procesamiento iniciado en puerto " + PORT);
        System.out.println("Bucket configurado: " + bucketName);
    }

    private void initializeGCS() {
        try {
            storage = StorageOptions.getDefaultInstance().getService();
            System.out.println("Conexión a GCS establecida exitosamente");
        } catch (Exception e) {
            System.err.println("Error inicializando GCS: " + e.getMessage());
            throw new RuntimeException("No se pudo inicializar GCS", e);
        }
    }
    
    private void initializeFileTitles() {
        fileTitles.put("pg77170.txt", "The adventures of Harlequin by Francis Bickley");
        fileTitles.put("pg77171.txt", "A diary of the wreck of His Majesty's ship Challenger, on the western coast of South America, in May, 1835 with an account of the subsequent encampment of the officers and crew, during a period of seven weeks, on the south coast of Chili by Anonymous");
    }

    class ProcessHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                System.out.println("Recibida solicitud de procesamiento de palíndromos");
                
                try {
                    List<String> fileNames = listFilesInBucket();
                    Map<String, Map<Integer, List<PalindromeResult>>> allResults = new HashMap<>();
                    
                    for (String fileName : fileNames) {
                        System.out.println("Procesando archivo: " + fileName);
                        String content = downloadFile(fileName);
                        
                        if (!content.isEmpty()) {
                            Map<Integer, List<PalindromeResult>> fileResults = 
                                PalindromeFinder.findPalindromesInText(content, fileName);
                            allResults.put(fileName, fileResults);
                            System.out.println("Encontrados palíndromos en " + fileName + ": " + 
                                countTotalPalindromes(fileResults));
                        }
                    }
                    
                    String response = gson.toJson(allResults);
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(responseBytes);
                    os.close();
                    
                    System.out.println("Procesamiento completado para " + fileNames.size() + " archivos");
                    
                } catch (Exception e) {
                    System.err.println("Error en procesamiento: " + e.getMessage());
                    e.printStackTrace();
                    String errorResponse = "{\"error\": \"Error en el procesamiento: " + e.getMessage() + "\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    byte[] errorBytes = errorResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(500, errorBytes.length);
                    exchange.getResponseBody().write(errorBytes);
                    exchange.getResponseBody().close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Metodo no permitido
            }
        }
        
        private int countTotalPalindromes(Map<Integer, List<PalindromeResult>> results) {
            int total = 0;
            for (List<PalindromeResult> list : results.values()) {
                total += list.size();
            }
            return total;
        }
        
        private List<String> listFilesInBucket() {
            List<String> fileNames = new ArrayList<>();
            try {
                Page<Blob> blobs = storage.list(bucketName);
                for (Blob blob : blobs.iterateAll()) {
                    if (blob.getName().endsWith(".txt")) {
                        fileNames.add(blob.getName());
                    }
                }
                System.out.println("Encontrados " + fileNames.size() + " archivos en el bucket");
            } catch (Exception e) {
                System.err.println("Error listando archivos en el bucket: " + e.getMessage());
                throw new RuntimeException("Error accediendo al bucket", e);
            }
            return fileNames;
        }
        
        private String downloadFile(String fileName) {
            try {
                Blob blob = storage.get(bucketName, fileName);
                if (blob != null && blob.exists()) {
                    byte[] content = blob.getContent();
                    String textContent = new String(content, StandardCharsets.UTF_8);
                    System.out.println("Descargado archivo: " + fileName + " (" + textContent.length() + " caracteres)");
                    return textContent;
                } else {
                    System.err.println("Archivo no encontrado en GCS: " + fileName);
                    return "";
                }
            } catch (Exception e) {
                System.err.println("Error descargando archivo " + fileName + ": " + e.getMessage());
                throw new RuntimeException("Error descargando archivo", e);
            }
        }
    }

    class FilesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    List<Map<String, String>> filesInfo = new ArrayList<>();
                    
                    List<String> fileNames = listFilesInBucket();
                    for (String fileName : fileNames) {
                        Map<String, String> fileInfo = new HashMap<>();
                        fileInfo.put("id", String.valueOf(PalindromeFinder.extractFileId(fileName)));
                        fileInfo.put("fileName", fileName);
                        fileInfo.put("title", fileTitles.getOrDefault(fileName, "Unknown Title - " + fileName));
                        filesInfo.add(fileInfo);
                    }
                    
                    String response = gson.toJson(filesInfo);
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(responseBytes);
                    os.close();
                    
                } catch (Exception e) {
                    System.err.println("Error obteniendo lista de archivos: " + e.getMessage());
                    String errorResponse = "{\"error\": \"Error obteniendo archivos\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    byte[] errorBytes = errorResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(500, errorBytes.length);
                    exchange.getResponseBody().write(errorBytes);
                    exchange.getResponseBody().close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
        
        private List<String> listFilesInBucket() {
            List<String> fileNames = new ArrayList<>();
            try {
                Page<Blob> blobs = storage.list(bucketName);
                for (Blob blob : blobs.iterateAll()) {
                    if (blob.getName().endsWith(".txt")) {
                        fileNames.add(blob.getName());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error listando archivos: " + e.getMessage());
                throw new RuntimeException("Error listando archivos del bucket", e);
            }
            return fileNames;
        }
    }

    class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    double cpuUsage = getRealCpuUsage();
                    
                    String response = String.format("{\"cpuUsage\": %.2f}", cpuUsage);
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(responseBytes);
                    os.close();
                    
                } catch (Exception e) {
                    System.err.println("Error obteniendo estado: " + e.getMessage());
                    String errorResponse = "{\"error\": \"Error obteniendo estado del sistema\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    byte[] errorBytes = errorResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(500, errorBytes.length);
                    exchange.getResponseBody().write(errorBytes);
                    exchange.getResponseBody().close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
        
        private double getRealCpuUsage() {
            try {
                if (osBean != null) {
                    return osBean.getSystemCpuLoad() * 100.0;
                }
            } catch (Exception e) {
                System.err.println("Error obteniendo uso real de CPU: " + e.getMessage());
            }
            
            // Fallback: uso simulado basado en actividad del sistema
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            return (double) usedMemory / totalMemory * 100.0;
        }
    }
}