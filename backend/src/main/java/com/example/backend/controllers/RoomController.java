package com.example.backend.controllers;

import com.example.backend.enums.RoomStates;
import com.example.backend.models.Player;
import com.example.backend.models.Room;
import com.example.backend.models.RoomRequest;
import com.example.backend.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@CrossOrigin
public class RoomController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired private SimpUserRegistry userRegistry;
    public void printConnectedUsers() {

    }


    @PostMapping("/room-create")
    public Room createRoom(@RequestBody Player player){
        Room room = new Room(Integer.toString(Storage.roomId), new ArrayList<Player>(List.of(player)), 4);
        Storage.rooms.add(room);
        Storage.roomId += 1;
        messagingTemplate.convertAndSend("/topic/rooms/all-rooms", Storage.rooms);
        return room;
    }

    @PostMapping("join-room/{id}")
    public Room joinRoom(@RequestBody Player player, @PathVariable String id){

        Room room = Storage.getRoomById(id);

        assert room != null;
        room.add(player);

        log.info("Player {} joined room {}", player, id);
        messagingTemplate.convertAndSend("/topic/rooms/" + id, room);
        return room;
    }

    @MessageMapping("/rooms/{roomId}")
    @SendTo("topic/rooms/{roomId}")
        public Room roomControl(@Payload Room room, @PathVariable String roomId) {
        System.out.println("Received room update for roomId: " + room.id());
        System.out.println("Room details: " + room.players().get(1).wordIndex);
        messagingTemplate.convertAndSend("/topic/rooms/" + room.id(), room);
        return room;
    }

    @MessageMapping("/rooms")
    @SendTo("topic/rooms/all-rooms")
    public void allRooms(){
        messagingTemplate.convertAndSend("/topic/rooms/all-rooms", Storage.rooms);
    }


    @GetMapping("/test")
    void test(){
        System.out.println("called");
        userRegistry.getUsers().stream()
                .map(u -> u.getName())
                .forEach(System.out::println);

    }
}
