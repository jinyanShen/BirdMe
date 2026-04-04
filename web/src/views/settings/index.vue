<template>
  <div class="profile-page">
    <!-- 顶部导航栏（与 Knowledge 页面一致） -->
    <div class="top-navbar">
      <div class="navbar-container">
        <div class="logo">
          <h3 @click="goToHome" style="cursor: pointer;">BirdME</h3>
        </div>
        <div class="nav-menu">
          <span class="nav-item" @click="goToHome">Homepage</span>
          <span class="nav-item" @click="goToKnowledge">Knowledge</span>
          <span class="nav-item" @click="goToRescue">Rescue</span>
          <span class="nav-item" @click="goToForum">Forum</span>
          <div class="nav-item game-dropdown" @click="goToGame">
            Game
            <div class="game-dropdown-menu">
              <div class="game-dropdown-item" @click.stop="selectGameFromNav('flappy')">Flappy Bird</div>
              <div class="game-dropdown-item" @click.stop="selectGameFromNav('2048')">2048 Bird</div>
              <div class="game-dropdown-item" @click.stop="selectGameFromNav('merge')">Merge To Giant Bird</div>
            </div>
          </div>


          <span class="nav-item active" @click="goToPersonalPage">Personal Setting</span>
          <span v-if="!isLoggedIn" class="nav-item login-btn" @click="goToLogin">Login</span>
          <span v-else class="nav-item logout-btn" @click="handleLogout">LogOut</span>
        </div>
      </div>
    </div>

    <div class="container">
      <!-- Page Title -->
      <div class="section-title">
        <h1>Personal Settings</h1>
        <p>Manage your personal information & rescue reports</p>
      </div>

      <!-- Personal Information Card -->
      <div class="profile-card">
        <div class="card-header">
          <h2>Personal Information</h2>
        </div>
        <div class="card-body">
          <el-form ref="form" :model="form" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="Username" prop="username">
                  <el-input v-model="form.username" class="custom-input" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Password" prop="password">
                  <el-input
                    v-model="form.password"
                    :type="passwordFieldType"
                    class="custom-input"
                  >
                    <template #suffix>
                      <i
                        :class="passwordFieldType === 'password' ? 'fas fa-eye' : 'fas fa-eye-slash'"
                        style="cursor: pointer; color: #999;"
                        @click="togglePassword"
                      ></i>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="Name" prop="name">
                  <el-input v-model="form.name" class="custom-input" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Age" prop="age">
                  <el-input v-model="form.age" class="custom-input" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="Contact" prop="phone">
                  <el-input v-model="form.phone" class="custom-input" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="User Avatar" prop="avatarUrl">
                  <div class="avatar-upload">
                    <el-upload
                      drag
                      action="http://localhost:8080/file/upload"
                      :on-success="handleUploadSuccess"
                      :on-error="handleUploadError"
                      list-type="picture"
                      :limit="1"
                      :file-list="pictureList"
                      :on-exceed="handleExceed"
                      accept=".png, .jpg, .jpeg"
                      class="avatar-uploader"
                    >
                      <div class="upload-content">
                        <i class="fas fa-cloud-upload-alt upload-icon"></i>
                        <div class="upload-text">Drag file here to upload</div>
                      </div>
                    </el-upload>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <div class="form-footer">
            <el-button type="primary" @click="updateProfile" class="submit-btn">Confirm</el-button>
          </div>
        </div>
      </div>

      <!-- My Reports Section -->
      <div class="reports-section">
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
  </div>
</template>

<script>
import { updateUser } from "@/api/user";
import { getReportsBySubmitter } from '@/api/report'
import ReportDetail from '@/components/ReportDetail/index.vue'

