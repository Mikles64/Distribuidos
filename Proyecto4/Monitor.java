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
import java.io.InputStream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Monitor {
    private static String server1IP = "34.58.144.49";
    private static String server2IP = "136.115.52.35";
    private static int serverPort = 8080;
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        try {
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

            BasicWindow window = new BasicWindow();
            window.setTitle("Monitor del Sistema - Proyecto 4");

            Panel mainPanel = new Panel();
            mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

            Label headerLabel = new Label("SSH en el navegador    SUBIR ARCHIVO");

            Label cpuTitleLabel = new Label("USO DE CPU POR INSTANCIA:");
            
            Label server1CpuLabel = new Label("");
            Label server2CpuLabel = new Label("");

            Label filesTitleLabel = new Label("TEXTOS EN GCS:");
            
            TextBox filesArea = new TextBox(new TerminalSize(120, 8), TextBox.Style.MULTI_LINE);
            filesArea.setReadOnly(true);

            mainPanel.addComponent(headerLabel);
            mainPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));
            mainPanel.addComponent(cpuTitleLabel);
            mainPanel.addComponent(server1CpuLabel);
            mainPanel.addComponent(server2CpuLabel);
            mainPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));
            mainPanel.addComponent(filesTitleLabel);
            mainPanel.addComponent(filesArea);

            window.setComponent(mainPanel);

            Thread monitorThread = new Thread(() -> {
                while (true) {
                    try {
                        updateCpuUsage(server1CpuLabel, server2CpuLabel);
                        updateFilesList(filesArea);
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        System.err.println("Error en monitor: " + e.getMessage());
                    }
                }
            });
            monitorThread.setDaemon(true);
            monitorThread.start();

            gui.addWindowAndWait(window);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void updateCpuUsage(Label server1CpuLabel, Label server2CpuLabel) {
        try {
            double cpu1 = getCpuUsage(server1IP);
            double cpu2 = getCpuUsage(server2IP);

            String bar1 = generateProgressBar(cpu1);
            String bar2 = generateProgressBar(cpu2);

            server1CpuLabel.setText(server1IP + " [" + bar1 + " " + String.format("%.2f", cpu1) + "%]");
            server2CpuLabel.setText(server2IP + " [" + bar2 + " " + String.format("%.2f", cpu2) + "%]");

        } catch (Exception e) {
            server1CpuLabel.setText(server1IP + " [ERROR]");
            server2CpuLabel.setText(server2IP + " [ERROR]");
        }
    }

    private static double getCpuUsage(String serverIP) {
        try {
            String response = sendRequestToServer(serverIP, "status");
            Map<String, Object> status = gson.fromJson(response, new TypeToken<Map<String, Object>>(){}.getType());
            Object cpuUsageObj = status.get("cpuUsage");
            if (cpuUsageObj instanceof Number) {
                return ((Number) cpuUsageObj).doubleValue();
            } else if (cpuUsageObj instanceof String) {
                return Double.parseDouble((String) cpuUsageObj);
            } else {
                return 0.0;
            }
        } catch (Exception e) {
            System.err.println("Error CPU " + serverIP + ": " + e.getMessage());
            return 0.0;
        }
    }

    private static String generateProgressBar(double percentage) {
        int bars = (int) (percentage / 2);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            if (i < bars) {
                sb.append("█");
            } else {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private static void updateFilesList(TextBox filesArea) {
        try {
            List<Map<String, String>> files = getFilesList();
            filesArea.setText("");
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
                throw new RuntimeException("No se pudo obtener lista");
            }
        }
    }

    private static String sendRequestToServer(String serverIP, String endpoint) {
        try {
            URL url = new URL("http://" + serverIP + ":" + serverPort + "/" + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
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