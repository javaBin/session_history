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
  <main>
    <h2>Search</h2>

    <div>
      <input type="search" v-model="search" @keyup.enter="performSearch"/>
      <button @click="performSearch">Search</button>
      <button @click="clear">Reset</button>
    </div>

    <AggregateTotals v-if="data && data?.aggregateResponse" :aggregate="data?.aggregateResponse"/>

    <div>
      <SessionItem v-for="session in data?.sessionsResponse" :session="session"/>
    </div>
  </main>
</template>
