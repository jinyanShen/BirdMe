<template>
  <div class="injury-heatmap-page">
    <!-- Control Bar -->
    <div class="control-bar">
      <div class="control-left">
        <h3>
          <i class="el-icon-location-information"></i>
          Bird Injury Report Heatmap
        </h3>
        <p class="subtitle">
          Distribution of reported bird injuries - help protect our feathered friends
        </p>
      </div>
      <div class="control-right">
        <el-select
          v-model="timeRange"
          placeholder="Time Range"
          size="medium"
          @change="refreshHeatmap"
          style="width: 140px; margin-right: 12px"
        >
          <el-option label="Last 7 days" value="7"></el-option>
          <el-option label="Last 30 days" value="30"></el-option>
          <el-option label="Last 90 days" value="90"></el-option>
          <el-option label="All Time" value="all"></el-option>
        </el-select>

        <el-select
          v-model="selectedSpecies"
          placeholder="Filter by Species"
          size="medium"
          clearable
          filterable
          @change="refreshHeatmap"
          style="width: 200px; margin-right: 12px"
        >
          <el-option
            v-for="species in speciesList"
            :key="species"
            :label="species"
            :value="species"
          ></el-option>
        </el-select>

        <el-select
          v-model="injuryType"
          placeholder="Injury Type"
          size="medium"
          clearable
          @change="refreshHeatmap"
          style="width: 140px; margin-right: 12px"
        >
          <el-option label="Broken Wing" value="Broken Wing"></el-option>
          <el-option label="Injured Leg" value="Injured Leg"></el-option>
          <el-option label="Illness" value="Illness"></el-option>
          <el-option label="Other" value="Other"></el-option>
        </el-select>

        <el-button
          type="primary"
          size="medium"
          icon="el-icon-refresh"
          @click="refreshHeatmap"
          :loading="loading"
        >
          Refresh
        </el-button>
      </div>
    </div>

    <!-- Map Container -->
    <div class="map-wrapper">
      <div id="injuryMap" class="map-container"></div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-overlay">
        <i class="el-icon-loading"></i>
        <span>Loading heatmap data...</span>
      </div>

      <!-- Legend -->
      <div class="legend-card">
        <h4>Injury Density</h4>
        <div class="legend-gradient"></div>
        <div class="legend-labels">
          <span>Low</span>
          <span>Medium</span>
          <span>High</span>
          <span>Severe</span>
        </div>
      </div>

      <!-- Statistics Summary -->
      <div class="stats-card" v-if="statsData">
        <div class="stat-item">
          <div class="stat-number">{{ statsData.totalReports }}</div>
          <div class="stat-label">Total Reports</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ statsData.affectedSpecies }}</div>
          <div class="stat-label">Species Affected</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ statsData.highRiskAreas }}</div>
          <div class="stat-label">High Risk Areas</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ statsData.rescuedCount || 0 }}</div>
          <div class="stat-label">Resolved</div>
        </div>
      </div>
    </div>

    <!-- Bottom Info Area -->
    <div class="info-wrapper">
      <div class="container">
        <!-- Recent Reports List -->
        <div class="reports-section">
          <div class="section-header">
            <h4>
              <i class="el-icon-document"></i>
              Recent Injury Reports
            </h4>
            <el-button type="text" @click="viewAllReports" icon="el-icon-arrow-right">
              View All Reports
            </el-button>
          </div>

          <div class="reports-list" v-loading="loadingReports">
            <div
              v-for="report in recentReports"
              :key="report.id"
              class="report-card"
              @click="focusOnReport(report)"
            >
              <div class="report-icon" :class="getInjuryClass(report.injuryType)">
                <i :class="getInjuryIcon(report.injuryType)"></i>
              </div>
              <div class="report-content">
                <div class="report-title">
                  <span class="bird-name">{{ report.birdName || report.species }}</span>
                  <el-tag :type="getInjuryTagType(report.injuryType)" size="small">
                    {{ report.injuryType }}
                  </el-tag>
                </div>
                <div class="report-location">
                  <i class="el-icon-location"></i>
                  {{ report.location || (report.latitude + ', ' + report.longitude) }}
                </div>
                <div class="report-time">
                  <i class="el-icon-time"></i>
                  {{ formatTime(report.createdAt) }}
                </div>
              </div>
              <div class="report-status" :class="{ resolved: report.status === 'RESOLVED' }">
                {{ report.status === 'RESOLVED' ? 'Resolved' : 'Pending' }}
              </div>
            </div>

            <div v-if="recentReports.length === 0 && !loadingReports" class="empty-state">
              <i class="el-icon-info"></i>
              <p>No injury reports available</p>
            </div>
          </div>
        </div>

        <!-- High Risk Areas Alert -->
        <div class="alert-section" v-if="hotspotsData.length > 0">
          <div class="section-header">
            <h4>
              <i class="el-icon-warning"></i>
              High Risk Areas
            </h4>
            <span class="alert-badge">{{ hotspotsData.length }} areas need attention</span>
          </div>
          <div class="hotspots-grid">
            <div
              v-for="hotspot in hotspotsData"
              :key="hotspot.area"
              class="hotspot-card"
              :style="{ borderLeftColor: getHotspotColor(hotspot.severity) }"
            >
              <div class="hotspot-name">
                <i class="el-icon-location"></i>
                {{ hotspot.area }}
              </div>
              <div class="hotspot-stats">
                <span>{{ hotspot.reportCount }} reports</span>
                <span class="severity" :style="{ color: getHotspotColor(hotspot.severity) }">
                  {{ hotspot.severity }}
                </span>
              </div>
              <div class="hotspot-species" v-if="hotspot.commonSpecies">
                Common: {{ hotspot.commonSpecies.slice(0, 3).join(', ') }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import { getInjuryHeatmap, getRecentReports } from '@/api/injury'

// Fix Leaflet default icon path issue
delete L.Icon.Default.prototype._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png'
})

