import router from './router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

// White list pages (no login required)
const whiteList = ['/', '/login']

// Route guard
router.beforeEach((to, from, next) => {
  NProgress.start()

  const isLoggedIn = sessionStorage.getItem('id') !== null
  console.log('=== Permission.js ===')
  console.log('Navigating to:', to.path)
  console.log('Is logged in:', isLoggedIn)
  console.log('From path:', from.path)

  // White list pages - allow access directly
  if (whiteList.includes(to.path)) {
    console.log('White list page, allow access')
    next()
    NProgress.done()
    return
  }

  // Check if route requires login
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth) || to.meta.requiresAuth
  console.log('Requires auth:', requiresAuth)

  // If login required and user is logged in - allow access
  if (requiresAuth && isLoggedIn) {
    console.log('Authenticated, allow access')
    next()
    NProgress.done()
    return
  }

  // If login required but not logged in - show login dialog
  if (requiresAuth && !isLoggedIn) {
    console.log('Need login, showing dialog for:', to.fullPath)
    NProgress.done()

    // Block navigation
    next(false)

    // Show login dialog
    if (window.$showLoginDialog) {
      window.$showLoginDialog(to.fullPath)
    }
    return
  }

  // Other pages - allow access (including home page)
  console.log('Other page, allow access')
  next()
  NProgress.done()
})

export default router
