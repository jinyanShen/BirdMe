<template>
  <div class="game-subpage-page">
    <div class="content-below">
      <div class="knowledge-cards-wrapper">
        <div class="section">
          <h3>{{ gameTitle }}</h3>
          <div class="section-divider"></div>
        </div>

        <div class="cards-container">
          <div v-if="gameKey === 'flappy'" class="card game-container">
            <iframe src="/clumsy-bird/index.html" frameborder="0" width="100%" height="800px"></iframe>
          </div>
          <div v-else-if="gameKey === '2048'" class="card game-container">
            <iframe src="https://ng2048.github.io/" frameborder="0" width="100%" height="800px"></iframe>
          </div>
          <div v-else-if="gameKey === 'merge'" class="card game-container">
            <h4>{{ gameTitle }}</h4>
            <p>Coming soon. This game is under development.</p>
            <button class="btn" @click="goBack">Back to Games</button>
          </div>
          <div v-else class="card">
            <i class="el-icon-info"></i>
            <h4>Game Not Found</h4>
            <p>The requested game does not exist.</p>
            <button class="btn" @click="goBack">Back to Games</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GameSubpage',
  computed: {
    gameKey() {
      return this.$route.params.gameKey
    },
    gameTitle() {
      const map = {
        flappy: 'Flappy Bird',
        '2048': '2048 Bird',
        merge: 'Merge To Giant Bird'
      }
      return map[this.gameKey] || 'Game'
    }
  },
  methods: {
    goBack() {
      this.$router.push('/game')
    }
  }
}
</script>

<style scoped lang="scss">
.game-subpage-page {
  width: 100%;
  min-height: 100vh;
  position: relative;
}

.content-below {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 20px 80px;
  color: white;
}

.knowledge-cards-wrapper {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 24px;
  padding: 40px 30px;
  margin: 0 auto;
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

    i {
      font-size: 48px;
      margin-bottom: 20px;
      display: block;
      color: white;
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

  .game-container {
    overflow: hidden;
    padding: 0;

    iframe {
      border-radius: 16px;
      width: 100%;
      height: 600px;
    }
  }
}
</style>
