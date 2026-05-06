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
        <button class="back-btn" @click="handleBack">Back</button>
      </div>
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

  </div>
</template>

<script>
import { pageRescueStation } from '@/api/rescueStation'

export default {
  data() {
    return {
      stations: [],
      loading: false,
      searchKeyword: '',
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
    handleSizeChange(val) {
      this.pageInfo.pageSize = val
      this.pageInfo.offset = 1
      this.fetchStationList()
    },
    handleCurrentChange(val) {
      this.pageInfo.offset = val
      this.fetchStationList()
    },
    handleBack() {
      this.$router.push('/')
    },
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
.back-btn {
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

.back-btn {
  background-color: #c12254;
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
