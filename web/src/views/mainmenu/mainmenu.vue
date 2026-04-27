<script>
import bgGif from '@/assets/images/bg.gif'

export default {
  name: 'MainMenu',
  data() {
    return {
      showAnimation: true,
      leftClouds: [],
      rightClouds: [],
      bgGif: bgGif,
      showNavbar: false,
      isLoggedIn: false,
      dropdowns: {
        knowledge: false,
        forum: false,
        game: false
      },
      isKnowledgeActive: false,

      newsList: [
        {
          id: 1,
          title: "Indigenous Groups Track Epic Bird Migration",
          description: "Communities in Australia and Alaska collaborate to track migratory birds and protect their routes.",
          image: "https://picsum.photos/800/400?random=1",
          url: "https://www.theguardian.com/australia-news/2026/apr/26/indigenous-groups-australia-alaska-track-short-tailed-shearwater-bird"
        },
        {
          id: 2,
          title: "Endangered Birds Learn to Sing Again",
          description: "Scientists help critically endangered birds recover their lost songs to improve breeding success.",
          image: "https://picsum.photos/800/400?random=2",
          url: "https://www.theguardian.com/environment/2026/mar/04/wild-birds-teach-honeyeaters-sing-songs"
        },
        {
          id: 3,
          title: "One Bird Inspires a Conservation Movement",
          description: "A pet cockatiel sparks a nationwide effort to build habitats and protect bird populations.",
          image: "https://picsum.photos/800/400?random=3",
          url: "https://www.washingtonpost.com/climate-solutions/2026/01/10/texas-bird-conservation-movement/"
        }
      ]
    }
  },
  // mounted() {
  //   this.generateClouds()
  //   this.initScrollAnimation()
  //   this.checkLoginStatus()
  // },
  mounted() {
    // 修改了云朵动画，使其在会话内只播放一次动画--->每次打开网页都会播放，但是在网页间跳转则不会播放
    if (sessionStorage.getItem('animationPlayed')) {
      this.showAnimation = false
    } else {
      sessionStorage.setItem('animationPlayed', 'true')
    }

    this.generateClouds()
    this.initScrollAnimation()
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
    goToForumPage(tab) {
      if (this.isLoggedIn) {
        this.$router.push(`/forum/${tab}`)
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog(`/forum/${tab}`)
        }
      }
    },
    // 检查登录状态
    checkLoginStatus() {
      this.isLoggedIn = sessionStorage.getItem('id') !== null
    },

    // 登出
    handleLogout() {
      // 清除所有登录信息
      sessionStorage.removeItem('id')
      sessionStorage.removeItem('username')
      sessionStorage.removeItem('password')
      sessionStorage.removeItem('name')
      sessionStorage.removeItem('age')
      sessionStorage.removeItem('phone')
      sessionStorage.removeItem('avatarUrl')
      sessionStorage.removeItem('role')

      this.isLoggedIn = false
      this.$message.success('Logged out successfully')

      // 如果当前在需要登录的页面，跳转到首页
      const needAuthPages = ['/identification', '/settings']
      if (needAuthPages.includes(this.$route.path)) {
        this.$router.push('/')
      }
    },

    goToNews(url) {
      window.open(url, '_blank')
    },

    goToHome() {
      if (this.$route.path === '/') {
        return
      }
      this.$router.push('/')
    },

    // 跳转到 Knowledge 页面
    goToKnowledge() {
      if (this.isLoggedIn) {
        this.$router.push('/knowledge')
      } else {
        // 未登录，弹出登录弹窗
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/knowledge')
        }
      }
    },

    // 跳转到 Rescue 页面
    goToRescue() {
      if (this.isLoggedIn) {
        this.$router.push('/rescue')
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/rescue')
        }
      }
    },

    // 跳转到 Forum 页面
    goToForum() {
      if (this.isLoggedIn) {
        this.$router.push('/forum')
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/forum')
        }
      }
    },

    // 跳转到 Game 页面
    goToGame() {
      if (this.isLoggedIn) {
        this.$router.push('/game')
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/game')
        }
      }
    },

    // 顶部 Game 下拉子项点击（当前先做占位提示）
    selectGameFromNav(gameKey) {
      const targetPath = `/game/${gameKey}`
      if (this.$route.path === targetPath) return

      this.$router.push(targetPath)
    },

    // 跳转到 Personal Profile 页面
    goToPersonalPage() {
      if (this.isLoggedIn) {
        this.$router.push('/settings')
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/settings')
        }
      }
    },

    // 跳转到登录页
    goToLogin() {
      this.$router.push('/login')
    },

    generateClouds() {
      const leftClouds = []
      const rightClouds = []

      const leftPositions = [-10, -20, -30, -10, -20, -30, -10, -20, -30, 0]
      const rightPositions = [40, 50, 50, 30, 50, 20, 40, 50, 60, 30]
      const leftTopPositions = [-40, -20, 0, 20, 40, -30, -10, 10, 30, 50]
      const rightTopPositions = [-35, -15, 5, 25, 45, -25, -5, 15, 35, 55]

      for (let i = 0; i < 10; i++) {
        leftClouds.push({
          id: `left-${i}`,
          left: leftPositions[i],
          top: leftTopPositions[i],
          delay: i * 0.3,
          duration: 5,
          width: 1200,
          height: 750,
          opacity: 0.95,
          direction: 'left',
          image: require('@/assets/images/cloud1.png')
        })
      }

      for (let i = 0; i < 10; i++) {
        rightClouds.push({
          id: `right-${i}`,
          left: rightPositions[i],
          top: rightTopPositions[i],
          delay: i * 0.3,
          duration: 5,
          width: 1200,
          height: 750,
          opacity: 0.95,
          direction: 'right',
          image: require('@/assets/images/cloud1.png')
        })
      }

      this.leftClouds = leftClouds
      this.rightClouds = rightClouds
    },

    onCloudAnimationEnd() {
      setTimeout(() => {
        this.showAnimation = false
        this.$nextTick(() => {
          this.handleScroll()
        })
      }, 1000)
    },

    initScrollAnimation() {
      const scrollContainer = document.querySelector('.main-content')
      if (scrollContainer) {
        scrollContainer.addEventListener('scroll', this.handleScroll)
      }
      this.handleScroll()
    },

    handleScroll() {
      const scrollContainer = document.querySelector('.main-content')
      const scrollTop = scrollContainer ? scrollContainer.scrollTop : 0

      this.showNavbar = scrollTop > 100

      const contentBelowChildren = document.querySelectorAll('.content-below > *')
      if (contentBelowChildren.length > 0) {
        const maxScroll = 1200
        let opacity = scrollTop / maxScroll
        opacity = Math.min(opacity, 1)

        contentBelowChildren.forEach((child, index) => {
          child.style.opacity = opacity
          let translateY = 50 - (scrollTop / maxScroll) * 50
          translateY = Math.max(translateY, 0)
          child.style.transform = `translateY(${translateY * (1 - index * 0.2)}px)`
        })
      }

      const elements = document.querySelectorAll('.fade-in-up')
      const windowHeight = window.innerHeight

      elements.forEach(el => {
        const rect = el.getBoundingClientRect()
        if (rect.top < windowHeight + 100) {
          el.classList.add('visible')
        }
      })
    }
  },

  watch: {
    '$route'() {
      this.checkLoginStatus()
    }
  },

  beforeDestroy() {
    const scrollContainer = document.querySelector('.main-content')
    if (scrollContainer) {
      scrollContainer.removeEventListener('scroll', this.handleScroll)
    }
  }
}
</script>

