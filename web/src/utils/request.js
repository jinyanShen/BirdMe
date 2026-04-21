import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import { logout } from '@/api/login'

// Create axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // api base_url
  timeout: 300000 // Request timeout
})

// Request interceptor
service.interceptors.request.use(
  config => {
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    const config = response.config
    // Binary stream handling
    if (config && config.responseType === 'blob') {
      return response
    }
    if (res.code === 401) {
      MessageBox.confirm('Login status expired, please login again', 'System', {
        confirmButtonText: 'Login',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        logout()
      })
    } else {
      return res
    }
  },
  error => {
    console.log('Request error: ' + error)
    Message({
      message: error.message,
      type: 'error',
      duration: 2000
    })
    return Promise.reject(error)
  }
)

export default service
