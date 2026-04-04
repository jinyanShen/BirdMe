<template>
  <div class="knowledge-page">
    <div class="top-navbar">
      <div class="navbar-container">
        <div class="logo">
          <h3 @click="goToHome" style="cursor: pointer;">BirdME</h3>
        </div>
        <div class="nav-menu">
          <span class="nav-item" @click="goToHome">Homepage</span>
          <span class="nav-item active" @click="goToKnowledge">Knowledge</span>
          <span class="nav-item" @click="goToRescue">Rescue</span>
          <span class="nav-item" @click="goToForum">Forum</span>
          <span class="nav-item" @click="goToGame">Game</span>
          <span class="nav-item" @click="goToPersonalPage">Personal Setting</span>
          <span v-if="!isLoggedIn" class="nav-item login-btn" @click="goToLogin">Login</span>
          <span v-else class="nav-item logout-btn" @click="handleLogout">LogOut</span>
        </div>
      </div>
    </div>

    <div class="container">
      <!-- 子页面标签页 -->
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
      </div>

      <!-- 子页面路由出口 -->
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  name: 'Knowledge',
  data() {
    return {
      isLoggedIn: false,
      currentTab: 'migration'
    }
  },
  methods: {
    checkLoginStatus() {
      this.isLoggedIn = sessionStorage.getItem('id') !== null
    },

    handleStorageChange(e) {
      if (e.key === 'id') {
        this.checkLoginStatus()
      }
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
      this.$message.success('Logged out successfully')
      this.$router.push('/')
    },

    goToHome() {
      if (this.$route.path === '/') {
        return
      }
      this.$router.push('/')
    },

    goToKnowledge() {
      // 已经在knowledge页面，刷新当前标签页
      this.$router.go(0)
    },

    goToRescue() {
      console.log('Rescue page - to be implemented')
    },

    goToForum() {
      console.log('Forum page - to be implemented')
    },

    goToGame() {
      if (this.isLoggedIn) this.$router.push('/game')
      else window.$showLoginDialog && window.$showLoginDialog('/game')
    },

    goToPersonalPage() {
      if (this.isLoggedIn) this.$router.push('/settings/index')
      else window.$showLoginDialog && window.$showLoginDialog('/settings/index')
    },

    goToLogin() {
      if (window.$loginDialog) {
        window.$loginDialog.show('/')
      }
    },

    switchTab(tab) {
      this.currentTab = tab
      this.$router.push(`/knowledge/${tab}`)
    }
  },

  mounted() {
    this.checkLoginStatus()
    window.addEventListener('storage', this.handleStorageChange)

    // 根据当前路由设置currentTab
    const path = this.$route.path
    if (path.includes('migration')) {
      this.currentTab = 'migration'
    } else if (path.includes('identification')) {
      this.currentTab = 'identification'
    }
  },

  beforeDestroy() {
    window.removeEventListener('storage', this.handleStorageChange)
  },

  watch: {
    '$route.path'(newPath) {
      if (newPath.includes('migration')) {
        this.currentTab = 'migration'
      } else if (newPath.includes('identification')) {
        this.currentTab = 'identification'
      }
    }
  }
}
</script>

<style scoped>
.knowledge-page {
  padding-top: 70px;
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  min-height: 100vh;
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
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo h3 {
  margin: 0;
  font-size: 24px;
  color: #22b3c1;
  font-weight: bold;
  cursor: pointer;
  transition: color 0.3s;
}

.logo h3:hover {
  color: #1a9aa8;
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
  transition: color 0.3s;
  cursor: pointer;
  padding: 8px 0;
  position: relative;
}

.nav-item:hover {
  color: #22b3c1;
}

.nav-item.active {
  color: #22b3c1;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #22b3c1;
  animation: slideIn 0.3s ease-out;
}

.login-btn {
  background: #22b3c1;
  color: white !important;
  padding: 8px 20px;
  border-radius: 25px;
  transition: all 0.3s;
}

.login-btn:hover {
  background: #1a9aa8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.3);
  color: white !important;
}

.logout-btn {
  background: #ff6b6b;
  color: white !important;
  padding: 8px 20px;
  border-radius: 25px;
  transition: all 0.3s;
  cursor: pointer;
}

.logout-btn:hover {
  background: #ff5252;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
  color: white !important;
}

@keyframes slideIn {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: 100%;
    opacity: 1;
  }
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
  border-radius: 12px;
  padding: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 12px 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  color: #666;
  background: #f5f7fa;
}

.tab-item i {
  font-size: 18px;
}

.tab-item:hover {
  background: #e0f7fa;
  color: #22b3c1;
}