<template>
  <div class="bird-home-container">
    <!-- 开场动画遮罩层 -->
    <div v-if="showAnimation" class="opening-animation">
      <div class="sky-bg"></div>

      <!-- 文字内容 -->
      <div class="welcome-text">
        <h1>BirdME</h1>
        <p>Group2_TOT</p>
      </div>

      <!-- 左边云朵层 -->
      <div class="clouds-layer">
        <div
          v-for="cloud in leftClouds"
          :key="cloud.id"
          class="cloud cloud-left"
          :style="{
            left: cloud.left + '%',
            top: cloud.top + '%',
            width: cloud.width + 'px',
            height: cloud.height + 'px',
            opacity: cloud.opacity,
            animationDelay: cloud.delay + 's',
            animationDuration: cloud.duration + 's'
          }"
          @animationend="onCloudAnimationEnd"
        >
          <img :src="cloud.image" alt="cloud" style="width: 100%; height: 100%; object-fit: fill;">
        </div>
      </div>

      <!-- 右边云朵层 -->
      <div class="clouds-layer">
        <div
          v-for="cloud in rightClouds"
          :key="cloud.id"
          class="cloud cloud-right"
          :style="{
            left: cloud.left + '%',
            top: cloud.top + '%',
            width: cloud.width + 'px',
            height: cloud.height + 'px',
            opacity: cloud.opacity,
            animationDelay: cloud.delay + 's',
            animationDuration: cloud.duration + 's'
          }"
          @animationend="onCloudAnimationEnd"
        >
          <img :src="cloud.image" alt="cloud" style="width: 100%; height: 100%; object-fit: contain; transform: scaleX(-1);">        </div>
      </div>
    </div>

    <div class="main-content" :class="{ 'visible': !showAnimation }">
      <!-- 顶部导航栏 -->
      <div class="top-navbar" :class="{ 'navbar-visible': showNavbar }">
        <div class="navbar-container">
          <div class="logo">
            <h3>BirdME</h3>
          </div>
          <div class="nav-menu">
            <router-link to="/" class="nav-item">Homepage</router-link>

            <!-- Knowledge 下拉菜单 - 修改：主项可点击跳转 -->
            <div class="dropdown">
              <span class="nav-item" :class="{ active: isKnowledgeActive }" @click="goToKnowledge">Knowledge ▾</span>
              <div class="dropdown-menu">
                <div class="dropdown-item" @click="goToKnowledgePage('migration')">Migration Map</div>
                <div class="dropdown-item" @click="goToKnowledgePage('identification')">Identification</div>
                <div class="dropdown-item" @click="goToFunFacts">Fun Facts</div>
              </div>
            </div>

            <!-- Forum 下拉菜单 - 修改：主项可点击跳转 -->
            <div class="dropdown">
              <span class="nav-item" @click="goToForum">Forum ▾</span>
              <div class="dropdown-menu">
                <div class="dropdown-item" @click="goToForumPage('birdwatching')">Bird Watching</div>
                <div class="dropdown-item" @click="goToForumPage('qa')">Q&A</div>
              </div>
            </div>

            <!-- Game 下拉菜单 - 修改：主项可点击跳转 -->
            <div class="dropdown game-dropdown">
              <span class="nav-item" @click="goToGame">Game ▾</span>
              <div class="game-dropdown-menu">
                <div class="game-dropdown-item" @click="selectGameFromNav('flappy')">Flappy Bird</div>
                <div class="game-dropdown-item" @click="selectGameFromNav('2048')">2048 Bird</div>
                <div class="game-dropdown-item" @click="selectGameFromNav('merge')">Merge To Giant Bird</div>
              </div>
            </div>

            <span class="nav-item" @click="goToPersonalPage">Personal Settings</span>
            <span v-if="!isLoggedIn" class="nav-item login-btn" @click="goToLogin">Login</span>
            <span v-else class="nav-item logout-btn" @click="handleLogout">LogOut</span>
          </div>
        </div>
      </div>


      <!--      <div class="image-container">-->
