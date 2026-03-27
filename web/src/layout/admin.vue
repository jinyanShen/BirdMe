<template>
  <div class="admin-layout">
    <!-- Left Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2 class="sidebar-title">Admin Panel</h2>
      </div>
      <nav class="sidebar-nav">
        <ul class="nav-list">
          <li class="nav-item">
            <router-link to="/admin/users" class="nav-link" :class="{ active: $route.path === '/admin/users' }">
              <i class="fas fa-users"></i>
              <span>Users</span>
            </router-link>
          </li>
<!--          <li class="nav-item">-->
<!--            <router-link to="/admin/addresses" class="nav-link" :class="{ active: $route.path === '/admin/addresses' }">-->
<!--              <i class="fas fa-map-marker-alt"></i>-->
<!--              <span>Addresses</span>-->
<!--            </router-link>-->
<!--          </li>-->
        </ul>
      </nav>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
      <header class="top-header">
        <div class="header-left">
          <h1 class="page-title">{{ pageTitle }}</h1>
        </div>
        <div class="header-right">
          <div class="user-info">
            <div class="user-avatar" @click="showUserMenu = !showUserMenu">
              <img :src="userAvatar" alt="User" />
            </div>
            <div v-if="showUserMenu" class="user-menu">
              <router-link to="/identification" class="menu-item">Bird Identification</router-link>
              <a href="#" @click="handleLogout" class="menu-item">Logout</a>
            </div>
          </div>
        </div>
      </header>
      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'AdminLayout',
  data() {
    return {
      userAvatar: '',
      showUserMenu: false
    }
  },
  computed: {
    pageTitle() {
      const path = this.$route.path
      if (path === '/admin/users') return 'User Management'
      if (path === '/admin/addresses') return 'Address Management'
      return 'Admin Panel'
    }
  },
  mounted() {
    this.getUserAvatar()
  },
  methods: {
    getUserAvatar() {
      const avatar = sessionStorage.getItem('avatarUrl')
      this.userAvatar = avatar
    },
    handleLogout() {
      sessionStorage.clear()
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped lang="scss">
.admin-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.sidebar {
  width: 250px;
  background: linear-gradient(135deg, #22b3c1 0%, #1a9aa8 100%);
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.sidebar-header {
  padding: 30px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: white;
  text-align: center;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin-bottom: 5px;
}

.nav-link {
  display: flex;
  align-items: center;
  padding: 15px 25px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;

  &:hover {
    background-color: rgba(255, 255, 255, 0.1);
    color: white;
  }

  &.active {
    background-color: rgba(255, 255, 255, 0.2);
    color: white;
    border-left-color: white;
  }

  i {
    margin-right: 12px;
    font-size: 18px;
    width: 20px;
    text-align: center;
  }

  span {
    font-size: 15px;
    font-weight: 500;
  }
}

.main-content {
  flex: 1;
  margin-left: 250px;
  display: flex;
  flex-direction: column;
}

.top-header {
  background: white;
  padding: 20px 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  position: relative;
}

.user-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid #22b3c1;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 2px 8px rgba(34, 179, 193, 0.3);
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.user-menu {
  position: absolute;
  top: 55px;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 180px;
  overflow: hidden;
  z-index: 1001;
}

.menu-item {
  display: block;
  padding: 12px 20px;
  color: #333;
  text-decoration: none;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;

  &:hover {
    background-color: #22b3c1;
    color: white;
  }

  &:last-child {
    border-bottom: none;
  }
}

.content-wrapper {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}
</style>
