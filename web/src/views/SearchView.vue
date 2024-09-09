<script setup lang="ts">
import SessionItem from '../components/SessionItem.vue'
import {onMounted, ref} from "vue";
import AggregateTotals from "@/components/AggregateTotals.vue";

const search = ref("*")
const data = ref()

onMounted(() => {
  performSearch()
})

const performSearch = () => {
  fetch('/api/search', {
    method: "POST",
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      query: search.value
    })
  }).then(response => response.json())
      .then(res => data.value = res)
}

const clear = () => {
  search.value = "*"

  performSearch()
}

</script>

<template>
  <main class="p-3">
    <h2 class="text-2xl">Search</h2>

    <div class="p-3">
      <input type="search" class="w-1/2 rounded-full" v-model="search"/>
      <button class="rounded-full bg-blue-400 p-2 m-2" @click="performSearch">Search</button>
      <button class="rounded-full bg-blue-400 p-2 m-2" @click="clear">Reset</button>
    </div>

    <AggregateTotals v-if="data && data?.aggregateResponse" :aggregate="data?.aggregateResponse" />

    <div class="m-6 flex flex-wrap content-start">
      <SessionItem v-for="session in data?.sessionsResponse" :session="session"/>
    </div>
  </main>
</template>
