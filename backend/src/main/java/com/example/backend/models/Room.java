package com.example.backend.models;

import java.util.ArrayList;
import java.util.HashMap;

public record Room(int id, ArrayList<Player> players) {
    public void add(Player player){
        players.add(player);
    }
}
