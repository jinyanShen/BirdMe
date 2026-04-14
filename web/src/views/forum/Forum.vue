<template>
  <div class="forum-container">
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
              <div class="post-actions" v-if="isOwnPostOrAdmin(post)" @click.stop>
                <button class="pin-btn" :class="{ pinned: post.isPinned === 1 }" v-if="isOwnPost(post)"
                        @click="togglePin(post)">
                  <i :class="post.isPinned === 1 ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                  {{ post.isPinned === 1 ? 'Unpin' : 'Pin' }}
                </button>
                <button class="edit-btn" @click="editPost(post)" v-if="isOwnPost(post)">
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
          <h3>{{ isEditing ? 'Edit Post' : 'Create New Post' }}</h3>
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
          <div class="form-group" v-if="isAdmin">
            <label>
              <input type="checkbox" v-model="newPost.isPinned" :true-value="1" :false-value="0" />
              Pin this post
            </label>
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
      isAdmin: false,
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
        category: 'birdwatching',
        isPinned: 0
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
  methods: {
    checkLoginStatus() {
      this.isLoggedIn = sessionStorage.getItem('id') !== null
      const role = sessionStorage.getItem('role')
      this.isAdmin = role === '1'
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

    closePostDialog() {
      this.showPostDialog = false
      this.isEditing = false
      this.editingPostId = null
      this.newPost = {
        title: '',
        content: '',
        tag: '',
        category: 'birdwatching',
        isPinned: 0
      }
    },

    isOwnPost(post) {
      const userId = sessionStorage.getItem('id')
      return userId && post.authorId && post.authorId.toString() === userId.toString()
    },

    isOwnPostOrAdmin(post) {
      if (!this.isLoggedIn) return false
      if (this.isAdmin) return true
      return this.isOwnPost(post)
    },

    async togglePin(post) {
      if (!this.isOwnPostOrAdmin(post)) return

      try {
        const newPinned = post.isPinned === 1 ? 0 : 1
        const response = await updatePost(post.id, { isPinned: newPinned })
        if (response && response.code === 200) {
          post.isPinned = newPinned
          this.$message.success(newPinned === 1 ? 'Post pinned' : 'Post unpinned')
        }
        this.loadPosts()
      } catch (error) {
        console.error('Failed to toggle pin:', error)
        this.$message.error('Failed to update pin status')
      }
    },

    editPost(post) {
      this.isEditing = true
      this.editingPostId = post.id
      this.newPost = {
        title: post.title,
        content: post.content,
        tag: post.tag,
        category: post.category,
        isPinned: post.isPinned
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

.forum-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

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
}

.search-input {
  flex: 1;
  padding: 12px 20px;
  border: 2px solid #e0f7fa;
  border-radius: 25px;
  font-size: 15px;
  transition: all 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #22b3c1;
  box-shadow: 0 0 0 3px rgba(34, 179, 193, 0.1);
}

.search-btn {
  padding: 12px 30px;
  background: #22b3c1;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 15px;
}

.search-btn:hover {
  background: #1a9aa8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.3);
}

.post-btn {
  padding: 12px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.category-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 25px;
  background: white;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.tab-item {
  padding: 10px 25px;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
  font-size: 15px;
}

.tab-item:hover {
  color: #22b3c1;
  background: #e0f7fa;
}

.tab-item.active {
  background: linear-gradient(135deg, #22b3c1 0%, #4dd0e1 100%);
  color: white;
}

.post-list {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.loading, .empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

.post-item {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.post-item:last-child {
  border-bottom: none;
}

.post-item:hover {
  background: #fafafa;
}

.post-item.pinned {
  background: #fffbf0;
  border-left: 4px solid #ffc107;
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
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.pinned-badge {
  background: #ffc107;
  color: #333;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 8px;
}

.qa-badge {
  background: #22b3c1;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 8px;
}

.post-meta {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.post-content {
  color: #666;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 15px;
  padding-left: 65px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-left: 65px;
}

.post-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #999;
  font-size: 14px;
}

.post-actions {
  display: flex;
  gap: 10px;
}

.edit-btn {
  padding: 6px 15px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 13px;
}

.edit-btn:hover {
  background: #22b3c1;
  color: white;
}

.pin-btn {
  padding: 6px 15px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 13px;
}

.pin-btn:hover {
  background: #ffc107;
  color: #333;
}

.pin-btn.pinned {
  background: #ffc107;
  color: #333;
}

.pin-btn.pinned:hover {
  background: #e6ac00;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.post-dialog {
  background: white;
  border-radius: 20px;
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px 30px;
  border-bottom: 1px solid #eee;
}

.dialog-header h3 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 30px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
  line-height: 1;
}

.close-btn:hover {
  color: #333;
}

.dialog-body {
  padding: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-control {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e0f7fa;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.3s;
}

.form-group input[type="checkbox"] {
  width: 18px;
  height: 18px;
  margin-right: 8px;
  vertical-align: middle;
  cursor: pointer;
}

.form-control:focus {
  outline: none;
  border-color: #22b3c1;
  box-shadow: 0 0 0 3px rgba(34, 179, 193, 0.1);
}

.quill-editor {
  border-radius: 10px;
  overflow: hidden;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 20px 30px;
  border-top: 1px solid #eee;
}

.btn-cancel {
  padding: 12px 30px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 15px;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-submit {
  padding: 12px 30px;
  background: linear-gradient(135deg, #22b3c1 0%, #4dd0e1 100%);
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 15px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
