//package com.mkyong.java11.jep321;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClientSynchronous {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("17576000,IPN")) // Enviar parametros
                .uri(URI.create("http://numbersapi.com"))   // Endpoint searchtoken
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .setHeader("Content-Type", "text/plain;charset=UTF-8") // content type
                .setHeader("X-Debug", "true") // Habilitar debug para el tiempo
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print response headers
        HttpHeaders headers = response.headers();
        headers.map().forEach((k, v) -> System.out.println(k + ": " + v));

        // print status code
        System.out.println(response.statusCode());

        // print response body
        System.out.println(response.body());

         // Tiempo total del servidor 
        System.out.println("\nTiempo en procesar peticion");
        if (headers.firstValue("X-Debug").isPresent()) {
            String debugInfo = headers.firstValue("X-Debug").get();
            System.out.println(debugInfo);
        }
    }
}