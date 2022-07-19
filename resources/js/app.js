import './bootstrap.js'

import { createApp } from 'vue'
import navbar from './components/Navbar.vue'
import list from './components/List.vue'

const vueapp = createApp({})

vueapp.component('navbar', navbar)
      .component('List', list)
       ;

vueapp.mount('#app');