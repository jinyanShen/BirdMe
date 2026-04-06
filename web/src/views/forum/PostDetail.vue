<template>
  <div class="post-detail-container">
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <div class="navbar-container">
        <div class="logo">
          <h3 @click="goToHome" style="cursor: pointer;">BirdME</h3>
        </div>
        <div class="nav-menu">
          <span class="nav-item" @click="goToHome">Homepage</span>
          <div class="dropdown">
            <span class="nav-item">Knowledge ▾</span>
            <div class="dropdown-menu">
              <div class="dropdown-item" @click="goToKnowledgePage('migration')">Migration Map</div>
              <div class="dropdown-item" @click="goToKnowledgePage('identification')">Identification</div>
              <div class="dropdown-item" @click="goToFunFacts">Fun Facts</div>
            </div>
          </div>
          <div class="dropdown">
            <span class="nav-item" :class="{ active: true }">Forum ▾</span>
            <div class="dropdown-menu">
              <div class="dropdown-item" @click="goToForum('birdwatching')">Bird Watching</div>
              <div class="dropdown-item" @click="goToForum('qa')">Q&A</div>
            </div>
          </div>
          <div class="dropdown">
            <span class="nav-item">Game ▾</span>
            <div class="dropdown-menu">
              <div class="dropdown-item" @click="goToGamePage('flappy')">Flappy</div>
              <div class="dropdown-item" @click="goToGamePage('2048')">2048</div>
              <div class="dropdown-item" @click="goToGamePage('merge')">Merge</div>
            </div>
          </div>
          <span class="nav-item" @click="goToPersonalPage">Personal Setting</span>
          <span v-if="!isLoggedIn" class="nav-item login-btn" @click="goToLogin">Login</span>
          <span v-else class="nav-item logout-btn" @click="handleLogout">LogOut</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="post-detail-content">
      <div v-if="loading" class="loading">Loading...</div>
      <div v-else-if="!post" class="empty-state">Post not found</div>
      <div v-else>
        <!-- 帖子详情 -->
        <div class="post-detail">
          <div class="post-header">
            <h1 class="post-title">
              <span v-if="post.isPinned === 1" class="pinned-badge">📌 Pinned</span>
              <span v-if="post.category === 'qa'" class="qa-badge">❓ Q&A</span>
              {{ post.title }}
            </h1>
            <div class="post-meta">
              <div class="author-info">
                <img :src="post.authorAvatar || '/default-avatar.png'" class="author-avatar" />
                <span class="author-name">{{ post.authorName }}</span>
              </div>
              <span class="post-time">{{ formatDate(post.createdAt) }}</span>
              <span class="post-tag">{{ post.tag }}</span>
            </div>
          </div>
          
          <div v-if="post.imageUrl" class="post-image">
            <img :src="post.imageUrl" alt="Post image" />
          </div>
          
          <div class="post-content">
            {{ post.content }}
          </div>
          
          <div class="post-stats">
            <span><i class="icon-eye"></i> {{ post.viewCount }} views</span>
            <span><i class="icon-message"></i> {{ post.replyCount }} replies</span>
          </div>
        </div>

        <!-- 回复列表 -->
        <div class="replies-section">
          <h3 class="replies-title">Replies ({{ replies.length }})</h3>
          
          <div v-if="replies.length === 0" class="no-replies">
            No replies yet. Be the first to reply!
          </div>
          
          <div v-else class="replies-list">
            <div v-for="reply in replies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <img :src="reply.authorAvatar || '/default-avatar.png'" class="reply-avatar" />
                <span class="reply-author">{{ reply.authorName }}</span>
                <span class="reply-time">{{ formatDate(reply.createdAt) }}</span>
              </div>
              <div class="reply-content">
                {{ reply.content }}
              </div>
            </div>
          </div>
        </div>

        <!-- 回复表单 -->
        <div class="reply-form-section" v-if="isLoggedIn">
          <h3>Leave a Reply</h3>
          <textarea 
            v-model="replyContent" 
            class="reply-input"
            placeholder="Write your reply..."
            rows="4"
          ></textarea>
          <button class="submit-reply-btn" @click="submitReply" :disabled="submitting">
            {{ submitting ? 'Submitting...' : 'Submit Reply' }}
          </button>
        </div>
        <div v-else class="login-hint">
          Please <a href="#" @click.prevent="goToLogin">login</a> to leave a reply.
        </div>

        <!-- 返回按钮 -->
        <button class="back-btn" @click="goBack">← Back to Forum</button>
      </div>
    </div>
  </div>
