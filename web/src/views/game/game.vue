<template>
  <div class="game-page">
    <NavBar :showNavbar="true" />
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
    </div>
  </div>
</template>

<script>
import flappyImg from '@/assets/images/flappy.png'
import img2048 from '@/assets/images/2048.png'
import mergeImg from '@/assets/images/merge.png'
import NavBar from '@/components/NavBar/navbar.vue'

export default {
  name: 'Game',
  components: {
    NavBar
  },
  data() {
    return {
      flappyImg,
      img2048,
      mergeImg
    }
  },
  methods: {
    selectGame(gameKey) {
      this.$router.push(`/game/${gameKey}`)
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

.content-below {
  background: linear-gradient(135deg, #97a1d3 0%, #856ad5 100%);
  padding: 100px 20px 80px;
  color: white;
}

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

.section {
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
</style>

