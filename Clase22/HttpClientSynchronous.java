
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
                .uri(URI.create("https://storage.googleapis.com/storage/v1/b/bucket-mike/o/WebServer.jar"))
                .setHeader("User-Agent", "Java 17 HttpClient Bot") // add request header
                .setHeader("Content-Type", "application/json")
                .setHeader("Accept", "application/json")
                // Header con el token
                .setHeader("Authorization", "Bearer ya29.c.c0ASRK0GY9flGfmuLssm6_Lv53LcdrUhCEr3imDER7QR1b8FvO2qImcNwHsMnla1HHd2p3cH0xyZiuByYsv9nbWfPyIQ9PG4g1pG3w7FTSGMeRa83GaGVB8yCrACE-KR9rsvFhb5ikauGh8MWbG_HPRUPFmpIiYKpwSLO_SZBNk-u_26d94IU-HTcyjtOKvetH8zPPfPE1-vhedeUTRBwqXZoBTG06jcmqBMH_37IArfoXJ1fpNAFm_Pwe75Va_c5A4hZhs_pmB_tuU39ERmPA4DQZC1oPjv96WGuUcg4Ux1BEAm83gib17uFCAaLtA78C2CheyyYWi2o3JGM5G3ilyuFZ-J4G2KtRDUEFbvpkBwf7fzf6497rzW-sac1yzswQsP5QT397AnS0Vz9vvrI-_4xla9fpqpbwvoQbXOVM9xSaFcV3uQslpYohg5427pukJt7tJk60w2vs6ZpVe2qO9XQIB2bFiIV6aa9ijr5n0azj54l9g911wBtys8syFuw7kmIcZ4fof0X6fX6JkR0Wnd5wasostktghbfJZF-I06ww1Vd68qX2OI0Vqr8eS49dBmFzzbseXRnqddFqM4QiwWX9oB6IjqYuXfzXJrY6_xet_kmuMtp2i8vwRo7t6jj7YUw74lqx8dgpwB2VluasZf0pqnIe7p0RO_MVW95fYoISRXaongZRbR0q0-Y9Od01lZbvstl0Yqo8O6c6yooezrV1t7idRBkzlmYBYRsIyZ5vxkiMVcc1Y7uZrai2hvvavF-fgOa_pnB14e6xXnphYoZFr79Wd3YwXpQ318FOzMBywqV63x4w1ftxm6U4Vsytefly9txRqUIZxzr5R96rf-7wBn1sSUvjwoJr6X0u13jRyXMRWgFn7YW9ayqF_5d8zsn_skiBVJeiMkMrhbIRji8Wio7BdejOxmY7I_q7mUz-wue0-wy9Bo0wZcaizmuBvy2x2kv1bf5pXcx0jvSi8SiB0BOWgyZ5_0Qiy0cSWnvJ0w9aral8dMh")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        /*// print response headers
        HttpHeaders headers = response.headers();
        headers.map().forEach((k, v) -> System.out.println(k + ":" + v));*/

        // print response body
        System.out.println(response.body());

    }

}
