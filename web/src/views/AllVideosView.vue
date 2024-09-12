<script setup lang="ts">
import {onMounted, ref} from 'vue'
import type {Video} from "@/types/api";

const data = ref<Video[]>([])

onMounted(() => {
  fetch('/api/search/videos', {
    method: 'GET',
    headers: {'Content-Type': 'application/json'}
  })
      .then((response) => response.json())
      .then((res) => (data.value = res))
})
</script>

<template>
  <div class="text-h4 ma-3">All Videos</div>

  <v-data-table :items="data" items-per-page="100">
    <template #[`item.video`]="{ value }">
      <v-btn>
        <a :href="value">
          <v-icon icon="fas fa-video"/>
        </a>
      </v-btn>
    </template>
  </v-data-table>
</template>
