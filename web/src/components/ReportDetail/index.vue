<template>
  <el-dialog
    :title="'Report #' + (report ? report.id : '')"
    :visible.sync="visible"
    width="700px"
    class="report-detail-dialog"
    @close="handleClose">
    <div v-if="report">
      <div class="detail-header">
        <div class="bird-title">
          <h2>{{ report.birdName }}</h2>
          <el-tag :type="getStatusType(report.status)" size="large">
            {{ report.status }}
          </el-tag>
        </div>
        <p class="species">{{ report.species || 'Unknown Species' }}</p>
      </div>

      <el-divider></el-divider>

      <div class="detail-section">
        <h4><i class="el-icon-location"></i> Location Information</h4>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">Address:</span>
            <span class="value">{{ report.location || 'Not specified' }}</span>
          </div>
          <div class="info-item">
            <span class="label">Coordinates:</span>
            <span class="value">{{ formatCoordinates(report.latitude, report.longitude) }}</span>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h4><i class="el-icon-warning"></i> Injury Information</h4>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">Injury Type:</span>
            <span class="value">{{ report.injuryType || 'Unknown' }}</span>
          </div>
        </div>
        <div v-if="report.injuryDescription" class="description-box">
          <p>{{ report.injuryDescription }}</p>
        </div>
      </div>

      <div v-if="report.imageUrl" class="detail-section image-section">
        <h4><i class="el-icon-picture"></i> Report Image</h4>
        <div class="image-container">
          <img :src="report.imageUrl" :alt="report.birdName" class="report-detail-image">
        </div>
      </div>

      <div class="detail-section">
        <h4><i class="el-icon-office-building"></i> Rescue Station</h4>
        <div v-if="report.rescueStationId && stationName" class="station-card">
          <p class="station-name">{{ stationName }}</p>
          <p v-if="report.notes" class="station-notes">{{ report.notes }}</p>
        </div>
        <p v-else class="no-station">Not assigned yet</p>
      </div>

      <el-divider></el-divider>

      <div class="detail-section timeline-section">
        <h4><i class="el-icon-time"></i> Timeline</h4>
        <div class="timeline">
          <div class="timeline-item">
            <span class="timeline-label">Submitted:</span>
            <span class="timeline-value">{{ formatDate(report.createdAt) }}</span>
          </div>
          <div class="timeline-item">
            <span class="timeline-label">Last Updated:</span>
            <span class="timeline-value">{{ formatDate(report.updatedAt) }}</span>
          </div>
        </div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">Close</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { allRescueStation } from '@/api/rescueStation'

export default {
  name: 'ReportDetail',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    report: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      rescueStations: {}
    }
  },
  computed: {
    stationName() {
      if (!this.report || !this.report.rescueStationId) return null
      return this.rescueStations[this.report.rescueStationId] || 'Unknown Station'
    }
  },
  methods: {
    getStatusType(status) {
      switch (status) {
        case 'PENDING': return 'warning'
        case 'PROCESSING': return 'info'
        case 'COMPLETED': return 'success'
        case 'CANCELLED': return 'danger'
        default: return 'info'
      }
    },
    formatDate(dateString) {
      if (!dateString) return 'N/A'
      const date = new Date(dateString)
      return date.toLocaleString()
    },
    formatCoordinates(lat, lng) {
      if (lat == null || lng == null) return 'N/A'
      return `${lat}, ${lng}`
    },
    handleClose() {
      this.$emit('update:visible', false)
      this.$emit('close')
    },
    loadRescueStations() {
      allRescueStation().then(response => {
        if (response.code === 200) {
          response.data.forEach(station => {
            this.rescueStations[station.id] = station.name
          })
        }
      }).catch(error => {
        console.error('Error loading rescue stations:', error)
      })
    }
  },
  mounted() {
    this.loadRescueStations()
  }
}
</script>

<style scoped>
.detail-header {
  margin-bottom: 20px;
}

.bird-title {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 8px;
}

.bird-title h2 {
  margin: 0;
  color: #22b3c1;
  font-size: 26px;
}

.species {
  color: #8492a6;
  font-size: 14px;
  margin: 0;
}

.detail-section {
  margin-bottom: 25px;
}

.detail-section h4 {
  color: #333;
  margin: 0 0 15px 0;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-section h4 i {
  color: #22b3c1;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  color: #8492a6;
  font-size: 13px;
}

.info-item .value {
  color: #333;
  font-size: 15px;
  font-weight: 500;
}

.description-box {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border-left: 3px solid #22b3c1;
  margin-top: 10px;
}

.description-box p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.image-section .image-container {
  text-align: center;
}

.report-detail-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.station-card {
  background: #e8f4f8;
  padding: 15px;
  border-radius: 8px;
  border-left: 3px solid #22b3c1;
}

.station-name {
  margin: 0 0 5px 0;
  color: #22b3c1;
  font-weight: 600;
  font-size: 15px;
}

.station-notes {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.no-station {
  color: #999;
  font-style: italic;
  margin: 0;
}

.timeline-section .timeline {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.timeline-item {
  display: flex;
  gap: 10px;
}

.timeline-label {
  color: #8492a6;
  font-size: 14px;
  min-width: 100px;
}

.timeline-value {
  color: #333;
  font-size: 14px;
}

.dialog-footer {
  text-align: center;
  padding-top: 10px;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>

<style>
.report-detail-dialog .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.report-detail-dialog .el-dialog__header {
  background: linear-gradient(135deg, #4dd0e1 0%, #22b3c1 100%);
  padding: 20px 30px;
}

.report-detail-dialog .el-dialog__title {
  color: white;
  font-size: 20px;
  font-weight: 600;
}

.report-detail-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
}

.report-detail-dialog .el-dialog__body {
  padding: 30px;
}
</style>