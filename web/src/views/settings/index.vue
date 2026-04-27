<template>
  <div class="profile-page">
    <NavBar :showNavbar="true" />
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
import NavBar from '@/components/NavBar/navbar.vue'
export default {
  components: {
    ReportDetail,
    NavBar
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
    checkLoginStatus() {
      this.isLoggedIn = sessionStorage.getItem('id') !== null;
    },

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
/* Profile Page Styles */
.profile-page {
  padding-top: 100px;
  background: linear-gradient(135deg, #e0effa 0%, #3bacff 100%);
  min-height: 100vh;
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
  color: #227fc1;
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
  background: linear-gradient(135deg, #4dade1 0%, #226cc1 100%);
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
  border-color: #4da3e1;
  box-shadow: 0 0 0 2px rgba(77, 208, 225, 0.2);
}

.avatar-upload {
  margin-top: 10px;
}

.avatar-uploader {
  border: 2px dashed #b2ccf2;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.avatar-uploader:hover {
  border-color: #4d92e1;
  background-color: #e0f7fa;
}

.upload-content {
  text-align: center;
  padding: 40px 20px;
}

.upload-icon {
  font-size: 40px;
  color: #4d9ee1;
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
  background: linear-gradient(135deg, #4d9ee1 0%, #2297c1 100%);
  border: none;
  padding: 12px 40px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 50px;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(77, 158, 225, 0.4);
  background: linear-gradient(135deg, #227fc1 0%, #0067a7 100%);
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
  color: #226cc1;
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
  color: #2284c1;
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
  color: #2272c1;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.description {
  margin-bottom: 15px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #183c80;
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
  color: #2289c1;
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
