<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <header class="p-3">
    <h1 class="text-6xl">JavaZone Sessions</h1>

    <RouterLink class="p-3" to="/" >Search</RouterLink>
    <RouterLink class="p-3" to="/videos">All Videos</RouterLink>
  </header>
  <RouterView />
</template>

