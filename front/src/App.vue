<template>
  <div>
    <button @click="createRoom">create room</button> <br/>
    <input type="text" v-model="name" />
    <button @click="join">join</button>
    <div class="sentence" ref="sentence">
      <div v-for="(word, i) in sen.split(' ')" :key="i">
        <div :class="{ 'current-word': i == wordIndex }">{{ word }}</div>
      </div>
    </div>
    <input v-model="inputVal" @keydown="handleKey" type="text" />
  </div>
  <p>Errors: {{ sentenceErrors }}</p>
  <p>players:</p>
  <ul v-if="player">
    <li v-for="player in playersList" :key="player">
      <span>{{ player.name }}</span>
      <span> {{ player.wordIndex }}</span>
    </li>
  </ul>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { Client } from "@stomp/stompjs";

const sentence = ref(null);
const sen = ref("мама мыла раму");
const http = import.meta.env.VITE_URL
const ws = import.meta.env.VITE_WS
const inputVal = ref("");
const name = ref("");
const wordIndex = ref(0);
let roomId;
const sentenceErrors = ref(0);
const playersList = ref({})
let letterIndex = 0;
let notErrorKeys = new Set(["Alt", "Shift", "Control", "CapsLock", "Tab"]);

let player = {
  name: name.value,
  wordIndex: wordIndex.value,
};
const client = new Client({
  brokerURL: `ws://localhost:8080/gs-guide-websocket`,
  onConnect: () => {
    console.log("sent?");
    client.subscribe("/topic/greetings", (message) =>{
      playersList.value = JSON.parse(message.body)
      console.log(`Received: ${message.body}`)
  });
    client.publish({
      destination: "/app/hello",
      body: JSON.stringify(player),
    });
  },
});
client.activate();

async function createRoom(){
  const player = {
    id:0,
    name:name.value,
    wordIndex:0,
  }
  await fetch(`http://localhost:8080/room-create`,{
    method:"POST",
    body:JSON.stringify(player)
  })
  .then((res) => {
    console.log(res)
    return res.json()
  })
  .then((data) => {
    roomId = data.id
    playersList.value = data
    console.log(data)
    joinWs()
  })
}

function joinWs(){
  const client = new Client({
    brokerURL:`${ws}/rooms`,
    onConnect: () => {
      client.subscribe(`/topic/rooms/${roomId}`, (message) =>{
        console.log(`: ${message.body}`)
      })
    }

  })
  client.activate()
}

function joinRoom() {
  // fetch(`${ws}`)
  player.name = name.value
  console.log(name.value);
}

function handleKey(e) {

  if (e.key == " " && inputVal.value == sen.value.split(" ")[wordIndex.value]) {
    wordIndex.value += 1;
    letterIndex = 0;
    console.log("spacebar pressed");
    player.wordIndex = wordIndex.value
    client.publish({
      destination: "/app/hello",
      body: JSON.stringify(player),
    });
    setTimeout(() => (inputVal.value = ""), 0);
    inputVal.value = "";
    return;
  }

  if (e.key == sen.value.split(" ")[wordIndex.value][letterIndex]) {
    console.log("key matched");
    letterIndex++;
  } else if (
    !notErrorKeys.has(e.key) &&
    e.key != sen.value.split(" ")[wordIndex.value][letterIndex]
  ) {
    sentenceErrors.value += 1;
  }
}

onMounted(() => {
  console.log(`${http} ra`)
});
</script>

<style scoped>
.current-word {
  color: green;
}
</style>
