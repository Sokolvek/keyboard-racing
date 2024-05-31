package com.example.backend.controllers;

import com.example.backend.models.Player;
import com.example.backend.storage.Storage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {

    @GetMapping("/player-create")
    private Player createPlayer(@RequestBody Player player){
        if (Storage.players.contains(player)){
            return null;
        }

        Storage.players.add(player);
        return player;
    }


}
