import router from './router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

// 添加 '/' 到白名单
const whiteList = ['/', '/login', '/auth-redirect']  // 这里改了

router.beforeEach(async(to, from, next) => {
  NProgress.start()
  let id = sessionStorage.getItem('id')
  if (id) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      next()
    }
  } else {
    /* 没有token */
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next('/login')
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
