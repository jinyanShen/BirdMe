import request from '@/utils/request'

// Query list
export function pageRescueStation(query) {
  return request({
    url: '/rescue-station/page',
    method: 'post',
    data: query
  })
}

// Query all list
export function allRescueStation() {
  return request({
    url: '/rescue-station/all',
    method: 'get'
  })
}

// Query details
export function getRescueStation(id) {
  return request({
    url: '/rescue-station/getOne',
    method: 'get',
    params: { id: id}
  })
}

// Add new
export function insertRescueStation(data) {
  return request({
    url: '/rescue-station/insert',
    method: 'post',
    data: data
  })
}

// Update
export function updateRescueStation(data) {
  return request({
    url: '/rescue-station/update',
    method: 'post',
    data: data
  })
}

// Delete
export function delRescueStation(ids) {
  return request({
    url: '/rescue-station/delete',
    method: 'post',
    data: ids
  })
}

// Get nearby rescue stations
export function getNearbyStations(latitude, longitude, radius = 50) {
  return request({
    url: '/rescue-station/nearby',
    method: 'get',
    params: { latitude, longitude, radius }
  })
}
