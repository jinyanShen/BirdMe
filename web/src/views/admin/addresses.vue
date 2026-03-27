<template>
  <div class="address-management">
    <div class="toolbar">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="Search by recipient name or phone"
          prefix-icon="el-icon-search"
          clearable
          @clear="handleSearch"
          @keyup.enter.native="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">Search</button>
      </div>
      <button class="add-btn" @click="handleAdd">
        <i class="fas fa-plus"></i>
        Add Address
      </button>
    </div>

    <div class="table-container">
      <el-table
        :data="addressList"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="User ID" width="100" />
        <el-table-column prop="recipientName" label="Recipient Name" width="150" />
        <el-table-column prop="recipientPhone" label="Recipient Phone" width="150" />
        <el-table-column label="Full Address" min-width="250">
          <template slot-scope="scope">
            {{ scope.row.province }} {{ scope.row.city }} {{ scope.row.district }} {{ scope.row.detailAddress }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Created At" width="180" />
        <el-table-column label="Operations" width="200" fixed="right">
          <template slot-scope="scope">
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

    <!-- Add/Edit Address Dialog -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="1200px"
      height="800px"
      :close-on-click-modal="false"
    >
      <div class="dialog-content">
        <div class="form-section">
          <el-form ref="addressForm" :model="form" :rules="rules" label-width="120px">
            <el-form-item label="User ID" prop="userId">
              <el-input v-model.number="form.userId" type="number" placeholder="Please enter user ID" />
            </el-form-item>
            <el-form-item label="Recipient Name" prop="recipientName">
              <el-input v-model="form.recipientName" placeholder="Please enter recipient name" />
            </el-form-item>
            <el-form-item label="Recipient Phone" prop="recipientPhone">
              <el-input v-model="form.recipientPhone" placeholder="Please enter recipient phone" />
            </el-form-item>
            <el-form-item label="Province" prop="province">
              <el-input v-model="form.province" placeholder="Please enter province" />
            </el-form-item>
            <el-form-item label="City" prop="city">
              <el-input v-model="form.city" placeholder="Please enter city" />
            </el-form-item>
            <el-form-item label="District" prop="district">
              <el-input v-model="form.district" placeholder="Please enter district" />
            </el-form-item>
            <el-form-item label="Detail Address" prop="detailAddress">
              <el-input
                v-model="form.detailAddress"
                type="textarea"
                :rows="3"
                placeholder="Please enter detailed address"
              />
            </el-form-item>
            <el-form-item label="Is Default" prop="isDefault">
              <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-form>
        </div>
        <div class="map-section">
          <div class="map-header">
            <h4>Select Address on Map</h4>
          </div>
          <div id="mapContainer" class="map-container"></div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'AddressManagement',
  data() {
    return {
      addressList: [],
      loading: false,
      submitLoading: false,
      searchKeyword: '',
      dialogVisible: false,
      isEdit: false,
      dialogTitle: 'Add Address',
      form: {
        id: null,
        userId: '',
        recipientName: '',
        recipientPhone: '',
        province: '',
        city: '',
        district: '',
        detailAddress: '',
        isDefault: 0
      },
      rules: {
        userId: [
          { required: true, message: 'Please enter user ID', trigger: 'blur' }
        ],
        recipientName: [
          { required: true, message: 'Please enter recipient name', trigger: 'blur' }
        ],
        recipientPhone: [
          { required: true, message: 'Please enter recipient phone', trigger: 'blur' }
        ],
        province: [
          { required: true, message: 'Please enter province', trigger: 'blur' }
        ],
        city: [
          { required: true, message: 'Please enter city', trigger: 'blur' }
        ],
        district: [
          { required: true, message: 'Please enter district', trigger: 'blur' }
        ],
        detailAddress: [
          { required: true, message: 'Please enter detailed address', trigger: 'blur' }
        ]
      },
      pageInfo: {
        offset: 1,
        pageSize: 10,
        total: 0
      },
      map: null,
      marker: null,
      bingMapKey: 'YOUR_BING_MAPS_KEY', // Replace with your Bing Maps key
      searchManager: null
    }
  },
  mounted() {
    this.fetchAddressList()
  },
  watch: {
    dialogVisible: {
      handler(newVal) {
        if (newVal) {
          this.$nextTick(() => {
            this.initMap()
          })
        } else {
          this.destroyMap()
        }
      },
      immediate: false
    }
  },
  methods: {
    async fetchAddressList() {
      this.loading = true
      try {
        const response = await request({
          url: '/address/page',
          method: 'post',
          data: {
            offset: this.pageInfo.offset,
            pageSize: this.pageInfo.pageSize,
            data: {
              recipientName: this.searchKeyword
            }
          }
        })
        if (response.data.length > 0) {
          this.addressList = response.data
          this.pageInfo.total = response.totalCount
        } else {
          this.$message.error(response.msg || 'Failed to fetch address list')
        }
      } catch (error) {
        console.error('Error fetching address list:', error)
        this.$message.error('Failed to fetch address list')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pageInfo.offset = 1
      this.fetchAddressList()
    },
    handleAdd() {
      this.isEdit = false
      this.dialogTitle = 'Add Address'
      this.resetForm()
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = 'Edit Address'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      this.$confirm('Are you sure to delete this address?', 'Warning', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await request({
            url: '/address/delete',
            method: 'post',
            data: [row.id]
          })
          if (response.code === 200) {
            this.$message.success('Delete successfully')
            this.fetchAddressList()
          } else {
            this.$message.error(response.msg || 'Delete failed')
          }
        } catch (error) {
          console.error('Error deleting address:', error)
          this.$message.error('Delete failed')
        }
      }).catch(() => {})
    },

    async handleSubmit() {
      this.$refs.addressForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            let response
            if (this.isEdit) {
              response = await request({
                url: '/address/update',
                method: 'post',
                data: this.form
              })
            } else {
              response = await request({
                url: '/address/insert',
                method: 'post',
                data: this.form
              })
            }

            if (response.code === 200) {
              this.$message.success(this.isEdit ? 'Update successfully' : 'Add successfully')
              this.dialogVisible = false
              this.fetchAddressList()
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
        userId: '',
        recipientName: '',
        recipientPhone: '',
        province: '',
        city: '',
        district: '',
        detailAddress: '',
        isDefault: 0
      }
      if (this.$refs.addressForm) {
        this.$refs.addressForm.resetFields()
      }
    },
    handleSizeChange(val) {
      this.pageInfo.pageSize = val
      this.pageInfo.offset = 1
      this.fetchAddressList()
    },
    handleCurrentChange(val) {
      this.pageInfo.offset = val
      this.fetchAddressList()
    },
    initMap() {
      // Check if Bing Maps is loaded
      if (typeof Microsoft === 'undefined' || typeof Microsoft.Maps === 'undefined') {
        // Load Bing Maps script dynamically if not loaded
        const script = document.createElement('script')
        script.src = `https://www.bing.com/api/maps/mapcontrol?callback=initBingMap&key=${this.bingMapKey}`
        script.async = true
        script.defer = true
        
        // Define callback function globally
        window.initBingMap = () => {
          this.initializeMap()
        }
        
        script.onerror = () => {
          console.error('Failed to load Bing Maps API')
          this.$message.error('Failed to load map. Please try again.')
        }
        document.head.appendChild(script)
      } else {
        // Bing Maps is already loaded, initialize map
        this.initializeMap()
      }
    },
    initializeMap() {
      try {
        // Initialize Bing Maps
        this.map = new Microsoft.Maps.Map('#mapContainer', {
          credentials: this.bingMapKey,
          center: new Microsoft.Maps.Location(39.90923, 116.397428), // Default to Beijing
          zoom: 11,
          mapTypeId: Microsoft.Maps.MapTypeId.aerial
        })

        // Add click event listener
        Microsoft.Maps.Events.addHandler(this.map, 'click', (e) => {
          if (e.location) {
            const lat = e.location.latitude
            const lng = e.location.longitude

            // Update marker position
            this.updateMarker(lat, lng)

            // Geocode reverse lookup
            this.reverseGeocode(lat, lng)
          }
        })
      } catch (error) {
        console.error('Error initializing map:', error)
        this.$message.error('Failed to initialize map. Please try again.')
      }
    },
    updateMarker(lat, lng) {
      if (!this.map || typeof Microsoft === 'undefined') {
        console.error('Map is not initialized')
        return
      }

      // Remove existing marker
      if (this.marker) {
        this.map.entities.remove(this.marker)
      }

      // Create new marker
      this.marker = new Microsoft.Maps.Pushpin(new Microsoft.Maps.Location(lat, lng), {
        draggable: true
      })

      // Add drag event listener
      Microsoft.Maps.Events.addHandler(this.marker, 'dragend', (e) => {
        const location = e.target.getLocation()
        this.reverseGeocode(location.latitude, location.longitude)
      })

      // Add marker to map
      this.map.entities.push(this.marker)
      
      // Center map on marker
      this.map.setView({ center: new Microsoft.Maps.Location(lat, lng) })
    },
    reverseGeocode(lat, lng) {
      if (typeof Microsoft === 'undefined' || !this.map) {
        console.error('Bing Maps is not loaded')
        return
      }

      // Use Bing Maps REST API for reverse geocoding
      const requestUrl = `https://dev.virtualearth.net/REST/v1/Locations/${lat},${lng}?key=${this.bingMapKey}`
      
      fetch(requestUrl)
        .then(response => response.json())
        .then(data => {
          if (data.resourceSets && data.resourceSets.length > 0 && 
              data.resourceSets[0].resources && data.resourceSets[0].resources.length > 0) {
            const address = data.resourceSets[0].resources[0].address
            
            // Parse address components
            this.form.province = address.adminDistrict || ''
            this.form.city = address.locality || ''
            this.form.district = address.adminDistrict2 || ''
            this.form.detailAddress = address.formattedAddress || ''
          }
        })
        .catch(error => {
          console.error('Error reverse geocoding:', error)
        })
    },
    destroyMap() {
      if (this.map) {
        this.map.dispose()
        this.map = null
        this.marker = null
      }
    }
  }
}
</script>

<style scoped lang="scss">
.address-management {
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
  max-width: 450px;

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

.dialog-content {
  display: flex;
  gap: 20px;
  height: 600px;

  .form-section {
    flex: 0 0 350px;
  }

  .map-section {
    flex: 1;
    min-width: 600px;

    .map-header {
      margin-bottom: 10px;
      h4 {
        margin: 0;
        font-size: 16px;
        font-weight: 500;
        color: #333;
      }
    }

    .map-container {
      width: 100%;
      height: 550px;
      border: 1px solid #eaeaea;
      border-radius: 4px;
    }
  }
}

@media (max-width: 1200px) {
  .dialog-content {
    flex-direction: column;
    height: auto;

    .form-section {
      flex: 1;
    }

    .map-section {
      min-width: auto;

      .map-container {
        height: 400px;
      }
    }
  }
}
</style>
