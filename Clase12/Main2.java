import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

class Main2 {
    // Objeto para sincronizar la impresion y evitar mezcla de salidas
    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        if (args.length < 2) {
            System.out.println("Uso: java Main2 <n> <m>");
            return;
        }

        int n = Integer.parseInt(args[0]); // CURPs por lista
        int m = Integer.parseInt(args[1]); // numero de listas

        // Crear un ArrayList de ArrayList<String> para almacenar las listas de CURPs
        ArrayList<ArrayList<String>> arregloListas = new ArrayList<>();

        // Generar listas con CURPs aleatorias
        for (int i = 0; i < m; i++) {
            ArrayList<String> lista = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                lista.add(getCURP());
            }
            arregloListas.add(lista);
        }

        // Imprimir el contenido de todas las listas generadas
        System.out.println("Contenido de todas las Listas:");
        for (int i = 0; i < arregloListas.size(); i++) {
            System.out.println("Lista " + (i + 1) + ": " + arregloListas.get(i));
        }

        // Crear un ThreadPool con 2 hilos
        ExecutorService pool = Executors.newFixedThreadPool(2);

        // Enviar las tareas de ordenamiento al ThreadPool
        ArrayList<Future<Void>> futures = new ArrayList<>();
        for (int i = 0; i < arregloListas.size(); i++) {
            final int index = i;
            Future<Void> future = pool.submit(() -> {
                ordenarYMostrar(arregloListas.get(index), index + 1);
                return null;
            });
            futures.add(future);
        }

        // Esperar que todas las tareas terminen
        for (Future<Void> future : futures) {
            future.get();
        }

        // Apagar el ThreadPool
        pool.shutdown();
    }

    // Metodo para ordenar una lista de CURPs y mostrarla
    static void ordenarYMostrar(ArrayList<String> lista, int numLista) {
        // Obtener informacion del hilo actual
        String threadInfo = Thread.currentThread().getName();
        
        // Sincronizar para evitar mezcla de salidas
        synchronized (lock) {
            System.out.println("Ordenando arrayList en hilo: " + threadInfo);
            
            ArrayList<String> listaOrdenada = new ArrayList<>(lista);
            Collections.sort(listaOrdenada);
            
            System.out.println(listaOrdenada);
        }
    }

    // Metodo que genera una CURP aleatoria (corregido)
    static String getCURP() {
        String Letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Numero = "0123456789";
        String Sexo = "HM";
        String Entidad[] = {"AS","BC","BS","CC","CS","CH","CL","CM","DF","DG","GT","GR","HG",
                            "JC","MC","MN","MS","NT","NL","OC","PL","QT","QR","SP","SL","SR",
                            "TC","TL","TS","VZ","YN","ZS"};
        int indice;

        StringBuilder sb = new StringBuilder(18);

        // Primeras 4 letras
        for (int i = 0; i < 4; i++) {
            indice = (int) (Letras.length() * Math.random());
            sb.append(Letras.charAt(indice));
        }

        // 6 numeros (años, meses, dias)
        for (int i = 0; i < 6; i++) {
            indice = (int) (Numero.length() * Math.random());
            sb.append(Numero.charAt(indice));
        }

        // Sexo
        indice = (int) (Sexo.length() * Math.random());
        sb.append(Sexo.charAt(indice));

        // Entidad federativa
        sb.append(Entidad[(int) (Math.random() * Entidad.length)]);

        // 3 letras
        for (int i = 0; i < 3; i++) {
            indice = (int) (Letras.length() * Math.random());
            sb.append(Letras.charAt(indice));
        }

        // 2 numeros
        for (int i = 0; i < 2; i++) {
            indice = (int) (Numero.length() * Math.random());
            sb.append(Numero.charAt(indice));
        }

        return sb.toString();
    }
}