<template>
  <div class="app-container">
    <!-- Header Navigation -->
    <header class="header-area">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <nav class="main-nav">
              <!-- Brand Logo -->
              <div class="logo">
                <a href="#" @click.prevent="$router.push('/identification')">
                  <span class="logo-text">BIRDME</span>
                </a>
              </div>

              <!-- Navigation Menu -->
              <ul class="nav">
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle">Knowledge</a>
                  <ul class="dropdown-menu">
                    <li><a href="#" @click.prevent="$router.push('/knowledge/migration')">Migration Map</a></li>
                    <li><a href="#" @click.prevent="$router.push('/knowledge/identification')">Identification</a></li>
                    <li><a href="#" @click.prevent="$router.push('/knowledge/facts')">Fun Facts</a></li>
                  </ul>
                </li>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle">Forum</a>
                  <ul class="dropdown-menu">
                    <li><a href="#" @click.prevent="$router.push('/forum/birdwatching')">Bird Watching</a></li>
                    <li><a href="#" @click.prevent="$router.push('/forum/qa')">Q&A</a></li>
                  </ul>
                </li>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle">Game</a>
                  <ul class="dropdown-menu">
                    <li><a href="#" @click.prevent="$router.push('/game/flappy')">Flappy</a></li>
                    <li><a href="#" @click.prevent="$router.push('/game/2048')">2048</a></li>
                    <li><a href="#" @click.prevent="$router.push('/game/merge')">Merge</a></li>
                  </ul>
                </li>
                <li><a href="#" :class="{ active: $route.path === '/settings' }" @click="$router.push('/settings')">Personal Center</a></li>
                <li><a href="#" :class="{ active: $route.path === '/report' }" @click="$router.push('/report')">My Reports</a></li>
              </ul>

              <!-- User Info -->
              <div class="user-info">
                <div class="user-avatar" @click="showUserMenu = !showUserMenu">
                  <img :src="userAvatar" alt="User" />
                </div>
                <div v-if="showUserMenu" class="user-menu">
                  <a href="#" @click="handleLogout">Logout</a>
                </div>
              </div>
            </nav>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content Area -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="footer-area">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <div class="footer-content">
              <p>&copy; 2026 BIRDME Intelligent Bird Identification System. All rights reserved.</p>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return {
      showUserMenu: false,
      userAvatar: ''
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

<style lang="scss" scoped>
/* Global Style Variables */
:root {
  --primary-color: #22b3c1;
  --secondary-color: #4dd0e1;
  --text-color: #333;
  --text-light: #666;
  --background-light: #e0f7fa;
  --white: #ffffff;
  --border-color: #b2ebf2;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  font-family: 'Poppins', sans-serif;
}

/* Header Navigation */
.header-area {
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  padding: 15px 0;
  box-shadow: 0 4px 15px rgba(34, 179, 193, 0.15);
  position: sticky;
  top: 0;
  z-index: 100;
  display: none !important;
}
  .main-nav {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .logo {
      display: flex;
      align-items: center;

      a {
        text-decoration: none;
      }

      .logo-text {
        font-size: 28px;
        font-weight: 800;
        color: #22b3c1;
        text-transform: uppercase;
        letter-spacing: 3px;
        transition: all 0.3s ease;

        &:hover {
          transform: scale(1.05);
        }
      }
    }

    .nav {
      display: flex;
      list-style: none;
      gap: 40px;

      li {
        position: relative;

        a {
          color: #22b3c1;
          text-decoration: none;
          font-weight: 500;
          font-size: 16px;
          transition: all 0.3s ease;
          position: relative;
          padding: 10px 0;
          display: block;

          &:hover {
            color: #0097a7;
          }

          &.active {
            color: #0097a7;
            font-weight: 600;

            &::after {
              content: '';
              position: absolute;
              bottom: 0;
              left: 0;
              width: 100%;
              height: 3px;
              background: var(--secondary-color);
              border-radius: 2px;
            }
          }
        }

        .dropdown-menu {
          position: absolute;
          top: 100%;
          left: 0;
          background: #ffffff;
          border-radius: 12px;
          box-shadow: 0 15px 30px rgba(34, 179, 193, 0.2);
          padding: 15px 0;
          min-width: 200px;
          z-index: 1000;
          margin-top: 5px;
          opacity: 0;
          visibility: hidden;
          transition: opacity 0.3s ease, visibility 0.3s ease;

          li {
            a {
              padding: 12px 20px;
              font-size: 14px;
              font-weight: 500;

              &:hover {
                background: var(--background-light);
                color: var(--primary-color);
                padding-left: 25px;
              }

              &::after {
                display: none;
              }
            }
          }
        }

        &.dropdown {
          &:hover {
            .dropdown-menu {
              opacity: 1;
              visibility: visible;
            }
          }

          .dropdown-toggle {
            cursor: pointer;
            position: relative;

            &::after {
              content: '▼';
              font-size: 10px;
              margin-left: 5px;
              vertical-align: middle;
              transition: transform 0.3s ease;
            }
          }
        }
      }
    }

    .user-info {
      position: relative;

      .user-avatar {
        width: 45px;
        height: 45px;
        border-radius: 50%;
        overflow: hidden;
        cursor: pointer;
        border: 3px solid rgba(34, 179, 193, 0.3);
        transition: all 0.3s ease;

        &:hover {
          border-color: var(--secondary-color);
          transform: scale(1.1);
        }

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .user-menu {
        position: absolute;
        top: 60px;
        right: 0;
        background: #ffffff;
        border-radius: 12px;
        box-shadow: 0 15px 30px rgba(34, 179, 193, 0.2);
        padding: 15px 0;
        min-width: 180px;
        z-index: 1000;
        animation: slideDown 0.3s ease;

        a {
          display: block;
          padding: 12px 20px;
          color: var(--text-color);
          text-decoration: none;
          transition: all 0.3s ease;
          font-size: 14px;
          font-weight: 500;

          &:hover {
            background: var(--background-light);
            color: var(--primary-color);
            padding-left: 25px;
          }
        }
      }
    }
  }

/* Main Content Area */
.main-content {
  flex: 1;
  padding: 60px 0;
}

/* Footer */
.footer-area {
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  padding: 40px 0;
  border-top: 1px solid var(--border-color);
  margin-top: 60px;

  .footer-content {
    text-align: center;
    color: #22b3c1;
    font-size: 14px;
    font-weight: 500;
  }
}

/* Animation Effects */
@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive Design */
@media (max-width: 992px) {
  .header-area {
    padding: 12px 0;

    .main-nav {
      flex-direction: column;
      gap: 20px;
      padding: 10px 0;

      .nav {
        gap: 30px;

        li a {
          font-size: 14px;
        }
      }

      .user-info {
        margin-top: 10px;
      }
    }
  }

  .main-content {
    padding: 40px 0;
  }

  .footer-area {
    padding: 30px 0;
    margin-top: 40px;
  }
}

@media (max-width: 768px) {
  .header-area {
    .main-nav {
      .logo {
        .logo-text {
          font-size: 28px;
        }
      }

      .nav {
        gap: 20px;

        li a {
          font-size: 13px;
        }
      }
    }
  }

  .main-content {
    padding: 30px 0;
  }
}
</style>
