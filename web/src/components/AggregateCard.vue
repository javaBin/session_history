<script setup lang="ts">
import type {Aggregate} from "@/types/api";
import {displayFormat, displayLanguage} from "@/types/aggregates";
import type {AggregateCardRow} from "@/types/helpers";

const props = defineProps<{
  title: string
  aggregate: AggregateCardRow[]
}>()
</script>

<template>
  <div class="card">
    <h2 class="card-title">{{ props.title }}</h2>

    <table class="table-auto w-full">
      <tr v-for="row in props.aggregate">
        <td>{{ row.name }}</td>
        <td class="w-32"><div class="card-count">{{ row.count }}</div></td>
      </tr>
    </table>
  </div>
</template>

<style scoped>
td {
  @apply p-1;
}

.card {
  @apply border shadow rounded-lg p-4 w-1/4 m-4 flex flex-col items-start;
}

.card-title {
  @apply text-2xl mb-2;
}

.card-count {
  @apply bg-blue-500 text-white rounded-full p-2 w-24 text-center;
}
</style>