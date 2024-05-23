package com.example.backend.models;

import java.util.ArrayList;
import java.util.HashMap;

public record Room(String id, ArrayList<Player> players, int limit) {
    public void add(Player player){
        players.add(player);
    }
}
