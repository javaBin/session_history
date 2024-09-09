<script setup lang="ts">
import type {VideoShort} from "@/types/api";

const props = defineProps<{
  video: VideoShort
}>()
</script>

<template>
  <tr>
    <td class="text-gray-400 p-3 border-2">
      {{ props.video.year }}
    </td>
    <td class="p-3 border-2">
      {{ props.video.title }}
    </td>
    <td class="p-3 border-2">
      <a class="text-blue-400 underline" :href="props.video.video" target="_blank">Video</a>
    </td>
  </tr>
</template>