import Vue from 'vue'
import VueRouter from 'vue-router'
import Step2 from '../views/Step2.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/2',
    name: 'Step2',
    component: Step2
  },
  {
    path: '/',
    name: 'Step1',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/Step1.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
