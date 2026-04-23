import Vue from 'vue'
import Cookies from 'js-cookie'
import Element from 'element-ui'
import './assets/styles/element-variables.scss'
import '@/assets/styles/index.scss'
import '@/assets/styles/ruoyi.scss'
import App from './App'
import router from './router'
import BaiduMap from 'vue-baidu-map'
import './assets/icons'
import './permission'
import Pagination from "@/components/Pagination";
import VueMeta from 'vue-meta'
import echarts from 'echarts'

// Import LoginDialog - using correct path
import LoginDialog from '@/views/loginDialog/index.vue'

Vue.prototype.$echarts = echarts

Vue.component('Pagination', Pagination)

Vue.use(VueMeta)
Vue.use(Element, {
  size: Cookies.get('size') || 'medium'
})

Vue.config.productionTip = false

// Create login dialog instance
const LoginDialogConstructor = Vue.extend(LoginDialog)
const loginDialogInstance = new LoginDialogConstructor()
loginDialogInstance.$mount()
document.body.appendChild(loginDialogInstance.$el)

// Mount login dialog method to global
window.$showLoginDialog = (redirectPath) => {
  console.log('Showing login dialog, redirectPath:', redirectPath)
  if (loginDialogInstance && loginDialogInstance.show) {
    loginDialogInstance.show(redirectPath)
  } else {
    console.error('LoginDialog instance not ready')
  }
}

window.$loginDialog = loginDialogInstance

// Create Vue instance
new Vue({
  el: '#app',
  router,
  render: h => h(App)
})
