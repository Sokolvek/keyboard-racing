<template>
  <div>
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
  <p>players: {{ playersList }}</p>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { Client } from "@stomp/stompjs";

const sentence = ref(null);
const sen = ref("мама мыла раму");
const inputVal = ref("");
const name = ref("");
const wordIndex = ref(0);
const sentenceErrors = ref(0);
const playersList = ref({})
let letterIndex = 0;
let notErrorKeys = new Set(["Alt", "Shift", "Control", "CapsLock", "Tab"]);

let player = {
  name: name.value,
  wordIndex: wordIndex.value,
};
const client = new Client({
  brokerURL: "ws://localhost:8080/gs-guide-websocket",
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
function join() {
  player.name = name.value
  console.log(name.value);
  client.activate();
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

onMounted(() => {});
</script>

<style scoped>
.current-word {
  color: green;
}
</style>
