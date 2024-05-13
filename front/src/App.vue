

<template>
  <div >
    <div class="sentence" ref="sentence">
      <div v-for="(word,i) in sen.split(' ')" :key="i">
        <div :class="{'current-word' : i == wordIndex }">{{ word }}</div>
      </div>
    </div>
    <input v-model="inputVal" @keydown="handleKey" type="text">
  </div>
  <p>Errors: {{ sentenceErrors }}</p>
</template>

<script setup>

import { onMounted, ref } from 'vue';
import SockJS from 'sockjs-client';
const sentence = ref(null)
const sen = ref("мама мыла раму")
const inputVal = ref("")

const wordIndex = ref(0)
const sentenceErrors = ref(0)
let letterIndex = 0
let notErrorKeys = new Set(["Alt", "Shift", "Control", "CapsLock", "Tab"])

function handleKey(e){
  console.log(e)
  if(e.key == " " && inputVal.value == sen.value.split(" ")[wordIndex.value]){
    wordIndex.value += 1
    letterIndex = 0
    console.log("spacebar pressed")
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

onMounted(() =>{
  try{
    const sock = new SockJS("ws://localhost:8080/hello")

  }catch(e){
    console.log("suka blyad")
  }
  //sock.onopen = () => console.log("connected")
  // socket.send("hello")
})

</script>

<style scoped>

.current-word{
  color: green;
}
</style>
