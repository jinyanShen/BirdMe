<template>
  <div class="rescue-station-management">
    <div class="toolbar">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="Search by name or address"
          prefix-icon="el-icon-search"
          clearable
          @clear="handleSearch"
          @keyup.enter.native="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">Search</button>
      </div>
      <button class="add-btn" @click="openAddDialog">
        <i class="fas fa-plus"></i>
        Add Rescue Station
      </button>
    </div>

    <div class="table-container">
      <el-table
        :data="stations"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="Name" width="150" />
        <el-table-column prop="address" label="Address" />
        <el-table-column prop="phone" label="Phone" width="150" />
        <el-table-column prop="email" label="Email" width="200" />
        <el-table-column prop="openingHours" label="Opening Hours" width="180" />
        <el-table-column label="Actions" width="200" fixed="right">
          <template slot-scope="scope">
            <button class="action-btn edit-btn" @click="editStation(scope.row)">
              <i class="fas fa-edit"></i>
              Edit
            </button>
            <button class="action-btn delete-btn" @click="deleteStation(scope.row.id)">
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

    <!-- Add/Edit Dialog -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="stationForm" :model="stationForm" :rules="rules" label-width="120px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="stationForm.name" placeholder="Rescue station name"></el-input>
        </el-form-item>
        <el-form-item label="Address" prop="address">
          <el-input v-model="stationForm.address" placeholder="Address"></el-input>
        </el-form-item>
        <el-form-item label="Latitude" prop="latitude">
          <el-input v-model.number="stationForm.latitude" type="number" placeholder="Latitude"></el-input>
        </el-form-item>
        <el-form-item label="Longitude" prop="longitude">
          <el-input v-model.number="stationForm.longitude" type="number" placeholder="Longitude"></el-input>
        </el-form-item>
        <el-form-item label="Phone" prop="phone">
          <el-input v-model="stationForm.phone" placeholder="Phone number"></el-input>
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="stationForm.email" placeholder="Email address"></el-input>
        </el-form-item>
        <el-form-item label="Opening Hours" prop="openingHours">
          <el-input v-model="stationForm.openingHours" placeholder="Opening hours"></el-input>
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input
            v-model="stationForm.description"
            type="textarea"
            placeholder="Description"
          ></el-input>
        </el-form-item>
        <el-form-item label="Contact Person" prop="contactPerson">
          <el-input v-model="stationForm.contactPerson" placeholder="Contact person"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="saveStation" :loading="submitLoading">Save</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { allRescueStation, insertRescueStation, updateRescueStation, delRescueStation, pageRescueStation } from '@/api/rescueStation'

export default {
  data() {
    return {
      stations: [],
      loading: false,
      submitLoading: false,
      searchKeyword: '',
      dialogVisible: false,
      isEdit: false,
      dialogTitle: 'Add Rescue Station',
      stationForm: {
        id: null,
        name: '',
        address: '',
        latitude: '',
        longitude: '',
        phone: '',
        email: '',
        openingHours: '',
        description: '',
        contactPerson: ''
      },
      rules: {
        name: [
          { required: true, message: 'Please enter rescue station name', trigger: 'blur' }
        ],
        address: [
          { required: true, message: 'Please enter address', trigger: 'blur' }
        ],
        latitude: [
          { required: true, message: 'Please enter latitude', trigger: 'blur' }
        ],
        longitude: [
          { required: true, message: 'Please enter longitude', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: 'Please enter phone number', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: 'Please enter valid email address', trigger: 'blur' }
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
    this.fetchStationList()
  },
  methods: {
    async fetchStationList() {
      this.loading = true
      try {
        const response = await pageRescueStation({
          offset: this.pageInfo.offset,
          pageSize: this.pageInfo.pageSize,
          data: {
            name: this.searchKeyword,
            address: this.searchKeyword
          }
        })
        if (response.data.length > 0) {
          this.stations = response.data
          this.pageInfo.total = response.totalCount
        } else {
          this.stations = []
          this.pageInfo.total = 0
        }
      } catch (error) {
        console.error('Error fetching rescue station list:', error)
        this.$message.error('Failed to fetch rescue station list')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pageInfo.offset = 1
      this.fetchStationList()
    },
    openAddDialog() {
      this.isEdit = false
      this.dialogTitle = 'Add Rescue Station'
      this.resetForm()
      this.dialogVisible = true
    },
    editStation(station) {
      this.isEdit = true
      this.dialogTitle = 'Edit Rescue Station'
      this.stationForm = { ...station }
      this.dialogVisible = true
    },
    async saveStation() {
      this.$refs.stationForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            let response
            if (this.isEdit) {
              response = await updateRescueStation(this.stationForm)
            } else {
              response = await insertRescueStation(this.stationForm)
            }

            if (response.code === 200) {
              this.$message.success(this.isEdit ? 'Rescue station updated successfully' : 'Rescue station added successfully')
              this.dialogVisible = false
              this.fetchStationList()
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
    async deleteStation(id) {
      this.$confirm('Are you sure to delete this rescue station?', 'Warning', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await delRescueStation([id])
          if (response.code === 200) {
            this.$message.success('Delete successfully')
            this.fetchStationList()
          } else {
            this.$message.error(response.msg || 'Delete failed')
          }
        } catch (error) {
          console.error('Error deleting rescue station:', error)
          this.$message.error('Delete failed')
        }
      }).catch(() => {})
    },
    resetForm() {
      this.stationForm = {
        id: null,
        name: '',
        address: '',
        latitude: '',
        longitude: '',
        phone: '',
        email: '',
        openingHours: '',
        description: '',
        contactPerson: ''
      }
      if (this.$refs.stationForm) {
        this.$refs.stationForm.resetFields()
      }
    },
    handleSizeChange(val) {
      this.pageInfo.pageSize = val
      this.pageInfo.offset = 1
      this.fetchStationList()
    },
    handleCurrentChange(val) {
      this.pageInfo.offset = val
      this.fetchStationList()
    }
  }
}
</script>

<style scoped lang="scss">
.rescue-station-management {
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
  max-width: 400px;

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
