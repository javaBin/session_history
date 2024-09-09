<script setup lang="ts">
import type {Aggregate} from "@/types/api";
import {displayFormat, displayLanguage} from "@/types/aggregates";

const props = defineProps<{
  aggregate: Aggregate
}>()
</script>

<template>
  <div v-if="props.aggregate" class="m-6 flex flex-wrap">
    <div class="border shadow rounded-lg p-4 w-1/4 m-4 flex justify-between flex-col items-start">
      <h2>Languages</h2>

      <ul>
        <li v-for="language in props.aggregate.languages">{{ displayLanguage(language.language) }}: {{ language.count }}</li>
      </ul>
    </div>

    <div class="border shadow rounded-lg p-4 w-1/4 m-4 flex justify-between flex-col items-start">
      <h2>Formats</h2>

      <ul>
        <li v-for="format in props.aggregate.formats">{{ displayFormat(format.format) }}: {{ format.count }}</li>
      </ul>
    </div>

    <div class="border shadow rounded-lg p-4 w-1/4 m-4 flex justify-between flex-col items-start">
      <h2>Years</h2>

      <ul>
        <li v-for="year in props.aggregate.years">{{ year.year }}: {{ year.count }}</li>
      </ul>
    </div>
  </div>
</template>