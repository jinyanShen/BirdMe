<template>
  <div class="game-page">
    <!-- 顶部导航栏：页面打开就可见（不做首页那种滚动/开场动画控制） -->
    <div class="top-navbar navbar-visible">
      <div class="navbar-container">
        <div class="logo">
          <h3>BirdME</h3>
        </div>

        <div class="nav-menu">
          <router-link to="/" class="nav-item">Homepage</router-link>

          <div class="dropdown">
            <span class="nav-item" :class="{ active: isKnowledgeActive }">Knowledge ▾</span>
            <div class="dropdown-menu">
              <div class="dropdown-item" @click="goToKnowledgePage('migration')">Migration Map</div>
              <div class="dropdown-item" @click="goToKnowledgePage('identification')">Identification</div>
              <div class="dropdown-item" @click="goToFunFacts">Fun Facts</div>
            </div>
          </div>

          <div class="dropdown">
            <span class="nav-item">Forum ▾</span>
            <div class="dropdown-menu">
              <div class="dropdown-item" @click="goToForumPage('birdwatching')">Bird Watching</div>
              <div class="dropdown-item" @click="goToForumPage('qa')">Q&A</div>
            </div>
          </div>

          <div class="dropdown game-dropdown">
            <span class="nav-item">Game ▾</span>
            <div class="game-dropdown-menu">
              <div class="game-dropdown-item" @click="selectGameFromNav('flappy')">Flappy Bird</div>
              <div class="game-dropdown-item" @click="selectGameFromNav('2048')">2048 Bird</div>
              <div class="game-dropdown-item" @click="selectGameFromNav('merge')">Merge To Giant Bird</div>
            </div>
          </div>

          <span class="nav-item" @click="goToPersonalPage">Personal Setting</span>
          <span v-if="!isLoggedIn" class="nav-item login-btn" @click="goToLogin">Login</span>
          <span v-else class="nav-item logout-btn" @click="handleLogout">LogOut</span>
        </div>
      </div>
    </div>

    <div class="content-below">
      <div class="knowledge-cards-wrapper" id="game-flappy">
        <div class="section">
          <h3>Flappy Bird</h3>
          <p>Control the bird and dodge obstacles, testing your reaction time.</p>
          <div class="section-divider"></div>
        </div>

        <div class="cards-container" id="game-flappy-card">
          <div class="card">
            <img :src="flappyImg" class="game-icon" alt="Flappy Bird">
            <h4>Flappy Bird</h4>
            <p>Easy to start, perfect your rhythm fast.</p>
            <button class="btn" @click="selectGame('flappy')">Start Game</button>
          </div>
        </div>
      </div>

      <div class="knowledge-cards-wrapper" id="game-2048">
        <div class="section">
          <h3>2048 Bird</h3>
          <p>Merge numbers and bird elements to reach new high scores.</p>
          <div class="section-divider"></div>
        </div>

        <div class="cards-container" id="game-2048-card">
          <div class="card">
            <img :src="img2048" class="game-icon" alt="2048 Bird">
            <h4>2048 Bird</h4>
            <p>Plan your moves and climb the score ladder.</p>
            <button class="btn" @click="selectGame('2048')">Start Game</button>
          </div>
        </div>
      </div>

      <div class="knowledge-cards-wrapper" id="game-merge">
        <div class="section">
          <h3>Merge To Giant Bird</h3>
          <p>Keep merging to evolve into a powerful giant bird.</p>
          <div class="section-divider"></div>
        </div>

        <div class="cards-container" id="game-merge-card">
          <div class="card">
            <img :src="mergeImg" class="game-icon" alt="Merge To Giant Bird">
            <h4>Merge To Giant Bird</h4>
            <p>Strategic merges, satisfying evolution.</p>
            <button class="btn" @click="selectGame('merge')">Start Game</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import flappyImg from '@/assets/images/flappy.png'
import img2048 from '@/assets/images/2048.png'
import mergeImg from '@/assets/images/merge.png'

export default {
  name: 'Game',
  data() {
    return {
      isLoggedIn: false,
      flappyImg,
      img2048,
      mergeImg,
      dropdowns: {
        knowledge: false,
        forum: false,
        game: false
      },
      isKnowledgeActive: false
    }
  },
  mounted() {
    this.checkLoginStatus()
  },
  methods: {
    toggleDropdown(dropdown) {
      Object.keys(this.dropdowns).forEach(key => {
        if (key !== dropdown) {
          this.dropdowns[key] = false
        }
      })
      this.dropdowns[dropdown] = !this.dropdowns[dropdown]
    },
    goToKnowledgePage(tab) {
      this.$router.push(`/knowledge/${tab}`)
    },
    goToFunFacts() {
      this.$router.push('/knowledge/facts')
    },
    goToForumPage(tab) {
      this.$router.push(`/forum/${tab}`)
    },
    checkLoginStatus() {
      this.isLoggedIn = sessionStorage.getItem('id') !== null
    },

    // 顶部导航：如果已经在 /game 就别重复 push，避免干扰滚动
    goToGame() {
      if (this.$route.path === '/game') return
      if (this.isLoggedIn) {
        this.$router.push('/game')
      } else if (window.$showLoginDialog) {
        window.$showLoginDialog('/game')
      }
    },

    goToKnowledge() {
      if (this.isLoggedIn) this.$router.push('/identification')
      else window.$showLoginDialog && window.$showLoginDialog('/identification')
    },

    goToRescue() {
      if (this.isLoggedIn) this.$router.push('/rescue')
      else window.$showLoginDialog && window.$showLoginDialog('/rescue')
    },

    goToForum() {
      if (this.isLoggedIn) this.$router.push('/forum')
      else window.$showLoginDialog && window.$showLoginDialog('/forum')
    },

    goToPersonalPage() {
      if (this.isLoggedIn) this.$router.push('/center')
      else window.$showLoginDialog && window.$showLoginDialog('/center')
    },

    goToLogin() {
      this.$router.push('/login')
    },

    handleLogout() {
      sessionStorage.clear()
      this.isLoggedIn = false
      if (this.$message && this.$message.success) {
        this.$message.success('Logged out successfully')
      }
      this.$router.push('/login')
    },

    selectGameFromNav(gameKey) {
      const targetPath = `/game/${gameKey}`
      if (this.$route.path === targetPath) return

      this.$router.push(targetPath)
    },

    selectGame(gameKey) {
      const targetPath = `/game/${gameKey}`
      if (this.$route.path === targetPath) return

      this.$router.push(targetPath)
    }
  }
}
</script>

