import request from '@/utils/request'

export function geocode(address) {
  return request({
    url: '//geocode',
    method: 'post',
    data: {
      address
    }
  })
}

export function searchNearbyPetHospitals(latitude, longitude, radius) {
  return request({
    url: '/geocode/searchPetHospitals',
    method: 'post',
    data: {
      latitude,
      longitude,
      radius
    }
  })
}

export function getAllStations() {
  return request({
    url: '/rescueStation/all',
    method: 'get'
  })
}

export function reverseGeocode(latitude, longitude) {
  return request({
    url: '/reverseGeocode',
    method: 'post',
    data: {
      latitude,
      longitude
    }
  })
}
