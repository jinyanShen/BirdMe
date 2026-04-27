<template>
  <div class="top-navbar" :class="{ 'navbar-visible': showNavbar }">
    <div class="navbar-container">
      <div class="logo">
        <h3 @click="goToHome" style="cursor: pointer;">BirdME</h3>
      </div>
      <div class="nav-menu">
        <router-link to="/" class="nav-item">Homepage</router-link>

        <!-- Help 页面 -->
        <span class="nav-item" @click="goToHelp">Help</span>

        <!-- Knowledge 下拉菜单 -->
        <div class="dropdown">
          <span class="nav-item" :class="{ active: isKnowledgeActive }" @click="goToKnowledge">Knowledge ▾</span>
          <div class="dropdown-menu">
            <div class="dropdown-item" @click="goToKnowledgePage('migration')">Migration Map</div>
            <div class="dropdown-item" @click="goToKnowledgePage('identification')">Identification</div>
            <div class="dropdown-item" @click="goToFunFacts">Fun Facts</div>
          </div>
        </div>

        <!-- Forum 下拉菜单 -->
        <div class="dropdown">
          <span class="nav-item" @click="goToForum">Forum ▾</span>
          <div class="dropdown-menu">
            <div class="dropdown-item" @click="goToForumPage('birdwatching')">Bird Watching</div>
            <div class="dropdown-item" @click="goToForumPage('qa')">Q&A</div>
          </div>
        </div>

        <!-- Game 下拉菜单 -->
        <div class="dropdown game-dropdown">
          <span class="nav-item" @click="goToGame">Game ▾</span>
          <div class="game-dropdown-menu">
            <div class="game-dropdown-item" @click="selectGameFromNav('flappy')">Flappy Bird</div>
            <div class="game-dropdown-item" @click="selectGameFromNav('2048')">2048 Bird</div>
          </div>
        </div>

        <span class="nav-item" @click="goToPersonalPage">Settings</span>
        <span v-if="!isLoggedIn" class="nav-item login-btn" @click="goToLogin">Login</span>
        <span v-else class="nav-item logout-btn" @click="handleLogout">Logout</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NavBar',
  props: {
    showNavbar: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isLoggedIn: false,
      isKnowledgeActive: false
    }
  },
  mounted() {
    this.checkLoginStatus()
  },
  methods: {
    checkLoginStatus() {
      this.isLoggedIn = sessionStorage.getItem('id') !== null
    },
    goToHome() {
      this.$router.push('/')
    },
    goToHelp() {
      this.$router.push('/help')
    },
    goToKnowledge() {
      if (this.isLoggedIn) {
        this.$router.push('/knowledge')
      } else if (window.$showLoginDialog) {
        window.$showLoginDialog('/knowledge')
      }
    },
    goToKnowledgePage(tab) {
      if (this.isLoggedIn) {
        this.$router.push(`/knowledge/index?tab=${tab}`)
      } else if (window.$showLoginDialog) {
        window.$showLoginDialog(`/knowledge/index?tab=${tab}`)
      }
    },
    goToFunFacts() {
      if (this.isLoggedIn) {
        this.$router.push('/knowledge/facts')
      } else if (window.$showLoginDialog) {
        window.$showLoginDialog('/knowledge/facts')
      }
    },
    goToForum() {
      if (this.isLoggedIn) {
        this.$router.push('/forum')
      } else if (window.$showLoginDialog) {
        window.$showLoginDialog('/forum')
      }
    },
    goToForumPage(tab) {
      if (this.isLoggedIn) {
        this.$router.push(`/forum/${tab}`)
      } else if (window.$showLoginDialog) {
        window.$showLoginDialog(`/forum/${tab}`)
      }
    },
    goToGame() {
      if (this.isLoggedIn) {
        this.$router.push('/game/index')
      } else if (window.$showLoginDialog) {
        window.$showLoginDialog('/game/index')
      }
    },
    selectGameFromNav(gameKey) {
      const targetPath = `/game/${gameKey}`
      if (this.$route.path !== targetPath) {
        this.$router.push(targetPath)
      }
    },
    goToPersonalPage() {
      if (this.isLoggedIn) {
        this.$router.push('/settings')
      } else if (window.$showLoginDialog) {
        window.$showLoginDialog('/settings')
      }
    },
    goToLogin() {
      this.$router.push('/login')
    },
    handleLogout() {
      sessionStorage.removeItem('id')
      sessionStorage.removeItem('username')
      sessionStorage.removeItem('password')
      sessionStorage.removeItem('name')
      sessionStorage.removeItem('age')
      sessionStorage.removeItem('phone')
      sessionStorage.removeItem('avatarUrl')
      sessionStorage.removeItem('role')
      this.isLoggedIn = false
      this.$message?.success('Logged out successfully')
      this.$router.push('/')
    }
  },
  watch: {
    '$route'() {
      this.checkLoginStatus()
    }
  }
}
</script>

