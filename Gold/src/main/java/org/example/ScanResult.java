package org.example;

import lombok.Data;

import java.util.Map;

@Data
class ScanResult {
    private ScanData scan;
    private boolean success;
    private Error[] errors;

    @Data
    static class ScanData {
        private Ship[] myShips;
        private Ship[] enemyShips;
        private Zone zone;
        private int tick;
    }

    @Data
    static class Ship {
        private int id;
        private int x;
        private int y;
        private int size;
        private int hp;
        private int maxHp;
        private Map<String, String> direction;
        private int speed;
        private int maxSpeed;
        private int minSpeed;
        private int maxChangeSpeed;
        private int cannonCooldown;
        private int cannonCooldownLeft;
        private int cannonRadius;
        private int scanRadius;
        private int cannonShootSuccessCount;
    }

    @Data
    static class Zone {
        private int x;
        private int y;
        private int radius;
    }

    @Data
    static class Error {
        private String message;
    }
}
