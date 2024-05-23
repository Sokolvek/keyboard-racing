<template>
  <div>
    <ul>
      <li>{{ rooms }}</li>
    </ul>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { Client } from "@stomp/stompjs";

const http = import.meta.env.VITE_HTTP;
const ws = import.meta.env.VITE_WS;
const rooms = ref({})
let roomsWS;

function joinRoomsWS() {
  roomsWS = new Client({
    brokerURL: `${ws}/rooms`,
    onConnect: () => {
      console.log(`Subscribed to /topic/rooms/}`);
      roomsWS.subscribe(`/topic/rooms/all-rooms`, (message) => {
        console.log(`Received message: ${message.body}`);
        rooms.value = JSON.parse(message.body);
      });
      // Optional initial message
      // client.publish({
      //   destination: `/app/rooms/${roomId}`,
      //   body: JSON.stringify({}),
      // });
      console.log("WebSocket connection established");
    },
    onStompError: (frame) => {
      console.error(`Broker reported error: ${frame.headers["message"]}`);
      console.error(`Additional details: ${frame.body}`);
    },
  });
  roomsWS.activate()
}

onMounted(() => {
    joinRoomsWS()
})
</script>
