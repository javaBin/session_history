<script setup lang="ts">
import type {Aggregate} from "@/types/api";
import {displayFormat, displayLanguage} from "@/types/aggregates";
import {computed} from "vue";
import AggregateCard from "@/components/AggregateCard.vue";
import type {AggregateCardRow} from "@/types/helpers";

const props = defineProps<{
  aggregate: Aggregate
}>()

const languageRow = computed(() => {
  return props.aggregate.languages.map((item) => {
        return {
          name: displayLanguage(item.language),
          count: item.count
        } as AggregateCardRow
      }
  )
})

const formatRow = computed(() => {
  return props.aggregate.formats.map((item) => {
        return {
          name: displayFormat(item.format),
          count: item.count
        } as AggregateCardRow
      }
  )
})

const yearRow = computed(() => {
  return props.aggregate.years.map((item) => {
        return {
          name: item.year.toString(),
          count: item.count
        } as AggregateCardRow
      }
  )
})
</script>

<template>
  <div v-if="props.aggregate" class="flex flex-wrap gap-4">
    <AggregateCard title="Languages" :aggregate="languageRow" />
    <AggregateCard title="Formats" :aggregate="formatRow" />
    <AggregateCard title="Years" :aggregate="yearRow" />
  </div>
</template>
