import java.util.Random;

public class PilaSincronizada {
    private char[] pila;
    private int tope;
    private int capacidad;
    private Random random;

    public PilaSincronizada(int capacidad) {
        this.capacidad = capacidad;
        this.pila = new char[capacidad];
        this.tope = -1;
        this.random = new Random();
    }

    public synchronized void push(char c) throws InterruptedException {
        while (tope >= capacidad - 1) {
            wait(); // Esperar si la pila está llena
        }
        tope++;
        pila[tope] = c;
        notifyAll(); // Notificar a todos los hilos
    }

    public synchronized char pop() throws InterruptedException {
        while (tope < 0) {
            wait(); // Esperar si la pila está vacía
        }
        char c = pila[tope];
        tope--;
        notifyAll(); // Notificar a todos los hilos
        return c;
    }

    public synchronized String obtenerContenido() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= tope; i++) {
            sb.append(pila[i]);
        }
        return sb.toString();
    }

    public synchronized int obtenerTope() {
        return tope;
    }

    public static void main(String[] args) {
        PilaSincronizada pila = new PilaSincronizada(10);

        // Hilo productor
        Thread productor = new Thread(() -> {
            try {
                while (true) {
                    int tp = new Random().nextInt(2000) + 500; // Tiempo aleatorio entre 500-2500ms
                    Thread.sleep(tp);
                    
                    char elemento = (char) ('A' + new Random().nextInt(26)); // Letra aleatoria
                    pila.push(elemento);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Hilo consumidor
        Thread consumidor = new Thread(() -> {
            try {
                while (true) {
                    int tc = new Random().nextInt(2000) + 500; // Tiempo aleatorio entre 500-2500ms
                    Thread.sleep(tc);
                    
                    pila.pop();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Hilo visualizador
        Thread visualizador = new Thread(() -> {
            try {
                int topeAnterior = -1;
                while (true) {
                    synchronized (pila) {
                        pila.wait(); // Esperar a que haya un cambio
                    }
                    
                    // Limpiar pantalla (funciona en la mayoría de terminales)
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    
                    String contenido = pila.obtenerContenido();
                    int topeActual = pila.obtenerTope();
                    
                    System.out.println(contenido);
                    System.out.println("Tope = " + (topeActual + 1));
                    System.out.println("Soy el graficador tope = " + (topeActual + 1) + 
                                      " tope_anterior = " + (topeAnterior + 1));
                    
                    topeAnterior = topeActual;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        productor.start();
        consumidor.start();
        visualizador.start();
    }
}