</template>

<script>
import { getPostById, getReplies, createReply } from '@/api/forum'

export default {
  name: 'PostDetail',
  data() {
    return {
      isLoggedIn: false,
      post: null,
      replies: [],
      loading: false,
      replyContent: '',
      submitting: false
    }
  },
  mounted() {
    this.checkLoginStatus()
    this.loadPostDetail()
  },
  methods: {
    checkLoginStatus() {
      this.isLoggedIn = sessionStorage.getItem('id') !== null
    },
    
    async loadPostDetail() {
      this.loading = true
      try {
        const postId = this.$route.params.id
        const postResponse = await getPostById(postId)
        if (postResponse && postResponse.code === 200) {
          this.post = postResponse.data
          
          const repliesResponse = await getReplies(postId)
          if (repliesResponse && repliesResponse.code === 200) {
            this.replies = repliesResponse.data || []
          }
        }
      } catch (error) {
        console.error('Failed to load post detail:', error)
        this.post = null
        this.replies = []
      } finally {
        this.loading = false
      }
    },
    
    async submitReply() {
      if (!this.replyContent.trim()) {
        alert('Please enter reply content')
        return
      }
      
      this.submitting = true
      try {
        const userId = sessionStorage.getItem('id')
        const username = sessionStorage.getItem('username') || 'User'
        const userAvatar = sessionStorage.getItem('avatarUrl')
        
        const replyData = {
          postId: parseInt(this.$route.params.id),
          content: this.replyContent,
          authorId: parseInt(userId),
          authorName: username,
          authorAvatar: userAvatar
        }
        
        const response = await createReply(replyData)
        if (response && response.code === 200) {
          alert('Reply submitted successfully!')
          this.replyContent = ''
          this.loadPostDetail()
        }
      } catch (error) {
        console.error('Failed to submit reply:', error)
        alert('Failed to submit reply. Please try again.')
      } finally {
        this.submitting = false
      }
    },
    
    goToHome() {
      this.$router.push('/')
    },
    
    goToKnowledgePage(tab) {
      if (this.isLoggedIn) {
        this.$router.push(`/knowledge/${tab}`)
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog(`/knowledge/${tab}`)
        }
      }
    },
    
    goToFunFacts() {
      if (this.isLoggedIn) {
        this.$router.push('/knowledge/facts')
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/knowledge/facts')
        }
      }
    },
    
    goToForum(category) {
      this.$router.push(`/forum?category=${category}`)
    },
    
    goToGamePage(game) {
      this.$router.push(`/game/${game}`)
    },
    
    goToPersonalPage() {
      if (this.isLoggedIn) {
        this.$router.push('/center')
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/center')
        }
      }
    },
    
    goToLogin() {
      this.$router.push('/login')
    },
    
    handleLogout() {
      sessionStorage.clear()
      this.isLoggedIn = false
      this.$router.push('/')
    },
    
    goBack() {
      this.$router.push('/forum')
    },
    
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const now = new Date()
      const diff = now - date
      
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)
      
      if (minutes < 1) return 'Just now'
      if (minutes < 60) return `${minutes}m ago`
      if (hours < 24) return `${hours}h ago`
      if (days < 7) return `${days}d ago`
      
      return date.toLocaleDateString()
    }
  }
}
</script>