<!--        <img :src="bgGif" alt="background" class="top-image">-->
<!--        <div class="overlay-text">-->
<!--          <h2>Welcome to BirdME<br>Here you can identify birds, track migrations, and submit rescue reports</h2>-->
<!--          <p>Group2_TOT | AI Bird Identification & Rescue System</p>-->
<!--        </div>-->
<!--      </div>-->
      <div class="image-container">
        <img :src="bgGif" alt="background" class="top-image">
        <div class="overlay-text">
          <h2>Welcome to <span>BirdME</span></h2>
          <p class="desc">Here you can identify birds, track migrations, and submit rescue reports</p>
          <p class="author">Group2_TOT | AI Bird Identification & Rescue System</p>
        </div>
      </div>

      <div class="content-below">

        <!-- 轮播图容器 -->
        <div class="carousel-section fade-in-up">
          <h3>News about Birds</h3>
          <el-carousel :interval="3000" arrow="always" height="400px" class="news-carousel">
            <el-carousel-item v-for="(item, index) in newsList" :key="index">
              <div class="carousel-item" @click="goToNews(item.url)">
                <img :src="item.image" :alt="item.title">
                <div class="carousel-overlay">
                  <h4>{{ item.title }}</h4>
                  <p>{{ item.description }}</p>
                  <span class="read-more">Check Detail →</span>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <div class="knowledge-cards-wrapper">
          <div class="section fade-in-up">
            <h3>Knowledge Base</h3>
            <p>A collection featuring bird identification, migration route maps, and educational resources.</p>
            <div class="section-divider"></div>
          </div>

          <div class="cards-container">
            <div class="card fade-in-up">
              <i class="el-icon-camera"></i>
              <h4>Identification</h4>
              <p>AI-powered bird species recognition</p>
              <button class="btn">Start Identifying</button>
            </div>

            <div class="card fade-in-up">
              <i class="el-icon-location-information"></i>
              <h4>Migration Map</h4>
              <p>Bird migration route map</p>
              <button class="btn">View Map</button>
            </div>

            <div class="card fade-in-up">
              <i class="el-icon-info"></i>
              <h4>Birding Tips</h4>
              <p>Bird conservation tips</p>
              <button class="btn">Learn More</button>
            </div>
          </div>
        </div>

        <!-- Rescue 板块 -->
        <div class="knowledge-cards-wrapper">
          <div class="section fade-in-up">
            <h3>Bird Rescue</h3>
            <p>Report injured birds, find rescue centers, and learn first‑aid tips to help birds in need.</p>
            <div class="section-divider"></div>
          </div>
          <div class="cards-container">
            <div class="card fade-in-up">
              <i class="el-icon-warning"></i>
              <h4>Report Injury</h4>
              <p>Submit rescue reports with location & photos</p>
              <button class="btn">Report Now</button>
            </div>
            <div class="card fade-in-up">
              <i class="el-icon-location"></i>
              <h4>Rescue Centers</h4>
              <p>Find nearest bird rescue & rehabilitation centers</p>
              <button class="btn">Find Centers</button>
            </div>
            <div class="card fade-in-up">
              <i class="el-icon-first-aid-kit"></i>
              <h4>First Aid Tips</h4>
              <p>Basic steps to help a bird before professional care</p>
              <button class="btn">Learn First Aid</button>
            </div>
          </div>
        </div>

        <!-- Forum 板块（重新设计版） -->
        <div class="knowledge-cards-wrapper">
          <div class="section fade-in-up">
            <h3>Community Forum</h3>
            <p>Expert tips, rescue assistance, and daily birdwatching stories – all in one place.</p>
            <div class="section-divider"></div>
          </div>
          <div class="cards-container">
            <div class="card fade-in-up">
              <i class="el-icon-star-on"></i>
              <h4>Featured & Sticky</h4>
              <p>Expert / admin pinned posts, guidelines, and essential resources</p>
              <button class="btn">Explore Highlights</button>
            </div>

            <div class="card fade-in-up">
              <i class="el-icon-warning-outline"></i>
              <h4>Rescue Help</h4>
              <p>Post urgent rescue requests or ask for help with injured birds</p>
              <button class="btn">Ask for Rescue</button>
            </div>

            <div class="card fade-in-up">
              <i class="el-icon-camera-solid"></i>
              <h4>Birdwatching & Life</h4>
              <p>Share daily sightings, bird photos, and casual conversations</p>
              <button class="btn">Join Discussion</button>
            </div>

          </div>
        </div>

        <!-- Game 板块 -->
        <div class="knowledge-cards-wrapper">
          <div class="section fade-in-up">
            <h3>Bird Games</h3>
            <p>Play fun bird‑themed games, challenge your friends, and earn badges.</p>
            <div class="section-divider"></div>
          </div>
          <div class="cards-container">
            <div class="card fade-in-up">
              <i class="fas fa-dove"></i>  <!-- 改为鸽子图标 -->
              <h4>Flappy Bird</h4>
              <p>Classic side‑scroller with bird characters</p>
              <button class="btn">Play Now</button>
            </div>
            <div class="card fade-in-up">
              <i class="fas fa-th-large"></i>  <!-- 改为网格图标 -->
              <h4>2048 Bird</h4>
              <p>Merge birds to reach the giant bird</p>
              <button class="btn">Play Now</button>
            </div>
            <div class="card fade-in-up">
              <i class="fas fa-code-branch"></i>  <!-- 改为分支图标 -->
              <h4>Merge To Giant Bird</h4>
              <p>Combine species to create rare birds</p>
              <button class="btn">Play Now</button>
            </div>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>

