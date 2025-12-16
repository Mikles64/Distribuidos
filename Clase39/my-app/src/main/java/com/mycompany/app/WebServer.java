package com.mycompany.app;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import networking.Aggregator;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.io.InputStream;

import com.fasterxml.jackson.databind.DeserializationFeature;   
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebServer {
   
    private static final String STATUS_ENDPOINT = "/status";
    private static final String HOME_PAGE_ENDPOINT = "/";
    private static final String HOME_PAGE_UI_ASSETS_BASE_DIR = "/ui_assets/";
    private static final String FACTORIAL_ENDPOINT = "/factorial";

    private final int port; 
    private HttpServer server; 
    private final ObjectMapper objectMapper;
    private final Aggregator aggregator;
    
    // Direcciones de los servidores de procesamiento
    private final List<String> processingServers = List.of(
        "http://localhost:8081/task",
        "http://localhost:8082/task",
        "http://localhost:8083/task",
        "http://localhost:8084/task"
    );

    public WebServer(int port) {
        this.port = port;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.aggregator = new Aggregator();
    }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        HttpContext statusContext = server.createContext(STATUS_ENDPOINT); 
        HttpContext factorialContext = server.createContext(FACTORIAL_ENDPOINT);
        HttpContext homePageContext = server.createContext(HOME_PAGE_ENDPOINT);
        
        statusContext.setHandler(this::handleStatusCheckRequest);
        factorialContext.setHandler(this::handleFactorialRequest);
        homePageContext.setHandler(this::handleRequestForAsset);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        
        System.out.println("Servidor despachador iniciado en el puerto " + port);
    }

    private void handleRequestForAsset(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        byte[] response;

        String asset = exchange.getRequestURI().getPath(); 

        if (asset.equals(HOME_PAGE_ENDPOINT)) { 
            response = readUiAsset(HOME_PAGE_UI_ASSETS_BASE_DIR + "index.html");
        } else {
            response = readUiAsset(asset); 
        }
        addContentType(asset, exchange);
        sendResponse(response, exchange);
    }

    private byte[] readUiAsset(String asset) throws IOException {
        InputStream assetStream = getClass().getResourceAsStream(asset);

        if (assetStream == null) {
            return new byte[]{};
        }
        return assetStream.readAllBytes(); 
    }

    private static void addContentType(String asset, HttpExchange exchange) {
        String contentType = "text/html";  
        if (asset.endsWith("js")) {
            contentType = "text/javascript";
        } else if (asset.endsWith("css")) {
            contentType = "text/css";
        }
        exchange.getResponseHeaders().add("Content-Type", contentType);
    }

    private void handleFactorialRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) { 
            exchange.close();
            return;
        }

        try {
            // Leer el número del cuerpo de la petición
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            BigInteger n = new BigInteger(requestBody.trim());
            
            System.out.println("Calculando factorial de " + n);
            
            // Dividir el cálculo en tareas
            List<String> tasks = divideFactorialTasks(n);
            
            // Enviar tareas a los servidores de procesamiento
            List<String> partialResults = aggregator.sendTasksToWorkers(processingServers, tasks);
            
            // Combinar resultados parciales
            BigInteger finalResult = combineResults(partialResults);
            
            // Enviar respuesta
            String response = "Factorial de " + n + " es:\n" + finalResult.toString();
            sendResponse(response.getBytes(), exchange);

        } catch (Exception e) {
            e.printStackTrace();
            String errorResponse = "Error: " + e.getMessage();
            exchange.sendResponseHeaders(500, errorResponse.length());
            OutputStream os = exchange.getResponseBody();
            os.write(errorResponse.getBytes());
            os.close();
        }
    }

    private List<String> divideFactorialTasks(BigInteger n) {
        List<String> tasks = new ArrayList<>();
        int numServers = processingServers.size();
        
        // Dividir el rango [1, n] en partes iguales
        BigInteger[] division = n.divideAndRemainder(BigInteger.valueOf(numServers));
        BigInteger chunkSize = division[0];
        BigInteger remainder = division[1];
        
        BigInteger start = BigInteger.ONE;
        for (int i = 0; i < numServers; i++) {
            BigInteger end = start.add(chunkSize).subtract(BigInteger.ONE);
            
            // Añadir el resto al último chunk
            if (i == numServers - 1) {
                end = end.add(remainder);
            }
            
            String task = start + ":" + end;
            tasks.add(task);
            start = end.add(BigInteger.ONE);
        }
        
        return tasks;
    }

    private BigInteger combineResults(List<String> partialResults) {
        BigInteger result = BigInteger.ONE;
        for (String partial : partialResults) {
            if (partial != null && !partial.isEmpty()) {
                result = result.multiply(new BigInteger(partial));
            }
        }
        return result;
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "Servidor despachador está vivo\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
    }

    public static void main(String[] args) {
        int serverPort = 8080;
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }
        
        WebServer webServer = new WebServer(serverPort);
        webServer.startServer();
    }
}