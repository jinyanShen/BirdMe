<template>
  <div class="funfacts-container">
    <NavBar :showNavbar="true" />
    <div class="content-below">
      <!-- 轮播图区域 -->
      <div class="carousel-section">
        <div class="section-header">
          <h3>Fun Facts & Games</h3>
          <p>Click on the games below to play and learn amazing bird facts!</p>
        </div>

        <el-carousel
          :interval="4000"
          arrow="always"
          height="450px"
          class="bird-carousel"
          @change="handleCarouselChange"
        >
          <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
            <div
              class="carousel-card"
              :style="{ backgroundImage: 'linear-gradient(135deg, rgba(0,0,0,0.5), rgba(0,0,0,0.7)), url(' + item.background + ')' }"
              @click="goToGame(item.link)"
            >
              <div class="carousel-content">
                <h2 class="title">{{ item.title }}</h2>
                <p class="description">{{ item.description }}</p>
                <div class="fun-fact">
                </div>
                <button class="play-button">
                  Play Now <i class="el-icon-arrow-right"></i>
                </button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>

        <!-- 自定义指示器 -->
        <div class="custom-indicators">
          <span
            v-for="(item, index) in carouselItems"
            :key="index"
            :class="['indicator', { active: currentSlide === index }]"
            @click="goToSlide(index)"
          >
          </span>
        </div>
      </div>

      <!-- 趣味知识列表 -->
      <div class="facts-wrapper">
        <div class="section-title">
          <h3>🌟 More Amazing Bird Facts</h3>
          <div class="title-divider"></div>
        </div>

        <div class="facts-list">
          <div v-for="(fact, index) in funFacts" :key="index" class="fact-item">
            <div class="fact-number">{{ (index + 1).toString().padStart(2, '0') }}</div>
            <div class="fact-content">
              <h4>{{ fact.title }}</h4>
              <p>{{ fact.description }}</p>
              <span class="fact-category">{{ fact.category }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from '@/components/NavBar/navbar.vue'
import flappyImg from '@/assets/images/flappy.png'
import img2048 from '@/assets/images/2048.png'

export default {
  name: 'FunFacts',
  components: {
    NavBar
  },
  data() {
    return {
      currentSlide: 0,
      carouselItems: [
        {
          title: 'Flappy Bird',
          description: 'Guide the bird through pipes and see how far you can go.',
          link: '/game/flappy',
          background: flappyImg,
          funFact: 'Hummingbirds are the only birds that can fly backward!'
        },
        {
          title: '2048 Bird',
          description: 'Challenge your brain! Combine tiles to reach the 2048 bird.',
          link: '/game/2048',
          background: img2048,
          funFact: 'An ostrich\'s eye is bigger than its brain!'
        }
      ],
      funFacts: [
        {
          title: 'Hummingbirds Can Fly Backward',
          description: 'Hummingbirds are the only birds that can fly backward and hover in mid-air. Their wings beat up to 80 times per second!',
          category: 'Amazing Flight'
        },
        {
          title: 'Ostriches Have Giant Eyes',
          description: 'An ostrich\'s eye is bigger than its brain! It measures about 2 inches (5 cm) in diameter.',
          category: 'Did You Know?'
        },
        {
          title: 'Birds Can Sleep While Flying',
          description: 'Alpine swifts can fly non-stop for up to 200 days, sleeping and eating while in the air!',
          category: 'Incredible'
        },
        {
          title: 'Penguins Propose with Pebbles',
          description: 'Male penguins search for the smoothest pebble to present to a female as a "proposal"!',
          category: 'Romantic'
        },
        {
          title: 'Crows Remember Faces',
          description: 'Crows can recognize human faces and remember people who were mean to them for years!',
          category: 'Super Smart'
        },
        {
          title: 'Longest Migration',
          description: 'Arctic Terns travel about 60,000 miles (96,000 km) each year from Arctic to Antarctica!',
          category: 'Travel Champion'
        },
        {
          title: 'Woodpeckers Have Shock Absorbers',
          description: 'Woodpeckers have special skull structures that protect their brains from repeated pounding.',
          category: 'Amazing Adaptations'
        },
        {
          title: 'Birds Have Dialects',
          description: 'Birds of the same species can have different "accents" depending on where they live!',
          category: 'Fun Fact'
        }
      ]
    }
  },
  methods: {
    goToGame(link) {
      this.$router.push(link)
    },
    handleCarouselChange(index) {
      this.currentSlide = index
    },
    goToSlide(index) {
      const carousel = this.$el.querySelector('.bird-carousel')
      if (carousel && carousel.__vue__) {
        carousel.__vue__.setActiveItem(index)
      }
      this.currentSlide = index
    }
  }
}
</script>

<style scoped lang="scss">
.funfacts-container {
  width: 100%;
  min-height: 100vh;
  position: relative;
}

.content-below {
  background: linear-gradient(135deg, #80c5e3 0%, #44bce1 100%);
  padding: 100px 20px 80px;
  color: white;
}

/* ========== 轮播图样式 ========== */
.carousel-section {
  max-width: 1200px;
  margin: 0 auto 80px;

  .section-header {
    text-align: center;
    margin-bottom: 40px;

    h3 {
      font-size: 42px;
      margin-bottom: 15px;
      font-weight: bold;
      text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
    }

    p {
      font-size: 18px;
      opacity: 0.95;
    }
  }

  .bird-carousel {
    border-radius: 24px;
    overflow: hidden;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);

    .carousel-card {
      width: 100%;
      height: 100%;
      background-size: cover;
      background-position: center;
      background-repeat: no-repeat;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: transform 0.3s;

      &:hover {
        .carousel-content {
          transform: scale(1.02);
        }
      }

      .carousel-content {
        text-align: center;
        color: white;
        padding: 50px;
        max-width: 700px;
        transition: transform 0.3s;
        animation: fadeInUp 0.5s ease-out;

        .title {
          font-size: 48px;
          font-weight: bold;
          margin-bottom: 20px;
          text-shadow: 2px 2px 12px rgba(0, 0, 0, 0.5);
        }

        .description {
          font-size: 18px;
          line-height: 1.6;
          margin-bottom: 30px;
          text-shadow: 1px 1px 6px rgba(0, 0, 0, 0.4);
        }

        .play-button {
          background: rgba(255, 255, 255, 0.3);
          backdrop-filter: blur(10px);
          border: 2px solid white;
          color: white;
          padding: 12px 32px;
          border-radius: 40px;
          font-size: 18px;
          font-weight: bold;
          cursor: pointer;
          transition: all 0.3s;
          display: inline-flex;
          align-items: center;
          gap: 8px;

          &:hover {
            background: white;
            color: #66b3ea;
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
          }
        }
      }
    }
  }
}

/* 自定义指示器 */
.custom-indicators {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 25px;

  .indicator {
    width: 12px;
    height: 12px;
    background: rgba(255, 255, 255, 0.5);
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: rgba(255, 255, 255, 0.8);
      transform: scale(1.2);
    }

    &.active {
      background: #ffd966;
      width: 30px;
      border-radius: 6px;
      transform: scale(1);
    }
  }
}