<style scoped lang="scss">
/* 复制你原来 .top-navbar 的所有样式到这里 */
.top-navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  transform: translateY(-100%);
  transition: transform 0.3s ease-in-out;

  &.navbar-visible {
    transform: translateY(0);
  }

  .navbar-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px 0px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .logo h3 {
      margin: 0;
      font-size: 32px;
      color: #89a1dc;
      font-weight: bold;
      cursor: pointer;
    }

    .nav-menu {
      display: flex;
      gap: 40px;
      align-items: center;

      .nav-item {
        text-decoration: none;
        color: #333;
        font-size: 22px;
        font-weight: bold;
        transition: color 0.3s;
        cursor: pointer;

        &:hover {
          color: #548ccb;
        }
      }

      .game-dropdown {
        position: relative;
        display: inline-flex;
        align-items: center;

        .game-dropdown-menu {
          position: absolute;
          top: 100%;
          left: 50%;
          transform: translateX(-50%) translateY(0);
          opacity: 0;
          visibility: hidden;
          transition: opacity 0.15s ease;
          z-index: 1001;
          background: rgba(255, 255, 255, 0.95);
          backdrop-filter: blur(10px);
          border-radius: 12px;
          box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
          min-width: 200px;
          padding: 8px 0;

          .game-dropdown-item {
            padding: 10px 20px;
            font-size: 14px;
            color: #333;
            cursor: pointer;
            &:hover {
              background: #f2f3ff;
              color: #6f5bb8;
            }
          }
        }

        &:hover .game-dropdown-menu {
          opacity: 1;
          visibility: visible;
        }
      }

      .dropdown {
        position: relative;
        display: inline-flex;
        align-items: center;

        .dropdown-menu {
          position: absolute;
          top: 100%;
          left: 50%;
          transform: translateX(-50%) translateY(0);
          opacity: 0;
          visibility: hidden;
          transition: opacity 0.15s ease;
          z-index: 1001;
          background: rgba(255, 255, 255, 0.95);
          backdrop-filter: blur(10px);
          border-radius: 12px;
          box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
          min-width: 180px;
          padding: 8px 0;

          .dropdown-item {
            padding: 10px 20px;
            font-size: 14px;
            color: #333;
            cursor: pointer;
            &:hover {
              background: #f2f3ff;
              color: #6f5bb8;
            }
          }
        }

        &:hover .dropdown-menu {
          opacity: 1;
          visibility: visible;
        }
      }

      .login-btn {
        background: #6589d9;
        color: white;
        padding: 8px 20px;
        border-radius: 25px;
        margin-left: 80px;
        &:hover {
          background: #d4dcf6;
          color: white;
        }
      }

      .logout-btn {
        background: #f36a6a;
        color: white !important;
        padding: 8px 20px;
        border-radius: 25px;
        &:hover {
          background: #e36969;
        }
      }
    }
  }
}
</style>
