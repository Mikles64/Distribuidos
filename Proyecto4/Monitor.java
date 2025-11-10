/**
 * PROYECTO 4
 * Rodríguez Gutiérrez Miguel Francisco
 * 7CM4
 */

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Monitor {
    private MultiWindowTextGUI gui;
    private ProgressBar cpuServidor1;
    private ProgressBar cpuServidor2;
    private Label estadoServidor1;
    private Label estadoServidor2;
    
    public static void main(String[] args) throws IOException {
        new Monitor().iniciar();
    }
    
    public void iniciar() throws IOException {
        Screen screen = new TerminalScreen(new DefaultTerminalFactory().createTerminal());
        screen.startScreen();
        gui = new MultiWindowTextGUI(screen);
        
        mostrarMonitor();
    }
    
    private void mostrarMonitor() {
        BasicWindow window = new BasicWindow("Monitor del Sistema - SSH en el navegador");
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        
        // Uso de CPU
        panel.addComponent(new Label("USO DE CPU POR INSTANCIA:"));
        
        panel.addComponent(new Label("10.128.0.3 (Servidor 1 - Puerto 8080):"));
        cpuServidor1 = new ProgressBar(0, 100);
        estadoServidor1 = new Label("Cargando...");
        panel.addComponent(cpuServidor1);
        panel.addComponent(estadoServidor1);
        
        panel.addComponent(new Label("10.128.0.4 (Servidor 2 - Puerto 8081):"));
        cpuServidor2 = new ProgressBar(0, 100);
        estadoServidor2 = new Label("Cargando...");
        panel.addComponent(cpuServidor2);
        panel.addComponent(estadoServidor2);
        
        // Lista de textos en GCS
        panel.addComponent(new Label("TEXTOS EN GCS:"));
        ListBox<String> textosList = new ListBox<>();
        textosList.addItem("1 Frankenstein; Or, The Modern Prometheus by Mary Wollstonecraft Shelley");
        textosList.addItem("2 Moby Dick; Or, The Whale by Herman Melville");
        textosList.addItem("3 Romeo and Juliet by William Shakespeare");
        textosList.addItem("4 Pride and Prejudice by Jane Austen");
        textosList.addItem("5 Alice's Adventures in Wonderland by Lewis Carroll");
        textosList.addItem("6 The Strange Case of Dr. Jekyll and Mr. Hyde by Robert Louis Stevenson");
        textosList.addItem("7 The Complete Works of William Shakespeare by William Shakespeare");
        panel.addComponent(textosList);
        
        window.setComponent(panel);
        gui.addWindowAndWait(window);
        
        // Actualización periódica
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                actualizarDatos();
            }
        }, 0, 3000);
    }
    
    private void actualizarDatos() {
        // Simular actualización de datos
        double cpu1 = 50 + (Math.random() * 40);
        double cpu2 = 50 + (Math.random() * 40);
        
        cpuServidor1.setValue(cpu1);
        cpuServidor2.setValue(cpu2);
        
        estadoServidor1.setText(String.format("Uso de CPU: %.2f%%", cpu1));
        estadoServidor2.setText(String.format("Uso de CPU: %.2f%%", cpu2));
    }
}