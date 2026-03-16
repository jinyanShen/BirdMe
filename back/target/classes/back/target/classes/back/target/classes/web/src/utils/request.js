import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import { logout } from '@/api/login'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // api的base_url
  timeout: 300000 // 请求超时时间
})

// request拦截器
service.interceptors.request.use(
  config => {
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

// response拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    const config = response.config
    // 二进制流处理
    if (config && config.responseType === 'blob') {
      return response
    }
    if (res.code === 401) {
      MessageBox.confirm('登录状态已过期，请您重新登录', '系统提示', {
        confirmButtonText: '重新登录',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        logout()
      })
    } else {
      return res
    }
  },
  error => {
    console.log('请求异常：' + error)
    Message({
      message: error.message,
      type: 'error',
      duration: 2000
    })
    return Promise.reject(error)
  }
)

export default service
