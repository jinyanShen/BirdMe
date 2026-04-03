<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :show-close="true"
    width="1200px"
    custom-class="login-dialog"
    append-to-body
    @close="handleClose"
  >
    <div class="login-wrapper" ref="panel" @mousemove="onPanelMouseMove" @mouseleave="onPanelMouseLeave">

      <!-- 关闭按钮 -->
      <div class="login-close-btn" @click="handleClose">×</div>

      <!-- 左侧品牌区域 -->
      <div class="login-left" ref="leftPanel">
        <div class="brand-section">
          <h1 class="brand-name">BIRDME</h1>
          <p class="brand-slogan">A Website for Bird Rescue and Migration Monitoring</p>

          <div class="character-stage" aria-hidden="true">
            <div
              v-for="c in characters"
              :key="c.id"
              class="character"
              :class="[
                `character--${c.variant}`,
                `character--${c.shape}`,
                {
                  'is-main': c.isMain,
                  'is-input-mode': c.isMain && lookMode === 'input',
                  'is-away': c.isMain && lookMode === 'away'
                }
              ]"
              :style="getCharacterStyle(c)"
            >
              <div class="monster">
                <div class="bird-crown"></div>
                <div class="bird-beak"></div>
                <div class="monster-eyes">
                  <div class="eye eye-left"><div class="pupil"></div></div>
                  <div class="eye eye-right"><div class="pupil"></div></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-right">
        <div class="login-card">
          <div class="login-header">
            <h2>Welcome Back :)</h2>
            <p>Please log in to your account</p>
          </div>

          <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" autocomplete="on">
            <div class="form-group">
              <label>Username</label>
              <div class="input-wrapper">
                <el-input
                  ref="username"
                  v-model="loginForm.username"
                  placeholder="Please enter username"
                  name="username"
                  type="text"
                  autocomplete="off"
                  clearable
                  @focus="onFieldFocus('username')"
                  @blur="onFieldBlur"
                  @input="onFieldInput('username')"
                  @keyup.enter.native="handleLogin"
                >
                  <template #prefix>
                    <i class="input-icon">👤</i>
                  </template>
                </el-input>
              </div>
            </div>

            <div class="form-group">
              <label>Password</label>
              <div class="input-wrapper">
                <el-input
                  ref="password"
                  v-model="loginForm.password"
                  placeholder="Please enter password"
                  name="password"
                  :type="passwordVisible ? 'text' : 'password'"
                  clearable
                  @focus="onFieldFocus('password')"
                  @blur="onFieldBlur"
                  @input="onFieldInput('password')"
                  @keyup.enter.native="handleLogin"
                >
                  <template #prefix>
                    <i class="input-icon fas fa-lock"></i>
                  </template>
                  <template #suffix>
                    <i
                      :class="passwordVisible ? 'fas fa-eye-slash' : 'fas fa-eye'"
                      style="cursor: pointer; color: #999; font-size: 16px;"
                      @click="togglePasswordVisible"
                    ></i>
                  </template>
                </el-input>
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

            <button class="register-btn border-button w-100" @click.prevent="openRegister">
              Create New Account
            </button>
          </el-form>
        </div>
      </div>
    </div>

    <!-- Register Dialog -->
    <el-dialog
      title="Create New Account"
      :visible.sync="registerVisible"
      width="600px"
      append-to-body
      custom-class="register-dialog"
      :close-on-click-modal="false"
    >
      <div class="register-form">
        <div class="form-row">
          <div class="form-group half">
            <label>Username</label>
            <el-input v-model="registerForm.username" placeholder="Please enter username" />
          </div>
          <div class="form-group half">
            <label>Password</label>
            <el-input v-model="registerForm.password" type="password" placeholder="Please enter password" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group half">
            <label>Name</label>
            <el-input v-model="registerForm.name" placeholder="Please enter name" />
          </div>
          <div class="form-group half">
            <label>Age</label>
            <el-input v-model="registerForm.age" type="number" placeholder="Please enter age" />
          </div>
        </div>
        <div class="form-group">
          <label>Contact</label>
          <el-input v-model="registerForm.phone" placeholder="Please enter contact information" />
        </div>
        <div class="form-group">
          <label>User Avatar</label>
          <div class="upload-area" :class="{ 'drag-over': isDragOver }"
               @dragover.prevent="isDragOver = true"
               @dragleave.prevent="isDragOver = false"
               @drop.prevent="handleDrop">
            <div v-if="!registerForm.avatarUrl" class="upload-placeholder">
              <i class="upload-icon">📷</i>
              <p>Drag image here or click to upload</p>
              <input type="file" @change="handleImageUpload" accept="image/*" ref="fileInput" style="display: none;">
              <button class="border-button" @click="$refs.fileInput.click()">Select Image</button>
            </div>
            <div v-else class="image-preview">
              <img :src="registerForm.avatarUrl" alt="Avatar">
              <button class="remove-btn" @click="removeAvatar">
                <i>×</i>
              </button>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <button class="main-button" @click="doRegister">Confirm</button>
        <button class="border-button" @click="cancelRegister">Cancel</button>
      </div>
    </el-dialog>
  </el-dialog>
