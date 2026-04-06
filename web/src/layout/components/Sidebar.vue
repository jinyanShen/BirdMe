<template>
  <div>

    <el-menu
    @select="handleSelect"
    default-active="1"
    :background-color="'#0d2035'"
    :text-color="'#ffffff'"
    :active-text-color="'#ffffff'"
    mode="vertical"
    class="custom-menu"
    >
      <el-menu-item style="height: 120px;justify-items: center;text-align: center;margin-bottom: 20px;margin-top: 20px;">
        <el-image :src="url"></el-image>
      </el-menu-item>
      <el-menu-item index="1" v-show="role=='0'">
        <span slot="title">鸟类识别</span>
      </el-menu-item>
      <el-menu-item index="2" v-show="role=='0'">
        <span slot="title">个人中心</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script>
import logoImg from '@/assets/logo/logo.jpg'
export default {
  data() {
    return {
      role: sessionStorage.getItem('role'),
      url: logoImg
    }
  },
  methods: {
    handleSelect(key) {
        if(key==='1'){
          this.$router.replace("/knowledge");
        }
        else if(key==='2'){
          this.$router.replace("/settings");
        }
    }
  }
}
</script>

<style scoped>
.custom-menu .el-menu-item.is-active {
  height: 30px;
  line-height: 30px;
  font-size: 16px;
  background-color: #409EFF !important;/* 蓝色背景 */
  color: #ffffff; /* 激活时文本颜色 */
}
.custom-menu .el-menu-item {
  height: 30px;
  line-height: 30px;
  font-size: 16px;
  color: #333; /* 非激活时文本颜色 */
}
.custom-menu .el-menu-item span {
  display: inline-block !important;
  width: 100% !important;
  text-align: center !important;
}
</style>
