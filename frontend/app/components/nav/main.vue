<template>
  <v-app-bar title="JavaZone Sessions">
    <v-btn>
      <NuxtLink to="/">Search</NuxtLink>
    </v-btn>
    <v-btn>
      <NuxtLink to="/videos">All Videos</NuxtLink>
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
</template>