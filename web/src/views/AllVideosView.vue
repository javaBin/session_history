<script setup lang="ts">
import VideoItemShort from "@/components/VideoItemShort.vue";
import {computed, onMounted, ref} from "vue";

const data = ref([])

onMounted(() => {
  fetch('/api/search/videos', {
    method: "GET",
    headers: {'Content-Type': 'application/json'},
  }).then(response => response.json())
      .then(res => data.value = res)
})

const items = computed(() => {
  return data.value.map((video) => {
    return {
      title: video.title,
      year: video.year,
      link: video.video
    }
  })
})

</script>

<template>
  <div class="text-h4 ma-3">All Videos</div>

  <v-data-table :items="items" items-per-page="100">
    <template v-slot:item.link="{ value }">
      <v-btn><a :href="value"><v-icon icon="fas fa-video" /></a></v-btn>
    </template>
  </v-data-table>
</template>
