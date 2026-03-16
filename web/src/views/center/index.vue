<template>
  <div class="profile-page">
    <div class="container">
      <!-- Page Title -->
      <div class="section-title">
        <h1>Personal Center</h1>
        <p>Manage your personal information</p>
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
                  <el-input v-model="form.password" class="custom-input" />
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
    </div>
  </div>
</template>

<script>
import { pageUser, getUser, delUser, insertUser, updateUser } from "@/api/user";

export default {
  data() {
    return {
      form: {
        id: parseInt(sessionStorage.getItem("id")),
        username: sessionStorage.getItem("username"),
        name: sessionStorage.getItem("name"),
        password: sessionStorage.getItem("password"),
        age: sessionStorage.getItem("age"),
        phone: sessionStorage.getItem("phone"),
        avatarUrl: sessionStorage.getItem("avatarUrl"),
      },
      pictureList: []
    };
  },

  methods: {
    handleUploadSuccess(response, file) {
      this.form.avatarUrl = "http://localhost:8080/file/download?id=" + response.data.id
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
  },
};
</script>

<style scoped>
.profile-page {
  padding: 60px 0;
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
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

/* Responsive Design */
@media (max-width: 768px) {
  .profile-page {
    padding: 40px 0;
  }
  
  .section-title h1 {
    font-size: 28px;
  }
  
  .card-body {
    padding: 20px;
  }
  
  .card-header {
    padding: 15px 20px;
  }
  
  .card-header h2 {
    font-size: 18px;
  }
  
  .el-form-item {
    margin-bottom: 15px;
  }
  
  .submit-btn {
    padding: 10px 30px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .section-title h1 {
    font-size: 24px;
  }
  
  .card-body {
    padding: 15px;
  }
  
  .el-form-item {
    margin-bottom: 10px;
  }
  
  .upload-content {
    padding: 30px 15px;
  }
  
  .upload-icon {
    font-size: 30px;
  }
  
  .upload-text {
    font-size: 14px;
  }
}
</style>
