package com.example.backend.storage;

import com.example.backend.models.Room;

import java.util.ArrayList;

public class Storage {
    public static final ArrayList<Room> rooms = new ArrayList<>();

    public static Room getRoomById(int roomId){
        return rooms.stream().filter(room -> room.id() == roomId).findFirst().orElse(null);
    }
}
