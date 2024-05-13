

<template>
  <div >
    <div class="sentence" ref="sentence">
      <div v-for="(word,i) in sen.split(' ')" :key="i">
        <div :class="{'current-word' : i == wordIndex }">{{ word }}</div>
      </div>
    </div>
    <input v-model="inputVal" @keydown="handleKey" type="text">
  </div>
</template>

<script setup>

import { onMounted, ref } from 'vue';

const sentence = ref(null)
const sen = ref("мама мыла раму")
const inputVal = ref("")
const wordIndex = ref(0)
let letterIndex = 0


function handleKey(e){
  if(e.key == " " && inputVal.value == sen.value.split(" ")[wordIndex.value]){
    wordIndex.value += 1
    letterIndex = 0
    inputVal.value = ""
    console.log("spacebar pressed")
    return
  }

  if(e.key == sen.value.split(" ")[wordIndex.value][letterIndex]){
    console.log("key matched")
    letterIndex++
  }
}

onMounted(() =>{
  console.log(sentence.value)
})

</script>

<style scoped>

.current-word{
  color: green;
}
</style>
