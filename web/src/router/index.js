import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layouts */
import Layout from '@/layout'
import AdminLayout from '@/layout/admin'

export const constantRoutes = [
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
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/users',
    hidden: true,
    children: [
      {
        path: 'users',
        component: () => import('@/views/admin/users'),
        name: 'AdminUsers',
        meta: { title: 'User Management', icon: 'user', noCache: true }
      },
      {
        path: 'addresses',
        component: () => import('@/views/admin/addresses'),
        name: 'AdminAddresses',
        meta: { title: 'Address Management', icon: 'user', noCache: true }
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