</template>

<script>
import { login, register } from '@/api/login'
import router from '@/router'

export default {
  name: 'LoginDialog',
  data() {
    return {
      dialogVisible: false,
      redirectPath: null,
      loginForm: {
        username: '',
        password: '',
      },
      loginRules: {
        username: [{ required: true, message: 'Please enter username', trigger: 'blur' }],
        password: [{ required: true, message: 'Please enter password', trigger: 'blur' }]
      },
      loading: false,
      passwordVisible: false,
      registerVisible: false,
      registerForm: {
        username: '',
        password: '',
        name: '',
        age: '',
        phone: '',
        avatarUrl: ''
      },
      isDragOver: false,

      lookMode: 'mouse',
      gaze: { x: 0, y: 0 },
      head: { x: 0, y: 0 },
      neck: { stretch: 0 },

      characters: [
        {
          id: 'main',
          isMain: true,
          variant: 'purple',
          shape: 'rect',
          offsetX: 0,
          offsetY: 0,
          scale: 1,
          height: 240,
          eyeTop: 60
        },
        {
          id: 'buddy1',
          isMain: false,
          variant: 'black',
          shape: 'rect-round',
          offsetX: 170,
          offsetY: 20,
          scale: 0.85,
          height: 220,
          eyeTop: 55
        },
        {
          id: 'buddy2',
          isMain: false,
          variant: 'yellow',
          shape: 'semicircle',
          offsetX: -140,
          offsetY: 20,
          scale: 0.9,
          height: 200,
          eyeTop: 50
        },
        {
          id: 'buddy3',
          isMain: false,
          variant: 'orange',
          shape: 'ellipse',
          offsetX: -20,
          offsetY: 70,
          scale: 0.95,
          height: 170,
          eyeTop: 45
        }
      ]
    }
  },
  watch: {
    redirectPath(newVal, oldVal) {
      console.log('redirectPath changed from:', oldVal, 'to:', newVal)
    }
  },
  methods: {
    show(redirectPath) {
      console.log('=== LoginDialog show called ===')
      console.log('Received redirectPath:', redirectPath)
      this.redirectPath = redirectPath
      console.log('this.redirectPath set to:', this.redirectPath)
      this.dialogVisible = true
      this.loginForm = { username: '', password: '' }
      if (this.$refs.loginForm) {
        this.$refs.loginForm.clearValidate()
      }
    },

    handleClose() {
      this.dialogVisible = false
    },

    onPanelMouseMove(e) {
      if (this.lookMode !== 'mouse') return;
      const rect = this.$refs.leftPanel?.getBoundingClientRect?.();
      if (!rect) return;

      const nx = ((e.clientX - rect.left) / rect.width) * 2 - 1;
      const ny = ((e.clientY - rect.top) / rect.height) * 2 - 1;

      this.setGaze(nx, ny);
      this.setHeadOffset(nx * 10, ny * 6);
      this.setNeckStretch(Math.min(0.25, Math.hypot(nx, ny) * 0.18));
    },

    onPanelMouseLeave() {
      if (this.lookMode !== 'mouse') return;
      this.relaxPose();
    },

    getCharacterStyle(c) {
      const base = {
        '--char-x': `${c.offsetX}px`,
        '--char-y': `${c.offsetY}px`,
        '--char-scale': String(c.scale),
        '--char-height': `${c.height}px`,
        '--eye-top': `${c.eyeTop}px`
      }

      if (c.isMain) {
        return {
          ...base,
          '--gaze-x': String(this.gaze.x),
          '--gaze-y': String(this.gaze.y),
          '--head-x': `${this.head.x}px`,
          '--head-y': `${this.head.y}px`,
          '--stretch': String(this.neck.stretch)
        }
      }

      const damp = 0.55
      return {
        ...base,
        '--gaze-x': String(this.gaze.x * damp),
        '--gaze-y': String(this.gaze.y * damp),
        '--head-x': `${this.head.x * 0.18}px`,
        '--head-y': `${this.head.y * 0.18}px`,
        '--stretch': '0'
      }
    },

    togglePasswordVisible() {
      this.passwordVisible = !this.passwordVisible
      if (this.passwordVisible) {
        this.lookMode = 'away'
        this.setGaze(-0.9, -0.2)
        this.setHeadOffset(-10, 4)
      } else {
        this.lookMode = 'input'
        this.onFieldInput('password')
      }
    },

    onFieldFocus(field) {
      if (this.passwordVisible) {
        this.lookMode = 'away'
        return
      }
      this.lookMode = 'input'
      this.onFieldInput(field)
    },

    onFieldBlur() {
      if (this.passwordVisible) {
        this.lookMode = 'away'
        return
      }
      this.lookMode = 'mouse'
      this.relaxPose()
    },

    onFieldInput(field) {
      if (this.passwordVisible) return
      this.lookMode = 'input'
      this.$nextTick(() => {
        const target = this.getInputEl(field)
        const stage = this.$refs.leftPanel
        if (!target || !stage) return

        const tr = target.getBoundingClientRect()
        const sr = stage.getBoundingClientRect()

        const tx = tr.left + tr.width / 2
        const ty = tr.top + tr.height / 2

        const nx = ((tx - sr.left) / sr.width) * 2 - 1
        const ny = ((ty - sr.top) / sr.height) * 2 - 1

        this.setGaze(nx * 1.05, ny * 1.05)
        this.setHeadOffset(nx * 16, ny * 10)
        this.setNeckStretch(Math.min(1, 0.35 + Math.min(0.65, Math.abs(nx) * 0.3 + Math.max(0, -ny) * 0.5)))
      })
    },

    getInputEl(field) {
      const ref = field === 'username' ? this.$refs.username : this.$refs.password
      const rootEl = ref && (ref.$el || ref)
      if (!rootEl || !rootEl.querySelector) return null
      return rootEl.querySelector('input')
    },

    setGaze(x, y) {
      const clamp = (n) => Math.max(-1, Math.min(1, n))
      this.gaze.x = clamp(x)
      this.gaze.y = clamp(y)
    },

    setHeadOffset(x, y) {
      this.head.x = Math.max(-18, Math.min(18, x))
      this.head.y = Math.max(-12, Math.min(12, y))
    },

    setNeckStretch(v) {
      this.neck.stretch = Math.max(0, Math.min(1, v))
    },

    relaxPose() {
      this.setGaze(0, 0)
      this.setHeadOffset(0, 0)
      this.setNeckStretch(0)
    },

    handleLogin() {
      const savedRedirectPath = this.redirectPath
      console.log('=== handleLogin called ===')
      console.log('savedRedirectPath:', savedRedirectPath)

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
                this.dialogVisible = false

                console.log('跳转到:', savedRedirectPath)

                setTimeout(() => {
                  if (savedRedirectPath && savedRedirectPath !== '/' && savedRedirectPath !== '/login') {
                    router.push(savedRedirectPath)
                  } else {
                    router.push('/identification')
                  }

                  setTimeout(() => {
                    window.location.reload()
                  }, 100)
                }, 200)
              } else {
                this.loading = false
                this.$message.error(res.msg)
              }
            })
            .catch((error) => {
              console.error('Login error:', error)
              this.loading = false
            })
        }
      })
    },

    openRegister() {
      this.registerVisible = true
      this.registerForm = {
        username: '',
        password: '',
        name: '',
        age: '',
        phone: '',
        avatarUrl: ''
      }
    },

    cancelRegister() {
      this.registerVisible = false
    },

    doRegister() {
      if (!this.registerForm.username || !this.registerForm.password) {
        this.$message.error('Username and password cannot be empty')
        return
      }
      register(this.registerForm).then(res => {
        if (res.code === 200) {
          this.registerVisible = false
          this.$message.success("Registration successful")
          this.loginForm.username = this.registerForm.username
          this.loginForm.password = this.registerForm.password
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
            this.registerForm.avatarUrl = "http://localhost:8080/file/download?id=" + data.data.id
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
      this.registerForm.avatarUrl = ''
    }
  }
}
</script>

<style lang="scss">
/* 变量 */
:root {
  --primary-color: #8aa8e2;
  --secondary-color: #9ab5e2;
  --text-color: #333;
  --text-light: #666;
  --background-light: #e0f7fa;
  --white: #fff;
  --border-color: #949bca;
}

/* 弹窗外层 */
.login-dialog.el-dialog {
  box-shadow: none !important;
  border-radius: 30px !important;
  overflow: hidden !important;
  width: 1200px !important;
  max-width: 1200px !important;
  padding: 0 !important;
  margin: 0 !important;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.login-dialog .el-dialog__body {
  padding: 0 !important;
  background: transparent !important;
}

.login-dialog .el-dialog__header {
  display: none !important;
}

.login-wrapper {
  display: flex;
  width: 100%;
  max-width: 1200px;
  min-height: 700px;
  background: linear-gradient(135deg, #ffffff 0%, #e0f7fa 100%);
  border-radius: 30px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgb(81, 73, 117);
  animation: fadeIn 0.5s ease;
  position: relative;
}

/* 左侧品牌区域 */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #7295e3 0%, #90afe3 50%, #f1f4f8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0; left: 0; right: 0; bottom: 0;
    background: radial-gradient(circle at 30% 20%, rgba(255,255,255,0.2) 0%, transparent 50%),
    radial-gradient(circle at 70% 80%, rgba(255,255,255,0.15) 0%, transparent 50%);
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
      color: #303351;
      margin-bottom: 20px;
      letter-spacing: 5px;
      text-transform: uppercase;
      text-shadow: 0 4px 15px rgb(255,255,255);
      animation: slideInLeft 0.8s ease;
    }

    .brand-slogan {
      font-size: 22px;
      color: rgba(255,255,255,0.9);
      font-weight: 400;
      margin-bottom: 50px;
      max-width: 350px;
      line-height: 1.4;
      animation: slideInLeft 0.8s ease 0.2s both;
    }

    .character-stage {
      width: 100%;
      max-width: 520px;
      height: 300px;
      margin-top: 10px;
      position: relative;
      animation: slideInLeft 0.8s ease 0.35s both;
      padding-left: 10px;
      margin-left: -22px;
    }

    .character {
      width: 200px;
      height: var(--char-height, 230px);
      position: absolute;
      left: 50%;
      bottom: 0;
      transform: translate(-50%, 0)
      translate(var(--char-x, 0px), var(--char-y, 0px))
      scale(var(--char-scale, 1))
      translate(var(--head-x, 0px), var(--head-y, 0px))
      translateZ(0);
      transform-origin: 50% 100%;
      will-change: transform, filter;
      user-select: none;
      transition: filter 180ms ease;
      animation: floatBob 3.6s ease-in-out infinite;
      z-index: 1;

      &.is-away {
        filter: saturate(0.95) brightness(0.98);
      }

      &.character--orange { z-index:4; animation-duration:4.5s; animation-delay:0.15s; }
      &.character--black  { z-index:3; animation-duration:3.9s; animation-delay:0.25s; }
      &.character--yellow { z-index:2; animation-duration:4.2s; animation-delay:0.1s; }
      &.is-main           { z-index:1; animation-duration:3.1s; }

      &.character--rect        .monster { border-radius:26px 26px 0 0; }
      &.character--rect-round  .monster { border-radius:26px; }
      &.character--semicircle  .monster { border-radius:100px 100px 0 0; height:100%; }
      &.character--ellipse     .monster { border-radius:50% / 30% 30% 0 0; }
    }

    .monster {
      width: 100%;
      height: 100%;
      background: var(--char-body, rgba(255,255,255,0.22));
      box-shadow: 0 22px 60px rgba(0,0,0,0.22);
      position: relative;
      transform-origin: 50% 100%;
      border: 1px solid rgba(255,255,255,0.18);
      backdrop-filter: blur(6px);
      transition: transform 140ms ease;
      overflow: visible;

      --skew: calc(var(--stretch) * -6deg);
      --scale: calc(1 + var(--stretch) * 0.12);
      transform: scaleY(var(--scale)) skewY(var(--skew));
    }

    .character.is-away .monster {
      transform: scaleY(1) skewY(0deg) rotate(-2deg);
    }

    .bird-crown {
      position: absolute;
      top: -18px;
      left: 50%;
      transform: translateX(-50%);
      width: 28px;
      height: 24px;
      display: flex;
      justify-content: center;
      gap: 4px;
      z-index: 2;
    }
    .bird-crown::before,
    .bird-crown::after {
      content: '';
      width: 6px;
      background: var(--char-body);
      border-radius: 10px;
    }
    .bird-crown::before { height:28px; }
    .bird-crown::after  { height:22px; }

    .bird-beak {
      position: absolute;
      top: 40%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 0; height: 0;
      border-left:9px solid transparent;
      border-right:9px solid transparent;
      border-top:13px solid #ffcc33;
      z-index:10;
    }

    .monster-eyes {
      position: absolute;
      top: var(--eye-top,50px);
      left: 50%;
      transform: translateX(-50%);
      width:78px; height:28px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .eye {
      width:26px; height:26px;
      background: rgba(255,255,255,0.92);
      border-radius:50%;
      box-shadow:inset 0 0 0 2px rgba(0,0,0,0.06);
      overflow:hidden;
      position: relative;
    }
    .eye-left { margin-right:10px; }
    .eye-right { margin-left:10px; }

    .pupil {
      position: absolute;
      left:50%; top:50%;
      width:10px; height:10px;
      border-radius:50%;
      background: rgba(35,35,35,0.95);
      transform: translate(-50%,-50%)
      translate(calc(var(--gaze-x,0)*6px), calc(var(--gaze-y,0)*4px));
      transition: transform 120ms ease;
    }
    .character.is-away .pupil {
      transform: translate(-50%,-50%)
      translate(calc(var(--gaze-x,0)*9px), calc(var(--gaze-y,0)*6px));
    }

    .character--purple { --char-body:#836fe6; }
    .character--black  { --char-body:#77b3dd; }
    .character--yellow { --char-body:rgb(241,217,119); }
    .character--orange { --char-body:#f1a482; }
  }
}

/* 右侧登录表单 */
.login-right {
  flex:1;
  padding:60px;
  display:flex;
  align-items:center;
  justify-content:center;
  animation:slideInRight 0.8s ease;
  background: linear-gradient(135deg, #ffffff 0%, #e0f7fa 100%);

  .login-card {
    width:100%;
    max-width:450px;
    background: linear-gradient(135deg, #ffffff 0%, #e0f7fa 100%);
    border-radius:20px;
    padding:40px;
    box-shadow:0 10px 30px rgb(52,55,104);
    transition:transform 0.3s ease;
    border:2px solid #98b4e4;

    &:hover {
      transform:translateY(-5px);
      box-shadow:0 15px 35px rgba(48,51,99,0.2);
    }

    .login-header {
      text-align:center;
      margin-bottom:40px;
      h2 { font-size:32px; color:#333; margin-bottom:10px; font-weight:700; }
      p { color:#666; font-size:16px; font-weight:500; }
    }

    .form-group {
      margin-bottom:25px;
      label { display:block; margin-bottom:10px; font-weight:500; color:#333; font-size:14px; }
      .input-wrapper {
        position:relative;

        ::v-deep .el-input__prefix {
          left: 20px !important;
          display: flex !important;
          align-items: center !important;
          height: 50% !important;
          top: 0 !important;
          transform: none !important;
        }

        ::v-deep .el-input__inner {
          padding-left: 50px !important;
          padding-right: 50px;
          height: 55px;
          border-radius: 12px;
          border: 2px solid #b2bff2;
          font-size: 14px;
          transition: all 0.3s ease;
          background: #e0f7fa;

          &:focus {
            border-color: #4d66e1;
            box-shadow: 0 0 0 4px rgba(77,97,225,0.2);
            background: #fff;
          }
        }

        .input-icon {
          font-size: 18px;
          color: #666;
          line-height: 1;
        }
      }
    }

    .login-btn { height:55px; font-size:16px; font-weight:600; margin-bottom:25px; }
    .divider {
      display:flex; align-items:center; margin:30px 0;
      color:#4d7cd6; font-size:14px; font-weight:500;
      &::before, &::after {
        content:''; flex:1; height:2px;
        background:linear-gradient(90deg,transparent,#4d77e1,transparent);
      }
      span { padding:0 20px; }
    }
    .register-btn { height:55px; font-size:16px; font-weight:600; }
  }
}

/* 按钮 */
.main-button {
  background:linear-gradient(135deg, #465ccd 0%, #5885e4 100%);
  color:#fff; border:none; border-radius:12px; padding:15px 30px;
  font-weight:600; cursor:pointer; transition:all 0.3s ease;
  box-shadow:0 6px 20px rgba(34,79,193,0.35); width:100%;
  &:hover {
    transform:translateY(-3px);
    box-shadow:0 10px 25px rgba(34,90,193,0.45);
    background:linear-gradient(135deg,#4559c6 0%,#80a5ea 100%);
  }
  &:disabled { opacity:0.6; cursor:not-allowed; transform:none; box-shadow:none; }
}
.border-button {
  background:transparent; color:#226cc1; border:2px solid #2d66ba;
  border-radius:12px; padding:13px 30px; font-weight:600;
  cursor:pointer; transition:all 0.3s ease; width:100%;
  &:hover {
    background:linear-gradient(135deg,#225ac1 0%,#6499df 100%);
    color:#fff; transform:translateY(-3px);
    box-shadow:0 6px 20px rgba(34,98,193,0.35);
  }
}
.spinner {
  width:20px; height:20px;
  border:2px solid rgba(255,255,255,0.3);
  border-radius:50%; border-top-color:#fff;
  animation:spin 0.8s linear infinite; display:inline-block;
}

/* 注册弹窗 */
::v-deep .register-dialog {
  .el-dialog { border-radius:20px; overflow:hidden; }
  .el-dialog__header {
    background:linear-gradient(135deg,#7295e3 0%,#90afe3 100%);
    padding:25px 30px; margin:0;
    .el-dialog__title { color:#fff; font-size:20px; font-weight:600; }
  }
}
.register-form {
  padding:30px 0;
  .form-row { display:grid; grid-template-columns:1fr 1fr; gap:20px; margin-bottom:20px; }
  .form-group {
    margin-bottom:20px;
    &.half { margin-bottom:0; }
    label { display:block; margin-bottom:8px; font-weight:500; color:#333; font-size:14px; }
    ::v-deep .el-input__inner {
      height:50px; border-radius:10px; border:2px solid #b2d2f2;
      font-size:14px; background:#e0f7fa;
      transition:all 0.3s ease;
      &:focus {
        border-color:#4d8de1;
        box-shadow:0 0 0 4px rgba(77,112,225,0.2);
        background:#fff;
      }
    }
  }
  .upload-area {
    border:2px dashed #b2d0f2; border-radius:15px; padding:35px 20px;
    text-align:center; transition:all 0.3s ease; min-height:180px;
    display:flex; align-items:center; justify-content:center; cursor:pointer; background:#e0f7fa;
    &:hover,&.drag-over { border-color:#4d92e1; background-color:rgba(56,83,172,0.1); }
    .upload-placeholder {
      width:100%;
      .upload-icon { font-size:48px; margin-bottom:15px; display:block; color:#666; }
      p { color:#666; font-size:14px; margin-bottom:20px; font-weight:500; }
    }
    .image-preview {
      position:relative; width:100%; height:100%;
      display:flex; align-items:center; justify-content:center;
      img { max-width:180px; max-height:180px; border-radius:15px; box-shadow:0 10px 30px rgba(0,0,0,0.1); }
      .remove-btn {
        position:absolute; top:-15px; right:-15px; width:35px; height:35px;
        border-radius:50%; background-color:#ff4757; color:#fff; border:none; cursor:pointer;
        font-size:20px; display:flex; align-items:center; justify-content:center;
        box-shadow:0 5px 15px rgba(255,71,87,0.3); transition:all 0.3s ease;
        &:hover { background-color:#ff6b81; transform:scale(1.1); }
      }
    }
  }
}
.dialog-footer {
  display:flex; justify-content:flex-end; gap:15px;
  padding:20px 30px; border-top:1px solid #b2d7f2;
  button { padding:12px 35px; font-size:14px; font-weight:600; }
}

/* 动画 */
@keyframes fadeIn { from{opacity:0; transform:scale(0.9);} to{opacity:1; transform:scale(1);} }
@keyframes slideInLeft { from{opacity:0; transform:translateX(-50px);} to{opacity:1; transform:translateX(0);} }
@keyframes slideInRight { from{opacity:0; transform:translateX(50px);} to{opacity:1; transform:translateX(0);} }
@keyframes spin { to{transform:rotate(360deg);} }
@keyframes floatBob {
  0%   { transform:translate(-50%,0) translate(var(--char-x,0),var(--char-y,0)) scale(var(--char-scale,1)) translate(var(--head-x,0),var(--head-y,0)) translateY(0) translateZ(0); }
  50%  { transform:translate(-50%,0) translate(var(--char-x,0),var(--char-y,0)) scale(var(--char-scale,1)) translate(var(--head-x,0),var(--head-y,0)) translateY(-8px) translateZ(0); }
  100% { transform:translate(-50%,0) translate(var(--char-x,0),var(--char-y,0)) scale(var(--char-scale,1)) translate(var(--head-x,0),var(--head-y,0)) translateY(0) translateZ(0); }
}

/* 关闭按钮 */
.login-close-btn {
  position:absolute; right:20px; top:20px;
  width:36px; height:36px; border-radius:50%;
  background:rgba(255,255,255,0.2); backdrop-filter:blur(6px);
  border:1px solid rgba(255,255,255,0.3);
  font-size: 50px;
  color:#303351;
  display:flex; align-items:center; justify-content:center;
  cursor:pointer; z-index:9999; transition:all 0.2s ease;
}
.login-close-btn:hover {
  background:rgba(255,255,255,0.4);
  transform:scale(1.1);
}

/* 响应式 */
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
      .brand-name { font-size: 42px; }
      .brand-slogan { font-size: 18px; }
    }
  }
  .login-right {
    padding: 40px;
    .login-card { padding: 30px; }
  }
}
@media (max-width: 576px) {
  .login-left .brand-section {
    padding: 30px 20px;
    .brand-name { font-size: 36px; }
    .brand-slogan { font-size: 16px; }
  }
  .login-right {
    padding: 30px 20px;
    .login-card { padding: 25px; }
  }
  .register-form .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
