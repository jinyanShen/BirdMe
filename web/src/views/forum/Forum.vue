<template>
  <div class="forum-container">
    <!-- 顶部导航栏 -->
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <div class="navbar-container">
        <div class="logo">
          <h3 @click="goToHome" style="cursor: pointer;">BirdME</h3>
        </div>
        <div class="nav-menu">
          <span class="nav-item" @click="goToHome">Homepage</span>
          <div class="dropdown">
            <span class="nav-item" @click="goToKnowledge">Knowledge ▾</span>
            <div class="dropdown-menu">
              <div class="dropdown-item" @click="goToKnowledgePage('migration')">Migration Map</div>
              <div class="dropdown-item" @click="goToKnowledgePage('identification')">Identification</div>
              <div class="dropdown-item" @click="goToFunFacts">Fun Facts</div>
            </div>
          </div>
          <div class="dropdown">
            <span class="nav-item" :class="{ active: isForumActive }" @click="goToForum">Forum ▾</span>
            <div class="dropdown-menu">
              <div class="dropdown-item" @click="switchCategory('birdwatching')">Bird Watching</div>
              <div class="dropdown-item" @click="switchCategory('qa')">Q&A</div>
            </div>
          </div>
          <div class="dropdown">
            <span class="nav-item" @click="goToGame">Game ▾</span>
            <div class="dropdown-menu">
              <div class="dropdown-item" @click="goToGamePage('flappy')">Flappy Bird</div>
              <div class="dropdown-item" @click="goToGamePage('2048')">2048 Bird</div>
              <div class="dropdown-item" @click="goToGamePage('merge')">Merge To Giant Bird</div>
            </div>
          </div>
          <span class="nav-item" @click="goToPersonalPage">Personal Setting</span>
          <span v-if="!isLoggedIn" class="nav-item login-btn" @click="goToLogin">Login</span>
          <span v-else class="nav-item logout-btn" @click="handleLogout">LogOut</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="forum-content">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <div class="search-container">
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="Search posts..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">Search</button>
          <button class="post-btn" @click="showPostDialog = true" v-if="isLoggedIn">
            <i class="icon-plus"></i> New Post
          </button>
        </div>
      </div>

      <!-- 分类标签 -->
      <div class="category-tabs">
        <span
          class="tab-item"
          :class="{ active: currentCategory === 'all' }"
          @click="switchCategory('all')"
        >All</span>
        <span
          class="tab-item"
          :class="{ active: currentCategory === 'birdwatching' }"
          @click="switchCategory('birdwatching')"
        >Bird Watching</span>
        <span
          class="tab-item"
          :class="{ active: currentCategory === 'qa' }"
          @click="switchCategory('qa')"
        >Q&A</span>
      </div>

      <!-- 帖子列表 -->
      <div class="post-list">
        <div v-if="loading" class="loading">Loading...</div>
        <div v-else-if="posts.length === 0" class="empty-state">No posts found</div>
        <div v-else>
          <div
            v-for="post in posts"
            :key="post.id"
            class="post-item"
            :class="{ 'pinned': post.isPinned === 1 }"
          >
            <div class="post-header" @click="goToPostDetail(post.id)" style="cursor: pointer;">
              <div class="post-avatar">
                <img :src="post.authorAvatar || '/default-avatar.png'" :alt="post.authorName" />
              </div>
              <div class="post-info">
                <div class="post-title">
                  <span v-if="post.isPinned === 1" class="pinned-badge">📌 Pinned</span>
                  <span v-if="post.category === 'qa'" class="qa-badge">❓ Q&A</span>
                  {{ post.title }}
                </div>
                <div class="post-meta">
                  <span class="author-name">{{ post.authorName }}</span>
                  <span class="post-time">{{ formatDate(post.createdAt) }}</span>
                  <span class="post-tag">{{ post.tag }}</span>
                </div>
              </div>
            </div>
            <div class="post-content" @click="goToPostDetail(post.id)" style="cursor: pointer;">
              {{ truncateContent(post.content) }}
            </div>
            <div class="post-footer">
              <span class="post-stats">
                <span class="stat-item">
                  <i class="el-icon-view"></i> {{ post.viewCount }}
                </span>
                <span class="stat-item">
                  <i class="el-icon-chat-dot-round"></i> {{ post.replyCount }}
                </span>
              </span>
              <div class="post-actions" v-if="isOwnPost(post)" @click.stop>
                <button class="edit-btn" @click="editPost(post)">
                  <i class="el-icon-edit"></i> Edit
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 发帖对话框 -->
    <div v-if="showPostDialog" class="dialog-overlay" @click="closePostDialog">
      <div class="post-dialog" @click.stop>
        <div class="dialog-header">
          <h3>Create New Post</h3>
          <button class="close-btn" @click="closePostDialog">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>Category</label>
            <select v-model="newPost.category" class="form-control">
              <option value="birdwatching">Bird Watching</option>
              <option value="qa">Q&A</option>
            </select>
          </div>
          <div class="form-group">
            <label>Title</label>
            <input
              type="text"
              v-model="newPost.title"
              class="form-control"
              placeholder="Enter post title"
              maxlength="200"
            />
          </div>
          <div class="form-group">
            <label>Tag</label>
            <input
              type="text"
              v-model="newPost.tag"
              class="form-control"
              placeholder="e.g., Bird Watching, Help, Identification"
              maxlength="50"
            />
          </div>
          <div class="form-group">
            <label>Content</label>
            <div class="quill-editor">
              <quill-editor
                ref="editor"
                v-model="newPost.content"
                :options="editorOptions"
              ></quill-editor>
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="closePostDialog">Cancel</button>
          <button class="btn-submit" @click="submitPost" :disabled="submitting">
            {{ submitting ? 'Posting...' : 'Post' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getPosts, createPost, updatePost } from '@/api/forum'
import { quillEditor } from 'vue-quill-editor'
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

export default {
  name: 'Forum',
  components: {
    quillEditor
  },
  data() {
    return {
      isLoggedIn: false,
      currentCategory: 'all',
      searchKeyword: '',
      posts: [],
      loading: false,
      showPostDialog: false,
      submitting: false,
      newPost: {
        title: '',
        content: '',
        tag: '',
        category: 'birdwatching'
      },
      editorOptions: {
        theme: 'snow',
        placeholder: 'Enter post content...',
        modules: {
          toolbar: [
            ['bold', 'italic', 'underline', 'strike'],
            [{ 'list': 'ordered' }, { 'list': 'bullet' }],
            [{ 'header': [1, 2, 3, false] }],
            [{ 'color': [] }, { 'background': [] }],
            [{ 'align': [] }],
            ['link', 'image'],
            ['clean']
          ]
        }
      },
      isEditing: false,
      editingPostId: null
    }
  },
  mounted() {
    this.checkLoginStatus()
    this.initCategoryFromRoute()
    this.loadPosts()
  },
  watch: {
    '$route'(to, from) {
      this.initCategoryFromRoute()
      this.loadPosts()
    },
    currentCategory() {
      this.loadPosts()
    }
  },
  computed: {
    isForumActive() {
      return this.$route.path.startsWith('/forum')
    }
  },
  methods: {
    checkLoginStatus() {
      this.isLoggedIn = sessionStorage.getItem('id') !== null
    },

    initCategoryFromRoute() {
      const path = this.$route.path
      if (path.includes('/forum/birdwatching')) {
        this.currentCategory = 'birdwatching'
      } else if (path.includes('/forum/qa')) {
        this.currentCategory = 'qa'
      } else {
        this.currentCategory = 'all'
      }
    },

    async loadPosts() {
      this.loading = true
      try {
        const response = await getPosts(this.currentCategory, this.searchKeyword)
        if (response && response.code === 200) {
          this.posts = response.data || []
        } else {
          this.posts = []
        }
      } catch (error) {
        console.error('Failed to load posts:', error)
        this.posts = []
      } finally {
        this.loading = false
      }
    },

    switchCategory(category) {
      this.currentCategory = category
      if (category === 'all') {
        this.$router.push('/forum').catch(() => {})
      } else {
        this.$router.push(`/forum/${category}`).catch(() => {})
      }
    },

    handleSearch() {
      console.log('Search:', this.searchKeyword)
      this.loadPosts()
    },

    goToPostDetail(postId) {
      this.$router.push(`/forum/post/${postId}`)
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

    // goToGamePage(game) {
    //   this.$router.push(`/game/${game}`)
    // },

    goToPersonalPage() {
      if (this.isLoggedIn) {
        this.$router.push('/settings')
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/settings')
        }
      }
    },

    goToLogin() {
      this.$router.push('/login')
    },

    // Knowledge 主项点击
    goToKnowledge() {
      if (this.isLoggedIn) {
        if (this.$route.path !== '/knowledge') {
          this.$router.push('/knowledge')
        }
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/knowledge')
        }
      }
    },

// Forum 主项点击
    goToForum() {
      if (this.isLoggedIn) {
        if (this.$route.path !== '/forum') {
          this.$router.push('/forum')
        }
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/forum')
        }
      }
    },

// Game 主项点击
    goToGame() {
      if (this.isLoggedIn) {
        if (this.$route.path !== '/game') {
          this.$router.push('/game')
        }
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/game')
        }
      }
    },

