<script setup lang="ts">
import type {Session} from "@/types/api";
import {displayFormat, displayLanguage} from "../types/aggregates";

const props = defineProps<{
  session: Session
}>()

</script>
<template>
  <div>
    <span>{{ props.session.year }}</span>

    <div>
      {{ props.session.title }}
    </div>

    <div>
      {{ displayFormat(props.session.format) }} | {{ displayLanguage(props.session.language) }}
    </div>

    <div v-if="props.session.video">
      <a :href="props.session.video" target="_blank">Video</a>
    </div>

    <div v-if="props.session.intendedAudience">
      <h4>Intended Audience</h4>
      {{ props.session.intendedAudience }}
    </div>

    <div v-if="props.session.abstract">
      <h4>Abstract</h4>
      {{ props.session.abstract }}
    </div>

    <div v-for="speaker in props.session.speakers" class="mt-2 mb-2">
      <h3>{{ speaker.name }}</h3>

      <p v-if="speaker.bio">{{ speaker.bio }}</p>
    </div>
  </div>
</template>
