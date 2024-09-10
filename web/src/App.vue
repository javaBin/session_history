<script setup lang="ts">
import {RouterView} from 'vue-router'
</script>

<template>
  <v-layout class="rounded rounded-md">
    <v-app-bar title="JavaZone Sessions">
      <v-btn>
        <RouterLink to="/">Search</RouterLink>
      </v-btn>
      <v-btn>
        <RouterLink to="/videos">All Videos</RouterLink>
      </v-btn>
    </v-app-bar>

    <v-main class="d-flex align-center justify-center" style="min-height: 300px;">
      <RouterView/>
    </v-main>
  </v-layout>
</template>

