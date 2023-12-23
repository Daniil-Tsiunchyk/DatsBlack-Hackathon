package org.example.models.BattleMap;

import lombok.Data;

import java.util.List;

@Data
public class Island {
    private int[][] map;
    private List<Integer> start;

    @Override
    public String toString() {
        return "Island{" + "map=" + arrayToString(map) + ", start=" + start + '}';
    }

    private String arrayToString(int[][] array) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : array) {
            sb.append("[");
            for (int cell : row) {
                sb.append(cell).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("], ");
        }
        return sb.toString();
    }

}
