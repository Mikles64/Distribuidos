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

//Librerías necesarias para el servidor
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class Server8Threads {
    //Cadenas referentes a los endpoints del servidor
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";

    private final int port; //Puerto
    private HttpServer server;  //Implementación del servidor

    public static void main(String[] args) {
        int serverPort = 8081;  //Puerto por defecto
        if (args.length == 1) {
            //Puerto elejido por el usuario
            serverPort = Integer.parseInt(args[0]);
        }
        //Onjeto webserver, inicializa la configuración del servidor
        Server8Threads webServer = new Server8Threads(serverPort);
        webServer.startServer();

        System.out.println("Servidor escuchando en el puerto " + serverPort);
    }
    //Inicia el servidor en el puerto
    public Server8Threads(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            //Creamos una instancia de Socket TCP, con el tamaño de la cola de esoera
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //Crear contexto en la ruta asignada
        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(TASK_ENDPOINT);
        //Manejadores de los endpoints
        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);
        //Establece un objeto del tipo ejecutor, es necesario antes de iniciarlo
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }
    //Manejador del enpoint TASK
    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        //Obtenemos el método y verificamos que sea tipo POST
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        //Obtenemos todos los headers con un map
        Headers headers = exchange.getRequestHeaders();
        if (headers.containsKey("X-Test") && headers.get("X-Test").get(0).equalsIgnoreCase("true")) {
            String dummyResponse = "123\n";
            sendResponse(dummyResponse.getBytes(), exchange);
            return;
        }
        //Si el header tiene X-Debug, el modo depuración se activa
        boolean isDebugMode = false;
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true;
        }
        //Información de la depuración
        long startTime = System.nanoTime(); //Tiempo de inicio

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();//Info del cuerpo del mensaje
        byte[] responseBytes = calculateResponse(requestBytes);//Se multiplican los datos creadoso por requestBytes

        long finishTime = System.nanoTime();    //Se toma el tiempo de inicio y tiempo final
        // Si está en modo debug, devolvemos el tiempo de ejecución
        if (isDebugMode) {
            String debugMessage = String.format("La operación tomó %d nanosegundos", finishTime - startTime);
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage)); //Agregamos a headers
        }

        sendResponse(responseBytes, exchange);
    }

    //Multiplicación de dos numeros tipo Big Int
    private byte[] calculateResponse(byte[] requestBytes) {
        try {
            // Sleep de 5 segundos agregado aquí
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: La operación fue interrumpida".getBytes();
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
    //Buscamos su el tipo de petición es get, si es así, devolvemos que el servidor está vivo
    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }
    //Respuesta con headers y cuerpo de mensaje.
    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();//Se cierra el stream
        exchange.close();   
    }
}
