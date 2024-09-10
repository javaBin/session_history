<script setup lang="ts">
import type {VideoShort} from "@/types/api";

const props = defineProps<{
  video: VideoShort
}>()
</script>

<template>
  <tr>
    <td>
      {{ props.video.year }}
    </td>
    <td>
      {{ props.video.title }}
    </td>
    <td>
      <a :href="props.video.video" target="_blank">Video</a>
    </td>
  </tr>
</template>