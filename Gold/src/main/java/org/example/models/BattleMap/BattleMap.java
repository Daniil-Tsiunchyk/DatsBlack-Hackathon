package org.example.models.BattleMap;

import lombok.Data;

import java.util.List;

@Data
public class BattleMap {
    private int width;
    private int height;
    private List<Island> islands;

    @Override
    public String toString() {
        return "BattleMap{" + "width=" + width + ", height=" + height + ", islands=" + islands + '}';
    }
}