export default {
  components: {
    ReportDetail
  },
  data() {
    return {
      isLoggedIn: false,
      form: {
        id: parseInt(sessionStorage.getItem("id")),
        username: sessionStorage.getItem("username"),
        name: sessionStorage.getItem("name"),
        password: sessionStorage.getItem("password"),
        age: sessionStorage.getItem("age"),
        phone: sessionStorage.getItem("phone"),
        avatarUrl: sessionStorage.getItem("avatarUrl"),
      },
      pictureList: [],
      passwordFieldType: 'password',
      reports: [],
      loading: false,
      dialogVisible: false,
      selectedReport: null
    };
  },

  mounted() {
    this.checkLoginStatus();
    this.loadReports();
  },

  methods: {
    // 检查登录状态
    checkLoginStatus() {
      this.isLoggedIn = sessionStorage.getItem('id') !== null;
    },

    // 登出
    handleLogout() {
      sessionStorage.removeItem('id');
      sessionStorage.removeItem('username');
      sessionStorage.removeItem('password');
      sessionStorage.removeItem('name');
      sessionStorage.removeItem('age');
      sessionStorage.removeItem('phone');
      sessionStorage.removeItem('avatarUrl');
      sessionStorage.removeItem('role');

      this.isLoggedIn = false;
      this.$message.success('Logged out successfully');
      this.$router.push('/');
    },

    // 导航方法
    goToHome() {
      if (this.$route.path === '/') return;
      this.$router.push('/');
    },

    goToKnowledge() {
      if (this.isLoggedIn) {
        this.$router.push('/knowledge');
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/knowledge');
        }
      }
    },

    goToRescue() {
      if (this.isLoggedIn) {
        this.$router.push('/rescue');
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/rescue');
        }
      }
    },

    goToForum() {
      if (this.isLoggedIn) {
        this.$router.push('/forum');
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/forum');
        }
      }
    },

    goToGame() {
      if (this.isLoggedIn) {
        this.$router.push('/game');
      } else {
        if (window.$showLoginDialog) {
          window.$showLoginDialog('/game');
        }
      }
    },

    goToPersonalPage() {
      // 已在当前页面
      if (this.$route.path === '/settings/index') return;
      this.$router.push('/settings/index');
    },

    goToLogin() {
      if (window.$loginDialog) {
        window.$loginDialog.show('/');
      }
    },

    // Profile methods
    togglePassword() {
      this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
    },
    handleUploadSuccess(response, file) {
      this.form.avatarUrl = "http://localhost:8080/file/download?id=" + response.data.id;
      this.pictureList.push(response.data);
      this.$message.success('File uploaded successfully');
    },
    handleUploadError(err, file) {
      this.$message.error('File upload failed');
    },
    handleExceed(){
      this.$message.error('Only one file can be uploaded');
    },
    updateProfile() {
      updateUser(this.form).then(res => {
        if (res.code === 200) {
          sessionStorage.setItem("id", this.form.id);
          sessionStorage.setItem("username", this.form.username);
          sessionStorage.setItem("password", this.form.password);
          sessionStorage.setItem("name", this.form.name);
          sessionStorage.setItem("age", this.form.age);
          sessionStorage.setItem("phone", this.form.phone);
          sessionStorage.setItem("avatarUrl", this.form.avatarUrl);
          this.$message.success("Update successful");
        }
      });
    },

    // Report methods
    loadReports() {
      this.loading = true;
      getReportsBySubmitter("user").then(response => {
        if (response.code === 200) {
          this.reports = response.data || [];
        } else {
          this.$message.error('Failed to load reports');
        }
        this.loading = false;
      }).catch(error => {
        console.error('Error loading reports:', error);
        this.$message.error('Failed to load reports');
        this.loading = false;
      });
    },
    viewDetails(report) {
      this.selectedReport = { ...report };
      this.dialogVisible = true;
    },
    getStatusType(status) {
      switch (status) {
        case 'PENDING': return 'warning';
        case 'PROCESSING': return 'info';
        case 'COMPLETED': return 'success';
        case 'CANCELLED': return 'danger';
        default: return 'info';
      }
    },
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const date = new Date(dateString);
      return date.toLocaleString();
    },
    selectGameFromNav(gameKey) {
      const targetPath = `/game/${gameKey}`
      if (this.$route.path === targetPath) return

      this.$router.push(targetPath)
    },

    selectGame(gameKey) {
      const targetPath = `/game/${gameKey}`
      if (this.$route.path === targetPath) return

      this.$router.push(targetPath)
    }
  },

  watch: {
    '$route'() {
      this.checkLoginStatus();
    }
  }
};
</script>

