/*
*  MIT License
*
*  Copyright (c) 2019 Michael Pogrebinsky - Distributed Systems & Cloud Computing with Java
*
*  Permission is hereby granted, free of charge, to any person obtaining a copy
*  of this software and associated documentation files (the "Software"), to deal
*  in the Software without restriction, including without limitation the rights
*  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*  copies of the Software, and to permit persons to whom the Software is
*  furnished to do so, subject to the following conditions:
*
*  The above copyright notice and this permission notice shall be included in all
*  copies or substantial portions of the Software.
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
*  SOFTWARE.
*/
 
// importar librerias httpserver
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
 
// importar librerias java normales
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;
 
 
public class WebServer {
    private static final String TASK_ENDPOINT = "/task"; //se define el endpoint /task
    private static final String STATUS_ENDPOINT = "/status"; //se define el endpoint /status
    private static final String SEARCHTOKEN_ENDPOINT = "/searchtoken"; //se define el endpoint /searchtoken
 
    private final int port;
    private final int numberOfThreads;
    private HttpServer server; //se define el servidor http
 
    public static void main(String[] args) {
        int serverPort = 8080; // puerto por defecto
        int threads = 8; // número de threads por defecto
        
        if (args.length >= 1) {
            serverPort = Integer.parseInt(args[0]); //puerto por argumento
        }
        if (args.length >= 2) {
            threads = Integer.parseInt(args[1]); //número de threads por argumento
        }
 
        WebServer webServer = new WebServer(serverPort, threads);
        webServer.startServer(); // iniciar el servidor
 
        System.out.println("Servidor escuchando en el puerto " + serverPort + " con " + threads + " threads"); // imprime el puerto y threads
    }
 
    // se inicialza las variables privadas port y numberOfThreads
    public WebServer(int port, int numberOfThreads) {
        this.port = port;
        this.numberOfThreads = numberOfThreads;
    }
 
