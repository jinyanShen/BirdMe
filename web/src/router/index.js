import Vue from 'vue'
import Router from 'vue-router'
import MainMenu from '@/views/mainmenu/mainmenu.vue'  // 导入你的首页组件

Vue.use(Router)

/* Layouts */
import Layout from '@/layout'
import AdminLayout from '@/layout/admin'

export const constantRoutes = [
  // 添加首页路由 - 放在最前面
  {
    path: '/',
    component: MainMenu,  // 直接使用你的 mainmenu 组件
    name: 'MainMenu',
    meta: { title: '鸟类保护主页' },
    hidden: true  // 不在菜单中显示
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    meta: { title: 'Login' },
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
        meta: { title: 'Bird Identification', icon: 'user', noCache: true }
      }
    ]
  },
  // ... 其他路由保持不变，不用动
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
        meta: { title: 'Personal Center', icon: 'user', noCache: true }
      }
    ]
  },
  {
    path: '/report',
    component: Layout,
    redirect: '/report/index',
    hidden: true,
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
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export default router
