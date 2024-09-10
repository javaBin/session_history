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
  <div>
    <h2>{{ props.title }}</h2>

    <table>
      <tr v-for="row in props.aggregate">
        <td>{{ row.name }}</td>
        <td>
          <div class="card-count">{{ row.count }}</div>
        </td>
      </tr>
    </table>
  </div>
</template>
