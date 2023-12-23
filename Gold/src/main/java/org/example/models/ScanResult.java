package org.example.models;

import lombok.Data;

import java.util.Map;

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
        @Override
        public String toString() {
            return String.format("Корабль ID: %d%n" +
                            "Позиция: (%d, %d)%n" +
                            "Размер: %d%n" +
                            "Здоровье: %d/%d%n" +
                            "Направление: %s%n" +
                            "Скорость: %d/%d, Мин. скорость: %d, Макс. изменение скорости: %d%n" +
                            "Время перезарядки пушки: %d, Оставшееся время: %d%n" +
                            "Радиус пушки: %d, Радиус сканирования: %d%n" +
                            "Успешных выстрелов: %d",
                    id, x, y, size, hp, maxHp, direction, speed, maxSpeed, minSpeed, maxChangeSpeed,
                    cannonCooldown, cannonCooldownLeft, cannonRadius, scanRadius, cannonShootSuccessCount);
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