<style scoped>
.post-detail-container {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 顶部导航栏 */
.top-navbar {
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo h3 {
  margin: 0;
  color: #22b3c1;
  font-size: 24px;
  cursor: pointer;
}

.nav-menu {
  display: flex;
  gap: 30px;
  align-items: center;
}

.nav-item {
  color: #333;
  text-decoration: none;
  font-size: 15px;
  cursor: pointer;
  transition: color 0.3s;
  position: relative;
}

.nav-item:hover {
  color: #22b3c1;
}

.nav-item.active {
  color: #22b3c1;
}

.dropdown {
  position: relative;
  display: inline-flex;
  align-items: center;
}

.dropdown .dropdown-menu {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%) translateY(0);
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.15s ease, transform 0.15s ease, visibility 0.15s ease;
  z-index: 1001;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  min-width: 180px;
  padding: 8px 0;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.dropdown .dropdown-menu .dropdown-item {
  padding: 10px 20px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.dropdown .dropdown-menu .dropdown-item:hover {
  background: #e0f7fa;
  color: #22b3c1;
}

.dropdown:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(0);
}

.login-btn {
  background: #22b3c1;
  color: white !important;
  padding: 8px 20px;
  border-radius: 25px;
  transition: all 0.3s;
}

.login-btn:hover {
  background: #1a9aa8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.3);
}

.logout-btn {
  background: #ff6b6b;
  color: white !important;
  padding: 8px 20px;
  border-radius: 25px;
  transition: all 0.3s;
}

.logout-btn:hover {
  background: #ff5252;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

/* 主要内容区 */
.post-detail-content {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 20px;
}

/* 帖子详情 */
.post-detail {
  background: white;
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.post-header {
  margin-bottom: 30px;
}

.post-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.pinned-badge {
  background: #ffd700;
  color: #333;
  padding: 5px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}

.qa-badge {
  background: #ff6b6b;
  color: white;
  padding: 5px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  font-weight: 600;
  color: #333;
}

.post-time {
  color: #999;
  font-size: 14px;
}

.post-tag {
  background: #e0f7fa;
  color: #22b3c1;
  padding: 5px 12px;
  border-radius: 12px;
  font-size: 13px;
}

.post-image {
  margin: 20px 0;
  border-radius: 12px;
  overflow: hidden;
}

.post-image img {
  width: 100%;
  max-height: 500px;
  object-fit: cover;
}

.post-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  margin: 30px 0;
}

.post-stats {
  display: flex;
  gap: 30px;
  padding-top: 20px;
  border-top: 1px solid #e8e8e8;
  color: #999;
  font-size: 14px;
}

/* 回复区域 */
.replies-section {
  background: white;
  border-radius: 16px;
  padding: 30px 40px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.replies-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 25px;
}

.no-replies {
  text-align: center;
  color: #999;
  padding: 40px 20px;
}

.replies-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.reply-item {
  padding: 20px;
  background: #f9f9f9;
  border-radius: 12px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.reply-avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  object-fit: cover;
}

.reply-author {
  font-weight: 600;
  color: #333;
}

.reply-time {
  color: #999;
  font-size: 13px;
  margin-left: auto;
}

.reply-content {
  font-size: 15px;
  line-height: 1.6;
  color: #666;
}

/* 回复表单 */
.reply-form-section {
  background: white;
  border-radius: 16px;
  padding: 30px 40px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.reply-form-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.reply-input {
  width: 100%;
  padding: 15px;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  font-size: 15px;
  resize: vertical;
  outline: none;
  transition: border-color 0.3s;
  box-sizing: border-box;
  font-family: inherit;
}

.reply-input:focus {
  border-color: #22b3c1;
}

.submit-reply-btn {
  margin-top: 15px;
  padding: 12px 30px;
  background: #22b3c1;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-reply-btn:hover {
  background: #1a9aa8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.3);
}

.submit-reply-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.login-hint {
  background: white;
  border-radius: 16px;
  padding: 30px 40px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  text-align: center;
  color: #666;
  font-size: 15px;
}

.login-hint a {
  color: #22b3c1;
  text-decoration: none;
  font-weight: 600;
}

.login-hint a:hover {
  text-decoration: underline;
}

/* 返回按钮 */
.back-btn {
  padding: 12px 30px;
  background: white;
  color: #22b3c1;
  border: 2px solid #22b3c1;
  border-radius: 25px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
  display: block;
  margin: 0 auto;
}

.back-btn:hover {
  background: #22b3c1;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.3);
}

/* 加载和空状态 */
.loading, .empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}
</style>
