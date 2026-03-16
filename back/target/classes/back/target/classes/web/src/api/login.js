import request from '@/utils/request'

// Login method
export function login(username, password) {
  const data = {
    username,
    password
  }
  return request({
    url: '/login',
    method: 'post',
    data: data
  })
}

export function register(data) {
  return request({
    url: '/user/insert',
    method: 'post',
    data: data
  })
}

// Logout method
export function logoutSys() {
  return request({
    url: '/logout',
    method: 'post'
  })
}