let heatLayerLoaded = false

export default {
  name: 'BirdInjuryHeatmap',
  data() {
    return {
      map: null,
      heatLayer: null,
      markers: [],
      loading: false,
      loadingReports: false,
      timeRange: '30',
      selectedSpecies: null,
      injuryType: null,
      heatmapData: [],
      statsData: null,
      recentReports: [],
      hotspotsData: [],
      speciesList: []
    }
  },
  mounted() {
    this.loadLeafletHeat()
  },
  beforeDestroy() {
    if (this.map) {
      this.map.remove()
    }
  },
  methods: {
    async loadLeafletHeat() {
      try {
        await import('leaflet.heat')
        heatLayerLoaded = true
        this.initMap()
        this.loadHeatmapData()
        this.loadRecentReports()
      } catch (error) {
        console.error('Failed to load leaflet.heat:', error)
        this.initMap()
        this.loadHeatmapData()
        this.loadRecentReports()
      }
    },

    initMap() {
      const mapContainer = document.getElementById('injuryMap')
      if (!mapContainer) return

      this.map = L.map('injuryMap').setView([20, 0], 2)

      L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> &copy; CartoDB',
        subdomains: 'abcd',
        maxZoom: 19,
        minZoom: 1
      }).addTo(this.map)

      this.map.on('moveend', () => {
        if (this.heatmapData.length > 0 && heatLayerLoaded) {
          this.updateHeatmapForCurrentView()
        }
      })
    },

    async loadHeatmapData() {
      this.loading = true
      try {
        const params = {
          timeRange: this.timeRange,
          species: this.selectedSpecies || '',
          injuryType: this.injuryType || ''
        }

        const response = await getInjuryHeatmap(params)

        if (response.code === 200) {
          this.heatmapData = response.data.points || []
          this.statsData = response.data.stats
          this.hotspotsData = response.data.hotspots || []
          this.speciesList = response.data.speciesList || []
          this.updateHeatmap()
        } else {
          this.$message.error(response.msg || 'Failed to load heatmap data')
        }
      } catch (error) {
        console.error('Failed to load heatmap data:', error)
        this.$message.error('Failed to load heatmap data')
      } finally {
        this.loading = false
      }
    },

    updateHeatmap() {
      if (!this.map || !heatLayerLoaded || !window.L.heatLayer) return

      if (this.heatLayer) {
        this.map.removeLayer(this.heatLayer)
      }

      const heatData = this.heatmapData.map(point => [
        point.lat,
        point.lng,
        point.intensity || 0.5
      ])

      if (heatData.length === 0) return

      this.heatLayer = window.L.heatLayer(heatData, {
        radius: 25,
        blur: 15,
        maxZoom: 10,
        minOpacity: 0.3,
        gradient: {
          0.2: 'blue',
          0.4: 'lime',
          0.6: 'yellow',
          0.8: 'orange',
          1.0: 'red'
        }
      }).addTo(this.map)

      // Auto-fit view to data bounds
      const bounds = L.latLngBounds(heatData.map(d => [d[0], d[1]]))
      this.map.fitBounds(bounds, { padding: [50, 50] })

      this.addMarkersForHotspots()
    },

    updateHeatmapForCurrentView() {
      if (!this.map || !heatLayerLoaded) return

      const bounds = this.map.getBounds()
      const visibleData = this.heatmapData.filter(point =>
        bounds.contains([point.lat, point.lng])
      )

      if (this.heatLayer && visibleData.length > 0 && window.L.heatLayer) {
        const heatData = visibleData.map(point => [
          point.lat, point.lng, point.intensity || 0.5
        ])
        this.heatLayer.setLatLngs(heatData)
      }
    },

    addMarkersForHotspots() {
      this.markers.forEach(marker => this.map.removeLayer(marker))
      this.markers = []

      // Add markers for points with intensity >= 0.7
      const hotspots = this.heatmapData.filter(p => (p.intensity || 0) >= 0.7)
      const uniqueHotspots = hotspots.slice(0, 30)

      uniqueHotspots.forEach(hotspot => {
        const marker = L.circleMarker([hotspot.lat, hotspot.lng], {
          radius: 8,
          fillColor: '#ff4444',
          color: '#fff',
          weight: 2,
          opacity: 1,
          fillOpacity: 0.7
        }).addTo(this.map)

        marker.bindPopup(`
          <b>${hotspot.species || 'Unknown Bird'}</b><br>
          Injury: ${hotspot.injuryType}<br>
          Reports: ${hotspot.reportCount || 1}<br>
          <small>Click to view details</small>
        `)

        this.markers.push(marker)
      })
    },

    async loadRecentReports() {
      this.loadingReports = true
      try {
        const response = await getRecentReports({ limit: 10 })

        if (response.code === 200) {
          this.recentReports = response.data
        } else {
          this.$message.error(response.msg || 'Failed to load recent reports')
        }
      } catch (error) {
        console.error('Failed to load recent reports:', error)
        this.$message.error('Failed to load recent reports')
      } finally {
        this.loadingReports = false
      }
    },

    refreshHeatmap() {
      this.loadHeatmapData()
      this.loadRecentReports()
    },

    focusOnReport(report) {
      if (this.map && report.latitude && report.longitude) {
        this.map.setView([report.latitude, report.longitude], 14)
        // Add temporary highlight marker
        const tempMarker = L.circleMarker([report.latitude, report.longitude], {
          radius: 15,
          fillColor: '#ffaa00',
          color: '#fff',
          weight: 3,
          opacity: 1,
          fillOpacity: 0.8
        }).addTo(this.map)

        setTimeout(() => {
          this.map.removeLayer(tempMarker)
        }, 3000)
      }
    },

    viewAllReports() {
      this.$router.push('/report/list')
    },

    formatTime(timeString) {
      if (!timeString) return 'Recently'
      const date = new Date(timeString)
      const now = new Date()
      const diff = now - date
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))

      if (days === 0) return 'Today'
      if (days === 1) return 'Yesterday'
      if (days < 7) return days + ' days ago'
      return date.toLocaleDateString()
    },

    getInjuryIcon(injuryType) {
      const icons = {
        'Broken Wing': 'el-icon-warning',
        'Injured Leg': 'el-icon-foot',
        'Illness': 'el-icon-medicine',
        'Other': 'el-icon-info'
      }
      return icons[injuryType] || 'el-icon-warning'
    },

    getInjuryClass(injuryType) {
      const classes = {
        'Broken Wing': 'injury-wing',
        'Injured Leg': 'injury-leg',
        'Illness': 'injury-illness',
        'Other': 'injury-other'
      }
      return classes[injuryType] || ''
    },

    getInjuryTagType(injuryType) {
      const types = {
        'Broken Wing': 'danger',
        'Injured Leg': 'warning',
        'Illness': 'info',
        'Other': ''
      }
      return types[injuryType] || ''
    },

    getHotspotColor(severity) {
      const colors = {
        'Severe': '#ff4444',
        'High': '#ff8844',
        'Medium': '#ffcc44',
        'Low': '#88cc44'
      }
      return colors[severity] || '#ffcc44'
    }
  }
}
</script>

