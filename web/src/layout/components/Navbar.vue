<template>
  <div class="navbar">
    <div class="right-menu">
        <div class="right-menu-item">
          <el-button :style="{ borderRadius: '20px', backgroundColor: '#f5a537', color: 'white' }" @click="logout()">Logout</el-button>
        </div>
        <div class="right-menu-item">
          <img class="img" :src="avatarUrl" width="30px" height="30px"/>
        </div>
        <div class="right-menu-item">
          {{ username }}
        </div>
    </div>
  </div>
</template>

<script>
import {logoutSys} from '@/api/login'

export default {
  data() {
    return {
      updatePasswordVisiable: false,
      username: sessionStorage.getItem('name'),
      avatarUrl: sessionStorage.getItem('avatarUrl'),
    }
  },
  methods: {
    async logout() {
      this.$confirm('Are you sure you want to logout?', 'Confirm', {
        confirmButtonText: 'Logout',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(async() => {
        logoutSys()
        sessionStorage.clear()
        this.$router.push(`/login`)
        this.$message.success('Logout successful')
      }).catch(() => {
        this.$message.info('Logout cancelled')
      })
    },
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}

.img{
  width: 30px;
  height: 35px;
  margin-top: 8px;
}
</style>
