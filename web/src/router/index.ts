import { createRouter, createWebHistory } from 'vue-router'
import SearchView from '@/views/SearchView.vue'
import AllVideosView from '@/views/AllVideosView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'search',
      component: SearchView
    },
    {
      path: '/videos',
      name: 'allVideos',
      props: true,
      component: AllVideosView
    }
  ]
})

export default router