<style scoped>
.injury-heatmap-page {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6e9 100%);
  min-height: 100vh;
}

.control-bar {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.control-left h3 {
  margin: 0 0 4px 0;
  font-size: 22px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.control-left h3 i {
  color: #ff6b6b;
}

.control-left .subtitle {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.control-right {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.map-wrapper {
  position: relative;
  width: 100%;
  height: 600px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  margin-bottom: 20px;
}

.map-container {
  width: 100%;
  height: 100%;
  background: #f0f0f0;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  color: #409eff;
  z-index: 1000;
}

.loading-overlay i {
  font-size: 24px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.legend-card {
  position: absolute;
  bottom: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.95);
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.legend-card h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #333;
}

.legend-gradient {
  width: 200px;
  height: 12px;
  background: linear-gradient(90deg, blue, lime, yellow, orange, red);
  border-radius: 6px;
  margin-bottom: 8px;
}

.legend-labels {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #666;
}

.stats-card {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.75);
  padding: 16px 20px;
  border-radius: 16px;
  display: flex;
  gap: 24px;
  z-index: 1000;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #ff6b6b;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.info-wrapper {
  background: white;
  border-radius: 24px 24px 0 0;
  margin-top: -8px;
  padding: 24px 0 48px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.reports-section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e0f7fa;
}

.section-header h4 {
  margin: 0;
  font-size: 18px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header h4 i {
  color: #ff6b6b;
}

.alert-badge {
  background: #fff3e0;
  color: #ff9800;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
}

.reports-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.report-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.report-card:hover {
  transform: translateX(4px);
  background: #f0f7fa;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.report-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.report-icon.injury-wing {
  background: #ffebee;
  color: #f44336;
}

.report-icon.injury-leg {
  background: #fff3e0;
  color: #ff9800;
}

.report-icon.injury-illness {
  background: #e3f2fd;
  color: #2196f3;
}

.report-icon.injury-other {
  background: #e8eaf6;
  color: #9c27b0;
}

.report-content {
  flex: 1;
}

.report-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.report-title .bird-name {
  font-weight: 600;
  color: #333;
}

.report-location,
.report-time {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
}

.report-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  background: #ffebee;
  color: #f44336;
}

.report-status.resolved {
  background: #e8f5e9;
  color: #4caf50;
}

.empty-state {
  text-align: center;
  padding: 60px;
  color: #999;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
}

.hotspots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.hotspot-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  border-left: 4px solid #ffcc44;
  transition: all 0.3s;
}

.hotspot-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.hotspot-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.hotspot-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
}

.hotspot-species {
  font-size: 12px;
  color: #666;
}

@media (max-width: 768px) {
  .map-wrapper {
    height: 400px;
  }

  .control-bar {
    flex-direction: column;
    align-items: flex-start;
  }

  .control-right {
    width: 100%;
    justify-content: flex-start;
  }

  .stats-card {
    top: auto;
    bottom: 20px;
    right: 20px;
    padding: 10px 16px;
    gap: 16px;
  }

  .stat-number {
    font-size: 20px;
  }

  .legend-card {
    bottom: 20px;
    left: 20px;
    right: auto;
  }

  .hotspots-grid {
    grid-template-columns: 1fr;
  }
}
</style>
