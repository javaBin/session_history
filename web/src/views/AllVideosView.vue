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
  <main>
    <h2>All Videos</h2>
    <table>
      <thead>
      <tr>
        <th>Year</th>
        <th>Title</th>
        <th>Link</th>
      </tr>
      </thead>
      <tbody>
      <VideoItemShort v-for="video in data" :video="video"/>
      </tbody>
    </table>
  </main>
</template>
