<script setup lang="ts">
import type {Aggregate} from "@/types/api";
import {displayFormat, displayLanguage} from "@/types/aggregates";
import {computed} from "vue";
import AggregateCard from "@/components/AggregateCard.vue";
import type {AggregateCardRow} from "@/types/helpers";

const props = defineProps<{
  aggregate: Aggregate
}>()

const emit = defineEmits<{
  filterYear: [year: number]
  filterFormat: [format: string]
  filterLanguage: [language: string]
}>()

const languageRow = computed(() => {
  return props.aggregate.languages.map((item) => {
        return {
          code: item.language,
          filter: "LANGUAGE",
          name: displayLanguage(item.language),
          count: item.count
        } as AggregateCardRow
      }
  )
})

const formatRow = computed(() => {
  return props.aggregate.formats.map((item) => {
        return {
          code: item.format,
          filter: "FORMAT",
          name: displayFormat(item.format),
          count: item.count
        } as AggregateCardRow
      }
  )
})

const yearRow = computed(() => {
  return props.aggregate.years.map((item) => {
        return {
          code: item.year.toString(),
          filter: "YEAR",
          name: item.year.toString(),
          count: item.count
        } as AggregateCardRow
      }
  )
})

const applyFilter = (params: string[]) => {
  const [code, filter] = params
  switch (filter) {
    case "LANGUAGE": {
      emit('filterLanguage', [code])
      break;
    }
    case "FORMAT": {
      emit('filterFormat', [code])
      break;
    }
    case "YEAR": {
      emit('filterYear', [parseInt(code)])
      break;
    }
  }
}
</script>

<template>
  <div v-if="props.aggregate">
    <AggregateCard title="Languages" :aggregate="languageRow" @filter="applyFilter"/>
    <AggregateCard title="Formats" :aggregate="formatRow" @filter="applyFilter"/>
    <AggregateCard title="Years" :aggregate="yearRow" @filter="applyFilter"/>
  </div>
</template>
