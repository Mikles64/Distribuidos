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

public class Cliente {
    private MultiWindowTextGUI gui;
    
    public static void main(String[] args) throws IOException {
        new Cliente().iniciar();
    }
    
    public void iniciar() throws IOException {
        Screen screen = new TerminalScreen(new DefaultTerminalFactory().createTerminal());
        screen.startScreen();
        gui = new MultiWindowTextGUI(screen);
        
        mostrarMenuPrincipal();
    }
    
    private void mostrarMenuPrincipal() {
        BasicWindow window = new BasicWindow("Buscador de Palíndromos");
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        
        // Área de resultados
        panel.addComponent(new Label("PALINDROMOS ENCONTRADOS Y TEXTO DONDE SE ENCUENTRAN:"));
        TextBox resultadosArea = new TextBox(new TerminalSize(80, 10));
        resultadosArea.setReadOnly(true);
        panel.addComponent(resultadosArea);
        
        // Lista de textos
        panel.addComponent(new Label("TEXTOS EN GCS:"));
        ListBox<String> textosList = new ListBox<>();
        textosList.addItem("1 Frankenstein; Or, The Modern Prometheus by Mary Wollstonecraft Shelley");
        textosList.addItem("2 Moby Dick; Or, The Whale by Herman Melville");
        textosList.addItem("3 Romeo and Juliet by William Shakespeare");
        textosList.addItem("4 Pride and Prejudice by Jane Austen");
        textosList.addItem("5 Alice's Adventures in Wonderland by Lewis Carroll");
        textosList.addItem("6 The Strange Case of Dr. Jekyll and Mr. Hyde by Robert Louis Stevenson");
        panel.addComponent(textosList);
        
        // Menú
        panel.addComponent(new Separator(Direction.HORIZONTAL));
        panel.addComponent(new Button("1.- Iniciar la búsqueda", () -> iniciarBusqueda(resultadosArea)));
        panel.addComponent(new Button("2.- Salir del programa", () -> System.exit(0)));
        
        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }
    
    private void iniciarBusqueda(TextBox resultadosArea) {
        new Thread(() -> {
            // Simulación de búsqueda
            resultadosArea.setText("Buscando palíndromos...\n");
            
            try {
                Thread.sleep(2000); // Simular procesamiento
                
                String resultados = 
                    "UNA PALABRA: level-3, madam-3, racecar-6, civic-4, deified-2, refer-1\n" +
                    "DOS PALABRAS: too hot to hoot-6, live evil-3, step on no pets-5\n" +
                    "TRES PALABRAS: dog sees god-6, top spot pot-1, evil olive-2\n" +
                    "CUATRO PALABRAS: was it a car or a cat I saw-2, never odd or even-5, no devil lived on-1\n" +
                    "CINCO PALABRAS:";
                
                resultadosArea.setText(resultados);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}