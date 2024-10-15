<script setup lang="ts">
import type { AggregateCardRow } from '@/types/helpers'

const props = defineProps<{
  title: string
  aggregate: AggregateCardRow[]
  filter?: string
}>()

const emit = defineEmits<{
  filter: [value: string]
  clear: []
}>()

const performFilter = (value: string) => {
  emit('filter', value)
}

const clearFilter = () => {
  emit('clear')
}
</script>

<template>
  <v-card variant="elevated">
    <v-card-item>
      <v-card-title>
        {{ props.title }}
      </v-card-title>
    </v-card-item>
    <v-card-text>
      <v-row v-if="props.filter">
        <v-col class="text-body-2">
          <Icon name="mdi:close" @click="clearFilter()" />
          <div class="filter-link pl-2 d-inline-block" @click="clearFilter()">
            {{ props.filter }}
          </div>
        </v-col>
      </v-row>
      <v-row v-else v-for="row in props.aggregate" :key="row.code" align="center" dense>
        <v-col class="text-body-2">
          <div class="filter-link" @click="performFilter(row.code)">{{ row.name }}</div>
        </v-col>
        <v-col class="text-body-2 text-right">
          <v-chip>
            {{ row.count }}
          </v-chip>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<style scoped>
.filter-link:hover {
  text-decoration: underline;
}
</style>
