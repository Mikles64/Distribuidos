// Proyecto 4
// Rodríguez Gutiérrez Miguel Francisco
// 7CM4

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Cliente {
    private static String server1IP = "34.58.144.49";
    private static String server2IP = "136.115.52.35";
    private static int serverPort = 8080;
    private static Gson gson = new Gson();

    public static class PalindromeResult {
        private String phrase;
        private int fileId;
        private String fileName;
        
        public String getPhrase() { return phrase; }
        public int getFileId() { return fileId; }
        public String getFileName() { return fileName; }
        
        @Override
        public String toString() {
            return phrase + "-" + fileId;
        }
    }

    public static void main(String[] args) {
        try {
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

            BasicWindow window = new BasicWindow();
            window.setTitle("Buscador de Palíndromos - Proyecto 4");

            Panel mainPanel = new Panel();
            mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

            Label titleLabel = new Label("PALINDROMOS ENCONTRADOS Y TEXTO DONDE SE ENCUENTRAN:");

            TextBox resultsArea = new TextBox(new TerminalSize(100, 10), TextBox.Style.MULTI_LINE);
            resultsArea.setReadOnly(true);
            
            Label textsLabel = new Label("TEXTOS EN GCS:");
            
            TextBox filesArea = new TextBox(new TerminalSize(100, 6), TextBox.Style.MULTI_LINE);
            filesArea.setReadOnly(true);
            
            Button startButton = new Button("1.- Iniciar la búsqueda", new Runnable() {
                @Override
                public void run() {
                    resultsArea.setText("Buscando palíndromos...");
                    new Thread(() -> {
                        try {
                            String results = searchPalindromes();
                            resultsArea.setText(results);
                        } catch (Exception e) {
                            resultsArea.setText("Error: " + e.getMessage());
                        }
                    }).start();
                }
            });
            
            Button refreshFilesButton = new Button("Actualizar lista", new Runnable() {
                @Override
                public void run() {
                    refreshFilesList(filesArea);
                }
            });
            
            Button exitButton = new Button("2.- Salir", new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            });

            mainPanel.addComponent(titleLabel);
            mainPanel.addComponent(resultsArea);
            mainPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));
            mainPanel.addComponent(textsLabel);
            mainPanel.addComponent(filesArea);
            mainPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));
            mainPanel.addComponent(startButton);
            mainPanel.addComponent(refreshFilesButton);
            mainPanel.addComponent(exitButton);

            window.setComponent(mainPanel);
            
            refreshFilesList(filesArea);
            
            gui.addWindowAndWait(window);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void refreshFilesList(TextBox filesArea) {
        filesArea.setText("");
        try {
            List<Map<String, String>> files = getFilesList();
            for (Map<String, String> file : files) {
                filesArea.addLine(file.get("id") + " " + file.get("title"));
            }
        } catch (Exception e) {
            filesArea.setText("Error: " + e.getMessage());
        }
    }

    private static List<Map<String, String>> getFilesList() {
        try {
            String response = sendRequestToServer(server1IP, "files");
            return gson.fromJson(response, new TypeToken<List<Map<String, String>>>(){}.getType());
        } catch (Exception e) {
            try {
                String response = sendRequestToServer(server2IP, "files");
                return gson.fromJson(response, new TypeToken<List<Map<String, String>>>(){}.getType());
            } catch (Exception e2) {
                throw new RuntimeException("No se pudo obtener la lista");
            }
        }
    }

    private static String searchPalindromes() {
        StringBuilder results = new StringBuilder();
        
        try {
            String result1 = sendRequestToServer(server1IP, "process");
            String result2 = sendRequestToServer(server2IP, "process");
            
            Map<String, Map<Integer, List<PalindromeResult>>> combinedResults = 
                combineResults(result1, result2);
            
            results.append("UNA PALABRA: ").append(formatPalindromes(combinedResults, 1)).append("\n");
            results.append("DOS PALABRAS: ").append(formatPalindromes(combinedResults, 2)).append("\n");
            results.append("TRES PALABRAS: ").append(formatPalindromes(combinedResults, 3)).append("\n");
            results.append("CUATRO PALABRAS: ").append(formatPalindromes(combinedResults, 4)).append("\n");
            results.append("CINCO PALABRAS: ").append(formatPalindromes(combinedResults, 5)).append("\n");
            
        } catch (Exception e) {
            results.append("Error: ").append(e.getMessage());
        }
        
        return results.toString();
    }

    private static Map<String, Map<Integer, List<PalindromeResult>>> combineResults(
            String result1, String result2) {
        Map<String, Map<Integer, List<PalindromeResult>>> combined = new HashMap<>();
        
        try {
            Map<String, Map<Integer, List<PalindromeResult>>> parsed1 = 
                gson.fromJson(result1, new TypeToken<Map<String, Map<Integer, List<PalindromeResult>>>>(){}.getType());
            combined.putAll(parsed1);
        } catch (Exception e) {
            System.err.println("Error servidor 1: " + e.getMessage());
        }
        
        try {
            Map<String, Map<Integer, List<PalindromeResult>>> parsed2 = 
                gson.fromJson(result2, new TypeToken<Map<String, Map<Integer, List<PalindromeResult>>>>(){}.getType());
            for (String fileName : parsed2.keySet()) {
                if (!combined.containsKey(fileName)) {
                    combined.put(fileName, parsed2.get(fileName));
                } else {
                    Map<Integer, List<PalindromeResult>> existing = combined.get(fileName);
                    Map<Integer, List<PalindromeResult>> newResults = parsed2.get(fileName);
                    for (int wordCount : newResults.keySet()) {
                        if (!existing.containsKey(wordCount)) {
                            existing.put(wordCount, new ArrayList<>());
                        }
                        existing.get(wordCount).addAll(newResults.get(wordCount));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error servidor 2: " + e.getMessage());
        }
        
        return combined;
    }

    private static String formatPalindromes(
            Map<String, Map<Integer, List<PalindromeResult>>> results, 
            int wordCount) {
        
        Set<String> uniquePalindromes = new LinkedHashSet<>();
        
        for (Map<Integer, List<PalindromeResult>> fileResults : results.values()) {
            List<PalindromeResult> palindromes = fileResults.get(wordCount);
            if (palindromes != null) {
                for (PalindromeResult palindrome : palindromes) {
                    uniquePalindromes.add(palindrome.toString());
                }
            }
        }
        
        if (uniquePalindromes.isEmpty()) {
            return "";
        }
        
        return String.join(", ", uniquePalindromes);
    }

    private static String sendRequestToServer(String serverIP, String endpoint) {
        try {
            URL url = new URL("http://" + serverIP + ":" + serverPort + "/" + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();
                conn.disconnect();
                return response;
            } else {
                throw new IOException("HTTP error: " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error con " + serverIP + ": " + e.getMessage());
        }
    }
}