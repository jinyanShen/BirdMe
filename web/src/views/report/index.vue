<template>
  <div class="my-reports">
    <div class="container">
      <div class="page-header">
        <h2>My Rescue Reports</h2>
        <p>Track the status of your bird rescue reports</p>
      </div>

      <div class="reports-list">
        <el-card v-if="reports.length === 0 && !loading" class="empty-card">
          <div class="empty-content">
            <i class="el-icon-folder-opened"></i>
            <p class="no-data-text">No Data</p>
          </div>
        </el-card>

        <div v-if="loading" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>Loading reports...</p>
        </div>

        <el-card
          v-for="report in reports"
          :key="report.id"
          class="report-card"
          @click.native="viewDetails(report)">
          <div class="report-header">
            <div class="bird-info">
              <h3>{{ report.birdName }}</h3>
              <span class="species">{{ report.species || 'Unknown Species' }}</span>
            </div>
            <el-tag :type="getStatusType(report.status)" size="medium">
              {{ report.status }}
            </el-tag>
          </div>

          <div class="report-details">
            <div class="detail-item">
              <i class="el-icon-location"></i>
              <span>{{ report.location || 'Location not specified' }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-warning"></i>
              <span>{{ report.injuryType || 'Unknown' }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-time"></i>
              <span>{{ formatDate(report.createdAt) }}</span>
            </div>
          </div>

          <div v-if="report.injuryDescription" class="description">
            <p>{{ report.injuryDescription }}</p>
          </div>

          <div v-if="report.imageUrl" class="report-image">
            <img :src="report.imageUrl" :alt="report.birdName">
          </div>

          <div class="card-footer">
            <span class="click-hint">Click to view details</span>
          </div>
        </el-card>
      </div>

      <ReportDetail
        :visible.sync="dialogVisible"
        :report="selectedReport"
        @close="dialogVisible = false"
      />
    </div>
  </div>
</template>

<script>
import { getReportsBySubmitter } from '@/api/report'
import ReportDetail from '@/components/ReportDetail/index.vue'

export default {
  components: {
    ReportDetail
  },
  data() {
    return {
      reports: [],
      loading: false,
      dialogVisible: false,
      selectedReport: null
    }
  },
  methods: {
    loadReports() {
      this.loading = true
      const userId = sessionStorage.getItem('id')

      getReportsBySubmitter(userId).then(response => {
        if (response.code === 200) {
          this.reports = response.data || []
        } else {
          this.$message.error('Failed to load reports')
        }
        this.loading = false
      }).catch(error => {
        console.error('Error loading reports:', error)
        this.$message.error('Failed to load reports')
        this.loading = false
      })
    },
    viewDetails(report) {
      this.selectedReport = { ...report }
      this.dialogVisible = true
    },
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
    }
  },
  mounted() {
    this.loadReports()
  }
}
</script>

<style scoped>
.my-reports {
  padding: 40px 20px;
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 40px;
  text-align: center;
}

.page-header h2 {
  color: #22b3c1;
  margin-bottom: 10px;
  font-size: 32px;
  font-weight: 700;
}

.page-header p {
  color: #666;
  margin: 0;
  font-size: 16px;
}

.reports-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
}

.empty-card {
  grid-column: 1 / -1;
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 12px;
}

.empty-content i {
  font-size: 80px;
  color: #ddd;
  margin-bottom: 25px;
  display: block;
}

.no-data-text {
  color: #999;
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.loading-container {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
}

.loading-container i {
  font-size: 50px;
  color: #22b3c1;
  margin-bottom: 20px;
  display: block;
}

.loading-container p {
  color: #666;
  font-size: 16px;
}

.report-card {
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.report-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.report-card:hover .click-hint {
  opacity: 1;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.bird-info h3 {
  margin: 0 0 6px 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

.species {
  color: #8492a6;
  font-size: 14px;
}

.report-details {
  margin-bottom: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  color: #666;
  font-size: 14px;
}

.detail-item i {
  margin-right: 10px;
  color: #22b3c1;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.description {
  margin-bottom: 15px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #22b3c1;
}

.description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
  font-size: 14px;
}

.report-image {
  margin-bottom: 15px;
  text-align: center;
}

.report-image img {
  max-width: 100%;
  max-height: 180px;
  border-radius: 8px;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-footer {
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
}

.click-hint {
  color: #22b3c1;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

/* Dialog Styles */
.report-detail-dialog >>> .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.report-detail-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, #4dd0e1 0%, #22b3c1 100%);
  padding: 20px 30px;
}

.report-detail-dialog >>> .el-dialog__title {
  color: white;
  font-size: 20px;
  font-weight: 600;
}

.report-detail-dialog >>> .el-dialog__headerbtn .el-dialog__close {
  color: white;
}

.report-detail-dialog >>> .el-dialog__body {
  padding: 30px;
}

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
  .reports-list {
    grid-template-columns: 1fr;
  }

  .page-header h2 {
    font-size: 26px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .report-detail-dialog >>> .el-dialog__body {
    padding: 20px;
  }
}
</style>