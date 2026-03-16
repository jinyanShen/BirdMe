<template>
  <div class="login-page">
    <div class="login-wrapper">
      <!-- 左侧品牌区域 -->
      <div class="login-left">
        <div class="brand-section">
          <h1 class="brand-name">BIRDME</h1>
          <p class="brand-slogan">Intelligent Bird Identification System</p>
          <div class="brand-image">
            <img src="@/assets/images/bg.jpg" alt="Bird">
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-right">
        <div class="login-card">
          <div class="login-header">
            <h2>Welcome Back</h2>
            <p>Please log in to your account</p>
          </div>

          <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" autocomplete="on">
            <div class="form-group">
              <label>Username</label>
              <div class="input-wrapper">
                <i class="input-icon">👤</i>
                <el-input
                  ref="username"
                  v-model="loginForm.username"
                  placeholder="Please enter username"
                  name="username"
                  type="text"
                  clearable
                />
              </div>
            </div>

            <div class="form-group">
              <label>Password</label>
              <div class="input-wrapper">
                <i class="input-icon">🔒</i>
                <el-input
                  ref="password"
                  v-model="loginForm.password"
                  placeholder="Please enter password"
                  name="password"
                  type="password"
                  clearable
                  @keyup.enter.native="handleLogin"
                />
              </div>
            </div>

            <button
              :loading="loading"
              class="login-btn main-button w-100"
              @click.prevent="handleLogin"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner"></span>
              <span v-else>Log In</span>
            </button>

            <div class="divider">
              <span>Or</span>
            </div>

            <button class="register-btn border-button w-100" @click.prevent="register">
              Create New Account
            </button>
          </el-form>
        </div>
      </div>
    </div>

    <!-- Register Dialog -->
    <el-dialog title="Create New Account" :visible.sync="open" width="600px" append-to-body custom-class="register-dialog">
      <div class="register-form">
        <div class="form-row">
          <div class="form-group half">
            <label>Username</label>
            <el-input v-model="form.username" placeholder="Please enter username" />
          </div>
          <div class="form-group half">
            <label>Password</label>
            <el-input v-model="form.password" type="password" placeholder="Please enter password" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group half">
            <label>Name</label>
            <el-input v-model="form.name" placeholder="Please enter name" />
          </div>
          <div class="form-group half">
            <label>Age</label>
            <el-input v-model="form.age" type="number" placeholder="Please enter age" />
          </div>
        </div>
        <div class="form-group">
          <label>Contact</label>
          <el-input v-model="form.phone" placeholder="Please enter contact information" />
        </div>
        <div class="form-group">
          <label>User Avatar</label>
          <div class="upload-area" :class="{ 'drag-over': isDragOver }"
               @dragover.prevent="isDragOver = true"
               @dragleave.prevent="isDragOver = false"
               @drop.prevent="handleDrop">
            <div v-if="!form.avatarUrl" class="upload-placeholder">
              <i class="upload-icon">📷</i>
              <p>Drag image here or click to upload</p>
              <input type="file" @change="handleImageUpload" accept="image/*" ref="fileInput" style="display: none;">
              <button class="border-button" @click="$refs.fileInput.click()">Select Image</button>
            </div>
            <div v-else class="image-preview">
              <img :src="form.avatarUrl" alt="Avatar">
              <button class="remove-btn" @click="removeAvatar">
                <i>×</i>
              </button>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <button class="main-button" @click="doRegister">Confirm</button>
        <button class="border-button" @click="cancel">Cancel</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { login, register } from '@/api/login'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
      },
      loginRules: {
        username: [{ required: true, message: 'Please enter username', trigger: 'blur' }],
        password: [{ required: true, message: 'Please enter password', trigger: 'blur' }]
      },
      loading: false,
      rememberMe: false,
      open: false,
      form: {
        username: '',
        password: '',
        name: '',
        age: '',
        phone: '',
        avatarUrl: ''
      },
      isDragOver: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          login(this.loginForm.username, this.loginForm.password)
            .then(res => {
              if (res.code === 200) {
                sessionStorage.setItem('id', res.data.id)
                sessionStorage.setItem('username', res.data.username)
                sessionStorage.setItem('password', res.data.password)
                sessionStorage.setItem('name', res.data.name)
                sessionStorage.setItem('age', res.data.age)
                sessionStorage.setItem('phone', res.data.phone)
                sessionStorage.setItem('avatarUrl', res.data.avatarUrl)
                sessionStorage.setItem('role', res.data.role)
                this.$message.success("Login successful")
                this.$router.push({ path: '/identification' })
              } else {
                this.loading = false
                this.$message.error(res.msg)
              }
            })
            .catch(() => {
              this.loading = false
            })
        } else {
          return false
        }
      })
    },
    cancel() {
      this.open = false
      this.form = {}
    },
    register() {
      this.open = true
      this.form = {
        username: '',
        password: '',
        name: '',
        age: '',
        phone: '',
        avatarUrl: ''
      }
    },
    doRegister() {
      if (!this.form.username || !this.form.password) {
        this.$message.error('Username and password cannot be empty')
        return
      }
      register(this.form).then(res => {
        if (res.code === 200) {
          this.open = false
          this.$message.success("Registration successful")
        } else {
          this.$message.error(res.msg || 'Registration failed')
        }
      }).catch(() => {
        this.$message.error('Registration failed, please try again later')
      })
    },
    handleImageUpload(event) {
      const file = event.target.files[0]
      if (file) {
        this.uploadFile(file)
      }
    },
    handleDrop(event) {
      this.isDragOver = false
      const file = event.dataTransfer.files[0]
      if (file && file.type.startsWith('image/')) {
        this.uploadFile(file)
      } else {
        this.$message.error('Please upload image file')
      }
    },
    uploadFile(file) {
      const formData = new FormData()
      formData.append('file', file)

      fetch('http://localhost:8080/file/upload', {
        method: 'POST',
        body: formData,
      })
        .then(response => response.json())
        .then(data => {
          if (data.code === 200) {
            this.form.avatarUrl = "http://localhost:8080/file/download?id=" + data.data.id
            this.$message.success('Avatar uploaded successfully')
          } else {
            this.$message.error(data.msg || 'Upload failed')
          }
        })
        .catch(error => {
          this.$message.error('Upload failed, please try again later')
        })
    },
    removeAvatar() {
      this.form.avatarUrl = ''
    }
  }
}
</script>

