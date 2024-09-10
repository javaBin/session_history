<script setup lang="ts">
import type {Session} from "@/types/api";
import {displayFormat, displayLanguage} from "../types/aggregates";

const props = defineProps<{
  session: Session
}>()

</script>
<template>
  <v-card class="ma-2 pa-2" width="500">
    <v-card-title class="text-h4 position-relative pt-6">
      <div class="text-body-2 pa-2 position-absolute top-0 right-0 bg-primary rounded-lg d-inline-block">{{ props.session.year }}</div>
      {{ props.session.title }}
    </v-card-title>

    <v-card-subtitle>
      {{ displayFormat(props.session.format) }} | {{ displayLanguage(props.session.language) }}

      <span v-if="props.session.video">
        | <a :href="props.session.video" target="_blank">Video</a>
      </span>
    </v-card-subtitle>

    <v-card-text>
      <div class="mb-2" v-if="props.session.intendedAudience">
        <div class="text-h5">Intended Audience</div>
        {{ props.session.intendedAudience }}
      </div>

      <div class="mb-2" v-if="props.session.abstract">
        <div class="text-h5">Abstract</div>
        {{ props.session.abstract }}
      </div>

      <div class="mb-2" v-if="session.speakers?.length > 0">
        <div class="text-h5">Speakers</div>
      </div>

      <div v-for="speaker in props.session.speakers" class="mt-2 mb-2">
        <div class="text-h6">{{ speaker.name }}</div>

        <p v-if="speaker.bio">{{ speaker.bio }}</p>
      </div>
    </v-card-text>
  </v-card>
</template>

<style scoped>
.v-card-text, .v-card-title {
  white-space: normal;
}
</style>