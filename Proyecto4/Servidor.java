/**
 * PROYECTO 4
 * Rodríguez Gutiérrez Miguel Francisco
 * 7CM4
 */

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Servidor {
    private int puerto;
    private String nombreServidor;
    
    public Servidor(int puerto, String nombreServidor) {
        this.puerto = puerto;
        this.nombreServidor = nombreServidor;
    }
    
    public void iniciar() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        
        server.createContext("/procesar", new ProcesarHandler());
        server.createContext("/estado", new EstadoHandler());
        server.createContext("/health", new HealthHandler());
        
        server.start();
        System.out.println(nombreServidor + " iniciado en puerto: " + puerto);
    }
    
    class ProcesarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                System.out.println(nombreServidor + " procesando solicitud...");
                
                // Simular procesamiento
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                String respuesta = "{\"estado\": \"completado\", \"servidor\": \"" + nombreServidor + "\"}";
                enviarRespuesta(exchange, respuesta);
            }
        }
    }
    
    class EstadoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String estado = String.format("{\"cpu\": %.2f, \"servidor\": \"%s\", \"estado\": \"activo\"}", 
                obtenerUsoCPU(), nombreServidor);
            enviarRespuesta(exchange, estado);
        }
    }
    
    class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String respuesta = "{\"status\": \"OK\", \"servidor\": \"" + nombreServidor + "\"}";
            enviarRespuesta(exchange, respuesta);
        }
    }
    
    private double obtenerUsoCPU() {
        // Simular uso de CPU entre 50% y 90%
        return 50 + (Math.random() * 40);
    }
    
    private void enviarRespuesta(HttpExchange exchange, String respuesta) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, respuesta.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(respuesta.getBytes());
        os.close();
    }
    
    public static void main(String[] args) throws IOException {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        String nombre = args.length > 1 ? args[1] : "Servidor-" + puerto;
        new Servidor(puerto, nombre).iniciar();
    }
}