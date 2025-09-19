// Runnable Interface Implementation
public class Geeks {
    //Variables static
    static int variable_compartida = 0;
    static long idHilo1, idHilo2;

    public static synchronized void modifica() {
        long idActual = Thread.currentThread().getId();
            if (idActual == idHilo1) {
                variable_compartida++;  //Para que el hilo 1 sume
            } else if (idActual == idHilo2) {
                variable_compartida--;  //Para que el hilo 2 reste
            }
    }

// Main Method
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);

        Runnable tarea1 = new Runnable() {
            // Overriding the run method
            @Override
            public void run() {
                for (int i = 0; i < n; i++) {
                    modifica();
                }
            }
        };

        Runnable tarea2 = new Runnable() {
            // Overriding the run method
            @Override
            public void run() {
                for (int i = 0; i < n; i++) {
                    modifica();
                }
            }
        };

        Thread hilo1 = new Thread(tarea1);
        Thread hilo2 = new Thread(tarea2);

        idHilo1 = hilo1.getId();
        idHilo2 = hilo2.getId();

        hilo1.start();
        hilo2.start();

        try {
            //.join() hace que un hilo no inicie hasta que otro termine
            hilo1.join();
            hilo2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Valor final de variable_compartida: " + variable_compartida);
    }
}