<style scoped lang="scss">

.bird-home-container {
  position: relative;
  width: 100%;
  min-height: 100vh;
}

/* 开场动画容器 */
.opening-animation {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  overflow: hidden;

  /* 蓝天背景 */
  .sky-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #bfd2f8 0%, #d2d9f4 50%, #dadff6 100%);
  }

  /* 欢迎文字 */
  .welcome-text {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    color: white;
    text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
    z-index: 10;
    opacity: 0;
    animation: textReveal 0.6s ease-out 3s forwards;
    pointer-events: none;

    h1 {
      font-size: 56px;
      margin-bottom: 20px;
      font-weight: bold;

      @media (max-width: 768px) {
        font-size: 32px;
      }
    }

    p {
      font-size: 24px;

      @media (max-width: 768px) {
        font-size: 18px;
      }
    }
  }

  /* 云朵层 */
  .clouds-layer {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 20;
  }

  /* 云朵基础样式 */
  .cloud {
    position: absolute;
    pointer-events: none;
    filter: drop-shadow(8px 12px 20px rgba(0, 0, 0, 0.2));
  }

  /* 左边云朵动画 - 向左散开 */
  .cloud-left {
    animation: cloudMoveLeft 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  }

  /* 右边云朵动画 - 向右散开 */
  .cloud-right {
    animation: cloudMoveRight 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  }
}

