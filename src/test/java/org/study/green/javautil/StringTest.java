package org.study.green.javautil;

import org.junit.Test;
import org.study.green.resource.ResourceUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;


public class StringTest {

    @Test
    public void testResource() {

//        String url = "한글";
//        String encoded = URLEncoder.encode(url, StandardCharsets.UTF_8);
//        System.out.println(encoded);
//
//        String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
//        System.out.println(decoded);

        String resourceTestPath = ResourceUtils.getResourceTestPath();
        System.out.println("resourceTestPath = " + resourceTestPath);

    }

    @Test
    public void restApiTest01() {
        String url = "http://localhost:8080/hello/h1";
        String jsonBody = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            System.out.println("Status code: " + responseCode);

            try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response body: " + response);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void restApiTest02() {
        String dbContent = "Line1\nLine2\nLine3";
        byte[] bytes = dbContent.getBytes(StandardCharsets.UTF_8);

//        String url = "https://api.example.com/endpoint";
        String url = "http://localhost:8080/hello/h1";
        String jsonBody = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        // 우선 실행이 안됨 11버전인데 ?
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("response = " + response);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response body: " + response.body());

    }
}
