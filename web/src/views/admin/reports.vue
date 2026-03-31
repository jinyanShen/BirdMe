<template>
  <div class="report-management">
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
      <button class="add-btn" @click="handleAdd">
        <i class="fas fa-plus"></i>
        Add Report
      </button>
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
        <el-table-column label="Operations" width="250" fixed="right">
          <template slot-scope="scope">
            <button class="action-btn view-btn" @click="handleView(scope.row)">
              <i class="fas fa-eye"></i>
              View
            </button>
            <button class="action-btn edit-btn" @click="handleEdit(scope.row)">
              <i class="fas fa-edit"></i>
              Edit
            </button>
            <button class="action-btn delete-btn" @click="handleDelete(scope.row)">
              <i class="fas fa-trash"></i>
              Delete
            </button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageInfo.offset"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageInfo.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pageInfo.total"
        />
      </div>
    </div>

    <!-- Add/Edit Report Dialog -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form ref="reportForm" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Bird Name" prop="birdName">
              <el-input v-model="form.birdName" placeholder="Please enter bird name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Species" prop="species">
              <el-input v-model="form.species" placeholder="Please enter species" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Location" prop="location">
              <el-input v-model="form.location" placeholder="Please enter location" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Injury Type" prop="injuryType">
              <el-input v-model="form.injuryType" placeholder="Please enter injury type" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Latitude" prop="latitude">
              <el-input v-model.number="form.latitude" type="number" placeholder="Latitude" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Longitude" prop="longitude">
              <el-input v-model.number="form.longitude" type="number" placeholder="Longitude" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="Description" prop="injuryDescription">
          <el-input v-model="form.injuryDescription" type="textarea" :rows="3" placeholder="Please enter injury description" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Status" prop="status">
              <el-select v-model="form.status" placeholder="Please select status">
                <el-option label="Pending" value="PENDING"></el-option>
                <el-option label="Processing" value="PROCESSING"></el-option>
                <el-option label="Completed" value="COMPLETED"></el-option>
                <el-option label="Cancelled" value="CANCELLED"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Submitter ID" prop="submitterId">
              <el-input v-model="form.submitterId" placeholder="Submitter ID" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="Notes" prop="notes">
          <el-input v-model="form.notes" type="textarea" :rows="2" placeholder="Add notes" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">Confirm</el-button>
      </div>
    </el-dialog>

    <ReportDetail
      :visible.sync="viewDialogVisible"
      :report="selectedReport"
      @close="viewDialogVisible = false"
    />
  </div>
</template>

<script>
import { pageReport, insertReport, updateReport, delReport } from '@/api/report'
import ReportDetail from '@/components/ReportDetail/index.vue'

export default {
  name: 'ReportManagement',
  components: {
    ReportDetail
  },
  data() {
    return {
      reportList: [],
      loading: false,
      submitLoading: false,
      searchKeyword: '',
      searchStatus: '',
      dialogVisible: false,
      viewDialogVisible: false,
      isEdit: false,
      dialogTitle: 'Add Report',
      selectedReport: null,
      form: {
        id: null,
        birdName: '',
        species: '',
        location: '',
        latitude: '',
        longitude: '',
        injuryType: '',
        injuryDescription: '',
        status: 'PENDING',
        submitterId: '',
        notes: ''
      },
      rules: {
        birdName: [
          { required: true, message: 'Please enter bird name', trigger: 'blur' }
        ],
        location: [
          { required: true, message: 'Please enter location', trigger: 'blur' }
        ],
        injuryType: [
          { required: true, message: 'Please enter injury type', trigger: 'blur' }
        ],
        status: [
          { required: true, message: 'Please select status', trigger: 'change' }
        ]
      },
      pageInfo: {
        offset: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  mounted() {
    this.fetchReportList()
  },
  methods: {
    async fetchReportList() {
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
        if (response.data && response.data.length > 0) {
          this.reportList = response.data
          this.pageInfo.total = response.totalCount
        } else {
          this.reportList = []
          this.pageInfo.total = 0
        }
      } catch (error) {
        console.error('Error fetching report list:', error)
        this.$message.error('Failed to fetch report list')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pageInfo.offset = 1
      this.fetchReportList()
    },
    handleAdd() {
      this.isEdit = false
      this.dialogTitle = 'Add Report'
      this.resetForm()
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = 'Edit Report'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleView(row) {
      this.selectedReport = row
      this.viewDialogVisible = true
    },
    async handleDelete(row) {
      this.$confirm('Are you sure to delete this report?', 'Warning', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await delReport([row.id])
          if (response.code === 200) {
            this.$message.success('Delete successfully')
            this.fetchReportList()
          } else {
            this.$message.error(response.msg || 'Delete failed')
          }
        } catch (error) {
          console.error('Error deleting report:', error)
          this.$message.error('Delete failed')
        }
      }).catch(() => {})
    },
    async handleSubmit() {
      this.$refs.reportForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            let response
            if (this.isEdit) {
              response = await updateReport(this.form)
            } else {
              response = await insertReport(this.form)
            }

            if (response.code === 200) {
              this.$message.success(this.isEdit ? 'Update successfully' : 'Add successfully')
              this.dialogVisible = false
              this.fetchReportList()
            } else {
              this.$message.error(response.msg || 'Operation failed')
            }
          } catch (error) {
            console.error('Error submitting form:', error)
            this.$message.error('Operation failed')
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    resetForm() {
      this.form = {
        id: null,
        birdName: '',
        species: '',
        location: '',
        latitude: '',
        longitude: '',
        injuryType: '',
        injuryDescription: '',
        status: 'PENDING',
        submitterId: '',
        notes: ''
      }
      if (this.$refs.reportForm) {
        this.$refs.reportForm.resetFields()
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
    },
    handleSizeChange(val) {
      this.pageInfo.pageSize = val
      this.pageInfo.offset = 1
      this.fetchReportList()
    },
    handleCurrentChange(val) {
      this.pageInfo.offset = val
      this.fetchReportList()
    }
  }
}
</script>

<style scoped lang="scss">
.report-management {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.search-box {
  display: flex;
  gap: 10px;
  flex: 1;
  max-width: 600px;

  .el-input {
    flex: 1;
  }
}

.search-btn,
.add-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.search-btn {
  background-color: #22b3c1;
  color: white;

  &:hover {
    background-color: #1a9aa8;
  }
}

.add-btn {
  background-color: #22b3c1;
  color: white;

  &:hover {
    background-color: #1a9aa8;
  }

  i {
    font-size: 14px;
  }
}

.table-container {
  .el-table {
    border-radius: 8px;
    overflow: hidden;
  }
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-right: 5px;

  i {
    font-size: 12px;
  }
}

.view-btn {
  background-color: #67C23A;
  color: white;

  &:hover {
    background-color: #85ce61;
  }
}

.edit-btn {
  background-color: #409EFF;
  color: white;

  &:hover {
    background-color: #66b1ff;
  }
}

.delete-btn {
  background-color: #F56C6C;
  color: white;

  &:hover {
    background-color: #f78989;
  }
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  text-align: right;
}
</style>
