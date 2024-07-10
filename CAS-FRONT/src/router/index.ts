import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'HomeView',
      component: () => import ('../components/Main.vue'),
    },
    {
      path: '/main',
      name: 'MainView',
      component: () => import ('../components/Main.vue'),
    },
  ]
})

export default router
