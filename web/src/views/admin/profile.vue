<template>
  <div class="profile-management">
    <div class="profile-card">
      <div class="profile-header">
        <h2>Personal Information</h2>
        <p>Update your personal details and preferences</p>
      </div>

      <div class="profile-content">
        <div class="avatar-section">
          <div class="current-avatar">
            <img :src="userInfo.avatarUrl || defaultAvatar" alt="User Avatar">
          </div>
          <UploadImg
            ref="uploadImg"
            buttonText="Change Avatar"
            @image-uploaded="handleAvatarUploaded"
          />
        </div>

        <el-form ref="profileForm" :model="userInfo" :rules="rules" label-width="120px">
          <el-form-item label="Username" prop="username">
            <el-input v-model="userInfo.username" placeholder="Username" disabled />
          </el-form-item>
          <el-form-item label="Name" prop="name">
            <el-input v-model="userInfo.name" placeholder="Full name" />
          </el-form-item>
          <el-form-item label="Age" prop="age">
            <el-input v-model.number="userInfo.age" type="number" placeholder="Age" />
          </el-form-item>
          <el-form-item label="Phone" prop="phone">
            <el-input v-model="userInfo.phone" placeholder="Phone number" />
          </el-form-item>
          <el-form-item label="Password" prop="password">
            <el-input v-model="userInfo.password" type="password" placeholder="Leave blank to keep current password" />
          </el-form-item>
          <el-form-item label="Role" prop="role">
            <el-select v-model="userInfo.role" placeholder="Role" disabled>
              <el-option label="User" :value="0" />
              <el-option label="Admin" :value="1" />
            </el-select>
          </el-form-item>
        </el-form>

        <div class="action-buttons">
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">Save Changes</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { updateUser, getCurrentUser } from '@/api/user'
import UploadImg from '@/components/UploadImg/index.vue'

export default {
  components: {
    UploadImg
  },
  name: 'ProfileManagement',
  data() {
    return {
      userInfo: {
        id: null,
        username: '',
        name: '',
        age: '',
        phone: '',
        role: 0,
        avatarUrl: '',
        password: ''
      },
      originalUserInfo: {},
      defaultAvatar: '#',
      loading: false,
      submitLoading: false,
      rules: {
        name: [
          { required: true, message: 'Please enter name', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: 'Please enter phone number', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.fetchUserInfo()
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
          this.userInfo.avatarUrl = 'http://localhost:8080/file/download?id=' + data.data.id;
          this.$message.success('Avatar uploaded successfully');
        }else{
          this.$message.error(data.msg);
        }
      })
      .catch(error => {
        this.$message.error('Avatar upload failed');
      });
    },

    async fetchUserInfo() {
      this.loading = true
      try {
        // 获取当前用户信息
        const userId = sessionStorage.getItem('id')
        const response = await getCurrentUser(userId)
        if (response.code === 200) {
          this.userInfo = response.data
          this.originalUserInfo = { ...response.data }
        } else {
          this.$message.error(response.msg || 'Failed to fetch user information')
        }
      } catch (error) {
        console.error('Error fetching user info:', error)
        this.$message.error('Failed to fetch user information')
      } finally {
        this.loading = false
      }
    },

    async handleSubmit() {
      this.$refs.profileForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            // 移除密码字段如果为空
            const updateData = { ...this.userInfo }
            if (!updateData.password) {
              delete updateData.password
            }

            const response = await updateUser(updateData)
            if (response.code === 200) {
              this.$message.success('Profile updated successfully')
              this.originalUserInfo = { ...this.userInfo }
              // 更新sessionStorage中的头像
              if (this.userInfo.avatarUrl) {
                sessionStorage.setItem('avatarUrl', this.userInfo.avatarUrl)
              }
            } else {
              this.$message.error(response.msg || 'Update failed')
            }
          } catch (error) {
            console.error('Error updating profile:', error)
            this.$message.error('Update failed')
          } finally {
            this.submitLoading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.profile-management {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100%;
}

.profile-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 600px;
}

.profile-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;

  h2 {
    margin: 0 0 10px 0;
    font-size: 24px;
    font-weight: 600;
    color: #333;
  }

  p {
    margin: 0;
    color: #666;
    font-size: 14px;
  }
}

.profile-content {
  .avatar-section {
    text-align: center;
    margin-bottom: 30px;

    .current-avatar {
      margin-bottom: 20px;

      img {
        width: 120px;
        height: 120px;
        border-radius: 50%;
        object-fit: cover;
        border: 3px solid #22b3c1;
      }
    }
  }

  .action-buttons {
    margin-top: 30px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}
</style>
