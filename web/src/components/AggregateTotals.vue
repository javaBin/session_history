<script setup lang="ts">
import type {Aggregate} from "@/types/api";
import {displayFormat, displayLanguage} from "@/types/aggregates";
import {computed} from "vue";
import AggregateCard from "@/components/AggregateCard.vue";
import type {AggregateCardRow} from "@/types/helpers";

const props = defineProps<{
  aggregate: Aggregate
  filteredYear?: number
  filteredLanguage?: string
  filteredFormat?: string
}>()

const emit = defineEmits<{
  filterYear: [year: number]
  filterFormat: [format: string]
  filterLanguage: [language: string]
  clear: [filter: string]
}>()

const languageRow = computed(() => {
  return props.aggregate.languages.map((item) => {
        return {
          code: item.language,
          name: displayLanguage(item.language),
          count: item.count,
        } as AggregateCardRow
      }
  )
})

const formatRow = computed(() => {
  return props.aggregate.formats.map((item) => {
        return {
          code: item.format,
          name: displayFormat(item.format),
          count: item.count,
        } as AggregateCardRow
      }
  )
})

const yearRow = computed(() => {
  return props.aggregate.years.map((item) => {
        return {
          code: item.year.toString(),
          name: item.year.toString(),
          count: item.count,
        } as AggregateCardRow
      }
  )
})

const applyFilter = (filter: string, code: string) => {
  switch (filter) {
    case "LANGUAGE": {
      emit('filterLanguage', code)
      break;
    }
    case "FORMAT": {
      emit('filterFormat', code)
      break;
    }
    case "YEAR": {
      emit('filterYear', parseInt(code))
      break;
    }
  }
}

const clearFilter = (filter: string) => {
  emit('clear', filter)
}

</script>

<template>
  <div v-if="props.aggregate">
    <AggregateCard title="Languages"
                   :aggregate="languageRow"
                   @filter="applyFilter('LANGUAGE', $event)"
                   @clear="clearFilter('LANGUAGE')"
                   :filter="displayLanguage(filteredLanguage)"/>

    <AggregateCard title="Formats"
                   :aggregate="formatRow"
                   @filter="applyFilter('FORMAT', $event)"
                   @clear="clearFilter('FORMAT')"
                   :filter="displayFormat(filteredFormat)"/>

    <AggregateCard title="Years"
                   :aggregate="yearRow"
                   @filter="applyFilter('YEAR', $event)"
                   @clear="clearFilter('YEAR')"
                   :filter="filteredYear?.toString()"/>
  </div>
</template>
