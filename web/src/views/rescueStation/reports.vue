<template>
  <div class="rescue-station-reports">
    <div class="toolbar">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="Search by bird name or location"
          prefix-icon="el-icon-search"
          clearable
          @clear="handleSearch"
          @keyup.enter.native="handleSearch"
        />
        <el-select v-model="searchStatus" placeholder="All Statuses" clearable @change="handleSearch" style="width: 150px; margin-left: 10px;">
          <el-option label="All" value=""></el-option>
          <el-option label="Pending" value="PENDING"></el-option>
          <el-option label="Processing" value="PROCESSING"></el-option>
          <el-option label="Completed" value="COMPLETED"></el-option>
          <el-option label="Cancelled" value="CANCELLED"></el-option>
        </el-select>
        <button class="search-btn" @click="handleSearch">Search</button>
      </div>
    </div>

    <div class="table-container">
      <el-table
        :data="reportList"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="birdName" label="Bird Name" width="120" />
        <el-table-column prop="species" label="Species" width="150" />
        <el-table-column prop="location" label="Location" width="150" />
        <el-table-column prop="injuryType" label="Injury Type" width="120" />
        <el-table-column prop="status" label="Status" width="120">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Created At" width="180" />
        <el-table-column label="Operations" width="200" fixed="right">
          <template slot-scope="scope">
            <button class="action-btn view-btn" @click="handleView(scope.row)">
              <i class="fas fa-eye"></i>
              View
            </button>
            <button class="action-btn edit-btn" @click="handleEdit(scope.row)">
              <i class="fas fa-edit"></i>
              Update Status
            </button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
        @current-change="handlePageChange"
        :current-page="pageInfo.offset"
        :page-size="pageInfo.pageSize"
        :total="pageInfo.total"
        layout="total, prev, pager, next"
      />
    </div>

    <el-dialog :visible.sync="viewDialogVisible" title="Report Details" width="600px">
      <div class="report-detail" v-if="selectedReport">
        <div class="detail-row">
          <span class="label">Bird Name:</span>
          <span class="value">{{ selectedReport.birdName }}</span>
        </div>
        <div class="detail-row">
          <span class="label">Species:</span>
          <span class="value">{{ selectedReport.species || 'N/A' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">Location:</span>
          <span class="value">{{ selectedReport.location }}</span>
        </div>
        <div class="detail-row">
          <span class="label">Injury Type:</span>
          <span class="value">{{ selectedReport.injuryType }}</span>
        </div>
        <div class="detail-row">
          <span class="label">Status:</span>
          <el-tag :type="getStatusType(selectedReport.status)">{{ selectedReport.status }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="label">Description:</span>
          <span class="value">{{ selectedReport.injuryDescription || 'N/A' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">Submitted:</span>
          <span class="value">{{ selectedReport.createdAt }}</span>
        </div>
        <div class="detail-row" v-if="selectedReport.notes">
          <span class="label">Notes:</span>
          <span class="value">{{ selectedReport.notes }}</span>
        </div>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="dialogVisible" title="Update Report Status" width="500px" @close="resetForm">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Bird Name">
          <el-input v-model="form.birdName" disabled></el-input>
        </el-form-item>
        <el-form-item label="Current Status">
          <el-select v-model="form.status" placeholder="Select status">
            <el-option label="Pending" value="PENDING"></el-option>
            <el-option label="Processing" value="PROCESSING"></el-option>
            <el-option label="Completed" value="COMPLETED"></el-option>
            <el-option label="Cancelled" value="CANCELLED"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Notes">
          <el-input type="textarea" v-model="form.notes" rows="4" placeholder="Add notes about this update..."></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">Update</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { pageReport, updateReport } from '@/api/report'

export default {
  name: 'RescueStationReports',
  data() {
    return {
      reportList: [],
      loading: false,
      submitLoading: false,
      searchKeyword: '',
      searchStatus: '',
      dialogVisible: false,
      viewDialogVisible: false,
      selectedReport: null,
      rescueStationId: null,
      form: {
        id: null,
        birdName: '',
        status: '',
        notes: ''
      },
      pageInfo: {
        offset: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  mounted() {
    this.rescueStationId = parseInt(sessionStorage.getItem('rescueStationId')) || null
    this.fetchReportList()
  },
  methods: {
    async fetchReportList() {
      if (!this.rescueStationId) {
        this.$message.error('Rescue station ID not found')
        return
      }
      this.loading = true
      try {
        const response = await pageReport({
          offset: this.pageInfo.offset,
          pageSize: this.pageInfo.pageSize,
          data: {
            birdName: this.searchKeyword,
            location: this.searchKeyword,
            status: this.searchStatus
          }
        })
        if (response.data) {
          this.reportList = response.data.filter(r => r.rescueStationId === this.rescueStationId)
          this.pageInfo.total = response.totalCount
        }
      } catch (error) {
        console.error('Failed to fetch reports:', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pageInfo.offset = 1
      this.fetchReportList()
    },
    handlePageChange(page) {
      this.pageInfo.offset = page
      this.fetchReportList()
    },
    handleView(row) {
      this.selectedReport = row
      this.viewDialogVisible = true
    },
    handleEdit(row) {
      this.form.id = row.id
      this.form.birdName = row.birdName
      this.form.status = row.status
      this.form.notes = row.notes || ''
      this.dialogVisible = true
    },
    resetForm() {
      this.form = {
        id: null,
        birdName: '',
        status: '',
        notes: ''
      }
    },
    async submitForm() {
      if (!this.form.id || !this.form.status) {
        this.$message.error('Please fill all required fields')
        return
      }
      this.submitLoading = true
      try {
        const response = await updateReport({
          id: this.form.id,
          status: this.form.status,
          notes: this.form.notes
        })
        if (response.code === 200) {
          this.$message.success('Report updated successfully')
          this.dialogVisible = false
          this.fetchReportList()
        } else {
          this.$message.error(response.msg || 'Failed to update report')
        }
      } catch (error) {
        console.error('Failed to update report:', error)
        this.$message.error('Failed to update report')
      } finally {
        this.submitLoading = false
      }
    },
    getStatusType(status) {
      const statusMap = {
        'PENDING': 'warning',
        'PROCESSING': 'primary',
        'COMPLETED': 'success',
        'CANCELLED': 'info'
      }
      return statusMap[status] || 'info'
    }
  }
}
</script>

<style scoped>
.rescue-station-reports {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  align-items: center;
}

.search-btn {
  margin-left: 10px;
  padding: 10px 20px;
  background: #22b3c1;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.search-btn:hover {
  background: #1a9ca5;
}

.table-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.action-btn {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 5px;
  font-size: 12px;
}

.view-btn {
  background: #e0f7fa;
  color: #22b3c1;
}

.edit-btn {
  background: #fff3e0;
  color: #ff9800;
}

.delete-btn {
  background: #ffebee;
  color: #f44336;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.report-detail {
  padding: 10px 0;
}

.detail-row {
  display: flex;
  margin-bottom: 15px;
  line-height: 1.5;
}

.detail-row .label {
  font-weight: bold;
  width: 120px;
  color: #606266;
}

.detail-row .value {
  flex: 1;
  color: #303133;
}
</style>
