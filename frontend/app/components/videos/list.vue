<script setup lang="ts">
import type {Video} from "~/types/api";

const {data, status, error} = await useLazyFetch<Video[]>('/api/search/videos')
const search = ref('')

const headers = [
  {title: "Title", key: "title"},
  {title: "Year", key: "year"},
  {title: "Video", key: "video", filterable: false, sortable: false, align: "center"},
]

</script>

<template>
  <v-text-field
      v-model="search"
      label="Search"
      prepend-inner-icon="mdi-magnify"
      variant="outlined"
      hide-details
      single-line
      class="ma-2"
  ></v-text-field>

  <v-progress-circular
      color="primary"
      indeterminate
      class="ma-16"
      v-if="status === 'pending'"
  ></v-progress-circular>

  <v-data-table v-if="status === 'success'" :items="data" :headers="headers" density="comfortable" :search="search"
                items-per-page="100">
    <template #[`item.video`]="{ value }">
      <v-btn :href="value" target="_blank">
        <Icon name="mdi:video" size="32px"></Icon>
      </v-btn>
    </template>
  </v-data-table>
</template>
