<script setup lang="ts">
import SessionItem from '../components/SessionItem.vue'
import {onMounted, ref} from "vue";
import AggregateTotals from "@/components/AggregateTotals.vue";

const search = ref("")
const data = ref()

onMounted(() => {
  performSearch()
})

const performSearch = () => {
  let searchVal = search.value

  fetch('/api/search', {
    method: "POST",
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      query: searchVal
    })
  }).then(response => response.json())
      .then(res => data.value = res)
}

const clear = () => {
  search.value = ""

  performSearch()
}

</script>

<template>
  <div class="text-h4 ma-3">Search</div>

  <div class="ma-3">
    <v-form @submit.prevent>
      <v-text-field
          v-model="search"
          label="Search"
          @keyup.enter="performSearch"
      >
      </v-text-field>
      <v-btn class="me-2" @click="performSearch">Search</v-btn>
      <v-btn class="me-2" @click="clear">Reset</v-btn>
    </v-form>
  </div>

  <v-divider/>

  <v-navigation-drawer
      location="end"
      name="drawer"
      permanent
  >
    <AggregateTotals v-if="data && data?.aggregateResponse" :aggregate="data?.aggregateResponse"/>
  </v-navigation-drawer>


  <div class="">
    <div class="d-flex flex-row flex-wrap justify-center">
      <SessionItem v-for="session in data?.sessionsResponse" :session="session"/>
    </div>
  </div>
</template>