// 修改现有的 goToGamePage，添加登录检查
    goToGamePage(game) {
      if (!this.isLoggedIn) {
        if (window.$showLoginDialog) {
          window.$showLoginDialog(`/game/${game}`)
        }
        return
      }
      const targetPath = `/game/${game}`
      if (this.$route.path === targetPath) return
      this.$router.push(targetPath)
    },

    handleLogout() {
      sessionStorage.clear()
      this.isLoggedIn = false
      this.$router.push('/')
    },

    closePostDialog() {
      this.showPostDialog = false
      this.isEditing = false
      this.editingPostId = null
      this.newPost = {
        title: '',
        content: '',
        tag: '',
        category: 'birdwatching'
      }
    },

    isOwnPost(post) {
      const userId = sessionStorage.getItem('id')
      return userId && post.authorId && post.authorId.toString() === userId.toString()
    },

    editPost(post) {
      this.isEditing = true
      this.editingPostId = post.id
      this.newPost = {
        title: post.title,
        content: post.content,
        tag: post.tag,
        category: post.category
      }
      this.showPostDialog = true
    },

    async submitPost() {
      if (!this.newPost.title.trim() || !this.newPost.content.trim()) {
        alert('Please enter title and content')
        return
      }

      this.submitting = true
      try {
        const userId = sessionStorage.getItem('id')
        const username = sessionStorage.getItem('username') || 'User'
        const userAvatar = sessionStorage.getItem('avatarUrl')

        const postData = {
          ...this.newPost,
          authorId: parseInt(userId),
          authorName: username,
          authorAvatar: userAvatar,
          isPinned: 0,
          viewCount: 0,
          replyCount: 0
        }

        let response
        if (this.isEditing && this.editingPostId) {
          // 更新帖子
          const updateData = {
            ...postData,
            id: this.editingPostId
          }
          response = await updatePost(this.editingPostId, updateData)
          if (response && response.code === 200) {
            alert('Post updated successfully!')
          }
        } else {
          response = await createPost(postData)
          if (response && response.code === 200) {
            alert('Post created successfully!')
          }
        }

        if (response && response.code === 200) {
          this.closePostDialog()
          this.loadPosts()
        }
      } catch (error) {
        console.error('Failed to create post:', error)
        alert('Failed to create post. Please try again.')
      } finally {
        this.submitting = false
      }
    },

    truncateContent(content, maxLength = 150) {
      if (!content) return ''
      if (content.length <= maxLength) return content
      return content.substring(0, maxLength) + '...'
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
.forum-container {
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
  font-size: 28px;
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
  font-size: 20px;
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

/* Dropdown styles */
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
.forum-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

/* 搜索栏 */
.search-bar {
  background: white;
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.search-container {
  display: flex;
  gap: 15px;
  align-items: center;
}

.search-input {
  flex: 1;
  padding: 12px 20px;
  border: 2px solid #e8e8e8;
  border-radius: 25px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.3s;
}

.search-input:focus {
  border-color: #22b3c1;
}

.search-btn {
  padding: 12px 30px;
  background: #22b3c1;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn:hover {
  background: #1a9aa8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.3);
}

.post-btn {
  padding: 12px 30px;
  background: #6f5bb8;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-btn:hover {
  background: #5d4aa8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(111, 91, 184, 0.3);
}

/* 分类标签 */
.category-tabs {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
}

.tab-item {
  padding: 10px 25px;
  background: white;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.tab-item:hover {
  background: #e0f7fa;
  color: #22b3c1;
  transform: translateY(-2px);
}

.tab-item.active {
  background: #22b3c1;
  color: white;
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.3);
}

/* 帖子列表 */
.post-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-item {
  background: white;
  border-radius: 16px;
  padding: 25px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.post-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.post-item.pinned {
  border-left: 4px solid #ffd700;
  background: linear-gradient(to right, #fffef0, white);
}

.post-header {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.post-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.post-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-info {
  flex: 1;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.pinned-badge {
  background: #ffd700;
  color: #333;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.qa-badge {
  background: #ff6b6b;
  color: white;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.post-meta {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.author-name {
  color: #666;
  font-weight: 500;
}

.post-tag {
  background: #e0f7fa;
  color: #22b3c1;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.post-content {
  color: #666;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 15px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #999;
  font-size: 14px;
}

.post-stats {
  display: flex;
  gap: 15px;
}

.post-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.post-stats i {
  font-size: 16px;
  color: #22b3c1;
}

.post-actions {
  display: flex;
  gap: 10px;
}

.edit-btn {
  padding: 6px 15px;
  background: #22b3c1;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.edit-btn:hover {
  background: #1a9aa8;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(34, 179, 193, 0.3);
}

.edit-btn i {
  font-size: 14px;
}

/* 对话框 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.post-dialog {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #e8e8e8;
}

.dialog-header h3 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #333;
}

.dialog-body {
  padding: 25px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-control {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-control:focus {
  border-color: #22b3c1;
}

.form-control textarea {
  resize: vertical;
  min-height: 120px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 20px 25px;
  border-top: 1px solid #e8e8e8;
}

.btn-cancel {
  padding: 10px 25px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover {
  background: #e8e8e8;
}

.btn-submit {
  padding: 10px 30px;
  background: #22b3c1;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-submit:hover {
  background: #1a9aa8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.3);
}

.btn-submit:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 富文本编辑器样式 */
.quill-editor {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.quill-editor >>> .ql-toolbar {
  border: none;
  border-bottom: 1px solid #e8e8e8;
  background-color: #f9f9f9;
}

.quill-editor >>> .ql-container {
  border: none;
  min-height: 200px;
  font-size: 15px;
}

.quill-editor >>> .ql-editor {
  padding: 15px;
  min-height: 200px;
}

/* 加载和空状态 */

/* 加载和空状态 */
.loading, .empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}
</style>
