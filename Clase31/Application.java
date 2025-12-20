import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import networking.WebClient;

public class Application {
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 1 ===");
        ejecutarEjercicio1();
        
        // Pequeña pausa entre ejercicios
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\n=== EJERCICIO 2 ===");
        ejecutarEjercicio2();
    }

    private static void ejecutarEjercicio1() {
        try {
            WebClient client = new WebClient();
            
            // Crear objeto Demo con fecha/hora actual
            Demo objetoAEnviar = new Demo(2024, Instant.now().toString());
            System.out.println("Objeto a enviar (cliente):");
            System.out.println("a = " + objetoAEnviar.a);
            System.out.println("b = " + objetoAEnviar.b);

            // Serializar objeto
            byte[] datosSerializados = SerializationUtils.serialize(objetoAEnviar);

            // Enviar al servidor
            String urlServidor = "http://localhost:8080/task";
            CompletableFuture<String> respuestaFuture = client.sendTask(urlServidor, datosSerializados);

            // Esperar y mostrar respuesta
            String respuesta = respuestaFuture.get();
            System.out.println("Respuesta del servidor: " + respuesta);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ejecutarEjercicio2() {
        try {
            WebClient client = new WebClient();
            
            // Crear objeto Demo con fecha/hora actual
            Demo objetoAEnviar = new Demo(3050, Instant.now().toString());
            System.out.println("Objeto a enviar (cliente):");
            System.out.println("a = " + objetoAEnviar.a);
            System.out.println("b = " + objetoAEnviar.b);

            // Serializar objeto
            byte[] datosSerializados = SerializationUtils.serialize(objetoAEnviar);

            // Enviar al servidor usando el metodo que maneja bytes
            String urlServidor = "http://localhost:8080/task";
            CompletableFuture<byte[]> respuestaFuture = client.sendTaskAndReceiveObject(urlServidor, datosSerializados);

            // Esperar y procesar respuesta
            byte[] respuestaBytes = respuestaFuture.get();
            
            // Verificar si la respuesta es un objeto serializado
            if (respuestaBytes != null && respuestaBytes.length > 0) {
                try {
                    Demo objetoRespuesta = (Demo) SerializationUtils.deserialize(respuestaBytes);
                    if (objetoRespuesta != null) {
                        System.out.println("Objeto recibido del servidor:");
                        System.out.println("a = " + objetoRespuesta.a);
                        System.out.println("b = " + objetoRespuesta.b);
                    } else {
                        System.out.println("Error: No se pudo deserializar el objeto del servidor");
                    }
                } catch (Exception e) {
                    // Si falla la deserializacion, mostrar como string
                    System.out.println("Respuesta del servidor (como texto): " + new String(respuestaBytes));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}