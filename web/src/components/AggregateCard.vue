<script setup lang="ts">
import type {AggregateCardRow} from "@/types/helpers";

const props = defineProps<{
  title: string
  aggregate: AggregateCardRow[]
}>()

const emit = defineEmits<{
  filter: [value: string]
}>()

const performFilter = (value: string) => {
  emit('filter', value)
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
      <v-row v-for="row in props.aggregate" align="center" dense>
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