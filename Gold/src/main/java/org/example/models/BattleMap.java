package org.example.models;

import lombok.Data;

import java.util.List;

@Data
public class BattleMap {
    private int width;
    private int height;
    private List<Island> islands;

    @Data
    public static class Island {
        private int[][] map;
        private List<Integer> start;
    }

    @Data
    static
    class Coordinate {
        private int x;
        private int y;
    }
}

