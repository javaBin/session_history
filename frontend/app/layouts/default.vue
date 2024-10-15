<script setup lang="ts">
</script>

<template>
  <v-layout class="rounded rounded-md">
    <NavMain />

    <v-main class="align-center justify-center" style="min-height: 300px">
      <slot />
    </v-main>
  </v-layout>
</template>