<style scoped>
/* Game 顶部下拉菜单（hover 展开，不依赖点击） */
.game-dropdown {
  position: relative;
  display: inline-flex;
  align-items: center;

  .game-dropdown-menu {
    position: absolute;
    top: 100%;
    left: 50%;
    transform: translateX(-50%) translateY(0);
    opacity: 0;
    visibility: hidden;
    transition: opacity 0.15s ease, transform 0.15s ease, visibility 0.15s ease;
    z-index: 1001;

    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 12px;
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
    min-width: 200px;
    padding: 8px 0;

    display: flex;
    flex-direction: column;
    align-items: stretch;

    .game-dropdown-item {
      padding: 10px 20px;
      font-size: 14px;
      color: #333;
      cursor: pointer;
      transition: background 0.2s ease, color 0.2s ease;
      &:hover {
        background: #f2f3ff;
        color: #22b3c1;
      }
    }
  }

  &:hover {
    .game-dropdown-menu {
      opacity: 1;
      visibility: visible;
      transform: translateX(-50%) translateY(0);
    }
  }
}
/* Profile Page Styles */
.profile-page {
  padding-top: 70px;
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  min-height: 100vh;
}

/* 顶部导航栏样式（与 Knowledge 页面一致） */
.top-navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo h3 {
  margin: 0;
  font-size: 24px;
  color: #22b3c1;
  font-weight: bold;
  cursor: pointer;
  transition: color 0.3s;
}

.logo h3:hover {
  color: #1a9aa8;
}

.nav-menu {
  display: flex;
  gap: 30px;
  align-items: center;
}

.nav-item {
  text-decoration: none;
  color: #333;
  font-size: 16px;
  transition: color 0.3s;
  cursor: pointer;
  padding: 8px 0;
  position: relative;
}

.nav-item:hover {
  color: #22b3c1;
}

.nav-item.active {
  color: #22b3c1;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #22b3c1;
  animation: slideIn 0.3s ease-out;
}

.login-btn {
  background: #22b3c1;
  color: white !important;
  padding: 8px 20px;
  border-radius: 25px;
  transition: all 0.3s;
}

.login-btn:hover {
  background: #1a9aa8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 179, 193, 0.3);
  color: white !important;
}

.logout-btn {
  background: #ff6b6b;
  color: white !important;
  padding: 8px 20px;
  border-radius: 25px;
  transition: all 0.3s;
  cursor: pointer;
}

.logout-btn:hover {
  background: #ff5252;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
  color: white !important;
}

@keyframes slideIn {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: 100%;
    opacity: 1;
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  text-align: center;
  margin-bottom: 50px;
}

.section-title h1 {
  font-size: 36px;
  color: #22b3c1;
  margin-bottom: 15px;
  font-weight: 700;
}

.section-title p {
  font-size: 18px;
  color: #666;
  margin: 0;
}

.profile-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  overflow: hidden;
  margin-bottom: 40px;
}

.profile-card:hover {
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.card-header {
  background: linear-gradient(135deg, #4dd0e1 0%, #22b3c1 100%);
  color: white;
  padding: 20px 30px;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.card-body {
  padding: 30px;
}

.custom-input {
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s ease;
}

.custom-input:focus {
  border-color: #4dd0e1;
  box-shadow: 0 0 0 2px rgba(77, 208, 225, 0.2);
}

.avatar-upload {
  margin-top: 10px;
}

.avatar-uploader {
  border: 2px dashed #b2ebf2;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.avatar-uploader:hover {
  border-color: #4dd0e1;
  background-color: #e0f7fa;
}

.upload-content {
  text-align: center;
  padding: 40px 20px;
}

.upload-icon {
  font-size: 40px;
  color: #4dd0e1;
  margin-bottom: 15px;
}

.upload-text {
  color: #666;
  font-size: 16px;
}

.form-footer {
  margin-top: 30px;
  text-align: center;
}

.submit-btn {
  background: linear-gradient(135deg, #4dd0e1 0%, #22b3c1 100%);
  border: none;
  padding: 12px 40px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 50px;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(77, 208, 225, 0.4);
  background: linear-gradient(135deg, #22b3c1 0%, #0097a7 100%);
}

/* Reports Section Styles */
.reports-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
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

/* Responsive */
@media (max-width: 768px) {
  .profile-page { padding-top: 60px; }
  .section-title h1 { font-size: 28px; }
  .card-body { padding: 20px; }
  .reports-list { grid-template-columns: 1fr; }
  .page-header h2 { font-size: 26px; }

  .navbar-container {
    padding: 10px 15px;
  }
  .logo h3 {
    font-size: 20px;
  }
  .nav-menu {
    gap: 15px;
  }
  .nav-item {
    font-size: 14px;
  }
  .login-btn,
  .logout-btn {
    padding: 6px 15px;
  }
}
</style>
