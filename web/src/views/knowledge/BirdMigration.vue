<!-- BirdMigration.vue - 鸟类迁徙展示页面（增强版） -->
<template>
  <div class="migration-page">
    <!-- 地图容器 - 放在最上面 -->
    <div class="map-wrapper">
      <div class="map-container" ref="mapContainer">
        <div id="migrationMap" class="map"></div>
        <canvas id="flowCanvas" class="flow-canvas"></canvas>
      </div>
    </div>

    <!-- 下方内容区域 -->
    <div class="content-wrapper">
      <div class="container">
        <!-- 第一行：鸟类选择和基本信息 -->
        <div class="bird-selection-row">
          <div class="selection-card">
            <div class="card-header">
              <i class="el-icon-search"></i>
              <h3>Select Bird Species</h3>
            </div>
            <el-select
              v-model="selectedBird"
              filterable
              placeholder="Search or select a bird"
              @change="handleBirdChange"
              class="bird-select"
              size="large"
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

          <div class="info-card" v-if="selectedBirdData">
            <div class="card-header">
              <i class="el-icon-info"></i>
              <h3>Basic Information</h3>
            </div>
            <div class="info-content">
              <div class="info-row">
                <span class="label">Species:</span>
                <span class="value">{{ selectedBirdData.species }}</span>
              </div>
              <div class="info-row">
                <span class="label">Migration Routes:</span>
                <span class="value">{{ Object.keys(selectedBirdData.routes).length }}</span>
              </div>
              <div class="info-row" v-if="selectedBirdData.migrationPeriod">
                <span class="label">Migration Period:</span>
                <span class="value">{{ selectedBirdData.migrationPeriod }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 体型信息框 -->
        <div class="size-info-row" v-if="birdSizeInfo && Object.keys(birdSizeInfo).length > 0">
          <div class="size-card">
            <div class="card-header">
              <i class="el-icon-ruler"></i>
              <h3>Size Information</h3>
            </div>
            <div class="size-grid">
              <div v-if="birdSizeInfo.length" class="size-item">
                <span class="size-label">Length:</span>
                <span class="size-value">{{ birdSizeInfo.length }}</span>
              </div>
              <div v-if="birdSizeInfo.weight" class="size-item">
                <span class="size-label">Weight:</span>
                <span class="size-value">{{ birdSizeInfo.weight }}</span>
              </div>
              <div v-if="birdSizeInfo.wingspan" class="size-item">
                <span class="size-label">Wingspan:</span>
                <span class="size-value">{{ birdSizeInfo.wingspan }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 第二行：关于鸟类的详细信息（融合本地描述和维基百科） -->
        <div class="knowledge-row" v-if="selectedBirdData">
          <div class="knowledge-card">
            <div class="card-header">
              <i class="el-icon-reading"></i>
              <h3>About {{ selectedBird }}</h3>
              <a v-if="wikiData && wikiData.url" :href="wikiData.url" target="_blank" class="wiki-link">
                Read more on Wikipedia <i class="el-icon-link"></i>
              </a>
            </div>

            <!-- 加载中状态 -->
            <div v-if="loadingWiki" class="knowledge-content loading">
              <i class="el-icon-loading"></i>
              <span>Loading information...</span>
            </div>

            <!-- 加载完成 -->
            <div v-else class="knowledge-content">
              <!-- 本地描述 -->
              <div class="local-description" v-if="selectedBirdData.description">
                <p>{{ selectedBirdData.description }}</p>
              </div>

              <!-- 维基百科内容 -->
              <div v-if="wikiData && !wikiError" class="wiki-section">
                <div class="wiki-summary">
                  <img v-if="wikiData.thumbnail" :src="wikiData.thumbnail" class="wiki-image" :alt="wikiData.title">
                  <div class="wiki-text">
                    <p>{{ wikiData.extract }}</p>
                    <!-- 展开/收起按钮 -->
                    <div v-if="wikiData.hasMore" class="expand-button">
                      <el-button type="text" @click="expandWikiContent">
                        {{ wikiData.showFull ? 'Show less ↑' : 'Read more ↓' }}
                      </el-button>
                    </div>
                  </div>
                </div>
                <div v-if="wikiData.categories && wikiData.categories.length" class="wiki-categories">
                  <span class="category-label">Categories:</span>
                  <el-tag
                    v-for="cat in wikiData.categories.slice(0, 5)"
                    :key="cat"
                    size="small"
                    class="category-tag"
                  >
                    {{ cat }}
                  </el-tag>
                </div>
              </div>

              <!-- 维基百科信息不可用时的提示 -->
              <div v-else-if="wikiError" class="wiki-unavailable">
                <i class="el-icon-info"></i>
                <span>Additional Wikipedia information is not available for this species.</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BirdMigration',
  data() {
    return {
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
      // Wikipedia data
      wikiData: null,
      loadingWiki: false,
      wikiError: false,
      // 体型信息
      birdSizeInfo: {},
      // 防抖定时器
      wikiDebounceTimer: null
    }
  },
  methods: {
    handleBirdChange(birdName) {
      console.log('Selected bird:', birdName);
      this.ensureMigrationDataLoaded().then(() => {
        this.selectedBirdData = this.migrationData[birdName];
        this.selectedVelocityData = this.velocityData[birdName];
        console.log('Selected bird data:', this.selectedBirdData);

        // 重置维基百科数据和体型信息
        this.wikiData = null;
        this.wikiError = false;
        this.birdSizeInfo = {};

        if (this.selectedBirdData) {
          setTimeout(() => {
            this.updateMap();
          }, 100);

          // 获取维基百科信息
          this.fetchWikipediaInfo(birdName);
        }
      });
    },

    ensureMigrationDataLoaded() {
      return new Promise((resolve) => {
        if (this.migrationData) {
          resolve();
        } else {
          this.loadMigrationData().then(() => {
            resolve();
          });
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

    /**
     * 获取维基百科信息（增强版 - 获取更长的内容）
     */
    async fetchWikipediaInfo(birdName) {
      if (this.wikiDebounceTimer) {
        clearTimeout(this.wikiDebounceTimer);
      }

      this.loadingWiki = true;
      this.wikiError = false;
      this.wikiData = null;

      this.wikiDebounceTimer = setTimeout(async () => {
        try {
          const searchQuery = `${birdName} bird`;

          // 使用能返回更多内容的 API
          const apiUrl = `https://en.wikipedia.org/w/api.php?action=query&titles=${encodeURIComponent(birdName)}&prop=extracts&explaintext=1&exsectionformat=plain&format=json&origin=*`;
          const searchUrl = `https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=${encodeURIComponent(searchQuery)}&srlimit=5&format=json&origin=*`;

          // 先尝试直接获取页面
          const directResponse = await fetch(apiUrl);
          const directData = await directResponse.json();

          const pages = directData.query?.pages;
          const page = pages ? Object.values(pages)[0] : null;

          if (page && page.extract && !page.missing) {
            const fullExtract = page.extract;
            const displayExtract = fullExtract.length > 2000 ? fullExtract.substring(0, 2000) + '...' : fullExtract;

            this.wikiData = {
              title: page.title,
              extract: displayExtract,
              fullExtract: fullExtract,
              hasMore: fullExtract.length > 2000,
              showFull: false,
              url: `https://en.wikipedia.org/wiki/${encodeURIComponent(page.title)}`,
              thumbnail: null,
              categories: []
            };

            // 获取缩略图
            await this.fetchThumbnail(page.title);
            // 获取分类信息
            await this.fetchWikiCategories(page.title);
            // 提取体型信息
            this.extractSizeInfo(fullExtract);
            // 更新保育状态（如果本地数据没有）
            this.updateConservationStatusFromWiki(fullExtract);

            this.loadingWiki = false;
            return;
          }

          // 如果直接获取失败，尝试搜索
          const searchResponse = await fetch(searchUrl);
          const searchData = await searchResponse.json();

          if (searchData.query && searchData.query.search && searchData.query.search.length > 0) {
            const topResult = searchData.query.search[0];
            const pageUrl = `https://en.wikipedia.org/w/api.php?action=query&titles=${encodeURIComponent(topResult.title)}&prop=extracts&explaintext=1&exsectionformat=plain&format=json&origin=*`;
            const pageResponse = await fetch(pageUrl);
            const pageData = await pageResponse.json();

            const searchPages = pageData.query?.pages;
            const searchPage = searchPages ? Object.values(searchPages)[0] : null;

            if (searchPage && searchPage.extract) {
              const fullExtract = searchPage.extract;
              const displayExtract = fullExtract.length > 2000 ? fullExtract.substring(0, 2000) + '...' : fullExtract;

              this.wikiData = {
                title: searchPage.title,
                extract: displayExtract,
                fullExtract: fullExtract,
                hasMore: fullExtract.length > 2000,
                showFull: false,
                url: `https://en.wikipedia.org/wiki/${encodeURIComponent(searchPage.title)}`,
                thumbnail: null,
                categories: []
              };

              await this.fetchThumbnail(searchPage.title);
              await this.fetchWikiCategories(searchPage.title);
              this.extractSizeInfo(fullExtract);
              this.updateConservationStatusFromWiki(fullExtract);
            } else {
              this.wikiError = true;
            }
          } else {
            this.wikiError = true;
          }
        } catch (error) {
          console.error('Error fetching Wikipedia info:', error);
          this.wikiError = true;
        } finally {
          this.loadingWiki = false;
        }
      }, 500);
    },

    /**
     * 获取页面缩略图
     */
    async fetchThumbnail(pageTitle) {
      try {
        const url = `https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(pageTitle)}`;
        const response = await fetch(url);
        const data = await response.json();
        if (data.thumbnail?.source && this.wikiData) {
          this.wikiData.thumbnail = data.thumbnail.source;
        }
      } catch (error) {
        console.error('Error fetching thumbnail:', error);
      }
    },

    /**
     * 获取维基百科分类信息
     */
    async fetchWikiCategories(pageTitle) {
      try {
        const categoriesUrl = `https://en.wikipedia.org/w/api.php?action=query&titles=${encodeURIComponent(pageTitle)}&prop=categories&cllimit=20&format=json&origin=*`;
        const response = await fetch(categoriesUrl);
        const data = await response.json();

        const pages = data.query.pages;
        const page = Object.values(pages)[0];

        if (page && page.categories && this.wikiData) {
          this.wikiData.categories = page.categories
            .map(cat => cat.title.replace('Category:', ''))
            .filter(cat => !cat.includes('Articles') && !cat.includes('CS1') && !cat.includes('Webarchive'))
            .slice(0, 10);
        }
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    },

    /**
     * 从维基百科内容中提取体型信息
     */
    extractSizeInfo(content) {
      const sizeInfo = {};

      // 匹配长度信息（支持多种格式）
      const lengthPatterns = [
        /(?:Length|Body length)[:\s]+([\d.]+(?:\s*-\s*[\d.]+)?)\s*(?:cm|centimeters|centimetres|mm|millimeters)/i,
        /([\d.]+(?:\s*-\s*[\d.]+)?)\s*(?:cm|centimeters|centimetres)\s*(?:in length|long)/i,
        /length\s+(?:of|is)\s+([\d.]+(?:\s*-\s*[\d.]+)?)\s*(?:cm|centimeters)/i,
        /about\s+([\d.]+(?:\s*-\s*[\d.]+)?)\s*cm\s+long/i,
        /measures\s+([\d.]+(?:\s*-\s*[\d.]+)?)\s*cm\s+in length/i
      ];

      for (const pattern of lengthPatterns) {
        const match = content.match(pattern);
        if (match) {
          sizeInfo.length = match[1] + ' cm';
          break;
        }
      }

      // 匹配体重信息
      const weightPatterns = [
        /(?:Weight|Mass)[:\s]+([\d.]+(?:\s*-\s*[\d.]+)?)\s*(?:kg|kilograms|g|grams)/i,
        /weighs?\s+(?:about\s+)?([\d.]+(?:\s*-\s*[\d.]+)?)\s*(?:kg|kilograms|g|grams)/i,
        /([\d.]+(?:\s*-\s*[\d.]+)?)\s*(?:kg|kilograms|g|grams)\s+in weight/i,
        /weight\s+(?:of|is)\s+([\d.]+(?:\s*-\s*[\d.]+)?)\s*(?:kg|g)/i
      ];

      for (const pattern of weightPatterns) {
        const match = content.match(pattern);
        if (match) {
          let weightValue = match[1];
          // 如果是克，可能转换为千克显示更友好
          if (match[0].includes('g') && !match[0].includes('kg') && parseFloat(weightValue) > 100) {
            weightValue = (parseFloat(weightValue) / 1000).toFixed(1) + ' kg';
          } else {
            weightValue = weightValue + (match[0].includes('kg') ? ' kg' : ' g');
          }
          sizeInfo.weight = weightValue;
          break;
        }
      }

      // 匹配翼展信息
      const wingspanPatterns = [
        /(?:Wingspan|Wing span)[:\s]+([\d.]+(?:\s*-\s*[\d.]+)?)\s*(?:cm|centimeters|centimetres|m|meters)/i,
        /wingspan\s+(?:of|is|measures)\s+([\d.]+(?:\s*-\s*[\d.]+)?)\s*(?:cm|centimeters|m|meters)/i,
        /([\d.]+(?:\s*-\s*[\d.]+)?)\s*(?:cm|centimeters)\s+across the wings/i,
        /with a wingspan of\s+([\d.]+(?:\s*-\s*[\d.]+)?)\s*(?:cm|m)/i
      ];

      for (const pattern of wingspanPatterns) {
        const match = content.match(pattern);
        if (match) {
          let wingspanValue = match[1];
          // 如果是米，转换为厘米显示更直观
          if (match[0].includes('m') && !match[0].includes('cm') && parseFloat(wingspanValue) < 5) {
            wingspanValue = (parseFloat(wingspanValue) * 100).toFixed(0) + ' cm';
          } else {
            wingspanValue = wingspanValue + (match[0].includes('m') && !match[0].includes('cm') ? ' m' : ' cm');
          }
          sizeInfo.wingspan = wingspanValue;
          break;
        }
      }

      this.birdSizeInfo = sizeInfo;
    },

    /**
     * 从维基百科内容中提取保育状态
     */
    updateConservationStatusFromWiki(content) {
      // 如果本地数据已经有保育状态，就不覆盖
      if (this.selectedBirdData && this.selectedBirdData.conservationStatus) {
        return;
      }

      // 保育状态关键词映射
      const statusMap = {
        'Least Concern': ['least concern', 'lc', 'least-concern'],
        'Near Threatened': ['near threatened', 'nt', 'near-threatened'],
        'Vulnerable': ['vulnerable', 'vu'],
        'Endangered': ['endangered', 'en'],
        'Critically Endangered': ['critically endangered', 'cr']
      };

      for (const [status, keywords] of Object.entries(statusMap)) {
        for (const keyword of keywords) {
          if (content.toLowerCase().includes(keyword)) {
            this.selectedBirdData.conservationStatus = status;
            break;
          }
        }
        if (this.selectedBirdData.conservationStatus) break;
      }
    },

    /**
     * 展开/收起维基百科内容
     */
    expandWikiContent() {
      if (this.wikiData && this.wikiData.fullExtract) {
        this.wikiData.showFull = !this.wikiData.showFull;
        if (this.wikiData.showFull) {
          this.wikiData.extract = this.wikiData.fullExtract;
        } else {
          this.wikiData.extract = this.wikiData.fullExtract.length > 2000
            ? this.wikiData.fullExtract.substring(0, 2000) + '...'
            : this.wikiData.fullExtract;
        }
      }
    },

    getStatusClass(status) {
      const statusMap = {
        'Least Concern': 'status-lc',
        'Near Threatened': 'status-nt',
        'Vulnerable': 'status-vu',
        'Endangered': 'status-en',
        'Critically Endangered': 'status-cr'
      };
      return statusMap[status] || '';
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

      mapContainer.style.width = '100%';
      mapContainer.style.height = '100%';
      mapContainer.style.display = 'block';
      mapContainer.style.position = 'relative';
      mapContainer.style.zIndex = '1';

      console.log('Initializing map...');

      this.map = L.map('migrationMap', {
        zoomControl: true,
        attributionControl: true,
        dragging: true,
        scrollWheelZoom: true,
        doubleClickZoom: true,
        touchZoom: true,
        boxZoom: true,
        keyboard: true
      }).setView([20, 0], 2);

      this.baseLayer = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
        attribution: 'Tiles &copy; Esri',
        maxZoom: 18
      }).addTo(this.map);

      this.canvas = document.getElementById('flowCanvas');
      if (this.canvas) {
        this.ctx = this.canvas.getContext('2d');
        this.canvas.style.position = 'absolute';
        this.canvas.style.top = '0';
        this.canvas.style.left = '0';
        this.canvas.style.width = '100%';
        this.canvas.style.height = '100%';
        this.canvas.style.pointerEvents = 'none';
        this.canvas.style.zIndex = '2';
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
      if (this.ctx && this.canvas) {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
      }
    },

    updateMap() {
      if (!this.selectedBirdData) return;

      console.log('Updating map for bird:', this.selectedBird);

      if (!this.map) {
        this.initMap();
      }

      if (!this.map) {
        console.error('Map not initialized, cannot update');
        return;
      }

      this.clearMap();

      const allPoints = [];

      Object.keys(this.selectedBirdData.routes).forEach((routeKey) => {
        const route = this.selectedBirdData.routes[routeKey];
        const points = route.points;

        points.forEach((point) => {
          allPoints.push([point.lat, point.lng]);
        });
      });

      if (allPoints.length > 0) {
        console.log('All points:', allPoints);
        this.bounds = L.latLngBounds(allPoints);
        console.log('Bounds:', this.bounds);

        const mapContainer = document.getElementById('migrationMap');
        if (mapContainer && mapContainer.clientWidth > 0 && mapContainer.clientHeight > 0) {
          this.map.fitBounds(this.bounds, { padding: [50, 50] });

          setTimeout(() => {
            if (this.map) {
              this.map.invalidateSize();
              this.map.fitBounds(this.bounds, { padding: [50, 50] });

              setTimeout(() => {
                this.resizeCanvas();
                if (this.ctx) {
                  this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
                }
                this.createVelocityField();
                this.startAnimation();
              }, 100);
            }
          }, 200);
        }
      }
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
            if (!this.map) return;

            const point = this.map.latLngToContainerPoint([particle.lat, particle.lng]);

            if (point && typeof point.x === 'number' && typeof point.y === 'number') {
              if (point.x >= 0 && point.x <= this.canvas.width && point.y >= 0 && point.y <= this.canvas.height) {
                const alpha = Math.sin((particle.maxAge - particle.age) / particle.maxAge * Math.PI);

                this.ctx.beginPath();
                this.ctx.arc(point.x, point.y, 2, 0, 2 * Math.PI);
                this.ctx.fillStyle = `rgba(14, 100, 143, ${alpha * 0.8})`;
                this.ctx.fill();

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
    }
  },

  mounted() {
    console.log('BirdMigration component mounted');

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

  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCanvas);
    window.removeEventListener('scroll', this.handleScroll);
    this.stopAnimation();
    if (this.wikiDebounceTimer) {
      clearTimeout(this.wikiDebounceTimer);
    }
  }
}
</script>

<style scoped>
.migration-page {
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  min-height: 100vh;
}

/* 地图容器 - 占据上半部分 */
.map-wrapper {
  width: 100%;
  height: 50vh;
  min-height: 400px;
  background: #1a1a2e;
  position: relative;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.map-container {
  width: 100%;
  height: 100%;
  position: relative;
}

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
  z-index: 2;
  background: transparent;
}

.map-container {
  isolation: isolate;
}

/* 下方内容区域 */
.content-wrapper {
  background: #f5f7fa;
  border-radius: 24px 24px 0 0;
  margin-top: -24px;
  position: relative;
  z-index: 10;
  padding: 24px 0 48px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 11;
}

/* 卡片通用样式 */
.selection-card,
.info-card,
.size-card,
.knowledge-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
  z-index: 12;
}

.selection-card:hover,
.info-card:hover,
.size-card:hover,
.knowledge-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

/* 第一行布局 */
.bird-selection-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

/* 体型信息行 */
.size-info-row {
  margin-bottom: 24px;
}

.size-grid {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
}

.size-item {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.size-label {
  font-size: 13px;
  color: #8492a6;
  font-weight: 500;
}

.size-value {
  font-size: 15px;
  color: #22b3c1;
  font-weight: 600;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e0f7fa;
}

.card-header i {
  font-size: 20px;
  color: #22b3c1;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.bird-select {
  width: 100%;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.info-row .label {
  color: #8492a6;
  font-weight: 500;
}

.info-row .value {
  color: #333;
  font-weight: 500;
}

/* 保育状态颜色 */
.status-lc { color: #2ecc71; }
.status-nt { color: #f39c12; }
.status-vu { color: #e67e22; }
.status-en { color: #e74c3c; }
.status-cr { color: #c0392b; }

/* 知识卡片 */
.knowledge-row {
  margin-bottom: 24px;
}

.knowledge-card .card-header {
  justify-content: space-between;
  flex-wrap: wrap;
}

.wiki-link {
  color: #22b3c1;
  text-decoration: none;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s;
}

.wiki-link:hover {
  color: #1a9aa8;
  text-decoration: underline;
}

.knowledge-content {
  min-height: 200px;
}

.knowledge-content.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #999;
  min-height: 200px;
}

.knowledge-content.loading i {
  font-size: 24px;
}

.local-description {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eaeaea;
}

.local-description p {
  margin: 0;
  line-height: 1.6;
  color: #555;
  font-size: 14px;
}

.wiki-section {
  margin-top: 8px;
}

.wiki-summary {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.wiki-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.wiki-text {
  flex: 1;
}

.wiki-text p {
  margin: 0;
  line-height: 1.6;
  color: #555;
  font-size: 14px;
}

.expand-button {
  margin-top: 12px;
  text-align: center;
}

.expand-button .el-button--text {
  color: #22b3c1;
  font-size: 13px;
  padding: 0;
}

.wiki-categories {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #eaeaea;
}

.category-label {
  font-size: 13px;
  color: #8492a6;
  margin-right: 8px;
}

.category-tag {
  background: #e0f7fa;
  border: none;
  color: #22b3c1;
}

.wiki-unavailable {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px;
  color: #999;
  background: #f8f9fa;
  border-radius: 12px;
}

.wiki-unavailable i {
  font-size: 20px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .bird-selection-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .map-wrapper {
    height: 45vh;
  }
}

@media (max-width: 768px) {
  .map-wrapper {
    height: 40vh;
    min-height: 300px;
  }

  .content-wrapper {
    margin-top: -16px;
    padding: 16px 0 32px;
  }

  .container {
    padding: 0 16px;
  }

  .selection-card,
  .info-card,
  .size-card,
  .knowledge-card {
    padding: 16px;
  }

  .wiki-summary {
    flex-direction: column;
  }

  .wiki-image {
    width: 100%;
    height: 160px;
  }

  .card-header {
    flex-wrap: wrap;
  }

  .wiki-link {
    margin-top: 8px;
  }

  .local-description {
    margin-bottom: 16px;
    padding-bottom: 16px;
  }

  .size-grid {
    gap: 16px;
  }
}

@media (max-width: 480px) {
  .map-wrapper {
    height: 35vh;
    min-height: 250px;
  }

  .info-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .info-row .value {
    text-align: left;
  }

  .wiki-unavailable {
    padding: 20px;
    text-align: center;
  }

  .size-grid {
    flex-direction: column;
    gap: 12px;
  }
}

@import url('https://unpkg.com/leaflet@1.9.4/dist/leaflet.css');

#migrationMap {
  width: 100%;
  height: 100%;
  background: #f0f0f0;
}
</style>