<style scoped lang="scss">
.game-page {
  width: 100%;
  min-height: 100vh;
  position: relative;
}

.top-navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;

  /* 始终可见 */
  transform: translateY(0);
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-menu {
  display: flex;
  gap: 30px;
  align-items: center;
}

.nav-item {
  text-decoration: none;
  color: #333;
  font-size: 16px;
  cursor: pointer;
  transition: color 0.3s;
  &:hover {
    color: #f4f4f8;
  }
}

.login-btn {
  background: #6f5bb8;
  color: white;
  padding: 8px 20px;
  border-radius: 25px;
  &:hover {
    background: #d4dcf6;
    color: white;
  }
}

.logout-btn {
  background: #ff6b6b;
  color: white;
  padding: 8px 20px;
  border-radius: 25px;
  transition: all 0.3s;
  &:hover {
    background: #ff5252;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
  }
}

/* Game 顶部下拉菜单（hover 展开，不依赖点击） */
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
    transition: opacity 0.15s ease, transform 0.15s ease, visibility 0.15s ease;
    z-index: 1001;

    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 12px;
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
    min-width: 200px;
    padding: 8px 0;

    display: flex;
    flex-direction: column;
    align-items: stretch;

    .game-dropdown-item {
      padding: 10px 20px;
      font-size: 14px;
      color: #333;
      cursor: pointer;
      transition: background 0.2s ease, color 0.2s ease;
      &:hover {
        background: #f2f3ff;
        color: #6f5bb8;
      }
    }
  }

  &:hover {
    .game-dropdown-menu {
      opacity: 1;
      visibility: visible;
      transform: translateX(-50%) translateY(0);
    }
  }
}

/* Dropdown styles for game page */
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

    .dropdown-item {
      padding: 10px 20px;
      font-size: 14px;
      color: #333;
      cursor: pointer;
      transition: background 0.2s ease, color 0.2s ease;
      &:hover {
        background: #f2f3ff;
        color: #6f5bb8;
      }
    }
  }

  &:hover {
    .dropdown-menu {
      opacity: 1;
      visibility: visible;
      transform: translateX(-50%) translateY(0);
    }
  }
}

/* 复用首页渐变背景（需要给顶部固定导航预留空间） */
.content-below {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 110px 20px 80px;
  color: white;
}

/* 每部分的框（对齐 mainmenu:631 的 wrapper 风格） */
.knowledge-cards-wrapper {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 24px;
  padding: 40px 30px;
  margin: 0 auto 60px;
  max-width: 1200px;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  transition: all 0.3s;

  &:hover {
    border-color: rgba(255, 255, 255, 0.4);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);

    &::before {
      opacity: 1;
    }
  }

  &::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.1));
    border-radius: 26px;
    z-index: -1;
    opacity: 0;
    transition: opacity 0.3s;
  }
}

/* wrapper 内标题（对齐 mainmenu:631 的 section） */
.content-below .section {
  max-width: 1200px;
  margin: 0 auto 40px;
  text-align: center;

  h3 {
    font-size: 36px;
    margin-bottom: 20px;
    color: white;
  }

  p {
    font-size: 18px;
    line-height: 1.6;
    color: rgba(255, 255, 255, 0.9);
  }

  .section-divider {
    width: 1000px;
    height: 3px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8), transparent);
    margin: 0 auto;
    border-radius: 2px;
    max-width: 100%;
  }
}

/* 对齐 mainmenu:638 的 cards-container */
.content-below .cards-container {
  display: flex;
  justify-content: center;
  gap: 30px;
  flex-wrap: wrap;
  max-width: 1200px;
  margin: 0 auto;

  .card {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    padding: 40px 30px;
    width: 100%;
    max-width: 820px;
    text-align: center;
    transition: transform 0.3s, background 0.3s;

    &:hover {
      transform: translateY(-10px);
      background: rgba(255, 255, 255, 0.25);
    }

    .game-icon {
      width: 100%;
      height: 500px;
      object-fit: cover;
      border-radius: 16px;
      margin: 0 auto 24px;
      display: block;
      filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.15));
    }

    h4 {
      font-size: 24px;
      margin-bottom: 15px;
      color: white;
    }

    p {
      font-size: 14px;
      margin-bottom: 20px;
      opacity: 0.9;
      color: rgba(255, 255, 255, 0.9);
      line-height: 1.5;
    }

    .btn {
      background: rgba(255, 255, 255, 0.3);
      border: 1px solid white;
      color: white;
      padding: 8px 20px;
      border-radius: 25px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: white;
        color: #667eea;
      }
    }
  }
}

@media (max-width: 768px) {
  .nav-menu {
    gap: 15px;
  }

  .nav-item {
    font-size: 14px;
  }

  .login-btn,
  .logout-btn {
    padding: 5px 12px;
  }
}
</style>

