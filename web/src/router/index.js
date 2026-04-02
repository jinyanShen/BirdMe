import Vue from 'vue'
import Router from 'vue-router'
import MainMenu from '@/views/mainmenu/mainmenu.vue'

Vue.use(Router)

/* Layouts */
import Layout from '@/layout'
import AdminLayout from '@/layout/admin'

export const constantRoutes = [
  {
    path: '/',
    component: MainMenu,
    name: 'MainMenu',
    meta: { title: '鸟类保护主页' },
    hidden: true
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    meta: { title: 'Login' },
    hidden: true
  },
  {
    path: '/identification',
    component: () => import('@/views/identification/index'),
    name: 'identification',
    meta: { title: 'Bird Identification', requiresAuth: true }
  },
  {
    path: '/center',
    component: Layout,
    redirect: '/center/index',
    hidden: true,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'index',
        component: () => import('@/views/center/index'),
        name: 'Center',
        meta: { title: 'Personal Center', icon: 'user', noCache: true }
      }
    ]
  },
  {
    path: '/report',
    component: Layout,
    redirect: '/report/index',
    hidden: true,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'index',
        component: () => import('@/views/report/index'),
        name: 'report',
        meta: { title: 'Report', icon: 'user', noCache: true }
      }
    ]
  },
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    hidden: true,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/admin/dashboard'),
        name: 'AdminDashboard',
        meta: { title: 'Dashboard', icon: 'dashboard', noCache: true }
      },
      {
        path: 'users',
        component: () => import('@/views/admin/users'),
        name: 'AdminUsers',
        meta: { title: 'User Management', icon: 'user', noCache: true }
      },
      {
        path: 'profile',
        component: () => import('@/views/admin/profile'),
        name: 'AdminProfile',
        meta: { title: 'Personal Profile', icon: 'user', noCache: true }
      },
      {
        path: 'reports',
        component: () => import('@/views/admin/reports'),
        name: 'AdminReports',
        meta: { title: 'Report Management', icon: 'document', noCache: true }
      },
      {
        path: 'rescue-stations',
        component: () => import('@/views/admin/rescue_stations'),
        name: 'AdminRescueStations',
        meta: { title: 'Rescue Station Management', icon: 'location', noCache: true }
      },
    ]
  },
]

export const errorRoutes = [
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  mode: 'history',
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// 删除所有路由守卫代码，让 permission.js 处理
export default router
