package com.example.backend.controllers;

import com.example.backend.models.Player;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Controller
public class GreetingController {

    private final HashMap<Integer, Player> players = new HashMap<>();
    int id = 0;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public HashMap<Integer, Player> greeting(Player player) throws Exception {
        System.out.println(player.wordIndex);
        if(players.size() > 0 && players.containsValue(player)){
            System.out.println("changed");
            players.get(player.id).wordIndex = player.wordIndex;
        }

        if (players.containsKey(player.id)){
            return players;
        }
        id += 1;
        players.put(id, player);
        return players;
    }

}