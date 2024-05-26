<template>
  <div>
    <input type="text" v-model="name" placeholder="enter your name" />
    <button @click="createRoom">Create Room</button>
    <p>or</p>
    <input type="text" v-model="roomCode" placeholder="enter room id" />
    <button @click="joinRoom">Join room</button>
    <button @click="router.push('/rooms')">browse rooms</button>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import {useCounterStore} from "../stores/counter"

const store = useCounterStore()
const name = ref("");

const http = import.meta.env.VITE_HTTP;
const ws = import.meta.env.VITE_WS;
const router = useRouter();
const roomCode = ref(0)

async function createRoom() {
  store.name = name.value
  const player = {
    id: 0,
    name: name.value,
    wordIndex: 0,
  };
  console.log(JSON.stringify(player));
  await fetch(`${http}/room-create`, {
    method: "POST",
    body: JSON.stringify(player),
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((res) => res.json())
    .then((data) => {
      store.room = data
      router.push({ path: `room/${data.id}`, params:{test:"huesos"} });
    });
}

function joinRoom() {
  store.name = name.value
  const player = {
    name: name.value,
    wordIndex: 0,
    id: 0,
  };
  fetch(`${http}/join-room/${roomCode.value}`, {
    method: "POST",
    body: JSON.stringify(player),
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((res) => res.json())
    .then((data) => {
      console.log(data);
      store.room = data
      // room.value = data;
      // roomId = data.id;
      // joinWs();
      router.push({path:`room/${roomCode.value}`})
    });
}
</script>
<style scoped>
.hello{
  color: green;
}
</style>