<template>
  <div>
    <button @click="startGame">startGame</button>
    <div class="sentence" ref="sentence">
      <div v-for="(word, i) in sen.split(' ')" :key="i">
        <div :class="{ 'current-word': i == wordIndex }">{{ word }}</div>
      </div>
    </div>
    
    <input :disabled="!store.room.gameStarted" v-model="inputVal" @keydown="handleKey" type="text" />
    <p>Errors: {{ sentenceErrors }}</p>
    <p>Players:</p>
    <ul v-if="store.room && store.room" class="room">
      <p>game status: {{ store.room.gameStarted }}</p>
      <li v-for="player in store.room.players" :key="player.id">
        <p>{{ player.name }}</p>
        <p>{{ player.wordIndex }}</p>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { Client } from "@stomp/stompjs";
import { useRouter, useRoute } from "vue-router";
import { useCounterStore } from "../stores/counter";

const store = useCounterStore();
const router = useRouter();
const route = useRoute();
const sen = ref("мама мыла раму");
const ws = import.meta.env.VITE_WS;
const roomId = route.params.id;
const inputVal = ref("");
const name = ref(""); 
const wordIndex = ref(0);
const sentenceErrors = ref(0);
const room = ref({ players: [] });
let notErrorKeys = new Set(["Alt", "Shift", "Control", "CapsLock", "Tab", "Backspace"]);
let roomSocket;
let letterIndex = 0;

function joinWs() {
  console.log(roomId);
  roomSocket = new Client({
    brokerURL: `${ws}/rooms`,
    onConnect: () => {
      console.log(`Subscribed to /topic/rooms/${roomId}`);
      roomSocket.subscribe(`/topic/rooms/${roomId}`, (message) => {
        console.log(`Received message: ${message.body}`);
        store.room = JSON.parse(message.body);
      });

      // Request initial state of the room
      roomSocket.publish({
        destination: `/app/rooms/${roomId}`,
        body: JSON.stringify(store.room),
      });

      console.log("Connected to WebSocket");
    },
    onStompError: (frame) => {
      console.error(`Broker reported error: ${frame.headers["message"]}`);
      console.error(`Additional details: ${frame.body}`);
    },
  });
  roomSocket.activate();
}

function findPlayer(players) {
  return players.findIndex((item) => item.name === store.name);
}

function startGame(){
  let req = {
    action:"START",
    room:{
      id: roomId,
      players: store.room.players,
      limit: 4,
      gameStarted: false
    }
  }
  roomSocket.publish({destination:`/app/rooms/${roomId}`, body: JSON.stringify(req)})
}

function handleKey(e){
    if(e.key == " " && inputVal.value == sen.value.split(" ")[wordIndex.value]){
      let plrId = findPlayer(store.room.players)
      console.log(plrId)
      store.room.players[plrId].wordIndex += 1
      console.log(plrId, room.value.players)
      wordIndex.value += 1
      let request = {
        id: roomId,
        players: store.room.players
      };
      letterIndex = 0
      console.log(request)
      // request.players[name.value].wordIndex += 1
      roomSocket.publish({ destination: `/app/rooms/${roomId}`, body: JSON.stringify(request) });
      setTimeout(() => inputVal.value = "",0)
      inputVal.value = ""
      return
    }
  
    if(e.key == sen.value.split(" ")[wordIndex.value][letterIndex]){
      console.log("key matched")
      letterIndex++
    }else if(!notErrorKeys.has(e.key) && e.key != sen.value.split(" ")[wordIndex.value][letterIndex]){
      sentenceErrors.value += 1
    }
  }


onMounted(() => {
  console.log(route.params)
  joinWs();
});
</script>

<style>
.current-word {
  font-weight: bold;
  color: green;
}
</style>
