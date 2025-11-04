
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
                .uri(URI.create("https://translation.googleapis.com/language/translate/v2?target=es&key=AIzaSyDyFDn7L2OEP5xsQbdk80qXwQhCfNDi1D8&q=People%20have%20the%20right%20to%20disagree%20with%20your%20opinions%20and%20to%20dissent."))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                //.setHeader("Content-Type", "application/json")
                //.setHeader("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        /*// print response headers
        HttpHeaders headers = response.headers();
        headers.map().forEach((k, v) -> System.out.println(k + ":" + v));*/

        // print response body
        System.out.println(response.body());

    }

}
