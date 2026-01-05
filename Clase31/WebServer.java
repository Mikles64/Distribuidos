import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class WebServer {
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String SEARCHTOKEN_ENDPOINT = "/searchtoken";

    private final int port;
    private final int numberOfThreads;
    private HttpServer server;

    public static void main(String[] args) {
        int serverPort = 8080;
        int threads = 8;
        
        if (args.length >= 1) {
            serverPort = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            threads = Integer.parseInt(args[1]);
        }

        WebServer webServer = new WebServer(serverPort, threads);
        webServer.startServer();

        System.out.println("Servidor escuchando en el puerto " + serverPort + " con " + threads + " threads");
    }

    public WebServer(int port, int numberOfThreads) {
        this.port = port;
        this.numberOfThreads = numberOfThreads;
    }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(TASK_ENDPOINT);
        HttpContext searchTokenContext = server.createContext(SEARCHTOKEN_ENDPOINT);

        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);
        searchTokenContext.setHandler(this::handleSearchTokenRequest);

        server.setExecutor(Executors.newFixedThreadPool(numberOfThreads));
        server.start();
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

        Headers headers = exchange.getRequestHeaders();
        boolean isDebugMode = false;
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true;
        }

        long startTime = System.nanoTime();

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        
        // Deserializar el objeto Demo recibido
        Demo objetoRecibido = (Demo) SerializationUtils.deserialize(requestBytes);
        System.out.println("---- Objeto Demo recibido en /task ----");
        System.out.println("a = " + objetoRecibido.a);
        System.out.println("b = " + objetoRecibido.b);

        byte[] responseBytes;
        boolean esEjercicio2 = false;

        // Verificar si es el Ejercicio 2 por el valor del campo 'a'
        if (objetoRecibido.a == 3050) {
            esEjercicio2 = true;
            // Ejercicio 2: Crear y enviar objeto Demo de respuesta
            Demo objetoRespuesta = new Demo(objetoRecibido.a + 1000, Instant.now().toString());
            responseBytes = SerializationUtils.serialize(objetoRespuesta);
            System.out.println("---- Objeto Demo de respuesta creado ----");
            System.out.println("a = " + objetoRespuesta.a);
            System.out.println("b = " + objetoRespuesta.b);
        } else {
            // Ejercicio 1: Enviar mensaje de confirmacion simple
            String respuesta = "Mensaje recibido correctamente en el servidor";
            responseBytes = respuesta.getBytes();
        }

        long finishTime = System.nanoTime();

        if (isDebugMode) {
            long totalNanoseconds = finishTime - startTime;
            long totalMilliseconds = totalNanoseconds / 1_000_000;
            long seconds = totalMilliseconds / 1000;
            long remainingMilliseconds = totalMilliseconds % 1000;
            
            String debugMessage = String.format("La operacion tomo %d nanosegundos = %d segundos con %d milisegundos.",
                                               totalNanoseconds, seconds, remainingMilliseconds);
            
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
            System.out.println("-- X-Debug-Info: " + debugMessage);
        }

        sendResponse(responseBytes, exchange);
    }

    private void handleSearchTokenRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

        Headers headers = exchange.getRequestHeaders();
        
        boolean isDebugMode = false;
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true;
        }

        long startTime = System.nanoTime();

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        
        System.out.println("----solicitud POST recibida en /searchtoken ----");
        System.out.println("Tamaño del cuerpo del mensaje: " + requestBytes.length + " bytes");
        System.out.println("Contenido del cuerpo del mensaje: " + new String(requestBytes));
        
        System.out.println("-- Total de headers recibidos: " + headers.size());
        System.out.println("-- Headers recibidos (key - value):");
        headers.forEach((key, value) -> {
            System.out.println("--   " + key + " = " + value);
        });
        

        byte[] responseBytes = buscarTokensEnCadena(requestBytes);
        long finishTime = System.nanoTime();

        if (isDebugMode) {
            long totalNanoseconds = finishTime - startTime;
            long totalMilliseconds = totalNanoseconds / 1_000_000;
            long seconds = totalMilliseconds / 1000;
            long remainingMilliseconds = totalMilliseconds % 1000;
            
            String debugMessage = String.format("La operacion tomo %d nanosegundos = %d segundos con %d milisegundos.",
                                               totalNanoseconds, seconds, remainingMilliseconds);
            
            exchange.getResponseHeaders().put("X-Debug", Arrays.asList(debugMessage));
            System.out.println("-- X-Debug-Info: " + debugMessage);
        }

        sendResponse(responseBytes, exchange);
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }

    private byte[] calculateResponse(byte[] requestBytes) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String bodyString = new String(requestBytes);
        String[] stringNumbers = bodyString.split(",");

        BigInteger result = BigInteger.ONE;

        for (String number : stringNumbers) {
            BigInteger bigInteger = new BigInteger(number);
            result = result.multiply(bigInteger);
        }

        return String.format("El resultado de la multiplicación es %s\n", result).getBytes();
    }

    private byte[] buscarTokensEnCadena(byte[] requestBytes) {
        String bodyString = new String(requestBytes);
        String[] parametros = bodyString.split(",");

        if (parametros.length != 2) {
            return "Error: Se requieren exactamente 2 parametros separados por coma (numero_de_tokens,subcadena)\n".getBytes();
        }

        try {
            int numeroTokens = Integer.parseInt(parametros[0]);
            String subcadenaBuscar = parametros[1].toUpperCase();
            
            if (subcadenaBuscar.length() != 3) {
                return "Error: La subcadena debe tener exactamente 3 caracteres\n".getBytes();
            }

            String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            int randIndex;
            StringBuilder palabra = new StringBuilder();
            StringBuilder cadena = new StringBuilder();
            int contador = 0;

            for(int i = 0; i < numeroTokens; i++){
                for(int j = 0; j < 3; j++){
                    randIndex = (int) (Math.random() * 26);
                    palabra.append(charset.charAt(randIndex));
                }
                cadena.append(palabra);
                
                if(palabra.toString().equals(subcadenaBuscar)){
                    contador++;
                }
                palabra.delete(0, 3);
                cadena.append(" ");
            }

            String cadenaCompleta = cadena.toString();
            int posicion = cadenaCompleta.indexOf(subcadenaBuscar);
            StringBuilder posiciones = new StringBuilder();
            
            while (posicion != -1){
                posiciones.append("Subcadena encontrada en el indice ").append(posicion).append("\n");
                posicion = cadenaCompleta.indexOf(subcadenaBuscar, posicion + 1);
            }

            String resultado = String.format("Tokens generados: %d\nSubcadena buscada: %s\nNumero de veces encontrada: %d",
                                            numeroTokens, subcadenaBuscar, contador);
            
            return resultado.getBytes();
        } catch (NumberFormatException e) {
            return "Error: El primer parametro debe ser un numero entero valido\n".getBytes();
        }
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }
}