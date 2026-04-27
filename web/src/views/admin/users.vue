<template>
  <div class="user-management">
    <div class="toolbar">
      <div class="search-box">
        <el-select v-model="searchRole" placeholder="Select role" clearable @change="handleSearch" style="width: 150px; margin-right: 10px;">
          <el-option label="All Roles" :value="null" />
          <el-option label="Admin" :value="1" />
          <el-option label="User" :value="0" />
          <el-option label="Rescue Station" :value="2" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="Search by name or username"
          prefix-icon="el-icon-search"
          clearable
          @clear="handleSearch"
          @keyup.enter.native="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">Search</button>
      </div>
      <button class="add-btn" @click="handleAdd">
        <i class="fas fa-plus"></i>
        Add User
      </button>
    </div>

    <div class="table-container">
      <el-table
        :data="userList"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="Username" width="150" />
        <el-table-column prop="name" label="Name" width="150" />
        <el-table-column prop="age" label="Age" width="80" />
        <el-table-column prop="phone" label="Phone" width="150" />
        <el-table-column prop="role" label="Role" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.role === 1 ? 'danger' : (scope.row.role === 2 ? 'success' : 'primary')">
              {{ scope.row.role === 1 ? 'Admin' : (scope.row.role === 2 ? 'Rescue Station' : 'User') }}
            </el-tag>
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

    <!-- Add/Edit User Dialog -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="userForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="Username" prop="username">
          <el-input v-model="form.username" placeholder="Please enter username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input v-model="form.password" type="password" placeholder="Please enter password" />
        </el-form-item>
        <el-form-item label="Name" prop="name">
          <el-input v-model="form.name" placeholder="Please enter name" />
        </el-form-item>
        <el-form-item label="Age" prop="age">
          <el-input v-model.number="form.age" type="number" placeholder="Please enter age" />
        </el-form-item>
        <el-form-item label="Phone" prop="phone">
          <el-input v-model="form.phone" placeholder="Please enter phone number" />
        </el-form-item>
        <el-form-item label="Role" prop="role">
          <el-select v-model="form.role" placeholder="Please select role">
            <el-option label="User" :value="0" />
            <el-option label="Admin" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="Avatar">
          <div v-if="currentAvatarUrl" class="current-avatar">
            <img :src="currentAvatarUrl" alt="Current Avatar">
          </div>
          <UploadImg
            ref="uploadImg"
            buttonText="Upload Avatar"
            @image-uploaded="handleAvatarUploaded"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { pageUser, insertUser, updateUser, delUser } from '@/api/user'
import UploadImg from '@/components/UploadImg/index.vue'

export default {
  components: {
    UploadImg
  },
  name: 'UserManagement',
  data() {
    return {
      userList: [],
      loading: false,
      submitLoading: false,
      searchKeyword: '',
      searchRole: null,
      dialogVisible: false,
      isEdit: false,
      dialogTitle: 'Add User',
      currentAvatarUrl: '',
      form: {
        id: null,
        username: '',
        password: '',
        name: '',
        age: '',
        phone: '',
        role: 0,
        avatarUrl: ''
      },
      rules: {
        username: [
          { required: true, message: 'Please enter username', trigger: 'blur' }
        ],
        password: [
          { required: true, message: 'Please enter password', trigger: 'blur' }
        ],
        name: [
          { required: true, message: 'Please enter name', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: 'Please enter phone number', trigger: 'blur' }
        ],
        role: [
          { required: true, message: 'Please select role', trigger: 'change' }
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
    this.fetchUserList()
  },
  methods: {
    handleAvatarUploaded(data) {
      const { file } = data;
      const formData = new FormData();
      formData.append('file', file);

      fetch('http://localhost:8080/file/upload', {
        method: 'POST',
        body: formData,
      })
      .then(response => response.json())
      .then(data => {
        if(data.code === 200){
          this.form.avatarUrl = 'http://localhost:8080/file/download?id=' + data.data.id;
          this.$message.success('Avatar uploaded successfully');
        }else{
          this.$message.error(data.msg);
        }
      })
      .catch(error => {
        this.$message.error('Avatar upload failed');
      });
    },

    async fetchUserList() {
      this.loading = true
      try {
        const response = await pageUser({
          offset: this.pageInfo.offset,
          pageSize: this.pageInfo.pageSize,
          data: {
            name: this.searchKeyword,
            role: this.searchRole
          }
        })
        if (response.data.length > 0) {
          this.userList = response.data
          this.pageInfo.total = response.totalCount
        } else {
          this.userList = []
          this.pageInfo.total = 0
        }
      } catch (error) {
        console.error('Error fetching user list:', error)
        this.$message.error('Failed to fetch user list')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pageInfo.offset = 1
      this.fetchUserList()
    },
    handleAdd() {
      this.isEdit = false
      this.dialogTitle = 'Add User'
      this.resetForm()
      this.$nextTick(() => {
        if (this.$refs.uploadImg) {
          this.$refs.uploadImg.reset()
        }
      })
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = 'Edit User'
      this.form = { ...row }
      this.currentAvatarUrl = row.avatarUrl || ''
      this.$nextTick(() => {
        if (this.$refs.uploadImg) {
          this.$refs.uploadImg.reset()
        }
      })
      this.dialogVisible = true
    },
    async handleDelete(row) {
      this.$confirm('Are you sure to delete this user?', 'Warning', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await delUser([row.id])
          if (response.code === 200) {
            this.$message.success('Delete successfully')
            this.fetchUserList()
          } else {
            this.$message.error(response.msg || 'Delete failed')
          }
        } catch (error) {
          console.error('Error deleting user:', error)
          this.$message.error('Delete failed')
        }
      }).catch(() => {})
    },
    async handleSubmit() {
      this.$refs.userForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            let response
            if (this.isEdit) {
              response = await updateUser(this.form)
            } else {
              response = await insertUser(this.form)
            }

            if (response.code === 200) {
              this.$message.success(this.isEdit ? 'Update successfully' : 'Add successfully')
              this.dialogVisible = false
              this.fetchUserList()
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
        username: '',
        password: '',
        name: '',
        age: '',
        phone: '',
        role: 0,
        avatarUrl: ''
      }
      this.currentAvatarUrl = ''
      if (this.$refs.userForm) {
        this.$refs.userForm.resetFields()
      }
    },
    handleSizeChange(val) {
      this.pageInfo.pageSize = val
      this.pageInfo.offset = 1
      this.fetchUserList()
    },
    handleCurrentChange(val) {
      this.pageInfo.offset = val
      this.fetchUserList()
    }
  }
}
</script>

<style scoped lang="scss">
.user-management {
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

.current-avatar {
  margin-bottom: 15px;
  text-align: center;
}

.current-avatar img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #22b3c1;
}
</style>