.tab-item.active {
  background: linear-gradient(135deg, #22b3c1 0%, #1a9aa8 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.3);
}

@media (max-width: 768px) {
  .knowledge-page {
    padding-top: 60px;
  }

  .navbar-container {
    padding: 10px 15px;
  }

  .logo h3 {
    font-size: 20px;
  }

  .nav-menu {
    gap: 15px;
  }

  .nav-item {
    font-size: 14px;
  }

  .login-btn,
  .logout-btn {
    padding: 6px 15px;
  }

  .container {
    padding: 0 15px;
  }

  .knowledge-tabs {
    gap: 10px;
  }

  .tab-item {
    padding: 8px 15px;
    font-size: 14px;
  }

  .tab-item i {
    font-size: 16px;
  }
}
</style>


<!--
<template>
  <div class="identification-page">
    <div class="top-navbar">
      <div class="navbar-container">
        <div class="logo">
          <h3 @click="goToHome" style="cursor: pointer;">BirdME</h3>
        </div>
        <div class="nav-menu">
          <span class="nav-item" @click="goToHome">Homepage</span>
          <span class="nav-item active" @click="goToKnowledge">Knowledge</span>
          <span class="nav-item" @click="goToRescue">Rescue</span>
          <span class="nav-item" @click="goToForum">Forum</span>
          <span class="nav-item" @click="goToGame">Game</span>
          <span class="nav-item" @click="goToPersonalPage">Personal Setting</span>
          <span v-if="!isLoggedIn" class="nav-item login-btn" @click="goToLogin">Login</span>
          <span v-else class="nav-item logout-btn" @click="handleLogout">LogOut</span>          </div>
      </div>
    </div>

    <div class="container">
      &lt;!&ndash; Bird Migration Flow &ndash;&gt;
      <div class="migration-container">
        <div class="sidebar">
          <div class="sidebar-header">
            <h3>Bird Migration Flow</h3>
          </div>

          <div class="filter-section">
            <label>Select Bird Species:</label>
            <el-select
              v-model="selectedBird"
              filterable
              placeholder="Search or select a bird"
              @change="handleBirdChange"
              class="bird-select"
            >
              <el-option
                v-for="bird in birdList"
                :key="bird.name"
                :label="bird.name"
                :value="bird.name"
              >
                <span>{{ bird.name }}</span>
                <span style="float: right; color: #8492a6; font-size: 12px">
                  {{ bird.recordCount }} records
                </span>
              </el-option>
            </el-select>
          </div>

          <div class="bird-info" v-if="selectedBirdData">
            <h4>{{ selectedBird }}</h4>
            <div class="info-item">
              <span class="label">Species:</span>
              <span class="value">{{ selectedBirdData.species }}</span>
            </div>
            <div class="info-item">
              <span class="label">Migration Routes:</span>
              <span class="value">{{ Object.keys(selectedBirdData.routes).length }}</span>
            </div>
          </div>

          &lt;!&ndash; Upload Section &ndash;&gt;
          <UploadImg
            @image-uploaded="handleImageUploaded"
          />


          &lt;!&ndash; Identification Result &ndash;&gt;
          <div v-if="form.name" class="result-section">
            <h4>Identification Result</h4>
            <div class="result-card">
              <h5 class="bird-name">{{ form.name }}</h5>
              <p class="bird-description">{{ form.description }}</p>
              <div class="protection-level" v-if="form.protectionLevel">
                <span class="label">Protection Level:</span>
                <span class="value">{{ form.protectionLevel }}</span>
              </div>
            </div>
          </div>

          &lt;!&ndash; Rescue Station Search &ndash;&gt;
          <div v-if="form.name" class="rescue-section">
            <h4>Nearby Rescue Stations</h4>
            <div class="rescue-card">
              <div class="location-input">
                <el-input
                  v-model="userLocation"
                  placeholder="Enter your location or enable GPS"
                  class="location-input"
                >
                  <el-button slot="append" icon="el-icon-position" @click="getCurrentLocation"></el-button>
                </el-input>
              </div>
              <el-button type="primary" @click="searchNearbyStations" class="search-button">
                Find Rescue Stations
              </el-button>
              <div v-if="nearbyStations.length > 0" class="stations-list">
                <div v-for="station in nearbyStations" :key="station.id" class="station-item">
                  <h6>{{ station.name }}</h6>
                  <p>{{ station.address }}</p>
                  <p>Phone: {{ station.phone }}</p>
                  <el-button type="info" size="small" @click="selectRescueStation(station)">
                    Select
                  </el-button>
                </div>
              </div>
              <div v-else-if="searchingStations" class="loading">
                <el-icon class="is-loading"><i class="el-icon-loading"></i></el-icon> Searching...
              </div>
              <div v-else-if="!searchingStations && form.name" class="no-stations">
                Click "Find Rescue Stations" to search nearby rescue stations
              </div>
            </div>
          </div>

          &lt;!&ndash; Report Generation &ndash;&gt;
          <div v-if="form.name && selectedStation" class="report-section">
            <h4>Generate Rescue Report</h4>
            <div class="report-card">
              <el-form :model="reportForm" label-width="100px">
                <el-form-item label="Injury Type">
                  <el-select v-model="reportForm.injuryType" placeholder="Select injury type">
                    <el-option label="Broken Wing" value="Broken Wing"></el-option>
                    <el-option label="Injured Leg" value="Injured Leg"></el-option>
                    <el-option label="Illness" value="Illness"></el-option>
                    <el-option label="Other" value="Other"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="Description">
                  <el-input
                    v-model="reportForm.injuryDescription"
                    type="textarea"
                    placeholder="Describe the bird's condition"
                  ></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submitReport">
                    Submit Report
                  </el-button>
                </el-form-item>
              </el-form>
              <div v-if="reportSubmitted" class="report-success">
                <el-alert
                  title="Report submitted successfully!"
                  type="success"
                  :closable="false"
                >
                  <div slot="default">
                    <p>Your report has been sent to {{ selectedStation.name }}</p>
                    <p>They will contact you soon.</p>
                    <div class="report-actions">
                      <el-button type="text" @click="goToMyReports">
                        View My Reports
                      </el-button>
                    </div>
                  </div>
                </el-alert>
              </div>
            </div>
          </div>

        </div>

        <div class="map-container" ref="mapContainer">
          <div id="migrationMap" class="map"></div>
          <canvas id="flowCanvas" class="flow-canvas"></canvas>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import UploadImg from '@/components/UploadImg/index.vue';
import { getNearbyStations } from '@/api/rescueStation';
import { identifyBird } from '@/api/file';
import { submitReport } from '@/api/report';

export default {
  components: {
    UploadImg
  },
  data() {
    return {
      imageSrc: '',
      form: {},
      migrationData: null,
      velocityData: null,
      birdList: null,
      selectedBird: null,
      selectedBirdData: null,
      selectedVelocityData: null,
      map: null,
      markers: [],
      polylines: [],
      regionLayers: [],
      canvas: null,
      ctx: null,
      particles: [],
      particleCount: 300,
      animationSpeed: 5,
      isPlaying: true,
      animationId: null,
      field: null,
      bounds: null,
      baseLayer: null,
      isLoggedIn: false,
      // Rescue station and report related data
      userLocation: '',
      nearbyStations: [],
      searchingStations: false,
      selectedStation: null,
      reportForm: {
        injuryType: '',
        injuryDescription: ''
      },
      reportSubmitted: false,
      userLatitude: null,
      userLongitude: null,
      userId: sessionStorage.getItem("id")
    }
  },
  methods: {
    checkLoginStatus() {
      this.isLoggedIn = sessionStorage.getItem('id') !== null
    },

    // 处理 storage 变化
    handleStorageChange(e) {
      if (e.key === 'id') {
        this.checkLoginStatus()
      }
    },

    // 登出
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
      this.$message.success('Logged out successfully')
      this.$router.push('/')
    },
    goToHome() {
      console.log('goToHome called')
      // 如果已经在首页，不做任何操作
      if (this.$route.path === '/') {
        console.log('Already on homepage, skip navigation')
        return
      }
      this.$router.push('/')
    },

    goToPersonalPage() {
      console.log('Rescue page - to be implemented')
      // this.$router.push('/rescue')
    },
    goToLogin() {
      if (window.$loginDialog) {
        window.$loginDialog.show('/')
      }
    },
    goToKnowledge() {
      this.$router.go(0)
    },
    goToRescue() {
      console.log('Rescue page - to be implemented')
      // this.$router.push('/rescue')
    },
    goToForum() {
      console.log('Forum page - to be implemented')
      // this.$router.push('/forum')
    },
    goToGame() {
      console.log('Game page - to be implemented')
      // this.$router.push('/game')
    },

    handleImageUploaded(data) {
      const { file, imageSrc } = data;
      this.imageSrc = imageSrc;
      this.processFile(file);
    },
    processFile(file) {
      if (file) {
        identifyBird(file).then(data => {
          if(data.code === 200){
            this.form = data.data
            const birdName = data.data.name
            this.selectedBird = birdName

            console.log('Identified bird:', birdName);

            // 确保迁移数据已加载
            this.ensureMigrationDataLoaded().then(() => {
              console.log('Migration data available:', this.migrationData);

              // 尝试直接匹配
              this.selectedBirdData = this.migrationData[birdName];
              this.selectedVelocityData = this.velocityData[birdName];

              // 如果直接匹配失败，尝试模糊匹配
              if (!this.selectedBirdData) {
                console.log('Direct match failed, trying fuzzy match...');
                const birdKeys = Object.keys(this.migrationData);
                for (const key of birdKeys) {
                  if (key.toLowerCase().includes(birdName.toLowerCase())) {
                    console.log('Fuzzy match found:', key);
                    this.selectedBirdData = this.migrationData[key];
                    this.selectedVelocityData = this.velocityData[key];
                    this.selectedBird = key;
                    break;
                  }
                }
              }

              console.log('Selected bird data:', this.selectedBirdData);

              if (this.selectedBirdData) {
                console.log('Updating map...');
                setTimeout(() => {
                  this.updateMap()
                }, 100)
              } else {
                console.log('No migration data found for:', birdName);
                this.$message.info('No migration data available for this bird');
              }
            });
          }else{
            this.$message.error(data.msg);
          }
        })
        .catch(error => {
          this.$message.error("Identification failed");
        });
      }else{
        this.$message.error("Please upload image");
      }
    },

    ensureMigrationDataLoaded() {
      return new Promise((resolve) => {
        if (this.migrationData) {
          resolve();
        } else {
          // 数据未加载，触发加载
          this.loadMigrationData().then(() => {
            resolve();
          });
        }
      });
    },
    handleBirdChange(birdName) {
      console.log('Selected bird:', birdName);
      this.ensureMigrationDataLoaded().then(() => {
        this.selectedBirdData = this.migrationData[birdName];
        this.selectedVelocityData = this.velocityData[birdName];
        console.log('Selected bird data:', this.selectedBirdData);
        if (this.selectedBirdData) {
          setTimeout(() => {
            this.updateMap();
          }, 100);
        }
      });
    },
    loadMigrationData() {
      return new Promise((resolve, reject) => {
        console.log('Loading migration data...');
        import('@/data/migration_data.json').then(data => {
          this.migrationData = data.default.migrationData;
          this.velocityData = data.default.velocityData;
          this.birdList = data.default.birdList;
          console.log('Migration data loaded successfully');
          resolve();
        }).catch(error => {
          console.error('Failed to load migration data:', error);
          reject(error);
        });
      });
    },
    initMap() {
      if (!window.L) {
        console.error('Leaflet is not loaded');
        return;
      }

      const mapContainer = document.getElementById('migrationMap');
      if (!mapContainer) {
        console.error('Map container not found');
        return;
      }

      // Set explicit size for map container
      mapContainer.style.width = '100%';
      mapContainer.style.height = '500px';
      mapContainer.style.display = 'block';
      mapContainer.style.position = 'relative';
      mapContainer.style.zIndex = '1';

      console.log('Initializing map...');
      console.log('Map container size:', mapContainer.clientWidth, 'x', mapContainer.clientHeight);

      // 创建地图实例
      this.map = L.map('migrationMap', {
        zoomControl: false,
        attributionControl: false,
        dragging: false,
        scrollWheelZoom: false,
        doubleClickZoom: false,
        touchZoom: false,
        boxZoom: false,
        keyboard: false
      }).setView([20, 0], 2);

      // 添加底图图层
      // 使用Bing Maps作为底图
      this.baseLayer = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
        attribution: 'Tiles &copy; Esri &mdash; Source: Esri, DeLorme, NAVTEQ, USGS, Intermap, iPC, NRCAN, Esri Japan, METI, Esri China (Hong Kong), Esri (Thailand), TomTom, 2012',
        maxZoom: 18
      }).addTo(this.map);

      // 监听图层加载事件
      this.baseLayer.on('load', function() {
        console.log('Map tiles loaded successfully');
      });

      this.baseLayer.on('error', function(err) {
        console.error('Error loading map tiles:', err);
      });

      // 获取画布并设置样式
      this.canvas = document.getElementById('flowCanvas');
      if (this.canvas) {
        this.ctx = this.canvas.getContext('2d');
        // 设置画布样式
        this.canvas.style.position = 'absolute';
        this.canvas.style.top = '0';
        this.canvas.style.left = '0';
        this.canvas.style.width = '100%';
        this.canvas.style.height = '100%';
        this.canvas.style.pointerEvents = 'none';
        this.canvas.style.zIndex = '1000';
        this.canvas.style.background = 'transparent';

        this.resizeCanvas();
        window.addEventListener('resize', this.resizeCanvas);
        window.addEventListener('scroll', this.handleScroll);
      }

      console.log('Map initialized successfully');
    },
    resizeCanvas() {
      const container = document.getElementById('migrationMap');
      if (container && this.canvas) {
        this.canvas.width = container.clientWidth;
        this.canvas.height = container.clientHeight;
      }
    },
    handleScroll() {
      // 当滚动时，重新绘制粒子
      if (this.ctx && this.canvas) {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
      }
    },
    updateMap() {
      if (!this.selectedBirdData) return;

      console.log('Updating map for bird:', this.selectedBird);
      console.log('Bird data:', this.selectedBirdData);

      // 确保地图已初始化
      if (!this.map) {
        this.initMap();
      }

      // 检查地图是否初始化成功
      if (!this.map) {
        console.error('Map not initialized, cannot update');
        return;
      }

      this.clearMap();

      const allPoints = [];
      const allCountries = new Set();

      Object.keys(this.selectedBirdData.routes).forEach((routeKey, routeIndex) => {
        const route = this.selectedBirdData.routes[routeKey];
        const points = route.points;



        points.forEach((point, pointIndex) => {
          allPoints.push([point.lat, point.lng]);
          if (point.country) {
            allCountries.add(point.country);
          }
        });
      });

      if (allPoints.length > 0) {
        console.log('All points:', allPoints);
        this.bounds = L.latLngBounds(allPoints);
        console.log('Bounds:', this.bounds);

        // 确保地图容器存在且有大小
        const mapContainer = document.getElementById('migrationMap');
        const flowCanvas = document.getElementById('flowCanvas');
        if (mapContainer && mapContainer.clientWidth > 0 && mapContainer.clientHeight > 0) {
          console.log('Map container size:', mapContainer.clientWidth, 'x', mapContainer.clientHeight);
          console.log('Map container position:', mapContainer.getBoundingClientRect());
          if (flowCanvas) {
            console.log('Canvas size:', flowCanvas.width, 'x', flowCanvas.height);
            console.log('Canvas position:', flowCanvas.getBoundingClientRect());
          }

          // 自动调整地图视图到迁徙区域
          this.map.fitBounds(this.bounds, { padding: [50, 50] });

          // 强制地图重绘
          setTimeout(() => {
            if (this.map) {
              this.map.invalidateSize();
              // 再次调整地图视图，确保完全适应
              this.map.fitBounds(this.bounds, { padding: [50, 50] });

              setTimeout(() => {
                // 重新调整画布大小
                this.resizeCanvas();
                // 清除画布
                if (this.ctx) {
                  this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
                }
                // 再次检查画布大小
                if (flowCanvas) {
                  console.log('Canvas size after resize:', flowCanvas.width, 'x', flowCanvas.height);
                }
                this.createVelocityField();
                this.startAnimation();
              }, 100);
            }
          }, 200);
        } else {
          console.error('Map container has no size:', mapContainer ? mapContainer.clientWidth + 'x' + mapContainer.clientHeight : 'container not found');
        }
      }
    },

    getRouteColor(index) {
      const colors = [
        '#22b3c1', '#3498db', '#9b59b6', '#e74c3c', '#f39c12',
        '#1abc9c', '#2ecc71', '#e67e22', '#95a5a6', '#34495e'
      ];
      return colors[index % colors.length];
    },

    clearMap() {
      this.stopAnimation();
      if (this.map) {
        this.markers.forEach(marker => this.map.removeLayer(marker));
        this.polylines.forEach(polyline => this.map.removeLayer(polyline));
        this.regionLayers.forEach(layer => this.map.removeLayer(layer));
      }
      this.markers = [];
      this.polylines = [];
      this.regionLayers = [];
      this.particles = [];
      this.field = null;

      if (this.ctx) {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
      }
    },
    createVelocityField() {
      if (!this.selectedVelocityData || !this.selectedVelocityData.velocityPoints) return;

      const velocityPoints = this.selectedVelocityData.velocityPoints;
      const bounds = this.selectedBirdData.bounds;

      this.field = {
        velocityPoints: velocityPoints,
        bounds: bounds,
        minLat: bounds.minLat,
        maxLat: bounds.maxLat,
        minLng: bounds.minLng,
        maxLng: bounds.maxLng
      };
    },
    startAnimation() {
      this.particles = [];

      for (let i = 0; i < this.particleCount; i++) {
        this.particles.push(this.createParticle());
      }

      this.isPlaying = true;
      this.animate();
    },
    stopAnimation() {
      this.isPlaying = false;
      if (this.animationId) {
        cancelAnimationFrame(this.animationId);
        this.animationId = null;
      }
    },
    createParticle() {
      const bounds = this.selectedBirdData.bounds;
      return {
        lat: bounds.minLat + Math.random() * (bounds.maxLat - bounds.minLat),
        lng: bounds.minLng + Math.random() * (bounds.maxLng - bounds.minLng),
        age: Math.random() * 100,
        maxAge: 80 + Math.random() * 40
      };
    },
    animate() {
      if (!this.isPlaying) return;

      // Clear canvas without covering the map
      this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

      this.particles.forEach((particle, index) => {
        particle.age++;

        if (particle.age > particle.maxAge) {
          this.particles[index] = this.createParticle();
          return;
        }

        const velocity = this.getVelocityAtPoint(particle.lat, particle.lng);

        if (velocity) {
          const speed = this.animationSpeed * 0.0005;
          particle.lat += velocity.v * speed;
          particle.lng += velocity.u * speed;

          try {
            // 确保地图存在且已初始化
            if (!this.map) {
              console.error('Map not initialized');
              return;
            }

            const point = this.map.latLngToContainerPoint([particle.lat, particle.lng]);

            // 检查点是否在画布范围内
            if (point && typeof point.x === 'number' && typeof point.y === 'number') {
              if (point.x >= 0 && point.x <= this.canvas.width && point.y >= 0 && point.y <= this.canvas.height) {
                const alpha = Math.sin((particle.maxAge - particle.age) / particle.maxAge * Math.PI);

                // Draw particle
                this.ctx.beginPath();
                this.ctx.arc(point.x, point.y, 2, 0, 2 * Math.PI);
                this.ctx.fillStyle = `rgba(14, 100, 143, ${alpha * 0.8})`;
                this.ctx.fill();

                // Draw particle trail
                const prevPoint = this.map.latLngToContainerPoint([
                  particle.lat - velocity.v * speed * 3,
                  particle.lng - velocity.u * speed * 3
                ]);

                if (prevPoint && typeof prevPoint.x === 'number' && typeof prevPoint.y === 'number') {
                  if (prevPoint.x >= 0 && prevPoint.x <= this.canvas.width && prevPoint.y >= 0 && prevPoint.y <= this.canvas.height) {
                    this.ctx.beginPath();
                    this.ctx.moveTo(prevPoint.x, prevPoint.y);
                    this.ctx.lineTo(point.x, point.y);
                    this.ctx.strokeStyle = `rgba(14, 100, 143, ${alpha * 0.5})`;
                    this.ctx.lineWidth = 1.5;
                    this.ctx.stroke();
                  }
                }
              }
            }
          } catch (error) {
            console.error('Error drawing particle:', error);
          }
        }
      });

      this.animationId = requestAnimationFrame(() => this.animate());
    },
    getVelocityAtPoint(lat, lng) {
      if (!this.field || !this.field.velocityPoints) return null;

      const velocityPoints = this.field.velocityPoints;
      let totalWeight = 0;
      let weightedU = 0;
      let weightedV = 0;

      const maxDistance = 10;

      velocityPoints.forEach(point => {
        const dLat = lat - point.lat;
        const dLng = lng - point.lng;
        const distance = Math.sqrt(dLat * dLat + dLng * dLng);

        if (distance < maxDistance && distance > 0) {
          const weight = 1 / (distance * distance);
          totalWeight += weight;
          weightedU += point.u * weight;
          weightedV += point.v * weight;
        }
      });

      if (totalWeight > 0) {
        return {
          u: weightedU / totalWeight,
          v: weightedV / totalWeight
        };
      }

      return null;
    },

    /**
     * Get user's current location using browser's geolocation API
     */
    getCurrentLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            this.userLatitude = position.coords.latitude;
            this.userLongitude = position.coords.longitude;
            this.userLocation = `${this.userLatitude.toFixed(6)}, ${this.userLongitude.toFixed(6)}`;
            this.$message.success('Location obtained successfully');
          },
          (error) => {
            console.error('Error getting location:', error);
            this.$message.error('Failed to get location. Please enter your location manually.');
          }
        );
      } else {
        this.$message.error('Geolocation is not supported by your browser.');
      }
    },

    /**
     * Search for nearby rescue stations
     */
    searchNearbyStations() {
      if (!this.userLatitude || !this.userLongitude) {
        // Use default location if user location not available
        this.userLatitude = 40.7128; // New York
        this.userLongitude = -74.0060;
      }

      this.searchingStations = true;

      getNearbyStations(this.userLatitude, this.userLongitude, 50)
        .then(response => {
          if (response.code === 200) {
            this.nearbyStations = response.data;
          } else {
            this.$message.error('Failed to search rescue stations');
          }
          this.searchingStations = false;
        })
        .catch(error => {
          console.error('Error searching rescue stations:', error);
          this.$message.error('Failed to search rescue stations');
          this.searchingStations = false;
        });
    },

    /**
     * Select a rescue station
     */
    selectRescueStation(station) {
      this.selectedStation = station;
      this.$message.success(`Selected ${station.name}`);
    },

    /**
     * Submit rescue report
     */
    submitReport() {
      if (!this.selectedStation || !this.reportForm.injuryType) {
        this.$message.error('Please select a rescue station and injury type');
        return;
      }

      const reportData = {
        birdName: this.form.name,
        species: this.form.species || this.form.name,
        latitude: this.userLatitude || 40.7128,
        longitude: this.userLongitude || -74.0060,
        location: this.userLocation,
        injuryType: this.reportForm.injuryType,
        injuryDescription: this.reportForm.injuryDescription,
        imageUrl: this.imageSrc,
        submitterId: this.userId,
        rescueStationId: this.selectedStation.id
      };

      submitReport(reportData)
      .then(data => {
        if (data.code === 200) {
          this.reportSubmitted = true;
          this.$message.success('Report submitted successfully');
        } else {
          this.$message.error('Failed to submit report');
        }
      })
      .catch(error => {
        console.error('Error submitting report:', error);
        this.$message.error('Failed to submit report');
      });
    },

    /**
     * Navigate to my reports page
     */
    goToMyReports() {
      this.$router.push('/report');
    }
  },
  mounted() {
    this.isLoggedIn = sessionStorage.getItem('id') !== null
    // this.checkLoginStatus()
    // window.addEventListener('storage', this.handleStorageChange)
    console.log('Component mounted, initializing map...');

    // Load migration data
    this.loadMigrationData().then(() => {
      console.log('Migration data loaded on mount');
    }).catch(error => {
      console.error('Failed to load migration data on mount:', error);
    });

    if (typeof L !== 'undefined') {
      this.initMap();
    } else {
      console.log('Loading Leaflet script...');
      const script = document.createElement('script');
      script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
      script.onload = () => {
        console.log('Leaflet script loaded');
        this.initMap();
      };
      script.onerror = () => {
        console.error('Failed to load Leaflet script');
      };
      document.head.appendChild(script);
    }
  },
  // beforeDestroy() {
  //   window.removeEventListener('resize', this.resizeCanvas);
  //   window.removeEventListener('scroll', this.handleScroll);
  //   this.stopAnimation();
  // }
  watch: {
    '$route'() {
      this.checkLoginStatus()
    }
  },

  beforeDestroy() {
    window.removeEventListener('storage', this.handleStorageChange)
    // ... 其他清理代码
  }
}
</script>

