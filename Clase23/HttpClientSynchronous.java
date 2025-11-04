
//package com.mkyong.java11.jep321;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.google.gson.*;

public class HttpClientSynchronous {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) throws IOException, InterruptedException {
        
        // Obtener 3 citas
        HttpRequest citasRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.breakingbadquotes.xyz/v1/quotes/3"))
                .build();

        HttpResponse<String> citasResponse = httpClient.send(citasRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(citasResponse.body());
        System.out.println("\n\n");

        // Convertir la respuesta a JsonArray
        JsonArray citasArray = JsonParser.parseString(citasResponse.body()).getAsJsonArray();

        // Procesar cada cita
        for (int i = 0; i < citasArray.size(); i++) {
            JsonObject citaObject = citasArray.get(i).getAsJsonObject();
            String citaOriginal = citaObject.get("quote").getAsString();
            String author = citaObject.get("author").getAsString();


            // Traducir la cita al español
            String citaTraducida = translateText(citaOriginal);
            
            // Mostrar resultados
            System.out.println("Autor: " + author);
            System.out.println("Cita: " + citaOriginal);
            System.out.println("Traducción: " + citaTraducida);
            System.out.println();
        }
    }

    private static String translateText(String text) throws IOException, InterruptedException {
        // Codificar el texto para URL
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        
        // Crear la petición a Google Translate API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://translation.googleapis.com/language/translate/v2?target=es&key=AIzaSyDyFDn7L2OEP5xsQbdk80qXwQhCfNDi1D8&q=" + encodedText))
                .setHeader("User-Agent", "Java 17 HttpClient Bot")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Extraer el texto traducido de la respuesta JSON
        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject data = responseJson.getAsJsonObject("data");
        JsonArray translations = data.getAsJsonArray("translations");
        String translatedText = translations.get(0).getAsJsonObject().get("translatedText").getAsString();

        return translatedText;
    }
}
