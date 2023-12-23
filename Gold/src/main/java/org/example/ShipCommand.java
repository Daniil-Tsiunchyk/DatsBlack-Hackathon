package org.example;

import lombok.Data;

@Data
class ShipCommand {
    private int id;
    private int changeSpeed;
    private int rotate;
    private CannonShoot cannonShoot;

@Data
    static class CannonShoot {
        private int x;
        private int y;

    }
}