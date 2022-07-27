import './bootstrap.js'

import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'

import navbar from './components/Navbar.vue'
import list from './components/List.vue'
import houseMap from './components/HouseMap.vue'

import VueSweetalert2 from 'vue-sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';

const vueapp = createApp({})
const router = createRouter({
      history: createWebHistory(),
      routes: [],
});

vueapp.use(router)
vueapp.use(VueSweetalert2);

vueapp.component('navbar', navbar)
      .component('List', list)
      .component('hosue-map', houseMap)
      ;

vueapp.mount('#app');
