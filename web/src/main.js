// import Vue from 'vue'
//
// import Cookies from 'js-cookie'
//
// import Element from 'element-ui'
// import './assets/styles/element-variables.scss'
//
// import '@/assets/styles/index.scss' // global css
// import '@/assets/styles/ruoyi.scss' // ruoyi css
// import App from './App'
// import router from './router'
//
//
// import BaiduMap from 'vue-baidu-map'
//
//
// import './assets/icons' // icon
// import './permission' // permission control
// // Pagination component
// import Pagination from "@/components/Pagination";
// // Header label component
// import VueMeta from 'vue-meta'
//
// import echarts from 'echarts'
// Vue.prototype.$echarts = echarts
//
// // Global component mount
// Vue.component('Pagination', Pagination)
//
// Vue.use(VueMeta)
//
// /**
//  * If you don't want to use mock-server
//  * you want to use MockJs for mock api
//  * you can execute: mockXHR()
//  *
//  * Currently MockJs will be used in the production environment,
//  * please remove it before going online! ! !
//  */
//
// Vue.use(Element, {
//   size: Cookies.get('size') || 'medium' // set element-ui default size
// })
//
// Vue.config.productionTip = false
//
// new Vue({
//   el: '#app',
//   router,
//   render: h => h(App)
// })
// / 创建登录弹窗实例
// const LoginDialogConstructor = Vue.extend(LoginDialog)
// const loginDialogInstance = new LoginDialogConstructor()
// loginDialogInstance.$mount()
// document.body.appendChild(loginDialogInstance.$el)
//
// // 将登录弹窗方法挂载到全局
// window.$showLoginDialog = (redirectPath) => {
//   loginDialogInstance.show(redirectPath)
// }
//
// // 添加全局路由守卫的备用方法
// window.$loginDialog = loginDialogInstance
//
// new Vue({
//   router,
//   store,
//   render: h => h(App)
// }).$mount('#app')
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

// 导入 LoginDialog - 使用正确的路径
import LoginDialog from '@/views/loginDialog/index.vue'

Vue.prototype.$echarts = echarts

Vue.component('Pagination', Pagination)

Vue.use(VueMeta)
Vue.use(Element, {
  size: Cookies.get('size') || 'medium'
})

Vue.config.productionTip = false

// 创建登录弹窗实例
const LoginDialogConstructor = Vue.extend(LoginDialog)
const loginDialogInstance = new LoginDialogConstructor()
loginDialogInstance.$mount()
document.body.appendChild(loginDialogInstance.$el)

// 将登录弹窗方法挂载到全局
window.$showLoginDialog = (redirectPath) => {
  console.log('Showing login dialog, redirectPath:', redirectPath)
  if (loginDialogInstance && loginDialogInstance.show) {
    loginDialogInstance.show(redirectPath)
  } else {
    console.error('LoginDialog instance not ready')
  }
}

window.$loginDialog = loginDialogInstance

// 创建 Vue 实例
new Vue({
  el: '#app',
  router,
  render: h => h(App)
})
