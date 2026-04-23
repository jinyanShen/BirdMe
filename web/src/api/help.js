import request from '@/utils/request'

export function geocode(address) {
  return request({
    url: '/geocode',
    method: 'post',
    data: {
      address
    }
  })
}
