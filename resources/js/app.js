import './bootstrap.js'

import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'

import navbar from './components/Navbar.vue'
import list from './components/List.vue'

const vueapp = createApp({})
const router = createRouter({
      history: createWebHistory(),
      routes: [],
});

vueapp.use(router)

vueapp.component('navbar', navbar)
      .component('List', list)
      ;

vueapp.mount('#app');
