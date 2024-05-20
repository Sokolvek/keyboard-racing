package com.example.backend.controllers;

import com.example.backend.enums.RoomStates;
import com.example.backend.models.Player;
import com.example.backend.models.Room;
import com.example.backend.models.RoomRequest;
import com.example.backend.storage.Storage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RoomController {

    @PostMapping("/room-create")
    public Room createRoom(Player player){
        Room room = new Room(1, new ArrayList<Player>(List.of(player)));
        Storage.rooms.add(room);
        return room;
    }

    @PostMapping("join-room/{id}")
    public void joinRoom(Player player, @PathVariable int id){
        Room room = Storage.getRoomById(id);

        room.add(player);
    }

    @MessageMapping("/rooms/{roomId}")
    @SendTo("topic/rooms/{roomId}")
    public void roomControl(RoomRequest room, Player player){


    }
}
