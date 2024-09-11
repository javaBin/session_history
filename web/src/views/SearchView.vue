<script setup lang="ts">
import SessionItem from '@/components/SessionItem.vue'
import {onMounted, ref} from "vue";
import AggregateTotals from "@/components/AggregateTotals.vue";

const search = ref("")
const data = ref()
const filteredYear = ref<number>(undefined)
const filteredFormat = ref<string>(undefined)
const filteredLanguage = ref<string>(undefined)

onMounted(() => {
  performSearch()
})

const performSearch = () => {
  let searchVal = search.value

  const query = {
    query: searchVal
  }

  if (filteredYear.value) {
    query.years = [filteredYear.value]
  }

  if (filteredFormat.value) {
    query.formats = [filteredFormat.value]
  }

  if (filteredLanguage.value) {
    query.languages = [filteredLanguage.value]
  }

  fetch('/api/search', {
    method: "POST",
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(query)
  }).then(response => response.json())
      .then(res => data.value = res)
}

const clear = () => {
  search.value = ""
  filteredYear.value = undefined
  filteredFormat.value = undefined
  filteredLanguage.value = undefined

  performSearch()
}

const filterYear = (params: number[]) => {
  const [year] = params

  filteredYear.value = year
  performSearch()
}

const filterFormat = (params: string[]) => {
  const [format] = params

  filteredFormat.value = format
  performSearch()
}

const filterLanguage = (params: string[]) => {
  const [language] = params

  filteredLanguage.value = language
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
    <AggregateTotals v-if="data && data?.aggregateResponse" :aggregate="data?.aggregateResponse" @filterYear="filterYear" @filterFormat="filterFormat" @filterLanguage="filterLanguage"/>
  </v-navigation-drawer>


  <div class="">
    <div class="d-flex flex-row flex-wrap justify-center">
      <SessionItem v-for="session in data?.sessionsResponse" :session="session"/>
    </div>
  </div>
</template>
