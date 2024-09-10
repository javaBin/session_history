<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <header class="p-3">
    <h1>JavaZone Sessions</h1>

    <RouterLink to="/" >Search</RouterLink>
    <RouterLink to="/videos">All Videos</RouterLink>
  </header>
  <RouterView />
</template>

