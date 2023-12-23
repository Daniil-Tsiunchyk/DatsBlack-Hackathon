package org.example.models.BattleMap;

import lombok.Data;

@Data
class Coordinate {
    private int x;
    private int y;

    @Override
    public String toString() {
        return "Coordinate{" + "x=" + x + ", y=" + y + '}';
    }
}