/* 主页面内容 */
.main-content {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  transition: opacity 0.8s ease-in-out;
  z-index: 1;
  overflow-y: auto;
  overflow-x: hidden;

  &.visible {
    opacity: 1;
  }
}

/* 顶部图片容器 */
.image-container {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;

  .top-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .overlay-text {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    color: white;
    text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.5);
    z-index: 2;
  }
}

.fade-in-up {
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.6s ease-out;

  &.visible {
    opacity: 1;
    transform: translateY(0);
  }
}

.content-below {
  background: linear-gradient(135deg, #bfbbe4 0%, #6668d8 100%);
  padding: 60px 20px;
  color: white;

  > * {
    transition: opacity 0.2s linear, transform 0.2s linear;
    opacity: 0;
    transform: translateY(50px);
  }

  .section {
    max-width: 1200px;
    margin: 0 auto 60px;
    text-align: center;

    h3 {
      font-size: 48px;  /* 原36px → 调大 */
      margin-bottom: 25px;
    }

    p {
      font-size: 24px;  /* 原18px → 调大 */
      line-height: 1.6;
    }
  }

  .cards-container {
    display: flex;
    justify-content: center;
    gap: 30px;
    flex-wrap: wrap;
    max-width: 1200px;
    margin: 0 auto 60px;

    .card {
      background: rgba(255, 255, 255, 0.15);
      backdrop-filter: blur(10px);
      border-radius: 16px;
      padding: 35px;
      width: 300px;  /* 原280px → 稍微调大 */
      text-align: center;
      transition: transform 0.3s, opacity 0.6s ease-out, transform 0.6s ease-out;

      &:hover {
        transform: translateY(-10px);
        background: rgba(255, 255, 255, 0.25);
      }

      i {
        font-size: 64px;  /* 原48px → 调大 */
        margin-bottom: 25px;
        display: block;
      }

      h4 {
        font-size: 28px;  /* 原24px → 调大 */
        margin-bottom: 18px;
      }

      p {
        font-size: 18px;  /* 原14px → 调大 */
        margin-bottom: 25px;
        opacity: 0.9;
      }

      .btn {
        background: rgba(255, 255, 255, 0.3);
        border: 1px solid white;
        color: white;
        padding: 10px 24px;  /* 原8px 20px → 调大 */
        border-radius: 25px;
        font-size: 16px;  /* 新增 */
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: white;
          color: #667eea;
        }
      }
    }
  }
  /*每部分的框*/
  .knowledge-cards-wrapper {
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.25);
    border-radius: 24px;
    padding: 50px 40px;  /* 原40px 30px → 调大 */
    margin: 0 auto 60px;
    max-width: 1200px;
    backdrop-filter: blur(10px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    position: relative;
    transition: all 0.3s;

    &::before {
      content: '';
      position: absolute;
      top: -2px;
      left: -2px;
      right: -2px;
      bottom: -2px;
      background: linear-gradient(135deg, rgba(255,255,255,0.3), rgba(255,255,255,0.1));
      border-radius: 26px;
      z-index: -1;
      opacity: 0;
      transition: opacity 0.3s;
    }

    &:hover {
      border-color: rgba(255, 255, 255, 0.4);
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);

      &::before {
        opacity: 1;
      }
    }

    .section {
      max-width: 1200px;
      margin: 0 auto 50px;  /* 原40px → 调大 */
      text-align: center;

      h3 {
        font-size: 48px;  /* 原36px → 调大 */
        margin-bottom: 25px;
        color: white;
      }

      p {
        font-size: 24px;  /* 原18px → 调大 */
        line-height: 1.6;
        color: rgba(255, 255, 255, 0.9);
      }

      .section-divider {
        width: 1000px;
        height: 3px;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8), transparent);
        margin: 0 auto;
        border-radius: 2px;
      }
    }

    .cards-container {
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
        padding: 35px;  /* 原30px → 调大 */
        width: 300px;  /* 原280px → 调大 */
        text-align: center;
        transition: transform 0.3s;

        &:hover {
          transform: translateY(-10px);
          background: rgba(255, 255, 255, 0.25);
        }

        i {
          font-size: 64px;  /* 原48px → 调大 */
          margin-bottom: 25px;
          display: block;
          color: white;
        }

        h4 {
          font-size: 28px;  /* 原24px → 调大 */
          margin-bottom: 18px;
          color: white;
        }

        p {
          font-size: 18px;  /* 原14px → 调大 */
          margin-bottom: 25px;
          opacity: 0.9;
          color: rgba(255, 255, 255, 0.9);
        }

        .btn {
          background: rgba(255, 255, 255, 0.3);
          border: 1px solid white;
          color: white;
          padding: 10px 24px;  /* 原8px 20px → 调大 */
          border-radius: 25px;
          font-size: 16px;  /* 新增 */
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            background: white;
            color: #667eea;
          }
        }
      }
    }
  }

  /*轮播图容器*/
  .carousel-section {
    max-width: 1200px;
    margin: 0 auto 80px;

    h3 {
      font-size: 48px;  /* 原36px → 调大 */
      text-align: center;
      margin-bottom: 50px;
    }

    .news-carousel {
      border-radius: 16px;
      overflow: hidden;
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);

      .carousel-item {
        position: relative;
        width: 100%;
        height: 100%;
        cursor: pointer;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .carousel-overlay {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
          padding: 50px 40px 40px;  /* 原40px 30px 30px → 调大 */
          color: white;
          text-align: left;

          h4 {
            font-size: 36px;  /* 原28px → 调大 */
            margin-bottom: 15px;
            font-weight: bold;
          }

          p {
            font-size: 20px;  /* 原16px → 调大 */
            margin-bottom: 20px;
            opacity: 0.9;
          }

          .read-more {
            display: inline-block;
            font-size: 18px;  /* 原14px → 调大 */
            color: #ffd966;
            transition: transform 0.3s;

            &:hover {
              transform: translateX(5px);
            }
          }
        }
      }
    }
  }
}

