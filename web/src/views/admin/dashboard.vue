<template>
  <div class="dashboard">
    <div class="container-fluid">
      <div class="page-header">
        <h2>Dashboard</h2>
        <p>Rescue operation statistics and insights</p>
      </div>

      <div class="stats-cards">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ totalReports }}</div>
            <div class="stat-label">Total Reports</div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ pendingReports }}</div>
            <div class="stat-label">Pending Reports</div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ completedReports }}</div>
            <div class="stat-label">Completed Reports</div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ totalStations }}</div>
            <div class="stat-label">Rescue Stations</div>
          </div>
        </el-card>
      </div>

      <div class="charts-row">
        <el-card class="chart-card">
          <div slot="header" class="chart-header">
            <span>Reports by Status</span>
          </div>
          <div class="chart-container">
            <div ref="statusChart" style="width: 100%; height: 300px;"></div>
          </div>
        </el-card>
        <el-card class="chart-card">
          <div slot="header" class="chart-header">
            <span>Reports by Injury Type</span>
          </div>
          <div class="chart-container">
            <div ref="injuryChart" style="width: 100%; height: 300px;"></div>
          </div>
        </el-card>
      </div>

      <div class="charts-row">
        <el-card class="chart-card full-width">
          <div slot="header" class="chart-header">
            <span>Reports Over Time</span>
          </div>
          <div class="chart-container">
            <div ref="timeChart" style="width: 100%; height: 300px;"></div>
          </div>
        </el-card>
      </div>

      <div class="recent-reports">
        <el-card>
          <div slot="header" class="card-header">
            <span>Recent Reports</span>
          </div>
          <el-table :data="recentReports" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="birdName" label="Bird Name"></el-table-column>
            <el-table-column prop="injuryType" label="Injury Type"></el-table-column>
            <el-table-column prop="status" label="Status">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="Created At" width="180"></el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getReportsByStatus, getReportsByInjuryType, getReportsOverTime } from '@/api/statistics'
import { allReport } from '@/api/report'
import { allRescueStation } from '@/api/rescueStation'

export default {
  data() {
    return {
      totalReports: 0,
      pendingReports: 0,
      completedReports: 0,
      totalStations: 0,
      recentReports: [],
      statusChart: null,
      injuryChart: null,
      timeChart: null
    }
  },
  methods: {
    async loadStatistics() {
      try {
        // Load total stations
        const stationResponse = await allRescueStation()
        if (stationResponse.code === 200) {
          this.totalStations = stationResponse.data ? stationResponse.data.length : 0
        }

        // Load recent reports
        const reportResponse = await allReport()
        if (reportResponse.code === 200) {
          this.recentReports = reportResponse.data ? reportResponse.data.slice(0, 5) : []

          // Calculate statistics from all reports
          const allReports = reportResponse.data || []
          this.totalReports = allReports.length
          this.pendingReports = allReports.filter(r => r.status === 'PENDING').length
          this.completedReports = allReports.filter(r => r.status === 'COMPLETED').length
        }
      } catch (error) {
        console.error('Error loading statistics:', error)
        this.$message.error('Failed to load statistics')
      }
    },

    async initCharts() {
      setTimeout(() => {
        this.createStatusChart()
        this.createInjuryChart()
        this.createTimeChart()
      }, 100)
    },

    async createStatusChart() {
      try {
        const response = await getReportsByStatus()
        if (response.code === 200) {
          const statusData = response.data || {}

          const chart = echarts.init(this.$refs.statusChart)
          chart.setOption({
            tooltip: {
              trigger: 'item',
              formatter: '{a} <br/>{b}: {c} ({d}%)'
            },
            legend: {
              orient: 'vertical',
              left: 'left'
            },
            series: [{
              name: 'Report Status',
              type: 'pie',
              radius: '50%',
              data: [
                { value: statusData.PENDING || 0, name: 'Pending' },
                { value: statusData.PROCESSING || 0, name: 'Processing' },
                { value: statusData.COMPLETED || 0, name: 'Completed' },
                { value: statusData.CANCELLED || 0, name: 'Cancelled' }
              ],
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              },
              color: ['#f39c12', '#3498db', '#27ae60', '#e74c3c']
            }]
          })
          this.statusChart = chart
        }
      } catch (error) {
        console.error('Error creating status chart:', error)
      }
    },

    async createInjuryChart() {
      try {
        const response = await getReportsByInjuryType()
        if (response.code === 200) {
          const injuryData = response.data || {}
          const injuryTypes = injuryData.injuryTypes || {}
          const labels = Object.keys(injuryTypes)
          const data = Object.values(injuryTypes)

          const chart = echarts.init(this.$refs.injuryChart)
          chart.setOption({
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'shadow'
              }
            },
            xAxis: {
              type: 'category',
              data: labels.length > 0 ? labels : ['No Data'],
              axisLabel: {
                rotate: 45
              }
            },
            yAxis: {
              type: 'value',
              minInterval: 1
            },
            series: [{
              name: 'Number of Reports',
              type: 'bar',
              data: data.length > 0 ? data : [0],
              itemStyle: {
                color: '#22b3c1'
              }
            }],
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            }
          })
          this.injuryChart = chart
        }
      } catch (error) {
        console.error('Error creating injury chart:', error)
      }
    },

    async createTimeChart() {
      try {
        const response = await getReportsOverTime()
        if (response.code === 200) {
          const timeData = response.data || {}
          const monthlyReports = timeData.monthlyReports || {}
          const labels = Object.keys(monthlyReports).sort()
          const data = labels.map(month => monthlyReports[month])

          const chart = echarts.init(this.$refs.timeChart)
          chart.setOption({
            tooltip: {
              trigger: 'axis'
            },
            xAxis: {
              type: 'category',
              data: labels.length > 0 ? labels : ['No Data'],
              boundaryGap: false
            },
            yAxis: {
              type: 'value',
              minInterval: 1
            },
            series: [{
              name: 'Reports',
              type: 'line',
              data: data.length > 0 ? data : [0],
              smooth: true,
              areaStyle: {
                color: 'rgba(34, 179, 193, 0.2)'
              },
              itemStyle: {
                color: '#22b3c1'
              },
              lineStyle: {
                color: '#22b3c1'
              }
            }],
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            }
          })
          this.timeChart = chart
        }
      } catch (error) {
        console.error('Error creating time chart:', error)
      }
    },

    getStatusType(status) {
      switch (status) {
        case 'PENDING': return 'warning'
        case 'PROCESSING': return 'info'
        case 'COMPLETED': return 'success'
        case 'CANCELLED': return 'danger'
        default: return ''
      }
    }
  },
  mounted() {
    this.loadStatistics()
    this.initCharts()
  },
  beforeDestroy() {
    if (this.statusChart) {
      this.statusChart.dispose()
    }
    if (this.injuryChart) {
      this.injuryChart.dispose()
    }
    if (this.timeChart) {
      this.timeChart.dispose()
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h2 {
  color: #22b3c1;
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
  margin: 0;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-content {
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #22b3c1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.full-width {
  grid-column: 1 / -1;
}

.chart-card {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chart-header {
  font-weight: 500;
  color: #333;
}

.chart-container {
  height: 300px;
  position: relative;
}

.recent-reports {
  margin-bottom: 30px;
}

.card-header {
  font-weight: 500;
  color: #333;
}

@media (max-width: 768px) {
  .charts-row {
    grid-template-columns: 1fr;
  }

  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>