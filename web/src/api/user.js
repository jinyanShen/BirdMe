import request from '@/utils/request'

// Query list
export function pageUser(query) {
  return request({
    url: '/user/page',
    method: 'post',
    data: query
  })
}

// Query all list
export function allUser() {
  return request({
    url: '/user/all',
    method: 'get'
  })
}

// Query details
export function getUser(id) {
  return request({
    url: '/user/getOne',
    method: 'get',
    params: { id: id}
  })
}

// Add new
export function insertUser(data) {
  return request({
    url: '/user/insert',
    method: 'post',
    data: data
  })
}

// Update
export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data: data
  })
}

// Delete
export function delUser(ids) {
  return request({
    url: '/user/delete',
    method: 'post',
    data: ids
  })
}

// Get current user info
export function getCurrentUser(id) {
  return request({
    url: '/user/getOne',
    method: 'get',
    params: { id: id}
  })
}