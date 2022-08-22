import './bootstrap.js'

import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'

import navbar from './components/Navbar.vue'
import list from './components/List.vue'
import houseMap from './components/HouseMap.vue'
import houseThreeD from './components/HouseThreeD.vue'
import houseThreeDTest from './components/HouseThreeDTest.vue' // test


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
      .component('house-three-d', houseThreeD)
      .component('house-three-d-test', houseThreeDTest) // test
      ;

vueapp.mount('#app');