<style lang="scss" scoped>
/* Global Style Variables */
:root {
  --primary-color: #22b3c1;
  --secondary-color: #4dd0e1;
  --text-color: #333;
  --text-light: #666;
  --background-light: #e0f7fa;
  --white: #fff;
  --border-color: #b2ebf2;
}

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 50%, #80deea 100%);
  padding: 20px;
  font-family: 'Poppins', sans-serif;
}

.login-wrapper {
  display: flex;
  width: 100%;
  max-width: 1200px;
  min-height: 700px;
  background: linear-gradient(135deg, #ffffff 0%, #e0f7fa 100%);
  border-radius: 30px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(34, 179, 193, 0.25);
  animation: fadeIn 0.5s ease;
}

/* Left Brand Section */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #22b3c1 0%, #4dd0e1 50%, #80deea 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--white);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(circle at 30% 20%, rgba(255, 255, 255, 0.2) 0%, transparent 50%),
                radial-gradient(circle at 70% 80%, rgba(255, 255, 255, 0.15) 0%, transparent 50%);
  }

  .brand-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 60px;
    position: relative;
    z-index: 1;

    .brand-name {
      font-size: 52px;
      font-weight: 800;
      color: var(--white);
      margin-bottom: 20px;
      letter-spacing: 5px;
      text-transform: uppercase;
      text-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
      animation: slideInLeft 0.8s ease;
    }

    .brand-slogan {
      font-size: 22px;
      color: rgba(255, 255, 255, 0.9);
      font-weight: 400;
      margin-bottom: 50px;
      max-width: 350px;
      line-height: 1.4;
      animation: slideInLeft 0.8s ease 0.2s both;
    }

    .brand-image {
      width: 100%;
      max-width: 320px;
      border-radius: 25px;
      overflow: hidden;
      box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
      backdrop-filter: blur(10px);
      background: rgba(255, 255, 255, 0.1);
      padding: 25px;
      margin-top: 30px;
      animation: slideInLeft 0.8s ease 0.4s both;

      img {
        width: 100%;
        height: auto;
        border-radius: 15px;
        object-fit: cover;
        transition: transform 0.5s ease;

        &:hover {
          transform: scale(1.1);
        }
      }
    }
  }
}

