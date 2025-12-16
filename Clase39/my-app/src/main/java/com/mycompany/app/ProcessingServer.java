package com.mycompany.app;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ProcessingServer {
   
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";

    private final int port; 
    private HttpServer server; 

    public ProcessingServer(int port) {
        this.port = port;
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
        
        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        
        System.out.println("Servidor de procesamiento iniciado en el puerto " + port);
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) { 
            exchange.close();
            return;
        }

        try {
            // Leer el rango del cuerpo de la petición (formato: "inicio:fin")
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            String[] range = requestBody.trim().split(":");
            
            if (range.length != 2) {
                throw new IllegalArgumentException("Formato inválido. Use: inicio:fin");
            }
            
            BigInteger start = new BigInteger(range[0]);
            BigInteger end = new BigInteger(range[1]);
            
            System.out.println("Procesando rango: " + start + " a " + end);
            
            // Calcular producto del rango
            BigInteger result = calculateProduct(start, end);
            
            // Enviar respuesta
            sendResponse(result.toString().getBytes(), exchange);

        } catch (Exception e) {
            e.printStackTrace();
            String errorResponse = "Error: " + e.getMessage();
            exchange.sendResponseHeaders(500, errorResponse.length());
            OutputStream os = exchange.getResponseBody();
            os.write(errorResponse.getBytes());
            os.close();
        }
    }

    private BigInteger calculateProduct(BigInteger start, BigInteger end) {
        BigInteger product = BigInteger.ONE;
        BigInteger current = start;
        
        while (current.compareTo(end) <= 0) {
            product = product.multiply(current);
            current = current.add(BigInteger.ONE);
        }
        
        return product;
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "Servidor de procesamiento en puerto " + port + " está vivo\n";
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
        if (args.length != 1) {
            System.err.println("Uso: java ProcessingServer <puerto>");
            System.exit(1);
        }
        
        int serverPort = Integer.parseInt(args[0]);
        ProcessingServer server = new ProcessingServer(serverPort);
        server.startServer();
    }
}