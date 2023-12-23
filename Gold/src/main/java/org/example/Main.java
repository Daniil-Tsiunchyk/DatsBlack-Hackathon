package org.example;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

class Ship {
}

class Island {
}

class ScanResult {
}

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        GameApiService apiService = new GameApiService("ваш API ключ");
        ScanResult scanResult = apiService.scan();
    }
}
