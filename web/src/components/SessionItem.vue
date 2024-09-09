<script setup lang="ts">
import type {Session} from "@/types/api";
import {displayFormat, displayLanguage} from "../types/aggregates";

const props = defineProps<{
  session: Session
}>()

</script>
<template>
  <div class="relative border shadow rounded-lg p-4 w-5/12 m-4 flex justify-between flex-col items-start">
    <span class="absolute top-0 right-0 text-white p-2 bg-blue-400">{{ props.session.year }}</span>

    <div class="mt-8 mb-2 text-2xl text-center">
      {{ props.session.title }}
    </div>

    <div>
      {{ displayFormat(props.session.format) }} | {{ displayLanguage(props.session.language) }}
    </div>

    <div v-if="props.session.video">
      <a class="text-blue-400 underline" :href="props.session.video" target="_blank">Video</a>
    </div>

    <div v-if="props.session.intendedAudience">
      <h4 class="text-gray-400">Intended Audience</h4>
      {{ props.session.intendedAudience }}
    </div>

    <div v-if="props.session.abstract">
      <h4 class="text-gray-400">Abstract</h4>
      {{ props.session.abstract }}
    </div>

    <div v-for="speaker in props.session.speakers" class="mt-2 mb-2">
      <h3 class="text-xl">{{ speaker.name }}</h3>

      <p v-if="speaker.bio">{{ speaker.bio }}</p>
    </div>
  </div>
</template>
