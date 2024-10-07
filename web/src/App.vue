<script setup lang="ts">
import { RouterView } from 'vue-router'
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
      <v-btn class="ms-16">
        <a href="https://java.no" target="_blank"><v-icon icon="fa-brands fa-java" /></a>
      </v-btn>
      <v-btn>
        <a href="https://github.com/javaBin/session_history" target="_blank"
          ><v-icon icon="fa-brands fa-github"
        /></a>
      </v-btn>
    </v-app-bar>

    <v-main class="align-center justify-center" style="min-height: 300px">
      <RouterView />
    </v-main>
  </v-layout>
</template>