<style scoped>
.logout-btn {
  background: #ff6b6b;
  color: white !important;
  padding: 8px 20px;
  border-radius: 25px;
  transition: all 0.3s;
  cursor: pointer;
}

.logout-btn:hover {
  background: #ff5252;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
  color: white !important;
}

.identification-page {
  padding-top: 70px;
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  min-height: 100vh;
}
/* 添加导航栏样式（与首页保持一致） */
.top-navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo h3 {
  margin: 0;
  font-size: 24px;
  color: #22b3c1;
  font-weight: bold;
  cursor: pointer;
  transition: color 0.3s;
}

.logo h3:hover {
  color: #1a9aa8;
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
  transition: color 0.3s;
  cursor: pointer;
  padding: 8px 0;
  position: relative;
}

.nav-item:hover {
  color: #22b3c1;
}

.nav-item.active {
  color: #22b3c1;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #22b3c1;
  animation: slideIn 0.3s ease-out;
}

.login-btn {
  background: #22b3c1;
  color: white !important;
  padding: 8px 20px;
  border-radius: 25px;
  transition: all 0.3s;
}

.login-btn:hover {
  background: #1a9aa8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.3);
  color: white !important;
}

@keyframes slideIn {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: 100%;
    opacity: 1;
  }
}


.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  text-align: center;
  margin-bottom: 50px;
}

