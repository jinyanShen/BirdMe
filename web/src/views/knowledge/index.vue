<template>
  <div class="knowledge-page">
    <NavBar :showNavbar="true" />
    <div class="container">
      <!-- Subpage tabs -->
      <div class="knowledge-tabs">
        <div
          class="tab-item"
          :class="{ active: currentTab === 'migration' }"
          @click="switchTab('migration')"
        >
          <i class="el-icon-location"></i>
          <span>Bird Migration</span>
        </div>
        <div
          class="tab-item"
          :class="{ active: currentTab === 'identification' }"
          @click="switchTab('identification')"
        >
          <i class="el-icon-picture"></i>
          <span>Bird Identification</span>
        </div>
        <div
          class="tab-item"
          :class="{ active: currentTab === 'funFacts' }"
          @click="switchTab('funFacts')"
        >
          <i class="el-icon-facts"></i>
          <span>Fun facts</span>
        </div>
        <div
          class="tab-item"
          :class="{ active: currentTab === 'injuryHeatmap' }"
          @click="switchTab('injuryHeatmap')"
        >
          <i class="el-icon-warning"></i>
          <span>Injury Heatmap</span>
        </div>
      </div>

      <!-- Subpage content -->
      <component :is="currentComponent" />
    </div>
  </div>
</template>

<script>
import BirdMigration from './BirdMigration.vue'
import BirdIdentification from './BirdIdentification.vue'
import NavBar from '@/components/NavBar/navbar.vue'
import FunFacts from "@/views/knowledge/FunFacts.vue";
import BirdInjuryHeatmap from "./BirdInjuryHeatmap.vue";

export default {
  name: 'Knowledge',
  components: {
    BirdMigration,
    BirdIdentification,
    FunFacts,
    NavBar,
    BirdInjuryHeatmap
  },
  data() {
    return {
      currentTab: 'migration'
    }
  },
  computed: {
    currentComponent() {
      if (this.currentTab === 'migration') {
        return BirdMigration
      } else if (this.currentTab === 'identification') {
        return BirdIdentification
      } else if (this.currentTab === 'funFacts'){
        return FunFacts
      } else if (this.currentTab === 'injuryHeatmap')
      return BirdInjuryHeatmap

    }
  },
  methods: {
    switchTab(tab) {
      this.currentTab = tab
      this.$router.push({ path: '/knowledge/index', query: { tab } })
    },
    updateTabFromRoute() {
      const tab = this.$route.query.tab
      if (tab === 'migration' || tab === 'identification' || tab === 'funFacts' || tab === 'injuryHeatmap') {
        this.currentTab = tab
      } else {
        this.currentTab = 'migration'
      }
    }
  },
  mounted() {
    this.updateTabFromRoute()
  },
  watch: {
    '$route.path': 'updateTabFromRoute'
  }
}
</script>

<style scoped>
.knowledge-page {
  padding-top: 100px;
  background: linear-gradient(135deg, #e0f5fa 0%, #63c9e8 100%);
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.knowledge-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  background: white;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 30px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
  font-size: 19px;
  font-weight: 500;
}

.tab-item:hover {
  background: #e0f3fa;
  color: #2289c1;
}

.tab-item.active {
  background: linear-gradient(135deg, #3a79cc 0%, #a6d1e3 100%);
  color: white;
}

.tab-item i {
  font-size: 20px;
}
</style>
