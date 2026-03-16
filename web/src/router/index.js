import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* 布局 */
import Layout from '@/layout'

export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    meta: { title: '登录' },
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/identification',
    hidden: true,
    children: [
      {
        path: 'identification',
        component: () => import('@/views/identification/index'),
        name: 'identification',
        meta: { title: '鸟类识别', icon: 'user', noCache: true }
      }
    ]
  },
  {
    path: '/center',
    component: Layout,
    redirect: '/center/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/center/index'),
        name: 'Center',
        meta: { title: '个人中心', icon: 'user', noCache: true }
      }
    ]
  },
]

export const errorRoutes = [
  // 404页必须放在末尾！
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export default router
