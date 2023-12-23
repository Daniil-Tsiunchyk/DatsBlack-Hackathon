package org.example.scripts;

import lombok.Data;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.example.Const.*;

public class ScriptRegister {
    public static void main(String[] args) {
        try {
            registerForDeathMatch();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void registerForDeathMatch() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "deathMatch/registration"))
                .header("X-API-Key", apiKey)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Register for DeathMatch: " + parseResponse(response.body()));
    }

    public void exitDeathMatch() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "deathMatch/exitBattle"))
                .header("X-API-Key", apiKey)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Exit DeathMatch: " + parseResponse(response.body()));
    }

    public void registerForRoyalBattle() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "royalBattle/registration"))
                .header("X-API-Key", apiKey)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Register for Royal Battle: " + parseResponse(response.body()));
    }

    private static String parseResponse(String responseBody) {
        Response response = gson.fromJson(responseBody, Response.class);
        return response.toString();
    }

    @Data
    static class Response {
        private boolean success;
        private Error[] errors;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Success: ").append(success).append("\n");
            if (errors != null) {
                for (Error error : errors) {
                    sb.append("Error: ").append(error.getMessage()).append("\n");
                }
            }
            return sb.toString();
        }

        @Data
        static class Error {
            private String message;

            public String getMessage() {
                return message;
            }
        }
    }
}
