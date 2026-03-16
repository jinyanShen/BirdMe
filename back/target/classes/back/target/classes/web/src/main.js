import Vue from 'vue'

import Cookies from 'js-cookie'

import Element from 'element-ui'
import './assets/styles/element-variables.scss'

import '@/assets/styles/index.scss' // global css
import '@/assets/styles/ruoyi.scss' // ruoyi css
import App from './App'
import router from './router'


import BaiduMap from 'vue-baidu-map'


import './assets/icons' // icon
import './permission' // permission control
// Pagination component
import Pagination from "@/components/Pagination";
// Header label component
import VueMeta from 'vue-meta'

import echarts from 'echarts'
Vue.prototype.$echarts = echarts

// Global component mount
Vue.component('Pagination', Pagination)

Vue.use(VueMeta)

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */

Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  render: h => h(App)
})