.section-title h1 {
  font-size: 36px;
  color: #22b3c1;
  margin-bottom: 15px;
  font-weight: 700;
}

.section-title p {
  font-size: 18px;
  color: #666;
  margin: 0;
}


/* Responsive Design */
@media (max-width: 768px) {
  .identification-page {
    padding: 40px 0;
  }

  .section-title h1 {
    font-size: 28px;
  }

  .upload-card {
    padding: 40px 20px;
  }

  .upload-icon {
    font-size: 40px;
  }

  .upload-content h3 {
    font-size: 20px;
  }

  .result-card {
    padding: 20px;
  }

  .bird-name {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .section-title h1 {
    font-size: 24px;
  }

  .upload-content h3 {
    font-size: 18px;
  }

  .file-input {
    padding: 10px 20px;
    font-size: 14px;
  }
}

/* Bird Migration Flow Styles */
.migration-container {
  display: flex;
  min-height: 600px;
  background: #f5f7fa;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  margin-bottom: 60px;
}

.sidebar {
  width: 350px;
  background: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.sidebar-header {
  padding: 20px;
  background: linear-gradient(135deg, #22b3c1 0%, #1a9aa8 100%);
  color: white;

  h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 500;
  }
}

.filter-section {
  padding: 20px;
  border-bottom: 1px solid #eaeaea;

  label {
    display: block;
    margin-bottom: 10px;
    font-weight: 500;
    color: #333;
  }

  .bird-select {
    width: 100%;
  }
}

.bird-info {
  padding: 20px;
  border-bottom: 1px solid #eaeaea;

  h4 {
    margin: 0 0 15px 0;
    color: #22b3c1;
    font-size: 16px;
  }

  .info-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 13px;

    .label {
      color: #8492a6;
      font-weight: 500;
    }

    .value {
      color: #333;
      text-align: right;
      flex: 1;
      margin-left: 10px;
    }
  }
}

/* Upload Section in Sidebar */
.upload-section {
  padding: 20px;
  border-bottom: 1px solid #eaeaea;
}

.upload-section h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.upload-card {
  background: white;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  min-height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-card:hover {
  border-color: #22b3c1;
}

.upload-card.dragover {
  background: rgba(34, 179, 193, 0.05);
  border: 2px dashed #22b3c1;
}

.file-input {
  display: none;
}

.preview-image {
  width: 100%;
  max-width: 200px;
  margin: 0 auto;
}

.preview-image img {
  max-width: 100%;
  max-height: 100px;
  border-radius: 4px;
  object-fit: contain;
}

.preview-placeholder {
  width: 100%;
  height: 100px;
  border: 1px dashed #ccc;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}

.upload-content {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.upload-button {
  background: linear-gradient(135deg, #22b3c1 0%, #1a9aa8 100%);
  border: none;
  border-radius: 4px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(34, 179, 193, 0.4);
}

/* Result Section in Sidebar */
.result-section {
  padding: 20px;
  border-bottom: 1px solid #eaeaea;
}

.result-section h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.result-card {
  background: white;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  padding: 15px;
}

.bird-name {
  font-size: 16px;
  font-weight: 600;
  color: #22b3c1;
  margin: 0 0 5px 0;
}

.bird-description {
  color: #666;
  font-size: 14px;
  line-height: 1.4;
  margin: 0;
}

.protection-level {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eaeaea;
  display: flex;
  justify-content: space-between;
  font-size: 13px;

  .label {
    color: #8492a6;
    font-weight: 500;
  }

  .value {
    color: #333;
    text-align: right;
    flex: 1;
    margin-left: 10px;
  }
}

/* Rescue Station Section */
.rescue-section {
  padding: 20px;
  border-bottom: 1px solid #eaeaea;
}

.rescue-section h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.rescue-card {
  background: white;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  padding: 15px;
}

.location-input {
  margin-bottom: 15px;

  .location-input {
    width: 100%;
  }
}

.search-button {
  width: 100%;
  margin-bottom: 15px;
}

.stations-list {
  margin-top: 15px;
}

.station-item {
  padding: 10px;
  border: 1px solid #eaeaea;
  border-radius: 4px;
  margin-bottom: 10px;

  h6 {
    margin: 0 0 5px 0;
    color: #22b3c1;
    font-size: 14px;
  }

  p {
    margin: 0 0 5px 0;
    font-size: 13px;
    color: #666;
  }
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}

.no-stations {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 13px;
}

/* Report Section */
.report-section {
  padding: 20px;
  border-bottom: 1px solid #eaeaea;
}

.report-section h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.report-card {
  background: white;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  padding: 15px;
}

.report-success {
  margin-top: 15px;
}

.report-actions {
  margin-top: 15px;
  text-align: center;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .station-item {
    padding: 8px;

    h6 {
      font-size: 13px;
    }

    p {
      font-size: 12px;
    }
  }

  .report-card {
    padding: 10px;
  }
}

.map-container {
  flex: 1;
  position: relative;
  min-height: 500px;
  height: 100%;

  .map {
    width: 100%;
    height: 100%;
    position: relative;
    z-index: 1;
  }

  .flow-canvas {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 1000;
    background: transparent;
  }
}

/* Leaflet CSS */
@import url('https://unpkg.com/leaflet@1.9.4/dist/leaflet.css');

/* Fix Leaflet map size */
#migrationMap {
  width: 100%;
  height: 100%;
  background: #f0f0f0;
}

/* Responsive Design */
@media (max-width: 1024px) {
  .migration-container {
    flex-direction: column;
    min-height: 800px;
  }

  .sidebar {
    width: 100%;
    max-height: 300px;
  }

  .map-container {
    height: 500px;
  }
}

@media (max-width: 768px) {
  .migration-container {
    min-height: 700px;
  }

  .sidebar {
    max-height: 250px;
  }

  .map-container {
    height: 400px;
  }
}

@media (max-width: 480px) {
  .migration-container {
    min-height: 600px;
  }

  .sidebar {
    max-height: 200px;
  }

  .map-container {
    height: 300px;
  }
}
</style>
-->
