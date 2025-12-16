import java.util.*;


class CargaWorker implements Runnable {
    private int porcentaje;
    private long duracionMilis;
    private Random ran = new Random(); // Cada hilo tiene su propio generador

    /**
     * Constructor para pasar los parámetros al hilo.
     * @param porcentaje El porcentaje de CPU a simular (0-100).
     * @param duracionMilis El tiempo total que debe correr la simulación.
     */
    public CargaWorker(int porcentaje, long duracionMilis) {
        this.porcentaje = porcentaje;
        this.duracionMilis = duracionMilis;
    }

    @Override
    public void run() {
        long tiempoInicioPrograma = System.currentTimeMillis();

        // Definimos la "rebanada" de tiempo (100ms)
        final int SLICE_MS = 100;
        final long tiempoDeTrabajo = (SLICE_MS * porcentaje) / 100;
        final long tiempoDeDescanso = SLICE_MS - tiempoDeTrabajo;

        try {
            // Bucle principal: se ejecuta mientras no hayamos alcanzado el tiempo total
            while (System.currentTimeMillis() - tiempoInicioPrograma < duracionMilis) {

                // 1. FASE DE TRABAJO (Busy-Wait)
                // Usamos un bucle "while" para consumir CPU al 100%
                if (tiempoDeTrabajo > 0) {
                    long inicioTrabajo = System.currentTimeMillis();
                    while (System.currentTimeMillis() - inicioTrabajo < tiempoDeTrabajo) {
                        // Operación que consume CPU
                        Math.sqrt(ran.nextInt(Integer.MAX_VALUE));
                    }
                }

                // 2. FASE DE DESCANSO (Sleep)
                // El hilo se duerme, cediendo el CPU (uso 0%)
                if (tiempoDeDescanso > 0) {
                    Thread.sleep(tiempoDeDescanso);
                }
            }
        } catch (InterruptedException e) {
            // El hilo fue interrumpido, simplemente termina su ejecución.
            // System.err.println("Hilo worker interrumpido.");
        }
    }
}

/**
 * Clase principal que lee los argumentos y lanza 'n' hilos (Workers).
 */
public class SimulaCargaCpu {

    public static void main(String args[]) {
        // Validamos que tengamos los 3 argumentos
        if (args.length < 3) {
            System.out.println("Uso: java SimulaCargaCpu <porcentaje> <segundos> <numHilos>");
            return;
        }

        int porcentaje = Integer.valueOf(args[0]);
        int segundos = Integer.valueOf(args[1]);
        int numHilos = Integer.valueOf(args[2]); // Nuevo argumento
        long milisTotales = segundos * 1000;

        System.out.println("Simulando " + porcentaje + "% de uso en " + numHilos + " HILOS durante " + segundos + " segundos.");

        // Lista para mantener una referencia a nuestros hilos
        List<Thread> hilos = new ArrayList<>();

        // 1. Crear e Iniciar todos los hilos
        for (int i = 0; i < numHilos; i++) {
            Runnable worker = new CargaWorker(porcentaje, milisTotales);
            Thread hilo = new Thread(worker);
            hilo.start(); // Inicia el hilo (llama a su método run())
            hilos.add(hilo);
        }

        // 2. Esperar a que todos los hilos terminen
        try {
            for (Thread hilo : hilos) {
                hilo.join(); // El hilo principal (main) espera aquí a que 'hilo' termine
            }
        } catch (InterruptedException e) {
            System.err.println("Hilo principal interrumpido mientras esperaba: " + e.getMessage());
        }

    }
}