<script setup lang="ts">
import VideoItemShort from "@/components/VideoItemShort.vue";
import {onMounted, ref} from "vue";

const data = ref()

onMounted(() => {
  fetch('/api/search/videos', {
    method: "GET",
    headers: {'Content-Type': 'application/json'},
  }).then(response => response.json())
      .then(res => data.value = res)
})

</script>

<template>
  <main class="p-3">
    <h2 class="text-2xl">All Videos</h2>
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
  </main>
</template>