/* 轮播图指示器样式 - 保持不变 */
::v-deep .el-carousel__indicators {
  bottom: 20px;

  .el-carousel__button {
    width: 30px;
    height: 3px;
    background-color: rgba(255, 255, 255, 0.5);

    &:hover {
      background-color: rgba(255, 255, 255, 0.8);
    }
  }

  .is-active .el-carousel__button {
    background-color: #ffd966;
  }
}

::v-deep .el-carousel__arrow {
  background-color: rgba(0, 0, 0, 0.5);

  &:hover {
    background-color: rgba(0, 0, 0, 0.8);
  }
}

/* 左边云朵向左移动动画 - 保持不变 */
@keyframes cloudMoveLeft {
  0% {
    transform: translateX(0) scale(1);
    opacity: 0.95;
  }
  100% {
    transform: translateX(-500px) scale(0.8);
    opacity: 0;
  }
}

/* 右边云朵向右移动动画 - 保持不变 */
@keyframes cloudMoveRight {
  0% {
    transform: translateX(0) scale(1);
    opacity: 0.95;
  }
  100% {
    transform: translateX(500px) scale(0.8);
    opacity: 0;
  }
}

/* 顶部导航栏 - 完全保持不变 */
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
    padding: 15px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .logo {
      h3 {
        margin: 0;
        font-size: 28px;
        color: #8fa0ca;
        font-weight: bold;
      }
    }

    .nav-menu {
      display: flex;
      gap: 40px;
      align-items: center;

      .nav-item {
        text-decoration: none;
        color: #333;
        font-size: 20px;
        transition: color 0.3s;
        cursor: pointer;

        &:hover {
          color: #f4f4f8;
        }
      }

      /* Game 顶部下拉菜单 */
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
        }

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

        &:hover {
          .game-dropdown-menu {
            opacity: 1;
            visibility: visible;
            transform: translateX(-50%) translateY(0);
          }
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
    }
  }
}

