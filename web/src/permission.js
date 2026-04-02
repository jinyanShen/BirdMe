// import router from './router'
// import NProgress from 'nprogress'
// import 'nprogress/nprogress.css'
//
// NProgress.configure({ showSpinner: false })
//
// // 添加 '/' 到白名单
// const whiteList = ['/', '/login', '/auth-redirect']  // 这里改了
//
// router.beforeEach(async(to, from, next) => {
//   NProgress.start()
//   let id = sessionStorage.getItem('id')
//   if (id) {
//     if (to.path === '/login') {
//       next({ path: '/' })
//       NProgress.done()
//     } else {
//       next()
//     }
//   } else {
//     /* 没有token */
//     if (whiteList.indexOf(to.path) !== -1) {
//       next()
//     } else {
//       next('/login')
//       NProgress.done()
//     }
//   }
// })
//
// router.afterEach(() => {
//   NProgress.done()
// })
import router from './router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

// 白名单页面（不需要登录）
const whiteList = ['/', '/login']

// 路由守卫
router.beforeEach((to, from, next) => {
  NProgress.start()

  const isLoggedIn = sessionStorage.getItem('id') !== null
  console.log('=== Permission.js ===')
  console.log('Navigating to:', to.path)
  console.log('Is logged in:', isLoggedIn)
  console.log('From path:', from.path)

  // 白名单页面直接放行
  if (whiteList.includes(to.path)) {
    console.log('White list page, allow access')
    next()
    NProgress.done()
    return
  }

  // 检查路由是否需要登录
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth) || to.meta.requiresAuth
  console.log('Requires auth:', requiresAuth)

  // 如果需要登录且已登录，放行
  if (requiresAuth && isLoggedIn) {
    console.log('Authenticated, allow access')
    next()
    NProgress.done()
    return
  }

  // 如果需要登录但未登录，显示登录弹窗
  if (requiresAuth && !isLoggedIn) {
    console.log('Need login, showing dialog for:', to.fullPath)
    NProgress.done()

    // 阻止导航
    next(false)

    // 显示登录弹窗
    if (window.$showLoginDialog) {
      window.$showLoginDialog(to.fullPath)
    }
    return
  }

  // 其他情况直接放行（包括首页）
  console.log('Other page, allow access')
  next()
  NProgress.done()
})

export default router
