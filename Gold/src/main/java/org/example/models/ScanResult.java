package org.example.models;

import lombok.Data;

@Data
public class ScanResult {
    private ScanData scan;
    private boolean success;
    private Error[] errors;

    @Data
    public static class ScanData {
        private Ship[] myShips;
        private Ship[] enemyShips;
        private Zone zone;
        private int tick;

    }

    @Data
    public static class Ship {
        private int id;
        private int x;
        private int y;
        private int size;
        private int hp;
        private String direction;
        private int speed;
        private int cannonCooldown;
        private int cannonCooldownLeft;
        private int cannonShootSuccessCount;
        public void move() {
            switch (direction.toLowerCase()) {
                case "north":
                    y -= speed;
                    break;
                case "south":
                    y += speed;
                    break;
                case "east":
                    x += speed;
                    break;
                case "west":
                    x -= speed;
                    break;
                default:
                    System.out.println("Неправильное направление: " + direction);
                    break;
            }
        }
    }
    @Data
    public static class Zone {
        private int x;
        private int y;
        private int radius;
    }
    @Data
    public static class Error {
        private String message;
    }
}
