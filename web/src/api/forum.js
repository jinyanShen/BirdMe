import request from '@/utils/request'

// Get post list by category
export function getPosts(category, keyword) {
  const params = {}
  if (category !== 'all') {
    params.category = category
  }
  if (keyword && keyword.trim()) {
    params.keyword = keyword
  }

  return request({
    url: '/forum/posts',
    method: 'get',
    params
  })
}

// Get post details by ID
export function getPostById(id) {
  return request({
    url: `/forum/post/${id}`,
    method: 'get'
  })
}

// Get replies by post ID
export function getReplies(postId) {
  return request({
    url: '/forum/replies',
    method: 'get',
    params: { postId }
  })
}

// Create new post
export function createPost(data) {
  return request({
    url: '/forum/post',
    method: 'post',
    data
  })
}

// Update post
export function updatePost(id, data) {
  return request({
    url: `/forum/post/${id}`,
    method: 'put',
    data
  })
}

// Create new reply
export function createReply(data) {
  return request({
    url: '/forum/reply',
    method: 'post',
    data
  })
}

// Increment view count
export function incrementViewCount(id) {
  return request({
    url: `/forum/post/${id}/view`,
    method: 'post'
  })
}