/* ========== 趣味知识列表样式 ========== */
.facts-wrapper {
  max-width: 1200px;
  margin: 0 auto;

  .section-title {
    text-align: center;
    margin-bottom: 50px;

    h3 {
      font-size: 38px;
      margin-bottom: 20px;
      font-weight: bold;
    }

    .title-divider {
      width: 80px;
      height: 4px;
      background: linear-gradient(90deg, transparent, #ffd966, transparent);
      margin: 0 auto;
      border-radius: 2px;
    }
  }

  .facts-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
    gap: 25px;

    .fact-item {
      display: flex;
      gap: 20px;
      background: rgba(255, 255, 255, 0.4);
      backdrop-filter: blur(10px);
      border-radius: 16px;
      padding: 25px;
      transition: all 0.3s;
      border: 1px solid rgba(255, 255, 255, 0.3);

      &:hover {
        transform: translateX(5px);
        background: rgba(255, 255, 255, 0.2);
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
      }

      .fact-number {
        font-size: 36px;
        font-weight: bold;
        color: #e38233;
        opacity: 0.5;
        min-width: 60px;
      }

      .fact-content {
        flex: 1;

        h4 {
          font-size: 22px;
          margin-bottom: 10px;
          color: #ff9100;
        }

        p {
          font-size: 18px;
          line-height: 1.5;
          margin-bottom: 12px;
          color: rgba(0, 0, 0, 0.9);
        }

        .fact-category {
          display: inline-block;
          background: rgba(255, 217, 102, 0.3);
          padding: 4px 12px;
          border-radius: 20px;
          font-size: 12px;
          color: #ffd966;
        }
      }
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Element UI Carousel 样式覆盖 */
::v-deep .el-carousel__arrow {
  background-color: rgba(0, 0, 0, 0.5);
  width: 45px;
  height: 45px;
  border-radius: 50%;

  &:hover {
    background-color: rgba(0, 0, 0, 0.8);
  }

  i {
    font-size: 20px;
  }
}

::v-deep .el-carousel__indicators {
  display: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .carousel-section {
    .section-header h3 {
      font-size: 28px;
    }

    .bird-carousel {
      height: 500px !important;

      .carousel-card .carousel-content {
        padding: 30px 20px;

        .title {
          font-size: 32px;
        }

        .description {
          font-size: 14px;
        }

        .fun-fact {
          font-size: 12px;
          padding: 8px 16px;
        }

        .play-button {
          padding: 10px 24px;
          font-size: 16px;
        }
      }
    }
  }

  .custom-indicators .indicator {
    width: 8px;
    height: 8px;

    &.active {
      width: 20px;
    }
  }

  .facts-wrapper .facts-list {
    grid-template-columns: 1fr;

    .fact-item {
      padding: 20px;

      .fact-number {
        font-size: 28px;
        min-width: 45px;
      }
    }
  }
}
</style>
