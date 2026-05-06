<template>
  <div class="funfacts-container">
    <NavBar :showNavbar="true" />
    <div class="content-below">
      <!-- Hero Section -->
      <div class="hero-section">
        <h1>Fun Facts & Games</h1>
        <p>Discover amazing facts about birds and play fun games</p>
      </div>

      <!-- Games Carousel -->
      <div class="carousel-section">
        <div class="section-header">
          <h2>🎮 Play & Learn</h2>
          <p>Click on any game to start playing</p>
        </div>

        <el-carousel
          :interval="4000"
          arrow="always"
          height="400px"
          class="bird-carousel"
          @change="handleCarouselChange"
        >
          <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
            <div
              class="carousel-card"
              :style="{ backgroundImage: 'linear-gradient(135deg, rgba(0,0,0,0.6), rgba(0,0,0,0.8)), url(' + item.background + ')' }"
              @click="goToGame(item.link)"
            >
              <div class="carousel-content">
                <h2 class="title">{{ item.title }}</h2>
                <p class="description">{{ item.description }}</p>
                <button class="play-button">
                  Play Now <i class="el-icon-arrow-right"></i>
                </button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>

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

      <!-- Loading Facts State -->
      <div v-if="loadingFacts" class="facts-loading">
        <i class="el-icon-loading"></i>
        <span>Discovering amazing bird facts...</span>
      </div>

      <!-- Bird Facts Section -->
      <div v-else-if="displayFacts.length > 0" class="facts-section">
        <div class="section-header">
          <h2>🐦 Amazing Bird Facts</h2>
          <p>Explore fascinating facts about our feathered friends</p>
        </div>

        <div class="facts-grid">
          <div
            v-for="(fact, index) in displayFacts"
            :key="fact.id"
            class="fact-card"
            :class="{ expanded: expandedId === fact.id }"
          >
            <div class="fact-card-header" @click="toggleFact(fact.id)">
              <div class="fact-number">{{ (index + 1).toString().padStart(2, '0') }}</div>
              <div class="fact-info">
                <h3 class="fact-title">{{ fact.title }}</h3>
                <div class="fact-meta">
                  <span class="fact-category">{{ fact.category }}</span>
                  <span class="fact-hint">
                    <i class="el-icon-reading"></i>
                    {{ expandedId === fact.id ? 'Click to collapse' : 'Click to read more' }}
                  </span>
                </div>
              </div>
              <div class="expand-icon">
                <i :class="expandedId === fact.id ? 'el-icon-remove-outline' : 'el-icon-circle-plus-outline'"></i>
              </div>
            </div>

            <transition name="expand">
              <div v-show="expandedId === fact.id" class="fact-card-body">
                <div class="fact-description">
                  <p>{{ fact.description }}</p>
                  <div class="fact-details" v-if="fact.scientificName || fact.habitat || fact.diet">
                    <div class="detail-row" v-if="fact.scientificName">
                      <span class="detail-label">🔬 Scientific Name</span>
                      <span class="detail-value">{{ fact.scientificName }}</span>
                    </div>
                    <div class="detail-row" v-if="fact.habitat">
                      <span class="detail-label">🌍 Habitat</span>
                      <span class="detail-value">{{ fact.habitat }}</span>
                    </div>
                    <div class="detail-row" v-if="fact.diet">
                      <span class="detail-label">🍽️ Diet</span>
                      <span class="detail-value">{{ fact.diet }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </transition>
          </div>
        </div>

        <div v-if="hasMoreFacts" class="load-more">
          <button class="load-more-btn" @click="loadMoreFacts" :disabled="loadingMore">
            <i v-if="loadingMore" class="el-icon-loading"></i>
            <span v-else>Load More Facts</span>
          </button>
        </div>
      </div>

      <!-- No Facts State -->
      <div v-else-if="!loadingFacts && funFacts.length === 0" class="facts-empty">
        <i class="el-icon-info"></i>
        <p>Unable to load bird facts</p>
        <p class="empty-hint">Please check your internet connection</p>
      </div>

      <!-- Bird Sounds Section -->
      <div class="sounds-section">
        <div class="section-header">
          <h2>🎵 Bird Sounds Library</h2>
          <p>Search for any bird to hear its real sound</p>
        </div>

        <div class="search-bar">
          <el-input
            v-model="searchQuery"
            placeholder="Search by bird name (e.g., robin, blue jay, cardinal)"
            prefix-icon="el-icon-search"
            clearable
            size="large"
            @clear="clearSearch"
            @keyup.enter="searchBirds"
          >
            <el-button slot="append" icon="el-icon-search" @click="searchBirds" :loading="searching">Search</el-button>
          </el-input>
        </div>

        <!-- Preset Birds -->
        <div class="preset-birds">
          <div class="preset-header">
            <span><i class="el-icon-star-on"></i> Popular Birds</span>
            <span class="preset-hint">Click to listen</span>
          </div>
          <div class="preset-grid">
            <div
              v-for="bird in presetBirdsList"
              :key="bird.id"
              class="preset-card"
              :class="{ playing: currentPlayingId === bird.id, loading: loadingPresetId === bird.id }"
              @click="playPresetBird(bird)"
            >
              <div class="preset-avatar">
                <img
                  v-if="bird.imageUrl"
                  :src="bird.imageUrl"
                  :alt="bird.name"
                  @error="handleImageError"
                >
                <div v-else class="preset-avatar-placeholder">
                  <i class="el-icon-microphone"></i>
                </div>
                <div class="preset-play-overlay" v-if="currentPlayingId === bird.id">
                  <i class="el-icon-headset"></i>
                </div>
                <div class="preset-loading-overlay" v-if="loadingPresetId === bird.id">
                  <i class="el-icon-loading"></i>
                </div>
              </div>
              <div class="preset-info">
                <h4>{{ bird.name }}</h4>
                <span class="preset-category">{{ bird.category }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="searching" class="sounds-loading">
          <i class="el-icon-loading"></i>
          <span>Searching for bird sounds...</span>
        </div>

        <!-- Search Results -->
        <div v-else-if="searchResults.length > 0" class="search-results">
          <div class="search-header">
            <h3>Results for "{{ searchQuery }}"</h3>
            <button class="clear-results" @click="clearSearch">Clear</button>
          </div>
          <div class="sounds-grid">
            <div v-for="bird in searchResults" :key="bird.id" class="sound-card">
              <div class="sound-card-header">
                <div class="bird-avatar" @click="playSearchBird(bird)">
                  <img v-if="bird.imageUrl" :src="bird.imageUrl" :alt="bird.name" @error="handleImageError">
                  <div v-else class="bird-avatar-placeholder"><i class="el-icon-microphone"></i></div>
                </div>
                <div class="bird-info">
                  <h4>{{ bird.name }}</h4>
                  <p class="bird-location"><i class="el-icon-location"></i> {{ bird.location }}</p>
                </div>
              </div>
              <button class="play-btn" @click="playSearchBird(bird)">
                <i class="el-icon-video-play"></i> Play Sound
              </button>
            </div>
          </div>
        </div>

        <div v-else-if="searchedOnce" class="sounds-empty">
          <i class="el-icon-info"></i>
          <p>No bird sounds found for "{{ searchQuery }}"</p>
          <p class="empty-hint">Try: robin, blue jay, cardinal, eagle, owl</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from '@/components/NavBar/navbar.vue'
import flappyImg from '@/assets/images/flappy.png'
import img2048 from '@/assets/images/2048.png'

const XENO_CANTO_API_KEY = 'd41f52801b9cd18764019261ca8a6518fe42169f'
const BACKEND_PROXY_URL = 'http://localhost:8080/api/audio/proxy'

const BIRD_SPECIES_FOR_FACTS = [
  'Hummingbird', 'Ostrich', 'Arctic Tern', 'Penguin', 'Crow',
  'Woodpecker', 'Albatross', 'Flamingo', 'Parrot', 'Eagle',
  'Owl', 'Peacock', 'Swan', 'Hawk', 'Kingfisher'
]

export default {
  name: 'FunFacts',
  components: { NavBar },
  data() {
    return {
      currentSlide: 0,
      expandedId: null,
      loadingMore: false,
      searchQuery: '',
      searching: false,
      searchedOnce: false,
      currentPlayingId: null,
      currentAudio: null,
      loadingPresetId: null,
      loadingFacts: false,
      searchResults: [],
      presetBirdsList: [],
      funFacts: [],
      carouselItems: [
        { title: 'Flappy Bird', description: 'Guide the bird through pipes.', link: '/game/flappy', background: flappyImg },
        { title: '2048 Bird', description: 'Combine tiles to reach 2048.', link: '/game/2048', background: img2048 }
      ],
      currentPage: 1,
      pageSize: 6,
      presetBirds: [
        { name: 'American Robin', category: 'Songbird', searchTerm: 'robin' },
        { name: 'Blue Jay', category: 'Songbird', searchTerm: 'blue jay' },
        { name: 'Northern Cardinal', category: 'Songbird', searchTerm: 'cardinal' },
        { name: 'Bald Eagle', category: 'Raptor', searchTerm: 'bald eagle' },
        { name: 'Great Horned Owl', category: 'Owl', searchTerm: 'great horned owl' },
        { name: 'Red-tailed Hawk', category: 'Raptor', searchTerm: 'red-tailed hawk' }
      ]
    }
  },
  computed: {
    displayFacts() {
      return this.funFacts.slice(0, this.currentPage * this.pageSize)
    },
    hasMoreFacts() {
      return this.displayFacts.length < this.funFacts.length
    }
  },
  mounted() {
    this.loadPresetBirds()
    this.loadFunFacts()
  },
  methods: {
    toggleFact(id) {
      this.expandedId = this.expandedId === id ? null : id
    },
    loadMoreFacts() {
      this.loadingMore = true
      setTimeout(() => {
        this.currentPage++
        this.loadingMore = false
      }, 300)
    },
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
    },

    async loadFunFacts() {
      this.loadingFacts = true
      this.funFacts = []
      for (const species of BIRD_SPECIES_FOR_FACTS) {
        const fact = await this.fetchWikipediaFact(species)
        if (fact) this.funFacts.push(fact)
      }
      this.loadingFacts = false
    },

    async fetchWikipediaFact(birdName) {
      try {
        const response = await fetch(`https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(birdName)}`)
        const data = await response.json()
        if (data.extract && !data.missing) {
          return {
            id: birdName.toLowerCase().replace(/ /g, '-'),
            title: birdName,
            description: this.extractInterestingFact(data.extract, birdName),
            category: this.determineCategory(data.extract),
            scientificName: data.title || birdName,
            habitat: this.extractHabitat(data.extract),
            diet: this.extractDiet(data.extract),
            imageUrl: data.thumbnail?.source || null
          }
        }
        return null
      } catch (error) {
        console.error(`Failed to fetch fact for ${birdName}:`, error)
        return null
      }
    },

    extractInterestingFact(extract, birdName) {
      const patterns = [
        /(?:can|is able to|has the ability to)[^.!?]*[.!?]/i,
        /(?:largest|smallest|fastest|slowest|longest|shortest)[^.!?]*[.!?]/i,
        /(?:only|rare|unique|extraordinary)[^.!?]*[.!?]/i
      ]
      for (const pattern of patterns) {
        const match = extract.match(pattern)
        if (match && match[0].length > 40 && match[0].length < 300) return match[0]
      }
      const sentences = extract.split('. ')
      let fact = sentences.slice(0, 2).join('. ')
      if (fact.length > 350) fact = fact.substring(0, 350) + '...'
      return fact || `Learn about the ${birdName}!`
    },

    determineCategory(extract) {
      const categories = {
        'Flight': ['wing', 'fly', 'flight', 'soar', 'glide', 'hover'],
        'Anatomy': ['eye', 'brain', 'feather', 'bone', 'skull', 'beak'],
        'Intelligence': ['smart', 'intelligent', 'remember', 'recognize', 'learn'],
        'Migration': ['migrate', 'migration', 'travel', 'journey'],
        'Adaptation': ['adapt', 'evolve', 'survive', 'unique']
      }
      const lower = extract.toLowerCase()
      for (const [cat, keywords] of Object.entries(categories)) {
        if (keywords.some(k => lower.includes(k))) return cat
      }
      return 'Did You Know?'
    },

    extractHabitat(extract) {
      const match = extract.match(/(?:habitat|found in|lives in|native to)[:\s]+([^.!?]{20,80})/i)
      if (match) return match[1].charAt(0).toUpperCase() + match[1].slice(1)
      return 'Various habitats worldwide'
    },

    extractDiet(extract) {
      const match = extract.match(/(?:diet|feeds on|eats)[:\s]+([^.!?]{20,70})/i)
      if (match) return match[1].charAt(0).toUpperCase() + match[1].slice(1)
      return 'Varies by species'
    },

    async loadPresetBirds() {
      for (const bird of this.presetBirds) {
        const imageUrl = await this.fetchBirdImage(bird.name)
        this.presetBirdsList.push({
          id: bird.name.replace(/\s/g, '-'),
          name: bird.name,
          category: bird.category,
          searchTerm: bird.searchTerm,
          audioUrl: null,
          imageUrl: imageUrl,
          location: 'Loading...'
        })
      }
      for (let i = 0; i < this.presetBirdsList.length; i++) {
        this.loadingPresetId = this.presetBirdsList[i].id
        await this.fetchAudioForPresetBird(i)
        this.loadingPresetId = null
      }
    },

    async fetchAudioForPresetBird(index) {
      const bird = this.presetBirdsList[index]
      try {
        const response = await fetch(`https://xeno-canto.org/api/3/recordings?query=en:"=${bird.searchTerm}"&key=${XENO_CANTO_API_KEY}&per_page=3`)
        const data = await response.json()
        if (data.recordings && data.recordings.length > 0) {
          const recording = data.recordings[0]
          bird.audioUrl = `${BACKEND_PROXY_URL}?url=${encodeURIComponent(`https://cdn.xeno-canto.org/${recording.id}/download.mp3`)}`
          bird.location = recording.cnt || 'Various locations'
        } else {
          bird.location = 'No audio found'
        }
      } catch (error) {
        bird.location = 'Audio unavailable'
      }
    },

    async fetchBirdImage(birdName) {
      try {
        const res = await fetch(`https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(birdName)}`)
        const data = await res.json()
        return data.thumbnail?.source || null
      } catch {
        return null
      }
    },

    handleImageError(e) {
      e.target.style.display = 'none'
    },

    playPresetBird(bird) {
      if (!bird.audioUrl) return this.$message.warning(`No audio available for ${bird.name}`)
      this.playBirdSound(bird)
    },

    playSearchBird(bird) {
      this.playBirdSound(bird)
    },

    playBirdSound(bird) {
      if (!bird.audioUrl) return
      if (this.currentAudio) {
        this.currentAudio.pause()
        this.currentAudio.currentTime = 0
      }
      const audio = new Audio(bird.audioUrl)
      audio.play().catch(() => this.$message.error(`Cannot play ${bird.name} sound`))
      audio.onended = () => {
        this.currentPlayingId = null
        this.currentAudio = null
      }
      audio.onerror = () => {
        this.$message.error(`Failed to load audio for ${bird.name}`)
        this.currentPlayingId = null
        this.currentAudio = null
      }
      this.currentPlayingId = bird.id
      this.currentAudio = audio
    },

    async searchBirds() {
      if (!this.searchQuery.trim()) return this.$message.warning('Please enter a bird name')
      this.searching = true
      this.searchedOnce = true
      this.searchResults = []
      try {
        const query = this.searchQuery.trim().toLowerCase()
        const res = await fetch(`https://xeno-canto.org/api/3/recordings?query=en:"=${query}"&key=${XENO_CANTO_API_KEY}&per_page=10`)
        const data = await res.json()
        if (data.recordings && data.recordings.length > 0) {
          const unique = new Map()
          for (const rec of data.recordings.slice(0, 8)) {
            if (!unique.has(rec.en)) {
              const imgUrl = await this.fetchBirdImage(rec.en)
              unique.set(rec.en, {
                id: rec.id,
                name: rec.en,
                audioUrl: `${BACKEND_PROXY_URL}?url=${encodeURIComponent(`https://cdn.xeno-canto.org/${rec.id}/download.mp3`)}`,
                location: rec.cnt || 'Unknown',
                imageUrl: imgUrl
              })
            }
          }
          this.searchResults = Array.from(unique.values())
        } else {
          this.$message.info(`No recordings found for "${query}"`)
        }
      } catch (error) {
        this.$message.error('Search failed')
      } finally {
        this.searching = false
      }
    },

    clearSearch() {
      this.searchQuery = ''
      this.searchedOnce = false
      this.searchResults = []
      if (this.currentAudio) {
        this.currentAudio.pause()
        this.currentAudio = null
        this.currentPlayingId = null
      }
    }
  },
  beforeDestroy() {
    if (this.currentAudio) this.currentAudio.pause()
  }
}
</script>

<style scoped lang="scss">
.funfacts-container {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #80c5e3 0%, #44bce1 100%);
}

.content-below {
  padding: 100px 20px 80px;
}

.hero-section {
  text-align: center;
  margin-bottom: 60px;
  h1 { font-size: 48px; font-weight: 700; color: white; margin-bottom: 16px; text-shadow: 2px 2px 12px rgba(0,0,0,0.2); }
  p { font-size: 18px; color: rgba(255,255,255,0.9); max-width: 600px; margin: 0 auto; }
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
  h2 { font-size: 32px; font-weight: 600; color: white; margin-bottom: 8px; }
  p { font-size: 16px; color: rgba(255,255,255,0.8); }
}

.carousel-section {
  max-width: 1000px;
  margin: 0 auto 80px;
}

.bird-carousel {
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0,0,0,0.3);
  .carousel-card {
    width: 100%;
    height: 100%;
    background-size: cover;
    background-position: center;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    .carousel-content {
      text-align: center;
      color: white;
      padding: 40px;
      max-width: 600px;
      .title { font-size: 48px; font-weight: 700; margin-bottom: 16px; }
      .description { font-size: 18px; line-height: 1.6; margin-bottom: 30px; opacity: 0.9; }
      .play-button {
        background: rgba(255,255,255,0.2);
        backdrop-filter: blur(10px);
        border: 2px solid white;
        color: white;
        padding: 12px 32px;
        border-radius: 40px;
        font-size: 16px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
        display: inline-flex;
        align-items: center;
        gap: 8px;
        &:hover { background: white; color: #44bce1; transform: translateY(-2px); }
      }
    }
  }
}

.custom-indicators {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
  .indicator {
    width: 10px; height: 10px;
    background: rgba(255,255,255,0.4);
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.3s;
    &:hover { background: rgba(255,255,255,0.8); }
    &.active { background: #ffd966; width: 28px; border-radius: 5px; }
  }
}

.facts-loading, .facts-empty {
  max-width: 900px;
  margin: 60px auto;
  text-align: center;
  padding: 60px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  i { font-size: 48px; color: white; margin-bottom: 16px; display: block; }
  p { font-size: 16px; color: white; }
}

.facts-loading i { animation: spin 1s linear infinite; }

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.facts-section {
  max-width: 900px;
  margin: 0 auto 60px;
}

.facts-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.fact-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  &:hover { box-shadow: 0 8px 25px rgba(0,0,0,0.15); }
  .fact-card-header {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 20px 24px;
    cursor: pointer;
    &:hover { background: #f8f9fa; }
    .fact-number { font-size: 28px; font-weight: 700; color: #44bce1; opacity: 0.5; min-width: 55px; font-family: monospace; }
    .fact-info { flex: 1;
      .fact-title { font-size: 18px; font-weight: 600; color: #1a1a2e; margin: 0 0 8px 0; }
      .fact-meta { display: flex; align-items: center; gap: 12px; flex-wrap: wrap;
        .fact-category { background: linear-gradient(135deg, #80c5e3 0%, #44bce1 100%); padding: 4px 12px; border-radius: 20px; font-size: 11px; font-weight: 500; color: white; }
        .fact-hint { font-size: 12px; color: #999; display: flex; align-items: center; gap: 4px; }
      }
    }
    .expand-icon { color: #44bce1; font-size: 24px; }
  }
  .fact-card-body {
    border-top: 1px solid #eee;
    padding: 24px;
    background: #fafbfc;
    .fact-description p { font-size: 15px; line-height: 1.7; color: #444; margin: 0 0 20px 0; }
    .fact-details {
      background: white;
      border-radius: 12px;
      padding: 16px;
      .detail-row { display: flex; flex-wrap: wrap; margin-bottom: 12px;
        .detail-label { width: 140px; font-size: 13px; font-weight: 600; color: #44bce1; }
        .detail-value { flex: 1; font-size: 13px; color: #555; }
      }
    }
  }
}

.load-more {
  text-align: center;
  margin-top: 40px;
  .load-more-btn {
    background: rgba(255,255,255,0.2);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255,255,255,0.4);
    color: white;
    padding: 12px 32px;
    border-radius: 40px;
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s;
    &:hover { background: white; color: #44bce1; transform: translateY(-2px); }
  }
}

.sounds-section {
  max-width: 1000px;
  margin: 0 auto;
}

.search-bar {
  margin-bottom: 30px;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

.preset-birds {
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 40px;
  .preset-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
    span:first-child { font-size: 18px; font-weight: 600; color: white; i { margin-right: 8px; color: #ffd966; } }
    .preset-hint { font-size: 12px; color: rgba(255,255,255,0.8); }
  }
  .preset-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }
  .preset-card {
    background: white;
    border-radius: 16px;
    padding: 16px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    &:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(0,0,0,0.2); }
    &.playing { border: 2px solid #44bce1; background: #f0f9ff; }
    .preset-avatar {
      width: 80px; height: 80px;
      border-radius: 50%;
      margin: 0 auto 12px;
      overflow: hidden;
      background: linear-gradient(135deg, #80c5e3 0%, #44bce1 100%);
      position: relative;
      img { width: 100%; height: 100%; object-fit: cover; }
      .preset-avatar-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 32px; color: white; }
      .preset-play-overlay {
        position: absolute;
        top: 0; left: 0; right: 0; bottom: 0;
        background: rgba(0,0,0,0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        i { font-size: 28px; color: white; }
      }
      .preset-loading-overlay {
        position: absolute;
        top: 0; left: 0; right: 0; bottom: 0;
        background: rgba(0,0,0,0.6);
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        i { font-size: 24px; color: white; animation: spin 1s linear infinite; }
      }
    }
    .preset-info h4 { font-size: 14px; font-weight: 600; color: #333; margin: 0; }
    .preset-category { font-size: 11px; color: #44bce1; }
  }
}

.sounds-loading, .sounds-empty {
  text-align: center;
  padding: 60px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  i { font-size: 48px; color: white; margin-bottom: 16px; display: block; }
  p { font-size: 16px; color: white; margin-bottom: 8px; }
  .empty-hint { font-size: 13px; opacity: 0.8; }
}

.search-results {
  .search-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    h3 { font-size: 18px; font-weight: 600; color: white; }
    .clear-results { background: rgba(255,255,255,0.2); border: none; color: white; padding: 6px 16px; border-radius: 30px; cursor: pointer; &:hover { background: rgba(255,255,255,0.4); } }
  }
}

.sounds-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.sound-card {
  background: white;
  border-radius: 16px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  &:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(0,0,0,0.15); }
  .sound-card-header {
    display: flex;
    align-items: center;
    gap: 12px;
    .bird-avatar {
      width: 50px; height: 50px;
      border-radius: 50%;
      overflow: hidden;
      background: linear-gradient(135deg, #80c5e3 0%, #44bce1 100%);
      cursor: pointer;
      img { width: 100%; height: 100%; object-fit: cover; }
      .bird-avatar-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 24px; color: white; }
    }
    .bird-info h4 { font-size: 16px; font-weight: 600; margin: 0 0 4px; }
    .bird-location { font-size: 11px; color: #999; }
  }
  .play-btn {
    background: linear-gradient(135deg, #80c5e3 0%, #44bce1 100%);
    border: none;
    padding: 8px 20px;
    border-radius: 30px;
    color: white;
    cursor: pointer;
    &:hover { transform: scale(1.02); }
  }
}

.expand-enter-active, .expand-leave-active { transition: all 0.3s ease; }
.expand-enter, .expand-leave-to { opacity: 0; transform: translateY(-10px); }

::v-deep .el-carousel__arrow {
  background-color: rgba(0,0,0,0.4);
  width: 42px; height: 42px;
  border-radius: 50%;
  &:hover { background-color: rgba(0,0,0,0.7); }
}
::v-deep .el-carousel__indicators { display: none; }

@media (max-width: 768px) {
  .hero-section h1 { font-size: 32px; }
  .section-header h2 { font-size: 24px; }
  .fact-card .fact-card-header {
    padding: 15px 20px; gap: 12px;
    .fact-number { font-size: 22px; min-width: 40px; }
    .fact-info .fact-title { font-size: 16px; }
  }
  .preset-birds .preset-grid { grid-template-columns: repeat(2, 1fr); }
  .sounds-grid { grid-template-columns: 1fr; }
  .sound-card { flex-direction: column; gap: 12px; text-align: center; }
}
</style>