    public void startServer() { //metodo start server
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0); // crea una instancia de socket en el puerto
        } catch (IOException e) {
            e.printStackTrace(); // imprime el error
            return;
        }
 
        HttpContext statusContext = server.createContext(STATUS_ENDPOINT); // crea objeto contexto para el endpoint /status
        HttpContext taskContext = server.createContext(TASK_ENDPOINT); // crea objeto contexto para el endpoint /task
        HttpContext searchTokenContext = server.createContext(SEARCHTOKEN_ENDPOINT); // crea objeto contexto para el endpoint /searchtoken
 
        statusContext.setHandler(this::handleStatusCheckRequest); // recibe de parametro el metodo handleStatusCheckRequest
        taskContext.setHandler(this::handleTaskRequest); // recibe de parametro el metodo handleTaskRequest
        searchTokenContext.setHandler(this::handleSearchTokenRequest); // recibe de parametro el metodo handleSearchTokenRequest
 
        server.setExecutor(Executors.newFixedThreadPool(numberOfThreads)); // establecer un objeto executor con el pool de hilos especificado
        server.start(); // iniciar el servidor en un nuevo hilo
    }
 
    private void handleTaskRequest(HttpExchange exchange) throws IOException { //metodo handleTaskRequest
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) { // si el metodo no es post cierra la conexion
            exchange.close();
            return;
        }
 
        Headers headers = exchange.getRequestHeaders(); // obtiene los headers de la peticion del exchange con un map
        if (headers.containsKey("X-Test") && headers.get("X-Test").get(0).equalsIgnoreCase("true")) { // si el header contiene X-Test=true
            String dummyResponse = "123\n"; // respuesta 123
            sendResponse(dummyResponse.getBytes(), exchange); // envia la respuesta y cierra la conexion
            return;
        }
 
        boolean isDebugMode = false; // variable para modo debug
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true; // si el header contiene X-Debug=true activa el modo debug
        }
 
        //informacion de depuracion
        long startTime = System.nanoTime(); // tiempo de inicio en nanosegundos
 
        byte[] requestBytes = exchange.getRequestBody().readAllBytes(); // lee el cuerpo de la peticion y lo convierte en bytes
        
        // Imprimir información del cuerpo del mensaje HTTP
        System.out.println("----solicitud POST recibida en /task ----");
        System.out.println("Tamaño del cuerpo del mensaje: " + requestBytes.length + " bytes");
        System.out.println("Contenido del cuerpo del mensaje: " + new String(requestBytes));
        
        // Imprimir información de los headers HTTP
        System.out.println("-- Total de headers recibidos: " + headers.size());
        System.out.println("-- Headers recibidos (key - value):");
        headers.forEach((key, value) -> {
            System.out.println("--   " + key + " = " + value);
        });
        
 
        byte[] responseBytes = calculateResponse(requestBytes); // llama al metodo calculateResponse y obtiene la respuesta en bytes
        long finishTime = System.nanoTime(); // tiempo de finalizacion en nanosegundos
 
        if (isDebugMode) { // si el modo debug esta activo
            long totalNanoseconds = finishTime - startTime;
            long totalMilliseconds = totalNanoseconds / 1_000_000; // convertir nanosegundos a milisegundos
            long seconds = totalMilliseconds / 1000; // obtener segundos completos
            long remainingMilliseconds = totalMilliseconds % 1000; // obtener milisegundos restantes
            
            String debugMessage = String.format("La operacion tomo %d nanosegundos = %d segundos con %d milisegundos.",
                                               totalNanoseconds, seconds, remainingMilliseconds);
            
            exchange.getResponseHeaders().put("-- X-Debug-Info", Arrays.asList(debugMessage)); // agrega el mensaje de depuracion a los headers de la respuesta
            System.out.println("-- X-debug-info: " + debugMessage); // imprimir en el servidor también
        }
 
        sendResponse(responseBytes, exchange); // envia la respuesta y cierra la conexion, parametros: respuesta en bytes y el exchange
    }
 
    private void handleSearchTokenRequest(HttpExchange exchange) throws IOException { //metodo handleSearchTokenRequest
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) { // si el metodo no es post cierra la conexion
            exchange.close();
            return;
        }
 
        Headers headers = exchange.getRequestHeaders(); // obtiene los headers de la peticion del exchange con un map
        
        boolean isDebugMode = false; // variable para modo debug
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true; // si el header contiene X-Debug=true activa el modo debug
        }
 
        //informacion de depuracion
        long startTime = System.nanoTime(); // tiempo de inicio en nanosegundos
 
        byte[] requestBytes = exchange.getRequestBody().readAllBytes(); // lee el cuerpo de la peticion y lo convierte en bytes
        
        // Imprimir información del cuerpo del mensaje HTTP
        System.out.println("----solicitud POST recibida en /searchtoken ----");
        System.out.println("Tamaño del cuerpo del mensaje: " + requestBytes.length + " bytes");
        System.out.println("Contenido del cuerpo del mensaje: " + new String(requestBytes));
        
        // Imprimir información de los headers HTTP
        System.out.println("-- Total de headers recibidos: " + headers.size());
        System.out.println("-- Headers recibidos (key - value):");
        headers.forEach((key, value) -> {
            System.out.println("--   " + key + " = " + value);
        });
        
 
        byte[] responseBytes = buscarTokensEnCadena(requestBytes); // llama al metodo buscarTokensEnCadena y obtiene la respuesta en bytes
        long finishTime = System.nanoTime(); // tiempo de finalizacion en nanosegundos
 
        if (isDebugMode) { // si el modo debug esta activo
            long totalNanoseconds = finishTime - startTime;
            long totalMilliseconds = totalNanoseconds / 1_000_000; // convertir nanosegundos a milisegundos
            long seconds = totalMilliseconds / 1000; // obtener segundos completos
            long remainingMilliseconds = totalMilliseconds % 1000; // obtener milisegundos restantes
            
            String debugMessage = String.format("La operacion tomo %d nanosegundos = %d segundos con %d milisegundos.",
                                               totalNanoseconds, seconds, remainingMilliseconds);
            
            exchange.getResponseHeaders().put("X-Debug", Arrays.asList(debugMessage)); // agrega el mensaje de depuracion a los headers de la respuesta
            System.out.println("-- X-debug-info: " + debugMessage); // imprimir en el servidor también
        }
 
        sendResponse(responseBytes, exchange); // envia la respuesta y cierra la conexion, parametros: respuesta en bytes y el exchange
    }
 
    private byte[] calculateResponse(byte[] requestBytes) {  //metodo calculateResponse que recibe los bytes de la peticion
        try {
            Thread.sleep(5000); // sleep de 5 segundos para simular una tarea pesada
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restaurar el estado de interrupción
        }
        
        String bodyString = new String(requestBytes); // convierte los bytes en string
        String[] stringNumbers = bodyString.split(","); // divide el string en un array de strings usando la coma como separador
 
        BigInteger result = BigInteger.ONE; // variable para almacenar el resultado de la multiplicacion y se inicializa en 1
 
        for (String number : stringNumbers) { // para cada numero en el array de strings
            BigInteger bigInteger = new BigInteger(number); // convierte el string en un objeto BigInteger
            result = result.multiply(bigInteger); // multiplica el resultado por el numero actual
        }
 
        return String.format("El resultado de la multiplicación es %s\n", result).getBytes(); // devuelve el resultado de la multiplicacion en bytes
    }
 
    private byte[] buscarTokensEnCadena(byte[] requestBytes) {  //metodo buscarTokensEnCadena que recibe los bytes de la peticion
        String bodyString = new String(requestBytes); // convierte los bytes en string
        String[] parametros = bodyString.split(","); // divide el string en un array de strings usando la coma como separador
 
        if (parametros.length != 2) {
            return "Error: Se requieren exactamente 2 parametros separados por coma (numero_de_tokens,subcadena)\n".getBytes();
        }
 
        try {
            int numeroTokens = Integer.parseInt(parametros[0]); // numero de tokens a generar
            String subcadenaBuscar = parametros[1].toUpperCase(); // subcadena a buscar en mayusculas
            
            if (subcadenaBuscar.length() != 3) {
                return "Error: La subcadena debe tener exactamente 3 caracteres\n".getBytes();
            }
 
            String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //letras en mayusculas
            int randIndex; //indice al azar
            StringBuilder palabra = new StringBuilder(); //palabra de 3 letras
            StringBuilder cadena = new StringBuilder(); //cadena final
            int contador = 0; //conteo de veces que aparece la subcadena
 
            // Generar tokens aleatorios
            for(int i = 0; i < numeroTokens; i++){
                for(int j = 0; j < 3; j++){
                    randIndex = (int) (Math.random() * 26);
                    palabra.append(charset.charAt(randIndex));
                }
                cadena.append(palabra);
                
                //Contamos las veces que sale la subcadena buscada
                if(palabra.toString().equals(subcadenaBuscar)){
                    contador++;
                }
                palabra.delete(0, 3);
                cadena.append(" ");
            }
 
            // Buscar todas las ocurrencias de la subcadena en la cadena completa
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
 
    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException { //metodo handleStatusCheckRequest
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) { // si el metodo no es get cierra la conexion
            exchange.close();  // cierra la conexion
            return;
        }
 
        String responseMessage = "El servidor está vivo\n"; // mensaje de respuesta
        sendResponse(responseMessage.getBytes(), exchange); // envia la respuesta y cierra la conexion
    }
 
    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException { //metodo sendResponse que recibe los bytes de la respuesta y el exchange
        exchange.sendResponseHeaders(200, responseBytes.length); // envia los headers de la respuesta con codigo 200 y la longitud de la respuesta
        OutputStream outputStream = exchange.getResponseBody(); // obtiene el cuerpo de la respuesta
        outputStream.write(responseBytes); // escribe los bytes de la respuesta en el cuerpo de la respuesta
        outputStream.flush(); // vacia el buffer
        outputStream.close(); // cierra el flujo de salida
        exchange.close(); // cierra el exchange
    }
}