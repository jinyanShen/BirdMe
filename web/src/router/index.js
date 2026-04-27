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
    meta: { title: 'Bird Conservation Home' },
    hidden: true
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    meta: { title: 'Login' },
    hidden: true
  },
  {
    path: '/knowledge',
    component: () => import('@/views/knowledge/index'),
    redirect: '/knowledge/index',
    hidden: true,
    meta: { title: 'Knowledge', requiresAuth: true },
    children: [
      {
        path: 'index',
        component: () => import('@/views/knowledge/index'),
        name: 'KnowledgeMain',
        meta: { title: 'Knowledge' }
      },
      {
        path: 'facts',
        component: () => import('@/views/knowledge/FunFacts'),
        name: 'FunFacts',
        meta: { title: 'Fun Facts', requiresAuth: true }
      }
    ]
  },
  {
    path: '/settings',
    component: () => import('@/views/settings/index'),
    redirect: '/settings/index',
    hidden: true,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'index',
        component: () => import('@/views/settings/index'),
        name: 'Settings',
        meta: { title: 'Personal Settings', icon: 'user', noCache: true }
      }
    ]
  },
  {
    path: '/help',
    component: () => import('@/views/help/index'),
    redirect: '/help/index',
    hidden: true,
    meta: { title: 'Help' },
    children: [
      {
        path: 'index',
        component: () => import('@/views/help/index'),
        name: 'Help',
        meta: { title: 'Help' }
      }
    ]
  },
  {
    path: '/game',
    // component: Layout,
    component: () => import('@/views/game/GameContainer'),
    redirect: '/game/index',
    hidden: true,
    meta: { title: 'Game' },
    children: [
      {
        path: 'index',
        component: () => import('@/views/game/game'),
        name: 'Game',
        meta: { title: 'Game' }
      },
      {
        path: ':gameKey',
        component: () => import('@/views/game/gameSubpage'),
        name: 'GameSubpage',
        hidden: true,
        meta: { title: 'Game' }
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
  {
    path: '/forum',
    component: () => import('@/views/forum/Forum'),
    redirect: '/forum/birdwatching',
    hidden: true,
    meta: { title: 'Forum' },
    children: [
      {
        path: 'birdwatching',
        component: () => import('@/views/forum/Forum'),
        name: 'ForumBirdWatching',
        meta: { title: 'Bird Watching' }
      },
      {
        path: 'qa',
        component: () => import('@/views/forum/Forum'),
        name: 'ForumQA',
        meta: { title: 'Q&A' }
      },
      {
        path: 'post/:id',
        component: () => import('@/views/forum/PostDetail'),
        name: 'PostDetail',
        hidden: true,
        meta: { title: 'Post Detail' }
      }
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

// Delete all route guard code, let permission.js handle
export default router
