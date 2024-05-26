<template>
  <div>
    <ul>
      <li v-for="room in rooms" :key="room.id">
        <p>{{ room.players.length }}/{{ room.limit }}</p>
        <button @click="joinRoom(room.id)">join room</button>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { Client } from "@stomp/stompjs";
import { useRouter } from "vue-router";
import { useCounterStore } from "../stores/counter";

const store = useCounterStore()
const router = useRouter()
const http = import.meta.env.VITE_HTTP;
const ws = import.meta.env.VITE_WS;
const rooms = ref({});
let roomsWS;

function joinRoomsWS() {
  roomsWS = new Client({
    brokerURL: `${ws}/rooms`,
    onConnect: () => {
      console.log('Connected to WebSocket');
      roomsWS.subscribe(`/topic/rooms/all-rooms`, (message) => {
        console.log('Received message:', message.body);
        rooms.value = JSON.parse(message.body);
      });
      roomsWS.publish({
        destination: `/app/rooms`,
        body: JSON.stringify({aba:"test"}),
      });
    },
    onStompError: (frame) => {
      console.error('WebSocket error:', frame.headers["message"], frame.body);
    },
  });
  roomsWS.activate();
}


function joinRoom(roomId) {
  const player = {
    name: store.name,
    wordIndex: 0,
    id: 0,
  };
  fetch(`${http}/join-room/${roomId}`, {
    method: "POST",
    body: JSON.stringify(player),
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((res) => res.json())
    .then((data) => {
      router.push({path:`room/${roomId}`})
    });
}

onMounted(() => {
  joinRoomsWS();
});
</script>
