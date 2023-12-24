package org.example;

import lombok.Data;

import static org.example.Const.gson;

public class CommonScripts {
    public static String parseResponse(String responseBody) {
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
        }
    }
}
