<template>
  <div class="identification-page">
    <div class="container">
      <!-- Bird Migration Flow -->
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

          <!-- Upload Section -->
          <div class="upload-section">
            <h4>Upload Image</h4>
            <div class="upload-card" 
                 :class="{ 'dragover': isDragover }"
                 @dragover.prevent="onDragOver"
                 @dragleave.prevent="onDragLeave"
                 @drop.prevent="onDrop"
                 @click="triggerFileInput">
              <input ref="fileInput" 
                     type="file" 
                     class="file-input" 
                     @change="handleImageUpload" 
                     accept="image/*">
              <div v-if="imageSrc" class="preview-image">
                <img :src="imageSrc" alt="Uploaded Image">
              </div>
              <div v-else class="upload-content">
                <el-button type="primary" class="upload-button">
                  Upload Image
                </el-button>
              </div>
            </div>
          </div>

          <!-- Identification Result -->
          <div v-if="form.name" class="result-section">
            <h4>Identification Result</h4>
            <div class="result-card">
              <h5 class="bird-name">{{ form.name }}</h5>
              <p class="bird-description">{{ form.description }}</p>
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
export default {
  data() {
    return {
      imageSrc: '',
      form: {},
      isDragover: false,
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
      baseLayer: null
    }
  },
  methods: {
    handleImageUpload(event) {
      const file = event.target.files[0];
      this.processFile(file);
    },
    onDragOver() {
      this.isDragover = true;
    },
    onDragLeave() {
      this.isDragover = false;
    },
    onDrop(event) {
      this.isDragover = false;
      const file = event.dataTransfer.files[0];
      if (file && file.type.startsWith('image/')) {
        this.processFile(file);
      } else {
        this.$message.error("Please upload image file");
      }
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    processFile(file) {
      if (file) {
        const reader = new FileReader();
        reader.onloadend = () => {
          this.imageSrc = reader.result;
        };
        reader.readAsDataURL(file);

        const formData = new FormData();
        formData.append('file', file); 
        fetch('http://localhost:8080/file/identification', {
          method: 'POST',
          body: formData,
        })
        .then(response => response.json())
        .then(data => {
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
        
        this.resizeCanvas();
        window.addEventListener('resize', this.resizeCanvas);
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
        if (mapContainer && mapContainer.clientWidth > 0 && mapContainer.clientHeight > 0) {
          // 自动调整地图视图到迁徙区域
          this.map.fitBounds(this.bounds, { padding: [50, 50] });
          
          // 强制地图重绘
          setTimeout(() => {
            if (this.map) {
              this.map.invalidateSize();
            }
          }, 100);
          
          this.createVelocityField();
          this.startAnimation();
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
          
          const point = this.map.latLngToContainerPoint([particle.lat, particle.lng]);
          
          // Check if point is within canvas bounds
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
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCanvas);
    this.stopAnimation();
  }
}
</script>

<style scoped>
.identification-page {
  padding: 60px 0;
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  min-height: 100vh;
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

.map-container {
  flex: 1;
  position: relative;
  min-height: 500px;

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
