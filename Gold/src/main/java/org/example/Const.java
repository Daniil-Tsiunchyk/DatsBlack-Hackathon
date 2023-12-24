package org.example;

import com.google.gson.Gson;

import java.net.http.HttpClient;

public class Const {
    public static final HttpClient httpClient = HttpClient.newHttpClient();
    public static final String apiKey = "a7e670d0-c3ff-4467-96ca-3ffcd79dca89";
    public static final Gson gson = new Gson();
    public static final String baseUrl = "https://datsblack.datsteam.dev/api/";
    public static final String mapUrl = "https://datsblack.datsteam.dev/json/map/6586b28f3cf184.48078812.json";
    public final static int DISTANCE_SCAN = 25;
    public static final String NORTH = "north";
    public static final String SOUTH = "south";
    public static final String EAST = "east";
    public static final String WEST = "west";
    public static final int THIS_TICK = 75;

}