/* 文字显现动画 - 保持不变 */
@keyframes textReveal {
  from {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
}

/* 响应式调整 - 保持不变 */
@media (max-width: 768px) {
  .top-navbar {
    .navbar-container {
      padding: 10px 15px;

      .logo h3 {
        font-size: 18px;
      }

      .nav-menu {
        gap: 15px;

        .nav-item {
          font-size: 14px;
        }

        .login-btn {
          padding: 5px 12px;
        }
      }
    }
  }

  .cloud-left {
    animation: cloudMoveLeft 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards !important;
  }

  .cloud-right {
    animation: cloudMoveRight 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards !important;
  }

  @keyframes cloudMoveLeft {
    100% {
      transform: translateX(-250px) scale(0.8);
    }
  }

  @keyframes cloudMoveRight {
    100% {
      transform: translateX(250px) scale(0.8);
    }
  }

  /* 移动端内容字号适配 - 新增 */
  .content-below {
    .section {
      h3 {
        font-size: 36px;
      }
      p {
        font-size: 18px;
      }
    }
    .cards-container .card {
      width: 260px;
      padding: 25px;
      h4 {
        font-size: 22px;
      }
      p {
        font-size: 14px;
      }
    }
    .knowledge-cards-wrapper {
      padding: 30px 20px;
      .section {
        h3 {
          font-size: 36px;
        }
        p {
          font-size: 18px;
        }
      }
      .cards-container .card {
        width: 260px;
        padding: 25px;
        h4 {
          font-size: 22px;
        }
        p {
          font-size: 14px;
        }
        i {
          font-size: 48px;
        }
      }
    }
    .carousel-section {
      h3 {
        font-size: 36px;
      }
      .news-carousel .carousel-item .carousel-overlay {
        h4 {
          font-size: 24px;
        }
        p {
          font-size: 14px;
        }
        .read-more {
          font-size: 14px;
        }
      }
    }
  }
}

.nav-item {
  cursor: pointer;
}

.logo h3 {
  cursor: pointer;
}

.logout-btn {
  background: #ff6b6b;
  color: white !important;
  padding: 8px 20px;
  border-radius: 25px;
  transition: all 0.3s;
  cursor: pointer;

  &:hover {
    background: #ff5252;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
  }
}

.overlay-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;

  padding: 80px 120px;
  border-radius: 32px;
  background: linear-gradient(135deg, rgba(103, 143, 230, 0.7), rgba(161, 191, 230, 0.7));
  backdrop-filter: blur(5px);
  box-shadow: 0 12px 40px rgba(63, 51, 128, 0.4);
  z-index: 2;
}

.overlay-text h2 {
  font-size: 90px;
  font-weight: 800;
  color: #ffffff;
  margin: 0 0 20px 0;
  text-shadow: 0 2px 12px rgba(0,0,0,0.3);
  line-height: 1.1;
}

.overlay-text h2 span {
  color: #ffd966;
}

.overlay-text .desc {
  font-size: 30px;
  font-weight: 400;
  color: rgba(255,255,255,0.95);
  margin: 0 0 28px 0;
  line-height: 1.3;
}

.overlay-text .author {
  font-size: 28px;
  font-weight: 300;
  color: rgba(255,255,255,0.75);
  margin: 0;
}

</style>