/* Right Login Form */
.login-right {
  flex: 1;
  padding: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: slideInRight 0.8s ease;

  .login-card {
    width: 100%;
    max-width: 450px;
    background: linear-gradient(135deg, #ffffff 0%, #e0f7fa 100%);
    border-radius: 20px;
    padding: 40px;
    box-shadow: 0 10px 30px rgba(34, 179, 193, 0.12);
    transition: transform 0.3s ease;
    border: 2px solid #b2ebf2;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 15px 35px rgba(34, 179, 193, 0.2);
    }

    .login-header {
      text-align: center;
      margin-bottom: 40px;

      h2 {
        font-size: 32px;
        color: var(--text-color);
        margin-bottom: 10px;
        font-weight: 700;
      }

      p {
        color: var(--text-light);
        font-size: 16px;
        font-weight: 500;
      }
    }

    .form-group {
      margin-bottom: 25px;

      label {
        display: block;
        margin-bottom: 10px;
        font-weight: 500;
        color: var(--text-color);
        font-size: 14px;
      }

      .input-wrapper {
        position: relative;

        .input-icon {
          position: absolute;
          left: 20px;
          top: 50%;
          transform: translateY(-50%);
          font-size: 18px;
          color: var(--text-light);
          z-index: 1;
        }

        ::v-deep .el-input__inner {
          padding-left: 50px;
          height: 55px;
          border-radius: 12px;
          border: 2px solid #b2ebf2;
          font-size: 14px;
          transition: all 0.3s ease;
          background: #e0f7fa;

          &:focus {
            border-color: #4dd0e1;
            box-shadow: 0 0 0 4px rgba(77, 208, 225, 0.2);
            background: #ffffff;
          }
        }
      }
    }

    .form-options {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;

      .remember-me {
        display: flex;
        align-items: center;
        gap: 10px;
        cursor: pointer;
        font-size: 14px;
        color: var(--text-color);
        font-weight: 500;

        input[type="checkbox"] {
          width: 18px;
          height: 18px;
          accent-color: var(--secondary-color);
        }
      }

      .forgot-password {
        font-size: 14px;
        color: var(--secondary-color);
        text-decoration: none;
        transition: color 0.3s ease;
        font-weight: 500;

        &:hover {
          color: var(--primary-color);
        }
      }
    }

    .login-btn {
      height: 55px;
      font-size: 16px;
      font-weight: 600;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      margin-bottom: 25px;
    }

    .divider {
      display: flex;
      align-items: center;
      margin: 30px 0;
      color: #22b3c1;
      font-size: 14px;
      font-weight: 500;

      &::before,
      &::after {
        content: '';
        flex: 1;
        height: 2px;
        background: linear-gradient(90deg, transparent, #4dd0e1, transparent);
      }

      span {
        padding: 0 20px;
      }
    }

    .register-btn {
      height: 55px;
      font-size: 16px;
      font-weight: 600;
    }
  }
}

/* Button Styles */
.main-button {
  background: linear-gradient(135deg, #22b3c1 0%, #4dd0e1 100%);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  padding: 15px 30px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(34, 179, 193, 0.35);
  width: 100%;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 25px rgba(34, 179, 193, 0.45);
    background: linear-gradient(135deg, #0097a7 0%, #80deea 100%);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
}

.border-button {
  background: transparent;
  color: #22b3c1;
  border: 2px solid #22b3c1;
  border-radius: 12px;
  padding: 13px 30px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;

  &:hover {
    background: linear-gradient(135deg, #22b3c1 0%, #4dd0e1 100%);
    color: #ffffff;
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(34, 179, 193, 0.35);
  }
}

/* Loading Animation */
.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: var(--white);
  animation: spin 0.8s linear infinite;
}

/* Register Dialog */
.register-dialog ::v-deep .el-dialog {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.register-dialog ::v-deep .el-dialog__header {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  padding: 25px 30px;
  margin: 0;
}

.register-dialog ::v-deep .el-dialog__title {
  color: var(--white);
  font-size: 20px;
  font-weight: 600;
}

.register-dialog ::v-deep .el-dialog__headerbtn .el-dialog__close {
  color: var(--white);
  font-size: 20px;
}

.register-form {
  padding: 30px 0;

  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    margin-bottom: 20px;
  }

  .form-group {
    margin-bottom: 20px;

    &.half {
      margin-bottom: 0;
    }

    label {
      display: block;
      margin-bottom: 8px;
      font-weight: 500;
      color: var(--text-color);
      font-size: 14px;
    }

    ::v-deep .el-input__inner {
      height: 50px;
      border-radius: 10px;
      border: 2px solid #b2ebf2;
      font-size: 14px;
      transition: all 0.3s ease;
      background: #e0f7fa;

      &:focus {
        border-color: #4dd0e1;
        box-shadow: 0 0 0 4px rgba(77, 208, 225, 0.2);
        background: #ffffff;
      }
    }
  }

  .upload-area {
    border: 2px dashed #b2ebf2;
    border-radius: 15px;
    padding: 35px 20px;
    text-align: center;
    transition: all 0.3s ease;
    min-height: 180px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    background: #e0f7fa;

    &:hover,
    &.drag-over {
      border-color: #4dd0e1;
      background-color: rgba(77, 208, 225, 0.1);
    }

    .upload-placeholder {
      width: 100%;

      .upload-icon {
        font-size: 48px;
        margin-bottom: 15px;
        display: block;
        color: var(--text-light);
      }

      p {
        color: var(--text-light);
        font-size: 14px;
        margin-bottom: 20px;
        font-weight: 500;
      }
    }

    .image-preview {
      position: relative;
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;

      img {
        max-width: 180px;
        max-height: 180px;
        border-radius: 15px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
      }

      .remove-btn {
        position: absolute;
        top: -15px;
        right: -15px;
        width: 35px;
        height: 35px;
        border-radius: 50%;
        background-color: #ff4757;
        color: var(--white);
        border: none;
        cursor: pointer;
        font-size: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 5px 15px rgba(255, 71, 87, 0.3);
        transition: all 0.3s ease;

        &:hover {
          background-color: #ff6b81;
          transform: scale(1.1);
        }
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 20px 30px;
  border-top: 1px solid #b2ebf2;

  button {
    padding: 12px 35px;
    font-size: 14px;
    font-weight: 600;
  }
}

/* Animation Effects */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Responsive Design */
@media (max-width: 992px) {
  .login-wrapper {
    flex-direction: column;
    max-width: 500px;
    min-height: 800px;
  }

  .login-left {
    min-height: 400px;

    .brand-section {
      padding: 40px;

      .brand-name {
        font-size: 42px;
      }

      .brand-slogan {
        font-size: 18px;
      }

      .brand-image {
        max-width: 280px;
      }
    }
  }

  .login-right {
    padding: 40px;

    .login-card {
      padding: 30px;

      .login-header h2 {
        font-size: 28px;
      }
    }
  }
}

@media (max-width: 576px) {
  .login-page {
    padding: 10px;
  }

  .login-wrapper {
    border-radius: 20px;
    min-height: 700px;
  }

  .login-left {
    min-height: 300px;

    .brand-section {
      padding: 30px 20px;

      .brand-name {
        font-size: 36px;
        letter-spacing: 3px;
      }

      .brand-slogan {
        font-size: 16px;
        margin-bottom: 30px;
      }

      .brand-image {
        max-width: 200px;
        padding: 15px;
      }
    }
  }

  .login-right {
    padding: 30px 20px;

    .login-card {
      padding: 25px;

      .login-header h2 {
        font-size: 24px;
      }

      .form-group {
        margin-bottom: 20px;

        .input-wrapper ::v-deep .el-input__inner {
          height: 48px;
        }
      }

      .login-btn,
      .register-btn {
        height: 48px;
        font-size: 14px;
      }
    }
  }

  .register-form {
    .form-row {
      grid-template-columns: 1fr;
    }

    .upload-area {
      min-height: 150px;
      padding: 25px 20px;

      .upload-placeholder {
        .upload-icon {
          font-size: 36px;
        }
      }

      .image-preview img {
        max-width: 150px;
        max-height: 150px;
      }
    }
  }
}
</style>
