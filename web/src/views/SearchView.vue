<script setup lang="ts">
import SessionItem from '@/components/SessionItem.vue'
import {onMounted, ref} from "vue";
import AggregateTotals from "@/components/AggregateTotals.vue";
import type {SearchQuery} from "@/types/api";

const search = ref("")
const data = ref()
const filteredYear = ref<number | undefined>(undefined)
const filteredFormat = ref<string | undefined>(undefined)
const filteredLanguage = ref<string | undefined>(undefined)

const showSpinner = ref(false)

onMounted(() => {
  performSearch()
})

const performSearch = () => {
  let searchVal = search.value

  const query = {
    query: searchVal
  } as SearchQuery

  if (filteredYear.value) {
    query.years = [filteredYear.value]
  }

  if (filteredFormat.value) {
    query.formats = [filteredFormat.value]
  }

  if (filteredLanguage.value) {
    query.languages = [filteredLanguage.value]
  }

  showSpinner.value = true

  fetch('/api/search', {
    method: "POST",
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(query)
  }).then(response => response.json())
      .then(res => {
        data.value = res

        showSpinner.value = false
      })
}

const clear = () => {
  search.value = ""
  filteredYear.value = undefined
  filteredFormat.value = undefined
  filteredLanguage.value = undefined

  performSearch()
}

const filterYear = (year: number) => {
  filteredYear.value = year
  performSearch()
}

const filterFormat = (format: string) => {
  filteredFormat.value = format
  performSearch()
}

const filterLanguage = (language: string) => {
  filteredLanguage.value = language
  performSearch()
}

const clearFilter = (filter: string) => {
  switch (filter) {
    case "LANGUAGE": {
      filteredLanguage.value = undefined
      break;
    }
    case "FORMAT": {
      filteredFormat.value = undefined
      break;
    }
    case "YEAR": {
      filteredYear.value = undefined
      break;
    }
  }

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

  <v-progress-circular
      color="primary"
      indeterminate
      class="ma-16"
      v-if="showSpinner"
  ></v-progress-circular>


  <v-navigation-drawer
      location="end"
      name="drawer"
      permanent
  >
    <AggregateTotals v-if="data && data?.aggregateResponse"
                     :aggregate="data?.aggregateResponse"
                     @filterYear="filterYear"
                     @filterFormat="filterFormat"
                     @filterLanguage="filterLanguage"
                     @clear="clearFilter"
                     :filteredFormat="filteredFormat"
                     :filteredLanguage="filteredLanguage"
                     :filteredYear="filteredYear"/>
  </v-navigation-drawer>


  <div>
    <div class="d-flex flex-row flex-wrap justify-center">
      <SessionItem v-for="session in data?.sessionsResponse" :key="session.id" :session="session"/>
    </div>
  </div>
</template>
