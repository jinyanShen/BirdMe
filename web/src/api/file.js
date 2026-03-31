import request from '@/utils/request'

// Upload and identify bird image
export function identifyBird(file) {
  const formData = new FormData();
  formData.append('file', file);
  return request({
    url: '/file/identification',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}