package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        GameApiService apiService = new GameApiService("a7e670d0-c3ff-4467-96ca-3ffcd79dca89");
        apiService.startRegularScans();

    }
}
