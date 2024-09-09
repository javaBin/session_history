<script setup lang="ts">
import {useFetch} from '@vueuse/core'
import VideoItemShort from "@/components/VideoItemShort.vue";

const {
  data,
  error,
  isFetching,
  isFinished,
} = useFetch("/api/search/videos").get().json()

</script>

<template>
  <main class="p-3">
    <h2 class="text-2xl">All Videos</h2>
    {{ error }}
    <div v-if="isFinished">
      <table class="table-auto border-collapse border-2">
        <thead>
        <tr>
          <th class="text-left p-3 border-2">Year</th>
          <th class="text-left p-3 border-2">Title</th>
          <th class="text-left p-3 border-2">Link</th>
        </tr>
        </thead>
        <tbody>
          <VideoItemShort v-for="video in data" :video="video"/>
        </tbody>
      </table>
    </div>
  </main>
</template>
